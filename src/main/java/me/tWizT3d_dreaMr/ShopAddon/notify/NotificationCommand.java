package me.tWizT3d_dreaMr.ShopAddon.notify;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import com.snowgears.shop.Shop;
import com.snowgears.shop.gui.PlayerSettingsWindow;
import com.snowgears.shop.handler.ShopGuiHandler.GuiIcon;
import com.snowgears.shop.util.PlayerSettings;
import com.snowgears.shop.util.PlayerSettings.Option;
import me.tWizT3d_dreaMr.ShopAddon.main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class NotificationCommand {

	/**
	 * use .name() for the command, .toColoredString() for the print value
	 */
	public enum NotificationSubcommand {
		SaleOwnerNotification(
				"Owner Notifications",
				Option.NOTIFICATION_SALE_OWNER,
				GuiIcon.SETTINGS_NOTIFY_OWNER_OFF), // TODO: fix - better to get these from a default PlayerSettings obj
		UserNotifcation(
				"User Notifications",
				Option.NOTIFICATION_SALE_USER,
				GuiIcon.SETTINGS_NOTIFY_USER_OFF),
		StockNotification(
				"Stock Notifications",
				Option.NOTIFICATION_STOCK,
				GuiIcon.SETTINGS_NOTIFY_STOCK_OFF);

		private final String label;
		private final Option option;
		public final GuiIcon defaultIcon;

		NotificationSubcommand(final String label, final Option option, final GuiIcon defaultIcon) {
			this.label = label;
			this.option = option;
			this.defaultIcon = defaultIcon;
		}

		@Override
		public String toString() {
			return label;
		}

		public void toggleOption(Player p) {
			Shop.getPlugin().getGuiHandler().toggleNotificationSetting(p, this.option);
		}
	}

	public static void Command(Player p, String args[]) {
		try {
			NotificationSubcommand.valueOf(args[0]).toggleOption(p);
			sendSettingsMenusInChat(p);
			return;
		} catch (IllegalArgumentException e) {
			// invalid subcommand
			p.sendMessage(ChatColor.RED + "Invalid subcommand for /ShopNotifications");
			return;
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			// no subcommand, args.length == 0, continue to interface
		}
		if (main.plugin.getConfig().getBoolean("Gui.Settings")){
			// Use native GUI window for player settings
			Shop.getPlugin().getGuiHandler().setWindow(p, new PlayerSettingsWindow((((OfflinePlayer)p).getUniqueId())));
			return;
		} else{
			// Use text interface for player settings
			sendSettingsMenusInChat(p);
		}
	}

	public static TextComponent MessageBuilder(String Message, String command) {
		TextComponent message = new TextComponent(Message);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shopnotifications " + command));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("[Click to change]")));
		return message;
	}

	public static void sendSettingsMenusInChat(Player p){
		PlayerSettings settings = PlayerSettings.loadFromFile(p);
		if (settings == null) {
			// error creating or retrieving settings file, fail silently
			return;
		}
		p.sendMessage(ChatColor.DARK_AQUA + "Your settings");
		p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");
		for (NotificationSubcommand c : NotificationSubcommand.values()){
			String toPrint = colorFromSetting(settings, c);
			String command = c.name();
			p.spigot().sendMessage(MessageBuilder(toPrint,command));
		}

		p.sendMessage(ChatColor.DARK_GRAY + "--------------------------------");

	}

	private static String colorFromSetting(PlayerSettings s, NotificationSubcommand c){
		return "" + (s.getGuiIcon(c.option).equals(c.defaultIcon) ? ChatColor.RED : ChatColor.GREEN) + c.toString();
	}
}
