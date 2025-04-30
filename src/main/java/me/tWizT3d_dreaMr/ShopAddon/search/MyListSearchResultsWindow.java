package me.tWizT3d_dreaMr.ShopAddon.search;

import java.util.UUID;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import com.snowgears.shop.Shop;
import com.snowgears.shop.handler.ShopGuiHandler;
import com.snowgears.shop.shop.AbstractShop;

public class MyListSearchResultsWindow extends MyListShopsWindow {

	public static enum ShopSearchType {
		PLAYER,
		ITEM,
		ITEM_NAME
	}

	protected Material itemSearched = Material.STONE;
	protected OfflinePlayer shopOwnerSearched;
	protected String displayNameSearched;
	protected ShopSearchType searchType = ShopSearchType.ITEM;

	public MyListSearchResultsWindow(UUID player, Material toSearch) {
		super(player);
		itemSearched = toSearch;
		searchType = ShopSearchType.ITEM;
		initInvContents();
	}

	public MyListSearchResultsWindow(UUID player, OfflinePlayer toSearch) {
		super(player);
		shopOwnerSearched = toSearch;
		searchType = ShopSearchType.PLAYER;
		// TODO: could consider using com.snowgears.shop.gui.ListPlayersWindow (for selection) and ListPlayerShopsWindow (for results)
		initInvContents();
	}

	public MyListSearchResultsWindow(UUID player, String toSearch) {
		super(player);
		displayNameSearched = toSearch;
		searchType = ShopSearchType.ITEM_NAME;
		initInvContents();
	}

	@Override
	protected List<Predicate<AbstractShop>> collectFilters() {
		List<Predicate<AbstractShop>> filters = super.collectFilters();

		switch (searchType) {
			case PLAYER:
				filters.add(shop -> shop.getOwnerUUID().equals(shopOwnerSearched.getUniqueId()));
				break;
			case ITEM_NAME:
				filters.add(new DisplayNameFilter(displayNameSearched));
				break;
			case ITEM:
			default:
				filters.add(shop -> (((shop.getItemStack() != null) &&
						(shop.getItemStack().getType().equals(itemSearched))) ||
						((shop.getSecondaryItemStack() != null) &&
								(shop.getSecondaryItemStack().getType().equals(itemSearched)))));
		}
		return filters;
	}

	@Override
	protected void sortShops() {
		super.sortShops();
		// TODO: add sorting by shop creation date?
	}

	@Override
	protected void makeMenuBarUpper() {
		super.makeMenuBarUpper();

		// ** CUSTOM CHANGES FOR OUR SERVER **

		// replace search icon with settings
        ItemStack settingsIcon = Shop.getPlugin().getGuiHandler().getIcon(ShopGuiHandler.GuiIcon.HOME_SETTINGS, null, null);
		page.setItem(8, settingsIcon);  
	}
}
