package me.tWizT3d_dreaMr.ShopAddon;

import org.bukkit.entity.Player;

import com.snowgears.shop.Shop;
import com.snowgears.shop.util.PlayerSettings;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class NotificationCommand {
public static void Command(Player p, String args[]) {
	p.sendMessage(ChatColor.DARK_AQUA+"Your settings");
	p.sendMessage(ChatColor.DARK_GRAY+"--------------------------------");
	if(args.length!=0) {
		//Shop.getPlugin().getGuiHandler().toggleSettingsOption(player, option);

		if(args[0].equalsIgnoreCase("SaleOwnerNotification")) {
			PlayerSettings.Option option = PlayerSettings.Option.SALE_OWNER_NOTIFICATIONS;
			Shop.getPlugin().getGuiHandler().toggleSettingsOption(p, option);
		}
		if(args[0].equalsIgnoreCase("UserNotifcation")) {
			PlayerSettings.Option option = PlayerSettings.Option.SALE_USER_NOTIFICATIONS;
			Shop.getPlugin().getGuiHandler().toggleSettingsOption(p, option);
		}
		if(args[0].equalsIgnoreCase("StockNotification")) {
			PlayerSettings.Option option = PlayerSettings.Option.STOCK_NOTIFICATIONS;
			Shop.getPlugin().getGuiHandler().toggleSettingsOption(p, option);
		}
	}
	String SON="Owner Notifications";
    if (Shop.getPlugin().getGuiHandler().getSettingsOption(p, PlayerSettings.Option.SALE_OWNER_NOTIFICATIONS)) {
    	SON=ChatColor.GREEN+SON;
    }
    else{
    	SON=ChatColor.RED+SON;
    }
    p.spigot().sendMessage(MessageBuilder(SON,"SaleOwnerNotification"));
    
    String UN="User Notifications";
    if (Shop.getPlugin().getGuiHandler().getSettingsOption(p, PlayerSettings.Option.SALE_USER_NOTIFICATIONS)) {
    	UN=ChatColor.GREEN+UN;
    }
    else{
    	UN=ChatColor.RED+UN;
    }
    p.spigot().sendMessage(MessageBuilder(UN,"UserNotifcation"));

    String SN="Stock Notifications";
    if (Shop.getPlugin().getGuiHandler().getSettingsOption(p, PlayerSettings.Option.STOCK_NOTIFICATIONS)) {
    	SN=ChatColor.GREEN+SN;
    }
    else{
    	SN=ChatColor.RED+SN;
    }
    p.spigot().sendMessage(MessageBuilder(SN,"StockNotification"));

	p.sendMessage(ChatColor.DARK_GRAY+"--------------------------------");
}
public static TextComponent MessageBuilder(String Message,String command) {
	TextComponent message = new TextComponent(Message);
	message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/ShopNotifications "+command ) );
	message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new Text( "Change This setting!" ) ) );
	return message;
}
}
