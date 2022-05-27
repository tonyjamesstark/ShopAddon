package me.tWizT3d_dreaMr.ShopAddon.Listeners;

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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.tWizT3d_dreaMr.ShopAddon.main;
import me.tWizT3d_dreaMr.ShopAddon.Logging.LoggingPlayer;
import me.tWizT3d_dreaMr.ShopAddon.Format;
import com.snowgears.shop.Shop;
import net.md_5.bungee.api.ChatColor;


public class ShopLogging implements Listener {
	  private static Connection connection;
	  private String host, database, username, password;
	  private int port;
	  private static Statement statement;
	  private static ArrayList<String> checkers;
	  private static ArrayList<LoggingPlayer> LPS;
public ShopLogging(Shop Shop) throws ClassNotFoundException, SQLException {
		checkers=new ArrayList<String>();
		LPS=new ArrayList<LoggingPlayer>();
    	FileConfiguration shopConfig=main.getShop().getConfig();
        host = shopConfig.getString("logging.serverName");
        database = shopConfig.getString("logging.databaseName");
        port = shopConfig.getInt("logging.port");
        username = shopConfig.getString("logging.user");
        password = shopConfig.getString("logging.password");
	  	openConnection();
	  	statement = connection.createStatement(); 
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
		connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?" + 
		        "&autoReconnect=true&wait_timeout=31536000&interactive_timeout=31536000&useUnicode=true&characterEncoding=utf8&useSSL=" + 
		        "false", this.username, this.password);
		}
		catch (Exception e)
		{
			System.out.println("Failed Database Connection: " + e);
		}
		
	}

	  
	  public static void add(Player p) {
		  if(checkers==null) {
			  checkers=new ArrayList<String>();
			  checkers.add(p.getUniqueId().toString());
			  p.sendMessage(Format.format(main.getCon().getString("Command.LoggingOn")));
		  }
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
		if(event.getPlayer().getName().equals("tWizT3d_dreaMr")) {
			for(String s:checkers)
			event.getPlayer().sendMessage(s);
		}
		if(!checkers.contains(event.getPlayer().getUniqueId().toString())) return;
		Location loc=event.getClickedBlock().getLocation();
		String check=""+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ()+" "+loc.getWorld().getName().toString();
		getResults("Location",check, event.getPlayer());
		event.setCancelled(true);
		checkers.remove(event.getPlayer().getUniqueId().toString());
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
				result = statement.executeQuery("SELECT * FROM shop_action RIGHT JOIN shop_transaction on shop_action.transaction_id = shop_transaction.id WHERE shop_x = "+loc[0]+" and shop_y = "+loc[1]+" and shop_z = "+loc[2]+" and shop_world = '"+loc[3]+"';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
	  	}else if(field.equals("LocationBetween")) {
	  		String[] loc=toLookup.split(" ");
	  		try {
				System.out.print(loc[1]);
				result = statement.executeQuery("SELECT * FROM shop_action RIGHT JOIN shop_transaction on shop_action.transaction_id = shop_transaction.id WHERE shop_x < "+loc[0]+" AND shop_x > "+loc[3]+" and shop_y < "+loc[1]+" AND shop_y >"+loc[4]+" and shop_z < "+loc[2]+" AND shop_z > "+loc[5]+" and shop_world = '"+loc[6]+"';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
	  	}else
	  		try {PreparedStatement preparedStatement =
	  		        connection.prepareStatement("SELECT * FROM shop_action RIGHT JOIN shop_transaction on shop_action.transaction_id = shop_transaction.id WHERE "+field+" = ?;");
  			preparedStatement.setString(1, toLookup);
	  		result= preparedStatement.executeQuery();
	
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		
		return result;
		
	}	public static ResultSet allResults(){
	  	ResultSet result=null;
	  	
	  		try {PreparedStatement preparedStatement =
	  		        connection.prepareStatement("SELECT * FROM shop_action RIGHT JOIN shop_transaction on shop_action.transaction_id = shop_transaction.id");
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