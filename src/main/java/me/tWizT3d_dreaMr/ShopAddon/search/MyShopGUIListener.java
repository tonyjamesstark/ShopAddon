package me.tWizT3d_dreaMr.ShopAddon.search;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.snowgears.shop.Shop;
import com.snowgears.shop.gui.PlayerSettingsWindow;
import com.snowgears.shop.gui.ShopGUIListener;
import com.snowgears.shop.gui.ShopGuiWindow;
import com.snowgears.shop.handler.ShopGuiHandler;
import com.snowgears.shop.handler.ShopGuiHandler.GuiIcon;
import com.snowgears.shop.shop.AbstractShop;
import com.snowgears.shop.util.EconomyUtils;
import com.snowgears.shop.util.PlayerSettings.Option;
import com.snowgears.shop.util.ShopMessage;
import com.snowgears.shop.util.UtilMethods;
import me.tWizT3d_dreaMr.ShopAddon.main;


// copy of ShopGUIListener in com.snowgears.shop.gui to accomodate new classes

public class MyShopGUIListener extends ShopGUIListener {

    private Shop plugin;

    private final GuiIcon[] sortIcons = {
            GuiIcon.MENUBAR_SORT_NAME_LOW,
            GuiIcon.MENUBAR_SORT_NAME_HIGH,
            GuiIcon.MENUBAR_SORT_PRICE_LOW,
            GuiIcon.MENUBAR_SORT_PRICE_HIGH
    };

    private final GuiIcon[] stockFilterIcons = {
            GuiIcon.MENUBAR_FILTER_STOCK_ALL,
            GuiIcon.MENUBAR_FILTER_STOCK_IN,
            GuiIcon.MENUBAR_FILTER_STOCK_OUT
    };

    private final GuiIcon[] typeFilterIcons = {
            GuiIcon.MENUBAR_FILTER_TYPE_ALL,
            GuiIcon.MENUBAR_FILTER_TYPE_SELL,
            GuiIcon.MENUBAR_FILTER_TYPE_BUY,
            GuiIcon.MENUBAR_FILTER_TYPE_BARTER,
            GuiIcon.MENUBAR_FILTER_TYPE_GAMBLE

    };

    public LoopingList sortIconsList;
    public LoopingList typeFilterIconsList;
    public LoopingList stockFilterIconsList;

    private Map<ItemStack, GuiIcon> itemToIcon = new HashMap<>();

    private Map<GuiIcon, Option> iconToOption = new HashMap<>();


    public MyShopGUIListener(Shop instance) {
        super(instance);
        plugin = instance;
        ShopGuiHandler gh = plugin.getGuiHandler();
        sortIconsList = new LoopingList(sortIcons);
        typeFilterIconsList = new LoopingList(typeFilterIcons);
        stockFilterIconsList = new LoopingList(stockFilterIcons);

        for (GuiIcon g : sortIcons) {
            ItemStack i = gh.getIcon(g, null, null);
            itemToIcon.put(i, g);
            iconToOption.put(g, Option.GUI_SORT);
        }

        for (GuiIcon g : stockFilterIcons) {
            ItemStack i = gh.getIcon(g, null, null);
            itemToIcon.put(i, g);
            iconToOption.put(g, Option.GUI_FILTER_SHOP_STOCK);
        }

        for (GuiIcon g : typeFilterIcons) {
            ItemStack i = gh.getIcon(g, null, null);
            itemToIcon.put(i, g);
            iconToOption.put(g, Option.GUI_FILTER_SHOP_TYPE);
        }

        itemToIcon.put(gh.getIcon(GuiIcon.HOME_SETTINGS, null, null), GuiIcon.HOME_SETTINGS);
        itemToIcon.put(gh.getIcon(GuiIcon.HOME_LIST_ALL_SHOPS, null, null), GuiIcon.HOME_LIST_ALL_SHOPS);

    }

    @EventHandler(ignoreCancelled = true)
    @Override
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Early exit for non-shop inventories to avoid creating stale windows
        // Regular chests have holders; shop GUI windows created with
        // Bukkit.createInventory(null, ...) don't
        if (event.getInventory().getHolder() != null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        ShopGuiWindow _window = plugin.getGuiHandler().getWindow(player);

        if (!getInventoryViewTitle(event).equals(_window.getTitle())) {
            return;
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            event.setCancelled(true);
            return;
        }
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }
        event.setCancelled(true);

        if (!(_window instanceof MyListSearchResultsWindow)) {
            return;
        }
        MyListSearchResultsWindow window = (MyListSearchResultsWindow) _window;

        // check if the clicked item is a shop icon
        String signLocation = clicked.getItemMeta().getPersistentDataContainer()
                .get(plugin.getSignLocationNameSpacedKey(), PersistentDataType.STRING);
        if (signLocation != null) {
            handleShopIconClick(signLocation, player);
            return;
        }

        // fall through to checking interface icons

        ShopGuiHandler gh = plugin.getGuiHandler();
        GuiIcon icon = itemToIcon.get(clicked);

        // settings button
        if (icon.equals(GuiIcon.HOME_SETTINGS)) {
            gh.setWindow(player, new PlayerSettingsWindow((((OfflinePlayer) player).getUniqueId())));
            return;
        }

        // list all shops button
        if (icon.equals(GuiIcon.HOME_LIST_ALL_SHOPS)) {
            gh.setWindow(player, new MyListSearchResultsWindow((((OfflinePlayer) player).getUniqueId())));
            return;
        }

        // filter and sort buttons
        Option opt = iconToOption.get(icon);

        switch (opt) {
            case GUI_FILTER_SHOP_STOCK:
                icon = stockFilterIconsList.next(icon);
                break;
            case GUI_FILTER_SHOP_TYPE:
                icon = typeFilterIconsList.next(icon);
                break;
            case GUI_SORT:
                icon = sortIconsList.next(icon);
            default:
                break;
        }
        gh.setIconForOption(player, opt, icon);
        window.initInvContents();
    }

    public void handleShopIconClick(String signLocation, Player player) {
        Location loc = UtilMethods.getLocation(signLocation);
        AbstractShop shop = plugin.getShopHandler().getShop(loc);

        if (shop == null) {
            return;
        }
        if (!Shop.getPlugin().usePerms() && player.isOp()) {
            shop.teleportPlayer(player);
            plugin.getGuiHandler().closeWindow(player);
            return;
        }

        if (!player.hasPermission("shop.operator") && !player.hasPermission("shop.gui.teleport")) {
            ShopMessage.sendMessage("interactionIssue", "regionRestriction", player, shop);
            return;
        }
        double cost = plugin.getTeleportCost();
        if (cost > 0) {
            PlayerInventory inv = player.getInventory();
            if (EconomyUtils.hasSufficientFunds(player, inv, cost)) {
                EconomyUtils.removeFunds(player, inv, cost);
            } else {
                ShopMessage.sendMessage("interactionIssue", "teleportInsufficientFunds", player, shop);
                plugin.getGuiHandler().closeWindow(player);
                return;
            }
        }
        if (plugin.getTeleportCooldown() > 0 && plugin.getShopListener().getTeleportCooldownRemaining(player) > 0) {
            ShopMessage.sendMessage("interactionIssue", "teleportInsufficientCooldown", player, shop);
            plugin.getGuiHandler().closeWindow(player);
            return;
        }
        if (main.ess != null) {
            // hook into essentials to teleport
            User u = new User(player, main.ess);
            Trade charge = new Trade(0, main.ess);
            TeleportCause cause = TeleportCause.PLUGIN;
            CompletableFuture<Boolean> future = getNewExceptionFuture(u.getSource(), "searchshops");
            future.thenAccept(success -> {
                if (success)
                    player.sendMessage(ChatColor.BLUE + "Teleporting...");

            });
            u.getAsyncTeleport().teleport(loc, charge, cause, future);

        } else {
            player.sendMessage(ChatColor.BLUE + "Teleporting...");
            shop.teleportPlayer(player);

        }
        plugin.getGuiHandler().closeWindow(player);
        return;
    }

    public CompletableFuture<Boolean> getNewExceptionFuture(final CommandSource sender, final String commandLabel) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.exceptionally(e -> {
            sender.getSender().sendMessage(ChatColor.RED + "Teleport failed.");
            return false;
        });
        return future;
    }

    public class LoopingList {

        private List<GuiIcon> icons;

        public LoopingList(GuiIcon[] toLoop) {
            icons = Arrays.asList(toLoop);
        }

        public GuiIcon next(GuiIcon prior) {
            int i = icons.indexOf(prior) + 1;
            if (i >= icons.size()) {
                i = 0;
            }
            return icons.get(i);
        }
    }
}
