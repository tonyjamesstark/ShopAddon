package me.tWizT3d_dreaMr.ShopAddon;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;



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
    	getConfig().addDefault("Logging.Enable", false);
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
        getConfig().addDefault("Command.NoPerms", "&cYou dont have permission!");
        getConfig().addDefault("Command.NotAArgumentr", "&cThat isn't a valid Argument!");
        getConfig().addDefault("Command.LoggingOn", "&aCheck logging on!");
        getConfig().addDefault("Command.LoggingOff", "&cCheck logging off!");
    	getConfig().options().copyDefaults(true);
    	saveConfig();
    }
    if(getConfig().get("Command.NotAArgument")==null) {
        getConfig().addDefault("Command.NotAArgument", "&cThat isn't a valid Argument!");
        getConfig().options().copyDefaults(true);
    	saveConfig();
    }if(getConfig().get("WorldGuard")==null) {
        getConfig().addDefault("Command.RegionDoesntExist", "&cRegion doesnt exist!");
        getConfig().addDefault("Command.WorldGuardNotEnabled", "&cWorldguard support not enabled!");
    	getConfig().addDefault("WorldGuard", false);
        getConfig().options().copyDefaults(true);
    	saveConfig();
    }
    if(getConfig().getBoolean("WorldGuard")) {
    	if(!getServer().getPluginManager().isPluginEnabled("WorldGuard")) {

    		getLogger().info("WorldGuard enabled in config and worldguard not enabled on server. Disabling worldguard support");
    		getConfig().set("WorldGuard", false);
    		saveConfig();
    	}
    		
    }
	config=getConfig();
	Bukkit.getPluginManager().registerEvents(new CheckOn(),this);
	
	if(getConfig().getBoolean("Logging.Enable"))
		try {
			{	
				ConfigurationSection s=getConfig().getConfigurationSection("Logging.SQL");
				Bukkit.getPluginManager().registerEvents(new ShopLogging(s.getString("host"),s.getInt("port"),s.getString("database"),s.getString("username"),s.getString("password")),this);
				
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    if(getConfig().getString("WhitelistItems").equalsIgnoreCase("true"))
    	wh=true;
    
    creationCheck=new CreationCheck(con,wh);        

}
public List<String> onTabComplete(CommandSender sender , Command cmd, String CommandLabel, String[] args){
	if(cmd.getName().equalsIgnoreCase("SA")) {
		ArrayList<String> ret= new ArrayList<String>();
		if(args.length==1) {
			ret.add("check");
			return ret;
		}
		if(args.length==2) {
			if(sender instanceof Player) {
			LoggingPlayer lp=ShopLogging.getLoggingPlayer((Player)sender);
			if(lp!=null){
				for(int i=1; i<=lp.getPageAmount();i++)
					ret.add(""+i);
			}
			}
			for(Player p: Bukkit.getOnlinePlayers())
				ret.add(p.getName());
			return ret;
		}
		}
	return null;
	}
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	  if(command.getName().equalsIgnoreCase("ShopNotifications")) {

			  if(!(sender instanceof Player)) {
				  sender.sendMessage("You cant change your notification settings");
				  return true;
			  }
			  NotificationCommand.Command((Player)sender, args);
	
		  return true;
	  }
	  if (command.getName().equalsIgnoreCase("SA"))
	  { 	if(!(sender instanceof Player)) {
		  sender.sendMessage(Format.format( config.getString("Command.OnlyPlayers")));
		  return true;
	  }
	  Player p=(Player)sender;
	  if(!p.hasPermission("ShopAddon.Check")) {
		  sender.sendMessage(Format.format( config.getString("Command.NoPerms")));
	  }
		  if(args.length==0) {
			  sender.sendMessage(Format.format( config.getString("Command.IncorectUsage")));
			  return true;
		  }
	  if(args.length==1) {
		  if(args[0].equalsIgnoreCase("check")) {
			ShopLogging.add(p);  
		  }
	  }else if(args.length==2) {
		  if(args[0].equalsIgnoreCase("check")) {
				if(isNumeric(args[1])&&(Integer.parseInt(args[1])-1)>=0) {
					ShopLogging.sendPage(Integer.parseInt(args[1])-1, p );
				}else if(args[1].equalsIgnoreCase("region")){
					if(!getConfig().getBoolean("WorldGuard")) {
						p.sendMessage(Format.format( config.getString("Command.WorldGuardNotEnabled")));
						return true;
					
					}
					Location loc=((Player)sender).getLocation();
					RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));

					
					ApplicableRegionSet set = rm.getApplicableRegions( BukkitAdapter.asBlockVector(loc));
					ProtectedRegion heaviest=null;
					int w=-1;
					if(set.size()!=0)
						for ( ProtectedRegion region : set ) {
							if(w<region.getPriority()) {
								w=region.getPriority();
								heaviest=region;
							}
						}
					if(heaviest==null) {
						p.sendMessage(Format.format( config.getString("Command.RegionDoesntExist")));
						return true;
					}
					BlockVector3 max=heaviest.getMaximumPoint();
					BlockVector3 min=heaviest.getMinimumPoint();
					String s=""+max.getBlockX()+" "+max.getBlockY()+" "+max.getBlockZ()+" "+min.getBlockX()+" "+min.getBlockY()+" "+min.getBlockZ()+" "+loc.getWorld().getName().toString();
					ShopLogging.getResults("LocationBetween",s,((Player)sender));
					
				}
				else {
					Player getp=getPlayer(args[1]);
					if(getp!=null) {
						ShopLogging.getResultsFromPlayer(getp, p);
					}
					else p.sendMessage(Format.format( config.getString("Command.NotAArgument")));}
		  }
	  } else if(args.length==3){
		  if(args[1].equalsIgnoreCase("region")){
			  if(!getConfig().getBoolean("WorldGuard")) {
				p.sendMessage(Format.format( config.getString("Command.WorldGuardNotEnabled")));
				return true;
			
			}
				Location loc=((Player)sender).getLocation();
				RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
				ProtectedRegion rg=rm.getRegion(args[2]);
				if(rg==null) {
					p.sendMessage(Format.format( config.getString("Command.RegionDoesntExist")));
					return true;
				}
				
				BlockVector3 max=rg.getMaximumPoint();
				BlockVector3 min=rg.getMinimumPoint();
				String s=""+max.getBlockX()+" "+max.getBlockY()+" "+max.getBlockZ()+" "+min.getBlockX()+" "+min.getBlockY()+" "+min.getBlockZ()+" "+loc.getWorld().getName().toString();
				ShopLogging.getResults("LocationBetween",s,((Player)sender));
				
			
		  }
	  }
	  else {
			  sender.sendMessage(Format.format( config.getString("Command.IncorectUsage")));
		  }
		  return true;
	  }
	  return false;
	}
public static CreationCheck getCreationCheck() {
	return creationCheck;
}
private Player getPlayer(String s) {
	Player backup=null;
for(Player p:Bukkit.getOnlinePlayers()) {
	if(p.getName().equalsIgnoreCase(s)) return p;
	else if(p.getUniqueId().toString().equalsIgnoreCase(s)) backup= p;
}
return backup;
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
