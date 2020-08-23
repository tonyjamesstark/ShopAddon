package me.tWizT3d_dreaMr.ShopAddon;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;



public class main extends JavaPlugin {
	private static CreationCheck creationCheck;
	private YamlConfiguration con;
	private static FileConfiguration config;
	public static JavaPlugin plugin;
public void onEnable()  {
	plugin=this;
    File bwconfigFile = new File(getDataFolder(), "itemlist.yml");
    if (!bwconfigFile.exists()) {
    	bwconfigFile.getParentFile().mkdirs();
    	con=YamlConfiguration.loadConfiguration(bwconfigFile);
    	con.set("itemListing.exampleitem.ListType", "price");
    	con.set("itemListing.exampleitem.material", "chest");
    	con.set("itemListing.exampleitem.lore-contains", "Example lore");
    	con.set("itemListing.exampleitem.name-contains", "Example name");
    	con.set("itemListing.exampleitem.pricemin", "1 64");
    	con.set("itemListing.exampleitem.pricemax", "2 64");
    	con.set("itemListing.exampleitem.ShopType", "all");
    	try {
			con.save(bwconfigFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } else con=YamlConfiguration.loadConfiguration(bwconfigFile);
    boolean wh=false;
    if(getConfig().get("WhitelistItems")==null) {
    	getConfig().addDefault("WhitelistItems",false);
    	getConfig().addDefault("Interaction.pricemin", "&#952f39Your item is too cheap");
    	getConfig().addDefault("Interaction.pricemax", "&#952f39Your item is too expensive");
    	getConfig().addDefault("Interaction.blacklist", "&#952f39That item can not be sold");
    	getConfig().addDefault("Interaction.whitelist", "&#952f39That item can not be sold");
    	getConfig().addDefault("Logging.Enable", "false");
    	getConfig().addDefault("Logging.Format", "%NUM%: &cDate %DATE%;&dUUID %UUID%;"
    			+ "&cName %NAME% Material %MATERIAL% Price %PRICE%;&dItemName %INAME% ItemLore %ILORE%;"
    			+ "&cSign Location %SIGNX%, %SIGNY%, %SIGNZ%, %SIGNWORLD%");
    	getConfig().addDefault("Logging.PageSize", 5);
        getConfig().addDefault("Logging.SQL.host", "localhost");
        getConfig().addDefault("Logging.SQL.port", 3306);
        getConfig().addDefault("Logging.SQL.database", "database");
        getConfig().addDefault("Logging.SQL.username", "username");
        getConfig().addDefault("Logging.SQL.password", "password");
        getConfig().addDefault("Command.OnlyPlayers", "&cOnly players can use this command!");
        getConfig().addDefault("Command.IncorrectUsage", "&cIncorrect usage!");
        getConfig().addDefault("Command.NotANumber", "&cThat isn't a valid number!");
        getConfig().addDefault("Command.LoggingOn", "&#11fb76Check logging on!");
        getConfig().addDefault("Command.LoggingOff", "&#de723fCheck logging off!");
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    }
    
	config=getConfig();
	Bukkit.getPluginManager().registerEvents(new CheckOn(),this);
	
	if(getConfig().getBoolean("Logging.Enable")){	
		ConfigurationSection s=getConfig().getConfigurationSection("Logging.SQL");
		Bukkit.getPluginManager().registerEvents(new ShopLogging(s.getString("host"),s.getInt("port"),s.getString("database"),s.getString("username"),s.getString("password")),this);
	}
    if(getConfig().getString("WhitelistItems").equalsIgnoreCase("true"))
    	wh=true;
    
    creationCheck=new CreationCheck(con,wh);        

}

public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

	  if (command.getName().equalsIgnoreCase("SA"))
	  { 	if(!(sender instanceof Player)) {
		  sender.sendMessage(Format.format( config.getString("Command.OnlyPlayers")));
		  return true;
	  }
	  if(args.length==1) {
		  if(args[0].equalsIgnoreCase("check")) {
			ShopLogging.add((Player)sender);  
		  }
	  }else if(args.length==2) {
		  if(args[0].equalsIgnoreCase("check")) {
				if(isNumeric(args[1])&&(Integer.parseInt(args[1])-1)>=0) {
					ShopLogging.sendPage(Integer.parseInt(args[1])-1, (Player)sender );
				} else sender.sendMessage(Format.format( config.getString("Command.NotANumber")));
		  }
	  } else {
			  sender.sendMessage(Format.format( config.getString("Command.IncorectUsage")));
		  }
		  return true;
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
