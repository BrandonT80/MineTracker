package bct.loadupstudios.MineTracker;	//Package connecting

import java.io.File;		//Used for File Opening
import java.io.FileWriter;	//Used for writing to files
import java.util.Scanner;	//Used for reading files line-by-line
import java.util.logging.Level;		//Used for types of logging
import java.util.logging.Logger;	//Used for logging (Spigot prefers this to STDOUT)
import org.bukkit.Bukkit;	//Bukkit code
import org.bukkit.GameMode;	//For spectator game-mode changes
import org.bukkit.command.Command;			//For executing commands
import org.bukkit.command.CommandSender;	//For executing commands
import org.bukkit.command.ConsoleCommandSender;	//For executing commands
import org.bukkit.configuration.file.FileConfiguration;	//For Config File
import org.bukkit.plugin.java.JavaPlugin;	//For Plugin information
import org.bukkit.Location;	//For world locations
import org.bukkit.World;	//For world information

/**
*Description: MineTracker Class - Holds start tasks, close tasks, command listening, reloading, and config file handling.
*Date: 1/24/2021
*@author Brandon Taylor - LoadUpStudios
*@version 2.2.0
*/ 
public class MineTracker extends JavaPlugin
{
	FileConfiguration config = this.getConfig();	//Gets config file
	Logger logger = this.getLogger();				//Gets the logger for printing
	String folder = this.getDataFolder().getPath();	//Gets the plugin path
	MyListener eventManager = new MyListener(config, folder, logger);		//Creates the MyListener using the config, folder, and logger (Eventhandler)
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();	//Gets the console for command sending
	File playersDir;									//Creates a player directory placeholder
	File configFile = new File(folder,"config.yml");	//Gets the config file for read/write purposes
	
	/**
	* Description: onEnable - Checks and reads the config, gets the server, and verifies directory for players.
	* @param None
	* @return None - Void
	* @throws None
	*/
	@Override
    public void onEnable() 
    {
		//Beginning of onEnable. First verify the config file, and then set up the eventmanager and verify the Players directory
		
		saveDefaultConfig();	//Saves the default config if one does not exist (This is generic for all plugins)
		try
		{
			Scanner reader = new Scanner(configFile);	//Open the config file in a scanner for line-by-line reading
			if(reader.hasNext())	//If the file has a next line, load it in and begin
			{
				if(!reader.nextLine().substring(9,12).equals("1.1"))	//This checks the version, change this if the config has been ammended
				{
					logger.log(Level.INFO, "New Config File Found, Updating...");	//Log that the file is being updated
					reader.close();		//Close the reader (No longer needed to read as we are going to overwrite this file)
					rewriteConfig();	//Call the re-write function
				}
				else	//If the config is current, continue on
				{
					reader.close();		//Close the reader as we are no longer needing to make changes
				}
			}
			else	//If the config file is empty
			{
				logger.log(Level.INFO, "Config File Empty, Updating...");	//Log that the file is empty
				reader.close();		//Close the reader (No longer needed to read as we are going to overwrite this file)
				rewriteConfig();	//Call the re-write function
			}
		}
		catch(Exception e)	//Catch anything that can be caused by the config file being corrupted, suggest deleting
		{
			logger.log(Level.INFO, "Failed reading config File, please delete and reload");	//Log that the config is problematic, and should be deleted manually
		}
		
		getServer().getPluginManager().registerEvents(eventManager, this);	//Register events (Sets up the event manager (Has to be done after config stuff)
		playersDir = new File(folder + "/Players");		//Set the player directory for later use
		if(!playersDir.exists())	//Make sure the directory opened, if not make it
		{
			playersDir.mkdir();		//Make the directory if one does not already exist
		}
		
		//End of onEnable. At this point we have verified the config file and ensured there is a Players directory as well as created the eventmanager
		
    }
	
	/**
	* Description: onDisable - Deletes player directory and re-creates it as empty
	* @param None
	* @return None - Void
	* @throws None
	*/
	@Override
    public void onDisable() 
    {
		//Beginning of onDisable. First log the disabling, then delete the players directory and re-create it as empty
		
		logger.log(Level.INFO, "Disabling...");				//Log the plugin is disabling
		logger.log(Level.INFO, "Removing Player Data...");	//Log the player data is being removed
		deleteDirectory(playersDir);	//Delete the player directory
		playersDir.mkdir();				//Re-make the player directory as empty
		
		//End of onDisable. At this point the Players directory is empty and the server has been notified of closing
    }
	
	/**
	* Description: onCommand - Checks for commands run begining with mt
	* @param CommandSender as sender (person sending the command), Command as command (the command itself), String as label, String[] as args (Command arguments)
	* @return Boolean - True if succeeded to run the command, False if command has failed
	* @throws None
	*/
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
		//Beginning of onCommand - Check the commands that staff or players can run. Currently sound and tp
		
		if(command.getName().equalsIgnoreCase("mt"))	//If the command begins with mt
		{ 
			if(args.length > 0)	//Make sure mt is not the only thing and there are other arugments
			{
				switch(args[0])	//Switch the first argument (NO MT IS NOT THE FIRST ARGUMENT :p)
				{
				case "sound":	//If the first argument is sound or Sound 
				case "Sound":
					if(eventManager.noPermSystem == "false" && sender.hasPermission("mt.staff"))	//Make sure the person is staff and a perms system is in use
					{
						if(eventManager.soundOff)	//Check if the sound is on, if so disable it
						{
							eventManager.soundOff = false;	//Set the sound to false
							sender.sendMessage("Ping Sounds Enabled For Everyone");	//Confirm to user the sound was disabled
						}
						else						//If sound is not on, enable it
						{
							eventManager.soundOff = true;	//Set the sound to true
							sender.sendMessage("Ping Sounds Disabled For Everyone");	//Confirm to user the sound was enabled
						}
					}
					else if(eventManager.noPermSystem == "true")	//If no perms system is used
					{
						for(int i = 0; i < eventManager.staffArray.length; i++)	//Check to see if the user is on the designated staff team (loop all staff)
						{
							if(Bukkit.getPlayer(eventManager.staffArray[i]) != null && Bukkit.getPlayer(eventManager.staffArray[i]) == sender)	//If the array is not null and the player is staff
							{
								if(eventManager.soundOff)	//Check if the sound is on, if so disable it
								{
									eventManager.soundOff = false;	//Set the sound to false
									sender.sendMessage("Ping Sounds Enabled For Everyone");	//Confirm to user the sound was disabled
								}
								else						//If sound is not on, enable it
								{
									eventManager.soundOff = true;	//Set the sound to true
									sender.sendMessage("Ping Sounds Disabled For Everyone");	//Confirm to user the sound was enabled
								}
							}
						}
					}
					return true;	//Return true, the operation was handled successfully
				case "tp":	//If the first argument is the teleport (tp)
					if(eventManager.noPermSystem == "false" && Bukkit.getPlayer(sender.getName()).hasPermission("mt.staff"))	//If the player is staff and a perms system is in use
					{
						Bukkit.getPlayer(sender.getName()).setGameMode(GameMode.SPECTATOR);		//First put the player in spectator (YOU DONT WANT TO TELEPORT A PLAYER TO THEIR DEATH DO YOU?)
						if((Bukkit.getVersion().contains("1.20") && Bukkit.getWorld(args[4]).getEnvironment() == World.Environment.NORMAL) || (Bukkit.getVersion().contains("1.19") && Bukkit.getWorld(args[4]).getEnvironment() == World.Environment.NORMAL) || (Bukkit.getVersion().contains("1.18") && Bukkit.getWorld(args[4]).getEnvironment() == World.Environment.NORMAL))	//If the version is 1.18 and they are in the overworld
						{
							if(Float.parseFloat(args[2]) < -60.0 )	//Check that the height isn't too close to the bedrock (1.18 is -60 and < is 5)
							{
								Location loc = new Location(Bukkit.getWorld(args[4]), Double.parseDouble(args[1]), -60.0, Double.parseDouble(args[3]));	//Set up the block location for teleporting to
								Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
								Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
							}
							else	//If the block isn't too close to bedrock, teleport directly to the block
							{
								Location loc = new Location(Bukkit.getWorld(args[4]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));	//Set up the block location for teleporting to
								Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
								Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
							}
						}
						else
						{
							if(Float.parseFloat(args[2]) < 5.0 )	//Check that the height isn't too close to the bedrock (1.18 is -60 and < is 5)
							{
								Location loc = new Location(Bukkit.getWorld(args[4]), Double.parseDouble(args[1]), 5.0, Double.parseDouble(args[3]));	//Set up the block location for teleporting to
								Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
								Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
							}	
							else	//If the block isn't too close to bedrock, teleport directly to the block
							{
								Location loc = new Location(Bukkit.getWorld(args[4]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));	//Set up the block location for teleporting to
								Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
								Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
							}
						}
					}
					else	//If no perms system is used
					{
						for(int i = 0; i < eventManager.staffArray.length; i++)	//Check to see if the user is on the designated staff team (loop all staff)
						{
							if(Bukkit.getPlayer(eventManager.staffArray[i]) != null && Bukkit.getPlayer(eventManager.staffArray[i]) == Bukkit.getPlayer(sender.getName()))	//If the array is not null and the player is staff
							{
								Bukkit.getPlayer(sender.getName()).setGameMode(GameMode.SPECTATOR);	//First put the player in spectator (YOU DONT WANT TO TELEPORT A PLAYER TO THEIR DEATH DO YOU?)
								if(Bukkit.getVersion().contains("1.18") && Bukkit.getWorld(args[4]).getEnvironment() == World.Environment.NORMAL)	//If the version is 1.18 and they are in the overworld
								{
									if(Float.parseFloat(args[2]) < -60.0 )	//Check that the height isn't too close to the bedrock (1.18 is -60 and < is 5)
									{
										Location loc = new Location(Bukkit.getWorld(args[4]), Double.parseDouble(args[1]), -60.0, Double.parseDouble(args[3]));	//Set up the block location for teleporting to
										Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
										Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
									}
									else
									{
										Location loc = new Location(Bukkit.getWorld(args[4]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));	//Set up the block location for teleporting to
										Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
										Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
									}
								}
								else
								{
									if(Float.parseFloat(args[2]) < 5.0 )	//Check that the height isn't too close to the bedrock (1.18 is -60 and < is 5)
									{
										Location loc = new Location(Bukkit.getWorld(args[4]), Double.parseDouble(args[1]), 5.0, Double.parseDouble(args[3]));	//Set up the block location for teleporting to
										Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
										Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
									}	
									else	//If the block isn't too close to bedrock, teleport directly to the block
									{
										Location loc = new Location(Bukkit.getWorld(args[4]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));	//Set up the block location for teleporting to
										Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
										Bukkit.getPlayer(sender.getName()).teleport(loc);	//Teleport the player twice (sometimes the first teleport sends them to 0,0 or where they last were in the world)
									}
								}
							}
						}
					}
					return true;	//Return true, the operation was handled successfully
				}
			}
		}
		return false;	//If anything broke, return fals as the operation was not handled successfully
		
		//End of of onCommand - Possibly executed the commands that staff or players can run
    }
	
	/**
	* Description: reload - Reloads the plugin (just reloads the config, not the code itself)
	* @param None
	* @return None - Void
	* @throws None
	*/
	public void reload()
	{
		//Beginning of reload - Just reloads the config file
		
		reloadConfig();				//Call the reloadconfig built-in function
		config = getConfig();		//Get the config again
		saveConfig();				//Save the config
		eventManager.reload(config);//Call the reload config on the eventmanager to get the new config over there
		
		//End of reload - Reloaded the config file
	}
	
	/**
	* Description: deleteDirectory - Deletes the specified directory recursively
	* @param File as directoryToBeDeleted - The directory location to be deleted
	* @return Boolean - True if the directory was deleted, False if the directory failed to delete
	* @throws None
	*/
	public boolean deleteDirectory(File directoryToBeDeleted) 
	{
		//Beginning of deleteDirectory - Just deletes the specified directory
		
	    File[] allContents = directoryToBeDeleted.listFiles();	//Gets all the files of the directory into an array
	    if (allContents != null) 	//If the array is not null (Files exist)
	    {
	        for (File file : allContents) //For each file
	        {
	            deleteDirectory(file);		//Call the function recursively to delete all files, and then the directory itself
	        }
	    }
	    return directoryToBeDeleted.delete();	//Delete the directory itself once all files inside are deleted
	    
	    //End of deleteDirectory - By this point the directory was removed
	}
	
	/**
	* Description: rewriteConfig - Checks the config to see if it is the latest, if not it will attempt to update it without losing data
	* @param None
	* @return None - Void
	* @throws None
	*/
	public void rewriteConfig()
	{
		//Beginning of rewriteConfig - Rewrites the config to a new setup. Creates temp file to hold old data
		
		try
		{
			File tempFile2 = new File(folder,"tempFile2.yml");	//Setup a temporary file to distract the config the server is using
			tempFile2.createNewFile();		//Create the temporary file
			config.load(tempFile2);			//Load the config the server is using as the temporary file (allows modifications of the actual config file)
			FileWriter writer = new FileWriter(configFile);	//Open the config file in for writing
			//Below is the writing of the file for the newest version
			//Currently 1.1 rewrote for all new items, so no checking and save/replacing is needed
			writer.write("version: 1.1\r\n"	
					+ "#Make this true ONLY if you do not use a permissions system\r\n"
					+ "nopermissionsystem: false\r\n"
					+ "#If the above is true, add in staff usernames here, add in like this\r\n"
					+ "#staffusernames: staffname1,staffname2,staffname3\r\n"
					+ "#Make sure its username comma username without spaces\r\n"
					+ "staffusernames: staffname1,staffname2,staffname3");
			writer.close();			//Close the configFile (writing done)
			config.load(configFile);	//Load the configFile back in to the server
			tempFile2.delete();			//Delete the temporary file
			
			
		}
		catch(Exception e)	//If for any reason an error has occurred, catch it
		{
			logger.log(Level.WARNING, "Failed to rewrite config file\n" + e);		//Print the fail and the exception thrown.
		}
	}
	
	//End of rewriteConfig - By this point, the config is re-written for the new setup
}
