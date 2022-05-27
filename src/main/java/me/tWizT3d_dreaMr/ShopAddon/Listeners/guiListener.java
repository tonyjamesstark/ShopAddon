package me.tWizT3d_dreaMr.ShopAddon.Listeners;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.snowgears.shop.util.ShopAction;
import com.snowgears.shop.util.ShopClickType;

import me.tWizT3d_dreaMr.ShopAddon.Gui.Gui;
import me.tWizT3d_dreaMr.ShopAddon.Gui.Guis;

import com.snowgears.shop.Shop;
import com.snowgears.shop.shop.AbstractShop;

public class guiListener implements Listener {
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void signOpenGui(PlayerInteractEvent e) {
    	if(!e.getHand().equals(EquipmentSlot.HAND))
    		return;
    	Player p=e.getPlayer();
    	ShopClickType click;
        if (e.getClickedBlock().getBlockData() instanceof WallSign) {
	            AbstractShop shop = Shop.getPlugin().getShopHandler().getShop(e.getClickedBlock().getLocation());
	        if (shop == null || !shop.isInitialized())
	            return;
	
		    if(p.isSneaking()) 
		        if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
		        	click= ShopClickType.SHIFT_RIGHT_CLICK_SIGN;
		        else
		        	click= ShopClickType.SHIFT_LEFT_CLICK_SIGN;
		    else
		        if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
		        	click= ShopClickType.RIGHT_CLICK_SIGN;
		        else
		            click= ShopClickType.LEFT_CLICK_SIGN;
	    	 ShopAction action = Shop.getPlugin().getShopAction(click);
	    	 if(action == null||!(action == ShopAction.TRANSACT||action == ShopAction.TRANSACT_FULLSTACK))
	    		 return;
	    	 e.setCancelled(true);
	    	 Guis.makeGui(p, shop);
        }
    }
    
    @EventHandler (ignoreCancelled = true)
    public void InventoryClick(InventoryClickEvent e) {
    	Gui gui= Guis.getGui(e.getView());
    	if(gui == null)
    		return;
    		
    	int i=e.getRawSlot();
    	if(e.getAction()== InventoryAction.MOVE_TO_OTHER_INVENTORY)
	    	if(i==26||i==0||i==18||i==9||i==0||i==17||i==8) {
		    	AbstractShop shop=gui.getShop();
		    	PlayerInteractEvent event= new PlayerInteractEvent((Player)e.getWhoClicked(), Action.LEFT_CLICK_BLOCK,
		    			e.getWhoClicked().getInventory().getItemInMainHand(), shop.getSignLocation().getBlock(),
		    			BlockFace.EAST, EquipmentSlot.HAND);
		    	event.setCancelled(true);
		    	Shop.getPlugin().getTransactionHelper().executeTransactionFromEvent(event, shop, true);
		    	gui.update();
	    	}
    	if(e.getAction()== InventoryAction.PICKUP_ALL)
	    	if(i==26||i==0||i==18||i==9||i==0||i==17||i==8) {
		    	AbstractShop shop=gui.getShop();
		    	PlayerInteractEvent event= new PlayerInteractEvent((Player)e.getWhoClicked(), Action.LEFT_CLICK_BLOCK,
		    			e.getWhoClicked().getInventory().getItemInMainHand(), shop.getSignLocation().getBlock(),
		    			BlockFace.EAST, EquipmentSlot.HAND);
		    	event.setCancelled(true);
		    	Shop.getPlugin().getTransactionHelper().executeTransactionFromEvent(event, shop, false);
		    	gui.update();
	    	}
    	e.setCancelled(true);
    }    @EventHandler (ignoreCancelled = true)
    public void inventoryClose(InventoryCloseEvent e) {
    	Gui gui= Guis.getGui(e.getView());
    	if(gui == null)
    		return;
    	Guis.guilist.remove(gui);
    	
    }
}
