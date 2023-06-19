package bct.loadupstudios.MineTracker;	//Package connecting


import org.bukkit.Bukkit;			//Bukkit code
import org.bukkit.ChatColor;		//Chatcolor - Used to customize messages to players
import org.bukkit.Sound;			//Sound - Used for ping sound to staff
import org.bukkit.block.Block;		//Block - Used for knowing what blocks are being broken (block information)
import java.util.logging.Level;		//Used for types of logging
import java.util.logging.Logger;	//Used for logging (Spigot prefers this to STDOUT)
import org.bukkit.command.ConsoleCommandSender;			//For executing commands
import org.bukkit.configuration.file.FileConfiguration;	//For Config File
import org.bukkit.entity.Player;		//Player - Used to gather player data such as staff or players breaking blocks
import org.bukkit.event.EventHandler;	//EventHandler - Used for the blockbreak events
import org.bukkit.event.Listener;		//Listener - Used for the blockbreak events
import org.bukkit.event.block.BlockBreakEvent;	//BlockBreakEvent - Used for the blockbreak events
import org.bukkit.Material;				//Material - Used for checking what block was broken (Ex Deepslate)
import net.md_5.bungee.api.chat.ClickEvent;		//ClickEvent - Used for sending clickable messages to staff
import net.md_5.bungee.api.chat.TextComponent;	//TextComponent - Used for sending clickable messages to staff
import java.io.File;		//Used for File Opening
import java.util.Scanner;	//Used for reading files line-by-line
import java.io.FileWriter;	//Used for writing to files
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
*Description: MyListener Class - Looks for block break events, pings staff, updates player information
*Date: 1/24/2021
*@author Brandon Taylor - LoadUpStudios
*@version 2.2.0
*/ 
public class MyListener implements Listener
{
	boolean eventRunning = false; 		//This is to cancel the event running multiple times
	long playerTime = 0;				//Playertime variable for checking amount of blocks broken in x time
	private FileConfiguration config;	//Config file
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();	//Gets the console for command sending
	String folder = "";			//Plugin folder location
	String noPermSystem;		//If a permissions system exists
	String[] staffArray;		//Array holding staff names if no permissions system exists
	boolean soundOff = false;	//Sound is default on
	String bVersion = "";		//Holds the version string of paper/minecraft
	Logger logger;				//Logger for console messages
	//LocalTime date;
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	
	/**
	* Description: MyListener Constructor - Sets the config, logger, folder, and permissions system information
	* @param FileConfiguration as conf (The config file), String as folder (The folder for players), Logger as l (the Logger for printing to console)
	* @return None - Void
	* @throws None
	*/
	MyListener(FileConfiguration conf, String folder, Logger l)
	{
		//Beginning of MyListener Constructor. Sets all parameters to local variables
		
		config = conf;			//Set the config file
		logger = l;				//Set the logger
		this.folder = folder;	//Store the folder location
		noPermSystem = config.getString("nopermissionsystem");	//Sets the permissions system type
		if(noPermSystem == "true")	//If not using a permissions system, store names in the array
		{	
			staffArray = config.getString("staffusernames").split(",");	//Split names using comma as a delimiter 
		}
		try
		{
			bVersion = Bukkit.getVersion();	//Set the version string
		}
		catch (Exception e)	//If an error is found
		{
			bVersion = "0";	//Set the version string to 0
		}
	}
	
	/**
	* Description: MyListener Constructor - Empty constructor that does nothing
	* @param None
	* @return None - Void
	* @throws None
	*/
	MyListener()
	{
		//Empty Constructor - Not used
	}
	
	/**
	* Description: reload - Reloads the config file (Note you cannot reload the code)
	* @param FileConfiguration as conf (The config file)
	* @return None - Void
	* @throws None
	*/
	public void reload(FileConfiguration conf)
	{
		//Beginning of reload - Just reloads the config file
		
		config = conf;	//Get the config again
		if(noPermSystem == "true")	//If no perms system, res-plit the staff names
		{
			staffArray = config.getString("staffusernames").split(",");	//Split names using comma as a delimiter 
		}
	}
	
	/**
	* Description: onInteract - Event handler for interations, checks block breaks and passes the on where needed
	* @param BlockBreakEvent as event (The block break event and information)
	* @return None - Void
	* @throws None
	*/
	@EventHandler
    public void onInteract(BlockBreakEvent event)
    {
		//Beginning of onInteract - Handles version control as well as block breaking checking
		//System.out.println("T1");
		if(!event.getPlayer().hasPermission("mt.bypass"))	//If the player is not bypassed (This works for perms or no perms)
		{
			//System.out.println("T2");
			if(bVersion.contains("1.20") || bVersion.contains("1.19") || bVersion.contains("1.18") || bVersion.contains("1.17"))	//If the version is 1.17 or 1.18 (Has deepslate and ancient debris)
			{
				//System.out.println("T3");
				//If the Y value is below 32 and the user is using a iron or better pickaxe (why care if they wasted the ore!)
				if((event.getPlayer().getLocation().getY() < 32) && (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE))
				{
					//System.out.println("T4");
					if(event.getBlock() != null)	//If the block broke is not null (rare but it happens)
					{
						//System.out.println("T5");
						if(event.getBlock().getType() != Material.STONE && event.getBlock().getType() != Material.NETHERRACK)	//If the block is not stone or netherrack (pointless to continue with if it is - Located before switch as it helps optimize)
						{
							//System.out.println("T6");
							//System.out.println("TEST");
							switch(event.getBlock().getBlockData().getMaterial())	//Get the block material
							{
								//If it is diamond, gold, emerald, or debris (deepslate too)
								case DIAMOND_ORE:
								case DEEPSLATE_DIAMOND_ORE:
								case GOLD_ORE:
								case DEEPSLATE_GOLD_ORE:
								case EMERALD_ORE:
								case DEEPSLATE_EMERALD_ORE:
								case ANCIENT_DEBRIS:
									//System.out.println("T7");
									updateInformation(event.getBlock(), event.getPlayer());	//Update the player file
									break;
								default:
									break;	//If not the above, just close
							}
						}
					}
				}
			}
			else if(bVersion.contains("1.16"))	//If 1.16
			{
				//If the Y value is below 32 and the user is using a iron or better pickaxe (why care if they wasted the ore!)
				if((event.getPlayer().getLocation().getY() < 32) && (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE))
				{
					if(event.getBlock() != null)	//If the block broke is not null (rare but it happens)
					{
						if(event.getBlock().getType() != Material.STONE && event.getBlock().getType() != Material.NETHERRACK)	//If the block is not stone or netherrack (pointless to continue with if it is - Located before switch as it helps optimize)
						{
							switch(event.getBlock().getBlockData().getMaterial())	//Get the block material
							{
								//If it is diamond, gold, emerald, or debris
								case DIAMOND_ORE:
								case GOLD_ORE:
								case EMERALD_ORE:
								case ANCIENT_DEBRIS:
									updateInformation(event.getBlock(), event.getPlayer());	//Update the player file
									break;
								default:
									break;	//If not the above, just close
							}
						}
					}
				}
			}
			else	//If below 1.16
			{
				//If the Y value is below 32 and the user is using a iron or better pickaxe (why care if they wasted the ore!)
				if((event.getPlayer().getLocation().getY() < 32) && (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE))
				{
					if(event.getBlock() != null)	//If the block broke is not null (rare but it happens)
					{
						if(event.getBlock().getType() != Material.STONE && event.getBlock().getType() != Material.NETHERRACK)	//If the block is not stone or netherrack (pointless to continue with if it is - Located before switch as it helps optimize)
						{
							//System.out.println("TEST");
							switch(event.getBlock().getType())	//Get the block material
							{
								//If it is diamond, gold, or emerald
								case DIAMOND_ORE:
								case GOLD_ORE:
								case EMERALD_ORE:
									updateInformation(event.getBlock(), event.getPlayer());	//Update the player file
									break;
								default:
									break;	//If not the above, just close
							}
						}
					}
				}
			}
		}
		
		//End of onInteract - Handled the block break event based on the server version
	}
    
	/**
	* Description: updateInformation - Updates the player information in the players folder
	* @param Block as block (The block broken), Player as player (The player information on who broke the block)
	* @return None - Void
	* @throws None
	*/
	public void updateInformation (Block block, Player player)
	{
		//Beginning of updateInformation - Updates the player file information and pings staff about the change
		
		try
		{
			File playerFile = new File(folder, "/Players/" + player.getName() + ".txt");	//Set up the players file for opening
			if(!playerFile.exists())	//If the player file does not already exist
			{	
				playerFile.createNewFile();									//Create the players file
				FileWriter writer = new FileWriter(playerFile);				//Open the file for writing
				writer.write("Time: " + dtf.format(LocalTime.now()));			//Write the players time
				//writer.write("Time: " + player.getPlayerTime());			//Write the players time
				writer.write("\nVeins: 1");									//Write the veins as 1 (first vein)
				writer.write("\nOriginal Time: " + dtf.format(LocalTime.now()));	//Write the original time of mining (useful later)
				//writer.write("\nOriginal Time: " + player.getPlayerTime());
				writer.close();												//Close the file
				pingStaffOnline(block, player, 1, null);					//Ping online staff
			}
			else	//If file already exists
			{
				Scanner reader = new Scanner(playerFile);	//Open the players file for reading
				String time = reader.nextLine();			//Read the time as the first line
				String veins = reader.nextLine();			//Read the veins as the second line
				String originalTime = reader.nextLine();	//Read the original time as the third line
				reader.close();								//Close the reading
				
				time = time.substring(6,time.length());		//Convert the time
				veins = veins.substring(7,veins.length());	//Convert the veins
				originalTime = originalTime.substring(15,originalTime.length());	//Convert the original time
				
				if(getSecondsBetween(time, dtf.format(LocalTime.now()).toString()) > 10)
				{
					//If < 15 minutes has passed since the first ore and the newest ore, combine. If more than 15 minutes has passed, start the clock over
					if(getSecondsBetween(time, dtf.format(LocalTime.now()).toString()) < 900)
					{
						FileWriter writer = new FileWriter(playerFile);				//Open the file for writing
						writer.write("Time: " + dtf.format(LocalTime.now()));
						//writer.write("Time: " + player.getPlayerTime());			//Write the time
						writer.write("\nVeins: " + (Integer.parseInt(veins) + 1));	//Write the veins
						writer.write("\nOriginal Time: " + originalTime);
						//writer.write("\nOriginal Time: " + player.getPlayerTime());			//Write the original time back in
						writer.close();												//Close the writer
						pingStaffOnline(block, player, Integer.parseInt(veins) + 1, getSecondsBetween(originalTime, dtf.format(LocalTime.now()).toString()));	//Ping online staff
						//pingStaffOnline(block, player, Integer.parseInt(veins) + 1, (player.getPlayerTime()/20)-(Long.parseLong(originalTime)/20));	//Ping online staff
					}
					else
					{
						FileWriter writer = new FileWriter(playerFile);				//Open the file for writing
						writer.write("Time: " + dtf.format(LocalTime.now()));
						//writer.write("Time: " + player.getPlayerTime());			//Write the time
						writer.write("\nVeins: 1");	//Write the veins
						writer.write("\nOriginal Time: " + dtf.format(LocalTime.now())); //Write the new time as original
						writer.close();												//Close the writer
						pingStaffOnline(block, player, 1, 0l);	//Ping online staff
//						//pingStaffOnline(block, player, Integer.parseInt(veins) + 1, (player.getPlayerTime()/20)-(Long.parseLong(originalTime)/20));	//Ping online staff
					}
					//if(player.getWorld().toString().contains("nether") && (player.getPlayerTime()/20) < ((Long.parseLong(time)/20) - 1000))
					
				}
				else	
				{
					//Time was not violated, continue (Left here in case a database is used later)
				}
//				if((player.getPlayerTime()/20) > ((Long.parseLong(time)/20) + 10 ) || (player.getWorld().toString().contains("nether") && (player.getPlayerTime()/20) < ((Long.parseLong(time)/20) - 1000)) || 
//						 (player.getWorld().toString().contains("nether") && (player.getPlayerTime()/20) < ((Long.parseLong(time)/20) + 1000)))	
//					//Check to see if the player violates a time constraint (/20 for TPS) (Checking if 10 seconds has passed for next vein)
//				{
//					
//				}
				
			}
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "Error handing files when opening/creating" + e);	//Log error
		}
		
		//End of updateInformation - Updated the player file information and pinged staff about the change
	}
	
	/**
	* Description: pingStaffOnline - Sends a message to the staff online about the block broken and player who broke
	* @param Block as block (The block broken), Player as player (The player information on who broke the block), integer as oreCount (The total ore mined in x time), Long as timeSeconds (The time the x ore was mined in)
	* @return None - Void
	* @throws None
	*/
	public void pingStaffOnline( Block block, Player player, int oreCount, Long timeSeconds)
	{		
		//Beginning  of pingStaffOnline - Pings staff online about the block break
		
		Object[] players = Bukkit.getOnlinePlayers().toArray();	//Load all players into an array

		if(noPermSystem == "false")	//If a permissions system is in use
		{
			for(int i = 0; i < players.length; i++)	//Check all online players for being staff
			{
				if(Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)) != null)	//If the player is not null
				{
					if(Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).hasPermission("mt.staff"))	//If the player is staff
					{
						//Create a text message
						TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&',"&a" + player.getName() + " &fhas mined a &b" + block.getType() + " &fblock. Click to &ateleport"));
						//Create a clickable event for the message
						msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mt tp " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ() + " " + block.getWorld().getName()));
						//Send the message to the staff member
						Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).spigot().sendMessage(msg);
						if(!soundOff)	//If the sound is on
						{
							//Ping the staff with sound
							Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).playSound(Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
						}
						if(timeSeconds != null)	//If the time is not null
						{
							if((Integer.parseInt(Long.toString(timeSeconds))/60) > 20 && (oreCount < 20))	//Algorithm for seeing if we should reset the player file (Ore count is less than 20 and time is greater than 20
							{
								try
								{
									File playerFile = new File(folder, "/Players/" + player.getName() + ".txt");	//Open player file
									FileWriter writer = new FileWriter(playerFile);				//Open file for writing
									writer.write("Time: " + dtf.format(LocalTime.now()));			//Write the time
									writer.write("\nVeins: 1");									//Write first vien
									writer.write("\nOriginal Time: " + dtf.format(LocalTime.now()));	//Write time as original time
									writer.close();			//Close the file
								}
								catch (Exception e)	//If any error
								{
									logger.log(Level.WARNING, "Error handing files when clearing" + e);	//Log the error to console
								}
							}
							else if(((Integer.parseInt(Long.toString(timeSeconds))/60)/(oreCount)) < 2)	//If the time to ore count is less than 2 minutes (Ex 5 minutes for 2 ores is 2.5 and doesn't violate with 2.5 minutes per vien)
							{
								//Ping staff with notice
								Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).sendMessage(ChatColor.translateAlternateColorCodes('&',"&cNotice: &b" + player.getName() + " &fhas mined &6" + oreCount + " veins &fin &c" + (Integer.parseInt(Long.toString(timeSeconds))/60) + " minutes!"));
							}
						}
					}
				}
			}
		}
		else	//If not using a perms system
		{
			for(int i = 0; i < staffArray.length; i++)	//Loop all staff stored
			{
				if(Bukkit.getPlayer(staffArray[i]) != null)	//If the staff player is online
				{
					//Create a text message
					TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&',"&a" + player.getName() + " &fhas mined a &b" + block.getType() + " &fblock. Click to &ateleport"));
					//Create a clickable event for the message
					msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mt tp " + staffArray[i] + " " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ()  + " " + block.getWorld().getName()));
					//Send the message to the staff member
					Bukkit.getPlayer(staffArray[i]).spigot().sendMessage(msg);
					if(!soundOff)	//If the sound is on
					{
						//Ping the staff with sound
						Bukkit.getPlayer(staffArray[i]).playSound(Bukkit.getPlayer(staffArray[i]).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);	
					}
					if(timeSeconds != null)	//If the time is not null
					{
						if((Integer.parseInt(Long.toString(timeSeconds))/60) > 20 && (oreCount < 20))	//Algorithm for seeing if we should reset the player file (Ore count is less than 20 and time is greater than 20
						{
							try
							{
								File playerFile = new File(folder, "/Players/" + player.getName() + ".txt");	//Open player file
								FileWriter writer = new FileWriter(playerFile);				//Open file for writing
								writer.write("Time: " + dtf.format(LocalTime.now()));			//Write the time
								writer.write("\nVeins: 1");									//Write first vien
								writer.write("\nOriginal Time: " + dtf.format(LocalTime.now()));	//Write time as original time
								writer.close();			//Close the file
							}
							catch (Exception e)	//If any error
							{
								logger.log(Level.WARNING, "MineTracker: Error handing files when clearing" + e);	//Log the error to console
							}
						}
						else if(((Integer.parseInt(Long.toString(timeSeconds))/60)/(oreCount)) < 2)	//If the time to ore count is less than 2 minutes (Ex 5 minutes for 2 ores is 2.5 and doesn't violate with 2.5 minutes per vein)
						{
							//Ping staff with notice
							Bukkit.getPlayer(staffArray[i]).sendMessage(ChatColor.translateAlternateColorCodes('&',"&cNotice: &b" + player.getName() + " &fhas mined &6" + oreCount + " veins &fin &c" + (Integer.parseInt(Long.toString(timeSeconds))/60) + " minutes!"));
						}
					}
				}
			}
		}
		
		//End  of pingStaffOnline - Pinged staff online about the block break
	}
	
	public long getSecondsBetween(String originalTime, String time)
	{
		return Duration.between(LocalTime.parse(originalTime, dtf), LocalTime.parse(time, dtf)).abs().getSeconds();
	}
}
