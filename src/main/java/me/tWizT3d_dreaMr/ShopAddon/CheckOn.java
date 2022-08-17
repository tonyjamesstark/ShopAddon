package me.tWizT3d_dreaMr.ShopAddon;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.snowgears.shop.event.PlayerInitializeShopEvent;
import com.snowgears.shop.shop.AbstractShop;


public class CheckOn implements Listener {

	 @EventHandler
	    public void onShopCreate(PlayerInitializeShopEvent  event) {
			Logger log=Bukkit.getLogger();
		 	log.log(Level.INFO,"Shop create 20");
	    	CreationCheck ItemList= main.getCreationCheck();
	    	AbstractShop shop=event.getShop();
	    	String type=ItemList.testfor(event.getPlayer().getInventory().getItemInMainHand(), shop.getPrice(), shop.getAmount(),shop.getType());
	    	log.log(Level.INFO,"Shop create 25 type= "+type);
	    	if(!type.equals("none")) {
	    		event.setCancelled(true);
	    		shop.delete();
	    		Player player=event.getPlayer();
	    		String message=Format.format(main.getCon().getString("Interaction."+type));
	    		
	    		player.sendMessage(message);
	    	}
	 }

   

}
