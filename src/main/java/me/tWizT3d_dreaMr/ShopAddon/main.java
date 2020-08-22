package me.tWizT3d_dreaMr.ShopAddon;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin {
	private static CreationCheck creationCheck;
	private YamlConfiguration con;
	private static FileConfiguration config;
public void onEnable()  {
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
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    }
	config=getConfig();
	Bukkit.getPluginManager().registerEvents(new CheckOn(),this);
    if(getConfig().getString("WhitelistItems").equalsIgnoreCase("true"))
    	wh=true;

    creationCheck=new CreationCheck(con,wh);        

}
public static CreationCheck getCreationCheck() {
	return creationCheck;
}
public static FileConfiguration getCon() {
	return config;
}
}
