package me.tWizT3d_dreaMr.ShopAddon;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.snowgears.shop.AbstractShop;
import com.snowgears.shop.event.PlayerInitializeShopEvent;


public class CheckOn implements Listener {
	private static final Pattern pattern = Pattern.compile("(?<!\\\\)(&#[a-fA-F0-9]{6})");

	 @EventHandler
	    public void onShopCreate(PlayerInitializeShopEvent  event) {
	    	CreationCheck ItemList= main.getCreationCheck();
	    	AbstractShop shop=event.getShop();
	    	String type=ItemList.testfor(event.getPlayer().getInventory().getItemInMainHand(), shop.getPrice(), shop.getAmount());
	    	if(!type.equals("none")) {
	    		event.setCancelled(true);
	    		shop.delete();
	    		Player player=event.getPlayer();
	    		String message=format(ChatColor.translateAlternateColorCodes('&', main.getCon().getString("Interaction."+type)));
	    		
	    		player.sendMessage(message);
	    	}
	 }

    public static String format(String message) {
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start()+1, matcher.end());
	            
	        message = message.replace("&"+color, "" + ChatColor.of(color));
	        matcher = pattern.matcher(message);
      
        }
        return message;
    }

}
