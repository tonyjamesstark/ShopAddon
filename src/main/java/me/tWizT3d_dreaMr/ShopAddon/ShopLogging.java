package me.tWizT3d_dreaMr.ShopAddon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.snowgears.shop.event.PlayerExchangeShopEvent;

import net.md_5.bungee.api.ChatColor;


public class ShopLogging implements Listener {
	  private static Connection connection;
	  private String host, database, username, password;
	  private int port;
	  private static Statement statement;
	  private static ArrayList<String> checkers;
	  private static ArrayList<LoggingPlayer> LPS;
public ShopLogging(String host,int port, String database,String username,String password) throws ClassNotFoundException, SQLException {
		checkers=new ArrayList<String>();
		LPS=new ArrayList<LoggingPlayer>();
	  	this.host=host;
	  	this.port=port;
	  	this.database=database;
	  	this.username=username;
	  	this.password=password;
	  	openConnection();
	  	statement = connection.createStatement(); 
	  		statement.execute("CREATE TABLE IF NOT EXISTS ShopTransaction (PlayerUUID varchar(50),PlayerName varchar(50), Type varchar(200), "
	  				+ "Price varchar(100), ItemName varchar(1000), ItemLore varchar(10000), Time varchar(100), SignX int(255), SignY int(255), SignZ int(255), SignWorld varchar(100))");
	  }

	public void openConnection() throws ClassNotFoundException
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch (ClassNotFoundException classNotFoundException)
		{
			try{
			Class.forName("com.mysql.jdbc.Driver");
			}
			catch (ClassNotFoundException classNotFoundException2)
			{
				System.out.println("DoublFail");
			}
		}
		try
		{
		this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?" + 
		        "&autoReconnect=true&wait_timeout=31536000&interactive_timeout=31536000&useUnicode=true&characterEncoding=utf8&useSSL=" + 
		        "false", this.username, this.password);
		}
		catch (Exception e)
		{
			System.out.println("Failed Database Connection: " + e);
		}
		
	}

	  
	  public static void add(Player p) {
		  if(checkers.contains(p.getUniqueId().toString())) {
			  checkers.remove(p.getUniqueId().toString());
			  p.sendMessage(Format.format(main.getCon().getString("Command.LoggingOff")));
			  return;
		  }
		  checkers.add(p.getUniqueId().toString());
		  p.sendMessage(Format.format(main.getCon().getString("Command.LoggingOn")));
	  }
	  
	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
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
	  	    		PreparedStatement preparedStatement =
	  	    		        connection.prepareStatement("INSERT INTO ShopTransaction (PlayerUUID, PlayerName, Type, Price, ItemName, ItemLore, time, SignX, SignY, SignZ, SignWorld) VALUES ("
	  		  	    				+"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
  	    			preparedStatement.setString(1, buyerUUID);
  	    			preparedStatement.setString(2, buyername);	  	    		
  	    			preparedStatement.setString(3, itemMat);
  	    			preparedStatement.setString(4, price);		
  	    			preparedStatement.setString(5, itemName);
  	    			preparedStatement.setString(6, itemLore);		
  	    			preparedStatement.setString(7, ""+time);
  	    			preparedStatement.setInt(8, x);		
  	    			preparedStatement.setInt(9, y);
  	    			preparedStatement.setInt(10, z);
  	    			preparedStatement.setString(11, world);
  	    			preparedStatement.executeUpdate();
	  	    		//statement.executeUpdate("INSERT INTO ShopTransaction (PlayerUUID, PlayerName, Type, Price, ItemName, ItemLore, time, SignX, SignY, SignZ, SignWorld) VALUES ('"
	  	    		//		+buyerUUID+"', '"+buyername+"', '"+itemMat+"', '"+price+"', '"+itemName+"', '"+itemLore+"', '"+time+"', '"+x+"', '"+y+"', '"+z+"', '"++"');");
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
				int num=1;
				for(String s:LP.message(i)) {
					String[]split=s.split(";");
					for(String send:split) {
						p.sendMessage(ChatColor.AQUA+send.replace("%NUM%", ""+num));
					}
					num++;
				
				}
				return;
			}
		}
	}
	public static void getResultsFromPlayer(Player of, Player to) {
		getResults("PlayerUUID",of.getUniqueId().toString(),to);
	}
	public static void getResults(String field, String toLookup, Player p){
		ArrayList<String> ret=new ArrayList<String>();
		BukkitRunnable r=new BukkitRunnable() {
	
	  	    @Override
	  	    public void run() {
				ResultSet result=results(field, toLookup);
		
				try {
					while(result.next()) {
						DateFormat format = new SimpleDateFormat("dd MMM yy HH:mm:ss"); 
						Date date = new Date(Long.parseLong(result.getString("Time")));
						
						String form=main.getCon().getString("Logging.Format");
						
						form=form.replace("%DATE%", format.format(date));
						form=form.replace("%UUID%", result.getString("PlayerUUID"));
						form=form.replace("%NAME%", result.getString("PlayerName"));
						form=form.replace("%MATERIAL%", result.getString("Type"));
						form=form.replace("%PRICE%", result.getString("Price"));
						form=form.replace("%INAME%", result.getString("ItemName"));
						form=form.replace("%ILORE%", result.getString("ItemLore"));
						form=form.replace("%SIGNX%", ""+result.getInt("SignX"));
						form=form.replace("%SIGNY%", ""+result.getInt("SignY"));
						form=form.replace("%SIGNZ%", ""+result.getInt("SignZ"));
						form=form.replace("%SIGNWORLD%", result.getString("SignWorld"));
						
						
						form=Format.format(form);
						ret.add(form);
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
				int num=1;
				for(String s:LP.message(0)) {
					String[]split=s.split(";");
					for(String send:split) {
						p.sendMessage(ChatColor.AQUA+send.replace("%NUM%", ""+num));
					}
					num++;
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
				result = statement.executeQuery("SELECT * FROM ShopTransaction WHERE SignX = "+loc[0]+" and SignY = "+loc[1]+" and SignZ = "+loc[2]+" and SignWorld = '"+loc[3]+"';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
	  	}else if(field.equals("LocationBetween")) {
	  		String[] loc=toLookup.split(" ");
	  		try {
				System.out.print(loc[1]);
				result = statement.executeQuery("SELECT * FROM ShopTransaction WHERE SignX < "+loc[0]+" AND SignX > "+loc[3]+" and SignY < "+loc[1]+" AND SignY >"+loc[4]+" and SignZ < "+loc[2]+" AND SignZ > "+loc[5]+" and SignWorld = '"+loc[6]+"';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
	  	}else
	  		try {PreparedStatement preparedStatement =
	  		        connection.prepareStatement("SELECT * FROM ShopTransaction WHERE "+field+" = ?;");
  			preparedStatement.setString(1, toLookup);
	  		result= preparedStatement.executeQuery();
	
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		
		return result;
		
	}
	public static LoggingPlayer getLoggingPlayer(Player p) {
		for(LoggingPlayer LP:LPS) {
			if(LP.isName(p.getName())) {
				return LP;
			}
		}
		return null;
	}
}