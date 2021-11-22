package bct.loadupstudios.MineTracker;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;

import bct.loadupstudios.MineTracker.MyListener;

public class MineTracker extends JavaPlugin
{
	FileConfiguration config = this.getConfig();
	String folder = this.getDataFolder().getPath();
	MyListener eventManager = new MyListener(config, folder);
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	//String commandOOLP = "";
	File playersDir;
	File configFile = new File(folder,"config.yml");
	
	@Override
    public void onEnable() 
    {
		 // TODO Insert logic to be performed when the plugin is enabled
    	//System.out.println("MineTracker: Enabling...");
		saveDefaultConfig();
		try
		{
			Scanner reader = new Scanner(configFile);
			if(reader.hasNext())
			{
				//String newS = reader.nextLine().substring(9,12);
				//System.out.println(newS);
				if(!reader.nextLine().substring(9,12).equals("1.1"))
				{
					System.out.println("[MineTracker] New Config File Found, Updating...");
					reader.close();
					rewriteConfig();
				}
				else
				{
					//System.out.println("Version matches");
					reader.close();
				}
			}
			else
			{
				System.out.println("[MineTracker] Config File Empty, Updating...");
				reader.close();
				rewriteConfig();
			}
		}
		catch(Exception e)
		{
			System.out.println("Failed reading config File, please delete and reload");
		}
		
		getServer().getPluginManager().registerEvents(eventManager, this);
		playersDir = new File(folder + "/Players");
		if(!playersDir.exists())
		{
			playersDir.mkdir();
		}
		
    }
	
	@Override
    public void onDisable() 
    {
        // TODO Insert logic to be performed when the plugin is disabled
		//saveConfig();
		System.out.println("[MineTracker] Disabling...");
		System.out.println("[MineTracker] Removing Player Data...");
		deleteDirectory(playersDir);
		playersDir.mkdir();
    }
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
		if(command.getName().equalsIgnoreCase("mt"))
		{ 
			if(args.length > 0)
			{
				switch(args[0])
				{
				case "sound":
				case "Sound":
					if(eventManager.noPermSystem == "false" && sender.hasPermission("mt.staff"))
					{
						if(eventManager.soundOff)
						{
							eventManager.soundOff = false;
							sender.sendMessage("Ping Sounds Disabled For Everyone");
						}
						else
						{
							eventManager.soundOff = true;
							sender.sendMessage("Ping Sounds Disabled For Everyone");
						}
					}
					else if(eventManager.noPermSystem == "true")
					{
						for(int i = 0; i < eventManager.staffArray.length; i++)
						{
							if(Bukkit.getPlayer(eventManager.staffArray[i]) != null && Bukkit.getPlayer(eventManager.staffArray[i]) == sender)
							{
								if(eventManager.soundOff)
								{
									eventManager.soundOff = false;
									sender.sendMessage("Ping Sounds Disabled For Everyone");
								}
								else
								{
									eventManager.soundOff = true;
									sender.sendMessage("Ping Sounds Disabled For Everyone");
								}
							}
						}
					}
					return true;
				case "tp":
					if(eventManager.noPermSystem == "false" && Bukkit.getPlayer(args[1]).hasPermission("mt.staff"))
					{
						//commandOOLP = "gamemode spectator " + args[1];
						//Bukkit.dispatchCommand(console, commandOOLP);
						Bukkit.getPlayer(args[1]).setGameMode(GameMode.SPECTATOR);
						if(Float.parseFloat(args[3]) < 5.0 )
						{
							//Location loc = new Location(Bukkit.getWorld(args[5]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
							//commandOOLP = args[1] + " " + args[2] + " " + "5" + " " + args[4];
							Location loc = new Location(Bukkit.getWorld(args[5]), Double.parseDouble(args[2]), 5.0, Double.parseDouble(args[4]));
							Bukkit.getPlayer(args[1]).teleport(loc);
						}
						else
						{
							Location loc = new Location(Bukkit.getWorld(args[5]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
							Bukkit.getPlayer(args[1]).teleport(loc);
						}
					}
					else
					{
						for(int i = 0; i < eventManager.staffArray.length; i++)
						{
							if(Bukkit.getPlayer(eventManager.staffArray[i]) != null && Bukkit.getPlayer(eventManager.staffArray[i]) == Bukkit.getPlayer(args[1]))
							{
								Bukkit.getPlayer(args[1]).setGameMode(GameMode.SPECTATOR);
								if(Float.parseFloat(args[3]) < 5.0 )
								{
									//Location loc = new Location(Bukkit.getWorld(args[5]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
									//commandOOLP = args[1] + " " + args[2] + " " + "5" + " " + args[4];
									Location loc = new Location(Bukkit.getWorld(args[5]), Double.parseDouble(args[2]), 5.0, Double.parseDouble(args[4]));
									Bukkit.getPlayer(args[1]).teleport(loc);
								}
								else
								{
									Location loc = new Location(Bukkit.getWorld(args[5]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
									Bukkit.getPlayer(args[1]).teleport(loc);
								}
								break;
							}
						}
					}
					//System.out.println("HEREE");
					return true;
				}
			}
		}
		return false;
    }
	
	public void reload()
	{
		reloadConfig();
		config = getConfig();
		//config = YamlConfiguration.loadConfiguration(configFile);
		saveConfig();
		eventManager.reload(config);
	}
	
	public boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	public void rewriteConfig()
	{
		try
		{
			File tempFile2 = new File(folder,"tempFile2.yml");
			tempFile2.createNewFile();
			config.load(tempFile2);
			FileWriter writer = new FileWriter(configFile);
			/*String configString = "version: 1.1\r\n"
					+ "#Make this true ONLY if you do not use a permissions system\r\n"
					+ "nopermissionsystem: false\r\n"
					+ "#If the above is true, add in staff usernames here, add in like this\r\n"
					+ "#staffusernames: staffname1,staffname2,staffname3\r\n"
					+ "#Make sure its username comma username without spaces\r\n"
					+ "staffusernames: staffname1,staffname2,staffname3";*/
			writer.write("version: 1.1\r\n"
					+ "#Make this true ONLY if you do not use a permissions system\r\n"
					+ "nopermissionsystem: false\r\n"
					+ "#If the above is true, add in staff usernames here, add in like this\r\n"
					+ "#staffusernames: staffname1,staffname2,staffname3\r\n"
					+ "#Make sure its username comma username without spaces\r\n"
					+ "staffusernames: staffname1,staffname2,staffname3");
			writer.close();
			config.load(configFile);
			tempFile2.delete();
			
			
		}
		catch(Exception e)
		{
			System.out.println("Failed to rewrite config file\n" + e);
		}
	}
}
