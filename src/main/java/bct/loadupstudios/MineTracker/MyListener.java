package bct.loadupstudios.MineTracker;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;


public class MyListener implements Listener
{
	boolean eventRunning = false; //This is to cancel the event running multiple times
	long playerTime = 0;
	private FileConfiguration config;
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	String blockName = "";
	String pingString = "";
	String folder = "";
	String noPermSystem;
	String[] staffArray;
	boolean soundOff = false;
	String bVersion = "";
	
	
	MyListener(FileConfiguration conf, String folder)
	{
		config = conf;
		this.folder = folder;
		noPermSystem = config.getString("nopermissionsystem");
		if(noPermSystem == "true")
		{
			staffArray = config.getString("staffusernames").split(",");
		}
		try
		{
			bVersion = Bukkit.getVersion().substring(Bukkit.getVersion().length()-7,Bukkit.getVersion().length()-1);
			bVersion = bVersion.substring(0,4);
		}
		catch (Exception e)
		{
			bVersion = "0";
		}
	}
	
	MyListener()
	{
		
	}
	
	public void reload(FileConfiguration conf)
	{
		config = conf;
		if(noPermSystem == "true")
		{
			staffArray = config.getString("staffusernames").split(",");
		}
	}
	/*@EventHandler
    public void onPlayerClick(EntityDamageByEntityEvent event)
    {
		System.out.println("Interact!");
    }*/
	
	@EventHandler
    public void onInteract(BlockBreakEvent event)
    {
		if(!event.getPlayer().hasPermission("mt.bypass"))
		{
			switch(bVersion)
			{
			case "1.17":
				if((event.getPlayer().getLocation().getY() < 32) && (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE))
				{
					if(event.getBlock() != null)
					{
						if(event.getBlock().getType() != Material.STONE && event.getBlock().getType() != Material.NETHERRACK)
						{
							//System.out.println("TEST");
							switch(event.getBlock().getBlockData().getMaterial())
							{
								case DIAMOND_ORE:
								case DEEPSLATE_DIAMOND_ORE:
								case GOLD_ORE:
								case DEEPSLATE_GOLD_ORE:
								case EMERALD_ORE:
								case DEEPSLATE_EMERALD_ORE:
								case ANCIENT_DEBRIS:
									updateInformation(event.getBlock(), event.getPlayer());
									break;
								default:
									break;
							}
						}
					}
				}
				break;
			case "1.16":
				if((event.getPlayer().getLocation().getY() < 32) && (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE))
				{
					if(event.getBlock() != null)
					{
						if(event.getBlock().getType() != Material.STONE && event.getBlock().getType() != Material.NETHERRACK)
						{
							//System.out.println("TEST");
							switch(event.getBlock().getBlockData().getMaterial())
							{
								case DIAMOND_ORE:
								case GOLD_ORE:
								case EMERALD_ORE:
								case ANCIENT_DEBRIS:
									updateInformation(event.getBlock(), event.getPlayer());
									break;
								default:
									break;
							}
						}
					}
				}
				break;
			default:
				if((event.getPlayer().getLocation().getY() < 32) && (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE))
				{
					if(event.getBlock() != null)
					{
						if(event.getBlock().getType() != Material.STONE && event.getBlock().getType() != Material.NETHERRACK)
						{
							//System.out.println("TEST");
							switch(event.getBlock().getType())
							{
								case DIAMOND_ORE:
								case GOLD_ORE:
								case EMERALD_ORE:
									updateInformation(event.getBlock(), event.getPlayer());
									break;
								default:
									break;
							}
						}
					}
				}
				break;
			}
		}
    }
	
	public void updateInformation ( Block block, Player player)
	{
		try
		{
			File playerFile = new File(folder, "/Players/" + player.getName() + ".txt");
			if(!playerFile.exists())
			{
				playerFile.createNewFile();
				FileWriter writer = new FileWriter(playerFile);
				writer.write("Time: " + player.getPlayerTime());
				writer.write("\nVeins: 1");
				writer.write("\nOriginal Time: " + player.getPlayerTime());
				writer.close();
				pingStaffOnline(block, player, 1, null);
			}
			else
			{
				Scanner reader = new Scanner(playerFile);
				String time = reader.nextLine();
				String veins = reader.nextLine();
				//System.out.println("Before Read");
				String originalTime = reader.nextLine();
				//System.out.println("After Read");
				reader.close();
				time = time.substring(6,time.length());
				veins = veins.substring(7,veins.length());
				originalTime = originalTime.substring(15,originalTime.length());
				if((player.getPlayerTime()/20) > ((Long.parseLong(time)/20) + 10 ))
				{
					FileWriter writer = new FileWriter(playerFile);
					writer.write("Time: " + player.getPlayerTime());
					writer.write("\nVeins: " + (Integer.parseInt(veins) + 1));
					writer.write("\nOriginal Time: " + originalTime);
					writer.close();
					pingStaffOnline(block, player, Integer.parseInt(veins) + 1, (player.getPlayerTime()/20)-(Long.parseLong(originalTime)/20));
				}
				else
				{
					//System.out.println("Player didnt surpass 10 seconds");
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("MineTracker: Error handing files when opening/creating" + e);
		}
	}
	
	public void pingStaffOnline( Block block, Player player, int oreCount, Long timeSeconds)
	{		
	
		Object[] players = Bukkit.getOnlinePlayers().toArray();
		
		//System.out.println("Ores Mined: " + oreCount + " Time elapsed: " + timeSeconds);
		//System.out.println("Time elapsed(C1): " + Long.toString(timeSeconds));
		//System.out.println("Time elapsed(C2): " + Integer.parseInt(Long.toString(timeSeconds)));
		//System.out.println("Time elapsed(C3): " + (Integer.parseInt(Long.toString(timeSeconds))/60));
		if(noPermSystem == "false")
		{
			for(int i = 0; i < players.length; i++)
			{
				if(Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)) != null)
				{
					if(Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).hasPermission("mt.staff"))
					{
						TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&',"&a" + player.getName() + " &fhas mined a &b" + block.getType() + " &fblock. Click to &ateleport"));
						msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mt tp " + players[i].toString().substring(17, players[i].toString().length()-1) + " " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ() + " " + block.getWorld().getName()));
						Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).spigot().sendMessage(msg);
						if(!soundOff)
						{
							Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).playSound(Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
						}
						if(timeSeconds != null)
						{
							if((Integer.parseInt(Long.toString(timeSeconds))/60) > 20 && (oreCount < 20))
							{
								try
								{
									File playerFile = new File(folder, "/Players/" + player.getName() + ".txt");
									FileWriter writer = new FileWriter(playerFile);
									writer.write("Time: " + player.getPlayerTime());
									writer.write("\nVeins: 1");
									writer.write("\nOriginal Time: " + player.getPlayerTime());
									writer.close();
								}
								catch (Exception e)
								{
									System.out.println("MineTracker: Error handing files when clearing" + e);
								}
							}
							else if(((Integer.parseInt(Long.toString(timeSeconds))/60)/(oreCount)) < 2)
							{
								Bukkit.getPlayer(players[i].toString().substring(17, players[i].toString().length()-1)).sendMessage(ChatColor.translateAlternateColorCodes('&',"&cNotice: &b" + player.getName() + " &fhas mined &6" + oreCount + " veins &fin &c" + (Integer.parseInt(Long.toString(timeSeconds))/60) + " minutes!"));
							}
						}
					}
				}
			}
		}
		else
		{
			for(int i = 0; i < staffArray.length; i++)
			{
				if(Bukkit.getPlayer(staffArray[i]) != null)
				{
					TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&',"&a" + player.getName() + " &fhas mined a &b" + block.getType() + " &fblock. Click to &ateleport"));
					msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mt tp " + staffArray[i] + " " + block.getLocation().getX() + " " + block.getLocation().getY() + " " + block.getLocation().getZ()  + " " + block.getWorld().getName()));
					Bukkit.getPlayer(staffArray[i]).spigot().sendMessage(msg);
					if(!soundOff)
					{
						Bukkit.getPlayer(staffArray[i]).playSound(Bukkit.getPlayer(staffArray[i]).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
					}
					if(timeSeconds != null)
					{
						if((Integer.parseInt(Long.toString(timeSeconds))/60) > 20 && (oreCount < 20))
						{
							try
							{
								File playerFile = new File(folder, "/Players/" + player.getName() + ".txt");
								FileWriter writer = new FileWriter(playerFile);
								writer.write("Time: " + player.getPlayerTime());
								writer.write("\nVeins: 1");
								writer.write("\nOriginal Time: " + player.getPlayerTime());
								writer.close();
							}
							catch (Exception e)
							{
								System.out.println("MineTracker: Error handing files when clearing" + e);
							}
						}
						else if(((Integer.parseInt(Long.toString(timeSeconds))/60)/(oreCount)) < 2)
						{
							Bukkit.getPlayer(staffArray[i]).sendMessage(ChatColor.translateAlternateColorCodes('&',"&cNotice: &b" + player.getName() + " &fhas mined &6" + oreCount + " veins &fin &c" + (Integer.parseInt(Long.toString(timeSeconds))/60) + " minutes!"));
						}
					}
				}
			}
		}
	}
}
