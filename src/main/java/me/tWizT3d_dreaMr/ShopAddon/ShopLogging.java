package me.tWizT3d_dreaMr.ShopAddon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import com.snowgears.shop.event.PlayerExchangeShopEvent;

import net.md_5.bungee.api.ChatColor;

public class ShopLogging implements Listener {
	  private Connection connection;
	  private String host, database, username, password;
	  private int port;
	  private static Statement statement;
	  private static ArrayList<String> checkers;
	  private static ArrayList<LoggingPlayer> LPS;
public ShopLogging(String host,int port, String database,String username,String password) {
		checkers=new ArrayList<String>();
		LPS=new ArrayList<LoggingPlayer>();
	  	this.host=host;
	  	this.port=port;
	  	this.database=database;
	  	this.username=username;
	  	this.password=password;
	  	try {
	  		openConnection();
	          statement = connection.createStatement();  
	  	} catch (ClassNotFoundException e) {
	  		e.printStackTrace();
	  	} catch (SQLException e) {
	  		e.printStackTrace();
	  	}
	  	try {
	  		statement.execute("CREATE TABLE IF NOT EXISTS ShopTransaction (PlayerUUID varchar(50),PlayerName varchar(50), Type varchar(200), "
	  				+ "Price varchar(100), ItemName varchar(1000), ItemLore varchar(10000), Time varchar(100), SignX varchar(100), SignY varchar(100), SignZ varchar(100), SignWorld varchar(100))");
	  		} catch (SQLException e) {
	  		// TODO Auto-generated catch block
	  		e.printStackTrace();
	  	}
	  }
	  public void openConnection() throws ClassNotFoundException, SQLException {
	      if (connection != null && !connection.isClosed()) {
	          return;
	      }
	   
	      synchronized (this) {
	          if (connection != null && !connection.isClosed()) {
	              return;
	          }
	          Class.forName("com.mysql.jdbc.Driver");
	          connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
	      }
	  }
	  public static void add(Player p) {
		  if(checkers.contains(p.getUniqueId().toString())) {
			  checkers.remove(p.getUniqueId().toString());
			  p.sendMessage(ChatColor.of("#E53C67")+"Turned off logging");
			  return;
		  }
		  checkers.add(p.getUniqueId().toString());
		  p.sendMessage(ChatColor.of("#E53C67")+"Turned on logging");
	  }
	  
	@EventHandler(priority=EventPriority.LOW)
	public void click(PlayerInteractEvent event) {
		if(!checkers.contains(event.getPlayer().getUniqueId().toString())) return;
		Location loc=event.getClickedBlock().getLocation();
		String check=""+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ()+" "+loc.getWorld().getName().toString();
		getResults("Location",check, event.getPlayer());
		event.setCancelled(true);
		checkers.remove(event.getPlayer().getUniqueId().toString());
	}
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void logShop(PlayerExchangeShopEvent event) {
		long time=System.currentTimeMillis();
		Location l=event.getShop().getSignLocation();
		int x=l.getBlockX();
		int y=l.getBlockY();
		int z=l.getBlockZ();
		String world=l.getWorld().getName().toString();
		String price=event.getShop().getPriceString();
		ItemStack item=event.getShop().getItemStack();
		String itemMat=item.getType().toString();
		String itemNametemp="none";
		String itemLoretemp="none";
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasDisplayName()) {
				itemNametemp=item.getItemMeta().getDisplayName();
			}if(item.getItemMeta().hasLore()) {
				itemLoretemp=item.getItemMeta().getLore().toString();
			}
		}
		String itemName=itemNametemp;
		String itemLore=itemLoretemp;
		String buyerUUID=event.getPlayer().getUniqueId().toString();
		String buyername=event.getPlayer().getName();
		BukkitRunnable r=new BukkitRunnable() {
	  	    @Override
	  	    public void run() {
	  	    	try {
	  	    
	  	    		statement.executeUpdate("INSERT INTO ShopTransaction (PlayerUUID, PlayerName, Type, Price, ItemName, ItemLore, time, SignX, SignY, SignZ, SignWorld) VALUES ('"
	  	    				+buyerUUID+"', '"+buyername+"', '"+itemMat+"', '"+price+"', '"+itemName+"', '"+itemLore+"', '"+time+"', '"+x+"', '"+y+"', '"+z+"', '"+world+"');");
	  	    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	 }
		};
		r.runTaskAsynchronously(main.plugin);

	}
	public static void sendPage(int i, Player p) {
		for(LoggingPlayer LP:LPS) {
			if(LP.isName(p.getName())) {
				for(String s:LP.message(i)) {
					p.sendMessage(ChatColor.of("#7DB6FF")+s);
				}
				return;
			}
		}
	}
	public static void getResults(String field, String toLookup, Player p){
		ArrayList<String> ret=new ArrayList<String>();
		BukkitRunnable r=new BukkitRunnable() {
	
	  	    @Override
	  	    public void run() {
				ResultSet result=results(field, toLookup);
		
				try {
					while(result.next()) {
						DateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss"); 
						Date date = new Date(Long.parseLong(result.getString("Time"))); 						
						String res= "Date "+
						format.format(date)
						+" UUID "
						+result.getString("PlayerUUID")
						+" Name "
						+result.getString("PlayerName")
						+" Material "
						+result.getString("Type")
						+" Price "
						+result.getString("Price")
						+" Item Name "
						+result.getString("ItemName")
						+" Item Lore "
						+result.getString("ItemLore")
						+" Sign location "
						+result.getString("SignX")
						+", "
						+result.getString("SignY")
						+", "
						+result.getString("SignZ")
						+", "
						+result.getString("SignWorld");
						ret.add(res);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LoggingPlayer LP=new LoggingPlayer(ret,p.getName());
				int i=-1;
				for(LoggingPlayer lop:LPS) {
					if(lop.isName(p.getName())) i=LPS.indexOf(lop);
				}
				if(i==-1)
					LPS.add(LP);
				else
					LPS.set(i, LP);
				for(String s:LP.message(0)) {
					p.sendMessage(ChatColor.of("#7DB6FF")+s);
				}
				
			}
		};
		r.runTaskAsynchronously(main.plugin);
	}
	
	public static ResultSet results(String field, String toLookup){
	  	ResultSet result=null;
	  	if(field.equals("Location")) {
	  		String[] loc=toLookup.split(" ");
	  		try {
				result = statement.executeQuery("SELECT * FROM ShopTransaction WHERE SignX = '"+loc[0]+"' and SignY = '"+loc[1]+"' and SignZ = '"+loc[2]+"' and SignWorld = '"+loc[3]+"';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
	  	}else
	  		try {
				result = statement.executeQuery("SELECT * FROM ShopTransaction WHERE "+field+" = '"+toLookup+"';");
	
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		
		return result;
		
	}
}
