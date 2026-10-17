package me.tWizT3d_dreaMr.ShopAddon.Listeners;

import com.snowgears.shop.event.PlayerInitializeShopEvent;
import com.snowgears.shop.shop.AbstractShop;
import com.snowgears.shop.shop.ComboShop;
import com.snowgears.shop.shop.ShopType;
import me.tWizT3d_dreaMr.ShopAddon.limits.CreationCheck;
import me.tWizT3d_dreaMr.ShopAddon.limits.MatchType;
import me.tWizT3d_dreaMr.ShopAddon.main;
import me.tWizT3d_dreaMr.ShopAddon.util.Filter;
import me.tWizT3d_dreaMr.ShopAddon.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CheckOn implements Listener {

    @EventHandler
    public void onShopCreate(PlayerInitializeShopEvent event) {
        AbstractShop shop = event.getShop();
        Player player = event.getPlayer();

        MatchType type = check(shop, player.getInventory().getItemInMainHand());
        if (type != null) {
            event.setCancelled(true);
            shop.delete();
            sendRejection(player, type);
            return;
        }

        // Shop sets the item after this event, and it need not be the one in hand: chat creation
        // finishes after the price prompts, and creative selection uses the cursor. Runs after
        // the sign refresh Shop schedules 2 ticks out, so the deleted text stays.
        Bukkit.getScheduler()
                .runTaskLater(
                        main.plugin,
                        () -> {
                            if (main.getShop().getShopHandler().getShop(shop.getSignLocation())
                                    != shop) return;
                            ItemStack item = shop.getItemStack();
                            if (item == null) return;
                            MatchType stored = check(shop, item);
                            if (stored == null) return;
                            shop.delete();
                            if (player.isOnline()) sendRejection(player, stored);
                        },
                        3);
    }

    private static MatchType check(AbstractShop shop, ItemStack item) {
        CreationCheck itemList = main.getCreationCheck();
        if (shop.getType() == ShopType.COMBO) {
            ComboShop cs = (ComboShop) shop;
            MatchType type = itemList.test(item, cs.getPrice(), shop.getAmount(), ShopType.BUY);
            if (type == null)
                type = itemList.test(item, cs.getPriceSell(), shop.getAmount(), ShopType.SELL);
            return rejecting(type);
        }
        if (shop.getType() == ShopType.BUY || shop.getType() == ShopType.SELL)
            return rejecting(
                    itemList.test(item, shop.getPrice(), shop.getAmount(), shop.getType()));
        return null;
    }

    private static MatchType rejecting(MatchType type) {
        return type == null || type.getType().equalsIgnoreCase("WhiteList") ? null : type;
    }

    private static void sendRejection(Player player, MatchType type) {
        if (type.getType().equalsIgnoreCase("BlackList"))
            player.sendMessage(Format.format(main.getCon().getString("Interaction.blacklist")));
        String message =
                Format.format(
                        main.getCon().getString("Interaction." + type.getType().toLowerCase()));
        Filter f = type.getFilter();
        if (type.getType().equalsIgnoreCase("pricemin")) {
            message = message.replace("%amount%", f.getFriendlyMinAmount());
            message = message.replace("%price%", f.getFriendlyMinPrice());
        }
        if (type.getType().equalsIgnoreCase("pricemax")) {
            message = message.replace("%amount%", f.getFriendlyMaxAmount());
            message = message.replace("%price%", f.getFriendlyMaxPrice());
        }
        message = message.replace("%title%", f.Title());
        player.sendMessage(message);
    }
}
