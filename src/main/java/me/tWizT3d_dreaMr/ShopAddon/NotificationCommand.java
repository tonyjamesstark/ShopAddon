package me.tWizT3d_dreaMr.ShopAddon;

import org.bukkit.entity.Player;

import com.snowgears.shop.Shop;
import com.snowgears.shop.handler.ShopGuiHandler.GuiIcon;
import com.snowgears.shop.util.PlayerSettings;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class NotificationCommand {
public static void Command(Player p, String args[]) {
	PlayerSettings settings = PlayerSettings.loadFromFile(p);
	if(settings==null) return;
	p.sendMessage(ChatColor.DARK_AQUA+"Your settings");
	p.sendMessage(ChatColor.DARK_GRAY+"--------------------------------");
	if(args.length!=0) {
		//Shop.getPlugin().getGuiHandler().toggleSettingsOption(player, option);

		if(args[0].equalsIgnoreCase("SaleOwnerNotification")) {
			PlayerSettings.Option option = PlayerSettings.Option.NOTIFICATION_SALE_OWNER;
			Shop.getPlugin().getGuiHandler().toggleNotificationSetting(p, option);
		}
		if(args[0].equalsIgnoreCase("UserNotifcation")) {
			PlayerSettings.Option option = PlayerSettings.Option.NOTIFICATION_SALE_USER;
			Shop.getPlugin().getGuiHandler().toggleNotificationSetting(p, option);//.toggleSettingsOption(p, option);
		}
		if(args[0].equalsIgnoreCase("StockNotification")) {
			PlayerSettings.Option option = PlayerSettings.Option.NOTIFICATION_STOCK;
			Shop.getPlugin().getGuiHandler().toggleNotificationSetting(p, option);
		}
	}
	GuiIcon guiIcon= settings.getGuiIcon(null);
	String SON="Owner Notifications";
	SON= guiIcon.equals(GuiIcon.SETTINGS_NOTIFY_OWNER_OFF)? ChatColor.RED+SON : ChatColor.GREEN+SON;
	
    p.spigot().sendMessage(MessageBuilder(SON,"SaleOwnerNotification"));
    
    String UN="User Notifications";

    UN= guiIcon.equals(GuiIcon.SETTINGS_NOTIFY_OWNER_OFF)? ChatColor.RED+UN : ChatColor.GREEN+UN;
    p.spigot().sendMessage(MessageBuilder(UN,"UserNotifcation"));

    String SN="Stock Notifications";

    SN= guiIcon.equals(GuiIcon.SETTINGS_NOTIFY_OWNER_OFF)? ChatColor.RED+SN : ChatColor.GREEN+SN;
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
