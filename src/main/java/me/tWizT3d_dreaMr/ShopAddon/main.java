package me.tWizT3d_dreaMr.ShopAddon;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.snowgears.shop.Shop;
import me.tWizT3d_dreaMr.ShopAddon.Gui.Guis;
import me.tWizT3d_dreaMr.ShopAddon.Listeners.CheckOn;
import me.tWizT3d_dreaMr.ShopAddon.Listeners.guiListener;
import me.tWizT3d_dreaMr.ShopAddon.limits.CreationCheck;
import me.tWizT3d_dreaMr.ShopAddon.notify.NotificationCommand;
import me.tWizT3d_dreaMr.ShopAddon.notify.NotificationCommand.NotificationSubcommand;
import me.tWizT3d_dreaMr.ShopAddon.offlinesales.OfflineSalesCommand;
import me.tWizT3d_dreaMr.ShopAddon.offlinesales.OfflineSalesListener;
import me.tWizT3d_dreaMr.ShopAddon.search.CommandSearch;
import me.tWizT3d_dreaMr.ShopAddon.search.MyShopGUIListener;
import me.tWizT3d_dreaMr.ShopAddon.util.Format;
import net.ess3.api.IEssentials;

public class main extends JavaPlugin {
	private static CreationCheck creationCheck;
	private YamlConfiguration con;
	private static FileConfiguration config;
	public static JavaPlugin plugin;
	public static Material Confirm;
	public static Material TransAmount;
	public static Material Default;
	public static Material NotStock;
	public static Material NotMoney;
	public static Material TransOut;
	public static Shop shop;
	public static IEssentials ess;

	@Override
	public void onEnable() {
		plugin = this;

		Plugin essPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
		if (essPlugin == null || !(essPlugin instanceof IEssentials)) {
			plugin.getLogger().warning("Essentials not present. Disabling Essentials support.");
			ess = null;
		} else {
			ess = (IEssentials) essPlugin;
		}

		shop = Shop.getPlugin();

		File bwconfigFile = new File(getDataFolder(), "itemlist.yml");
		if (!bwconfigFile.exists()) {
			bwconfigFile.getParentFile().mkdirs();
			InputStreamReader f = new InputStreamReader(getResource("itemlist.yml"));
			con = YamlConfiguration.loadConfiguration(f);
			try {
				con.save(bwconfigFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			con = YamlConfiguration.loadConfiguration(bwconfigFile);
		boolean isWhitelist = false;

		config = getConfig();
		Bukkit.getPluginManager().registerEvents(new CheckOn(), this);

		if (config.getBoolean("Gui.Active") && areAble()) {

			Guis.type = shop.getCurrencyType();
			Bukkit.getPluginManager().registerEvents(new guiListener(), this);
		}

		creationCheck = new CreationCheck(con, isWhitelist);

		Bukkit.getPluginManager().registerEvents(new MyShopGUIListener(shop), this);

		// Register offline sales listener if enabled
		if (config.getBoolean("OfflineSales.Enabled", true)) {
			Bukkit.getPluginManager().registerEvents(new OfflineSalesListener(), this);
		}

	}

	@Override
	public void onDisable() {
		saveConfig();
	}

	public static Material matchMaterial(String s) {
		return matchMaterial(s, Material.AIR);
	}

	public static Material matchMaterial(String s, Material _default) {
		Material m = null;
		NamespacedKey k;
		try {
			k = NamespacedKey.minecraft(s.trim().toLowerCase());
			m = Registry.MATERIAL.get(k);

		} catch (Exception e) {
			main.plugin.getLogger().warning("Invalid material: " + s);
			PrintWriter w = new PrintWriter(new StringWriter());
			e.printStackTrace(w);
			main.plugin.getLogger().warning(e.toString() + "\n" + w.toString());
			return _default;
		}
		if (m == null) {
			return _default;
		}
		return m;
	}

	private boolean areAble() {
		Confirm = matchMaterial(getConfig().getString("Gui.Material.Confirm"), Material.DIRT);
		TransAmount = matchMaterial(getConfig().getString("Gui.Material.TransAmount"), Material.DIRT);
		Default = matchMaterial(getConfig().getString("Gui.Material.Default"), Material.DIRT);
		NotStock = matchMaterial(getConfig().getString("Gui.Material.NotStock"), Material.DIRT);
		NotMoney = matchMaterial(getConfig().getString("Gui.Material.NotMoney"), Material.DIRT);
		TransOut = matchMaterial(getConfig().getString("Gui.Material.TransOut"), Material.DIRT);

		if (Confirm == null || TransAmount == null || Default == null || NotStock == null || NotMoney == null
				|| TransOut == null) {
			Bukkit.getLogger().log(Level.SEVERE, "One of gui items are null, disabling");
			return false;
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command obj, String alias, String[] args) {
		String cmd = obj.getName().toLowerCase();
		if (cmd.equals("searchshops")) {
			ArrayList<String> ret = new ArrayList<String>();
			if (args.length != 1) { // args is always at least [""]
				return null;
			}
			switch (args[0].toLowerCase()) {
				case "player":
					return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
				case "loredName":
					ret.add("<Lored Name>");
					return ret;
				default:
					ret.add(" player"); //leading spaces so they show up first, does not affect execution
					ret.add(" loredName");
					for (Material m : Material.values()) {
						ret.add(m.name());
					}
					return ret.stream().filter(s -> s.toLowerCase().contains(args[0].toLowerCase())).toList();
			}
		} else if (cmd.equals("shopnotifications")) {
			ArrayList<String> ret = new ArrayList<String>();
			ret.addAll(Arrays.stream(NotificationSubcommand.values()).map(s -> s.name()).toList());
			return ret;
		}
		return null;
	}

	public static Shop getShop() {
		return shop;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("searchshops")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Format.format(config.getString("Command.OnlyPlayers")));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(Format.format(config.getString("Command.IncorrectUsage")));
				return true;
			}
			if (!sender.hasPermission("shopaddon.canSearch")) {
				sender.sendMessage(Format.format(config.getString("Command.NoPerms")));
				return true;
			}
			CommandSearch.Search((Player) sender, args);
			return true;
		}

		if (command.getName().equalsIgnoreCase("ShopNotifications")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage("You cant change your notification settings");
				return true;
			}
			NotificationCommand.Command((Player) sender, args);

			return true;
		}

		if (command.getName().equalsIgnoreCase("offlinesales")) {
			return new OfflineSalesCommand().onCommand(sender, command, label, args);
		}

		return false;
	}

	public static CreationCheck getCreationCheck() {
		return creationCheck;
	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			Integer i = Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static FileConfiguration getCon() {
		return config;
	}
}
