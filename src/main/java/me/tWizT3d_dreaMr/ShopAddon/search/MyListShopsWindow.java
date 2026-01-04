package me.tWizT3d_dreaMr.ShopAddon.search;

import com.snowgears.shop.Shop;
import com.snowgears.shop.gui.ShopGuiWindow;
import com.snowgears.shop.handler.ShopGuiHandler;
import com.snowgears.shop.shop.AbstractShop;
import com.snowgears.shop.shop.ShopType;
// import com.snowgears.shop.util.*; // changed to specific imports
import com.snowgears.shop.util.ComparatorShopItemNameHigh;
import com.snowgears.shop.util.ComparatorShopItemNameLow;
import com.snowgears.shop.util.ComparatorShopPriceHigh;
import com.snowgears.shop.util.ComparatorShopPriceLow;
import com.snowgears.shop.util.PlayerSettings;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

// This class is a copy-paste of the ListShopsWindow in com.snowgears.shop.gui
// with a changes/refactors as noted for easier subclassing

public class MyListShopsWindow extends ShopGuiWindow {

    public static final int ITEMS_IN_FULL_PAGE =
            36; // 36 items is a full page in the inventory (moved to constant)
    public static final int SLOT_NEXT_PAGE = 53; // added name to constant

    protected ArrayList<AbstractShop> shops; // changed from private, changed 'allShops' to 'shops'

    public MyListShopsWindow(UUID player) {
        super(player);
        this.title = Shop.getPlugin().getGuiHandler().getTitle(ShopGuiHandler.GuiTitle.LIST_SHOPS);
        this.page = Bukkit.createInventory(null, INV_SIZE, this.title);
        // initInvContents();  // TODO: fix - need to call manually later
    }

    @Override
    protected void initInvContents() {
        super.initInvContents();
        this.clearInvBody();

        makeMenuBarUpper();
        makeMenuBarLower();

        shops = new ArrayList<>(Shop.getPlugin().getShopHandler().getAllShops());

        this.applyFilters();

        this.sortShops();

        this.addShopsToInv();
    }

    @Override
    protected void makeMenuBarUpper() {
        super.makeMenuBarUpper();

        // upper menu bar is first row (slots 1-9)

        // init the menu bar with the saved sort settings
        ShopGuiHandler.GuiIcon guiIcon =
                Shop.getPlugin()
                        .getGuiHandler()
                        .getIconFromOption(player, PlayerSettings.Option.GUI_SORT);
        ItemStack sortIcon = Shop.getPlugin().getGuiHandler().getIcon(guiIcon, player, null);
        page.setItem(3, sortIcon);

        // filter shop type - all, sell, buy, barter, gamble
        guiIcon =
                Shop.getPlugin()
                        .getGuiHandler()
                        .getIconFromOption(player, PlayerSettings.Option.GUI_FILTER_SHOP_TYPE);
        ItemStack filterTypeIcon = Shop.getPlugin().getGuiHandler().getIcon(guiIcon, player, null);
        page.setItem(5, filterTypeIcon);

        // filter stock - in stock, out of stock, all
        guiIcon =
                Shop.getPlugin()
                        .getGuiHandler()
                        .getIconFromOption(player, PlayerSettings.Option.GUI_FILTER_SHOP_STOCK);
        ItemStack filterStockIcon = Shop.getPlugin().getGuiHandler().getIcon(guiIcon, player, null);
        page.setItem(6, filterStockIcon);

        // list all shops
        // guiIcon =
        //
        // Shop.getPlugin().getGuiHandler().getIcon(ShopGuiHandler.GuiIcon.HOME_LIST_ALL_SHOPS,
        // null, null);
        ItemStack listAllIcon =
                Shop.getPlugin()
                        .getGuiHandler()
                        .getIcon(ShopGuiHandler.GuiIcon.HOME_LIST_ALL_SHOPS, null, null);
        page.setItem(1, listAllIcon);

        // search icon
        if (Shop.getPlugin().allowCreativeSelection()) {
            ItemStack searchIcon =
                    Shop.getPlugin()
                            .getGuiHandler()
                            .getIcon(ShopGuiHandler.GuiIcon.HOME_SEARCH, null, null);
            page.setItem(8, searchIcon);
        }
    }

    @Override
    protected void makeMenuBarLower() {
        super.makeMenuBarLower();
        // lower menu bar is last row (slots 45-53)
    }

    // added refactored methods below

    protected static ArrayList<AbstractShop> applyFilters(
            ArrayList<AbstractShop> toFilter, List<Predicate<AbstractShop>> filters) {
        return new ArrayList<>(
                toFilter.stream()
                        .filter(filters.stream().reduce(Predicate::and).orElse(x -> true))
                        .toList());
    }

    private void applyFilters() {
        this.shops = applyFilters(shops, this.collectFilters());
    }

    // private void applyFilters(List<Predicate<AbstractShop>> filters){
    //     this.shops = applyFilters(shops, filters);
    // }

    protected void sortShops() {
        // override in subclass to add sorting methods

        // now do sorting
        ShopGuiHandler.GuiIcon guiSortIcon =
                Shop.getPlugin()
                        .getGuiHandler()
                        .getIconFromOption(player, PlayerSettings.Option.GUI_SORT);

        Comparator<AbstractShop> sorter = new ComparatorShopItemNameLow(); // refactored sorting

        switch (guiSortIcon) {
            case MENUBAR_SORT_NAME_HIGH:
                sorter = new ComparatorShopItemNameHigh();
                break;
            case MENUBAR_SORT_PRICE_LOW:
                sorter = new ComparatorShopPriceLow();
                break;
            case MENUBAR_SORT_PRICE_HIGH:
                sorter = new ComparatorShopPriceHigh();
                break;
            default:
                sorter = new ComparatorShopItemNameLow();
                break;
        }
        this.shops.sort(sorter);
    }

    protected List<Predicate<AbstractShop>> collectFilters() {
        // override in subclass to add filters

        // first do shop type filtering
        ShopGuiHandler.GuiIcon guiFilterTypeIcon =
                Shop.getPlugin()
                        .getGuiHandler()
                        .getIconFromOption(player, PlayerSettings.Option.GUI_FILTER_SHOP_TYPE);
        // List<AbstractShop> filteredShops = new ArrayList<>();

        ArrayList<Predicate<AbstractShop>> filters =
                new ArrayList<Predicate<AbstractShop>>(); // refactored filtering

        switch (guiFilterTypeIcon) {
                // case MENUBAR_FILTER_TYPE_ALL: // no filter for default option
                //     filteredShops = allShops;
                //     break;
            case MENUBAR_FILTER_TYPE_SELL:
                filters.add(
                        shop ->
                                (shop.getType() == ShopType.SELL
                                        || shop.getType() == ShopType.COMBO));
                break;
            case MENUBAR_FILTER_TYPE_BUY:
                filters.add(
                        shop ->
                                (shop.getType() == ShopType.BUY
                                        || shop.getType() == ShopType.COMBO));
                break;
            case MENUBAR_FILTER_TYPE_BARTER:
                filters.add(shop -> shop.getType() == ShopType.BARTER);
                break;
            case MENUBAR_FILTER_TYPE_GAMBLE:
                filters.add(shop -> shop.getType() == ShopType.GAMBLE);
                break;
            default:
                break;
        }

        // first do shop type filtering
        ShopGuiHandler.GuiIcon guiFilterStockIcon =
                Shop.getPlugin()
                        .getGuiHandler()
                        .getIconFromOption(player, PlayerSettings.Option.GUI_FILTER_SHOP_STOCK);
        switch (guiFilterStockIcon) {
            case MENUBAR_FILTER_STOCK_IN:
                filters.add(shop -> (shop.getStock() > 0));
                break;
            case MENUBAR_FILTER_STOCK_OUT:
                filters.add(shop -> (shop.getStock() <= 0));
                break;
            default:
                break;
        }
        return filters;
    }

    protected void addShopsToInv() {
        int startIndex = pageIndex * ITEMS_IN_FULL_PAGE;
        ItemStack icon;
        boolean added = true;

        for (int i = startIndex; i < shops.size(); i++) {
            AbstractShop shop = shops.get(i);
            icon =
                    Shop.getPlugin()
                            .getGuiHandler()
                            .getIcon(ShopGuiHandler.GuiIcon.LIST_SHOP, null, shop);

            if (!this.addIcon(icon)) {
                added = false;
                break;
            }
        }

        if (added) {
            page.setItem(SLOT_NEXT_PAGE, null);
        } else {
            page.setItem(SLOT_NEXT_PAGE, this.getNextPageIcon());
        }
    }
}
