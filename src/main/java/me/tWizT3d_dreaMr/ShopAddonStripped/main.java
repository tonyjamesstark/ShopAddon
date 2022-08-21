package me.tWizT3d_dreaMr.ShopAddonStripped;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


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
public void onEnable()  {
	plugin=this;
	
    File bwconfigFile = new File("plugins/ShopAddon/itemlist.yml");
    if (!bwconfigFile.exists()) {
    	System.out.println("ShopAddon filters don't exist. Disabling.");
    	Bukkit.getPluginManager().disablePlugin(plugin);
    	return;
    } else con=YamlConfiguration.loadConfiguration(bwconfigFile);

    
    creationCheck=new CreationCheck(con);     

}
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	
	  if(command.getName().equalsIgnoreCase("filterall")) {

		  if((sender instanceof Player)) {
			  sender.sendMessage("You cant");
			  return true;
		  }
		  sender.sendMessage("Running");
		  Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			    @Override
			    public void run() {
			        File fileDirectory = new File( "plugins/Shop/Data");
			        if (!fileDirectory.exists())
			            return;

			        ConfigurationSection ShopConfig;
			        YamlConfiguration ShopConfig2;
			        for (File file : fileDirectory.listFiles()) {
			            if (file.isFile()) {
			                if (file.getName().endsWith(".yml")){
			                	String uuid=file.getName().replace(".yml","");
			 			        System.out.println("File "+uuid+ ".yml");
			                	ShopConfig2=YamlConfiguration.loadConfiguration(file);
			                	ShopConfig= ShopConfig2.getConfigurationSection("shops."+uuid);
			                	if(ShopConfig== null) continue;
			                	for(String s: ShopConfig.getKeys(false)) {
			                		String st=ShopConfig.getString(s+".type").toUpperCase();
			                		Double price=ShopConfig.getDouble(s+".price");
			                		int amount=ShopConfig.getInt(s+".amount");
			                		ItemStack is=ShopConfig.getItemStack(s+".item");
			                		MatchType type=main.getCreationCheck().test(is, price, amount, st);
			            	    	if(!(type==null||type.getType().equalsIgnoreCase("WhiteList"))) {
			            	    		System.out.println("Found ");
				                		System.out.println(s +" deleted");
			            	    		ShopConfig2.set("shops."+uuid+"."+s, null);
			            	    	}
			                	}
				                try {
									ShopConfig2.save(file);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			                }
			            }
			        }
			        
			        System.out.println("done");
			    }
			});
		  return true;
	  }
	  return false;
	}
public static CreationCheck getCreationCheck() {
	return creationCheck;
}
public static FileConfiguration getCon() {
	return config;
}
}
