package be.goldocelot.ep.minecraft;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import be.goldocelot.ep.object.Batiment;
import be.goldocelot.ep.utils.Utils;

public class Event implements Listener {
	
	public final static float FOOD_NERF = 0.70f;
	public final static float SATURATION_NERF = 0.70f;
	private Save save;
	
	public Event(Save save) {
		this.save = save;
	}
	
	@EventHandler
	private void onLogin(PlayerJoinEvent e) {
		YamlConfiguration yml = save.getNewConfiguration();
		Player p = e.getPlayer();
		if(yml.contains("Notification."+p.getName())) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 30, 30);
			List<String> notif = yml.getStringList("Notification."+p.getName());
			for(String send : notif) {
				p.sendMessage(send);			
			}
			yml.set("Notification."+p.getName(), null);
			try {
				yml.save(save.getFile());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}		
	}
	
	@EventHandler
	private void onClick(PlayerInteractEvent e) {
		Action a = e.getAction();
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		if(b!=null) {
			if(b.getType().equals(Material.CAKE_BLOCK) && p.getFoodLevel()!=20 && a.equals(Action.RIGHT_CLICK_BLOCK)){
				Utils.nerf(p, 2, 0.4f);
			}
		}	
	}
	
	@EventHandler
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		YamlConfiguration yml = save.getNewConfiguration();
		if(yml.contains("Batiment")) {
			for(String batNom : yml.getConfigurationSection("Batiment.").getKeys(false)) {
				Batiment bat = Utils.genBatiment(batNom, yml);
				if(!bat.getEditeur().contains(p)) {
					Location loc1 = new Location(Bukkit.getWorld(yml.getString("Batiment."+batNom+".World")), yml.getInt("Batiment."+batNom+".Coord1.x"), 0, yml.getInt("Batiment."+batNom+".Coord1.z"));
					Location loc2 = new Location(Bukkit.getWorld(yml.getString("Batiment."+batNom+".World")), yml.getInt("Batiment."+batNom+".Coord2.x"), 0, yml.getInt("Batiment."+batNom+".Coord2.z"));
					if((b.getLocation().getBlockX()>=Math.min(loc1.getBlockX(), loc2.getBlockX()) && b.getLocation().getBlockX()<=Math.max(loc1.getBlockX(), loc2.getBlockX())) && 
							b.getLocation().getBlockZ()>=Math.min(loc1.getBlockZ(), loc2.getBlockZ()) && b.getLocation().getBlockZ()<=Math.max(loc1.getBlockZ(), loc2.getBlockZ())) {
						p.sendMessage("§7[§6EP§7]§4 Vous ne pouvez pas casser de block sur ce terrain !");
						p.playSound(p.getLocation(), Sound.ANVIL_LAND, 30, 30);
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		YamlConfiguration yml = save.getNewConfiguration();
		if(yml.contains("Batiment")) {
			for(String batNom : yml.getConfigurationSection("Batiment.").getKeys(false)) {
				Batiment bat = Utils.genBatiment(batNom, yml);
				if(!bat.getEditeur().contains(p)) {
					Location loc1 = new Location(Bukkit.getWorld(yml.getString("Batiment."+batNom+".World")), yml.getInt("Batiment."+batNom+".Coord1.x"), 0, yml.getInt("Batiment."+batNom+".Coord1.z"));
					Location loc2 = new Location(Bukkit.getWorld(yml.getString("Batiment."+batNom+".World")), yml.getInt("Batiment."+batNom+".Coord2.x"), 0, yml.getInt("Batiment."+batNom+".Coord2.z"));
					if((b.getLocation().getBlockX()>=Math.min(loc1.getBlockX(), loc2.getBlockX()) && b.getLocation().getBlockX()<=Math.max(loc1.getBlockX(), loc2.getBlockX())) && 
							b.getLocation().getBlockZ()>=Math.min(loc1.getBlockZ(), loc2.getBlockZ()) && b.getLocation().getBlockZ()<=Math.max(loc1.getBlockZ(), loc2.getBlockZ())) {
						p.sendMessage("§7[§6EP§7]§4 Vous ne pouvez pas poser de block sur ce terrain !");
						p.playSound(p.getLocation(), Sound.ANVIL_LAND, 30, 30);
						e.setCancelled(true);
					}
				}
			}
		}		
	}
	
	@EventHandler
	private void onEat(PlayerItemConsumeEvent e) {
		ItemStack item = e.getItem();
		Player p = e.getPlayer();
		switch(item.getType()) {
		case APPLE:
			Utils.nerf(p, 4, 2.4f);
			break;
		case BAKED_POTATO:
			Utils.nerf(p, 5, 6f);
			break;
		case BREAD:
			Utils.nerf(p, 5, 6f);
			break;
		case CARROT_ITEM:
			Utils.nerf(p, 3, 3.6f);
			break;
		case COOKED_CHICKEN:
			Utils.nerf(p, 6, 7.2f);
			break;
		case COOKED_FISH:
			Utils.nerf(p, 5, 6f);
			break;
		case GRILLED_PORK:
			Utils.nerf(p, 8, 12.8f);
			break;
		case COOKIE:
			Utils.nerf(p, 2, 0.4f);
			break;
		case GOLDEN_APPLE:
			Utils.nerf(p, 4, 9.6f);
			break;
		case GOLDEN_CARROT:
			Utils.nerf(p, 6, 14.4f);
			break;
		case MELON:
			Utils.nerf(p, 2, 1.2f);
			break;
		case MUSHROOM_SOUP:
			Utils.nerf(p, 6, 7.2f);
			break;
		case POISONOUS_POTATO:
			Utils.nerf(p, 2, 1.2f);
			break;
		case POTATO_ITEM:
			Utils.nerf(p, 1, 0.6f);
			break;
		case PUMPKIN_PIE:
			Utils.nerf(p, 8, 4.8f);
			break;
		case RAW_BEEF:
			Utils.nerf(p, 3, 1.8f);
			break;
		case RAW_CHICKEN:
			Utils.nerf(p, 2, 1.2f);
			break;
		case RAW_FISH:
			Utils.nerf(p, 2, 0.4f);
			break;
		case PORK:
			Utils.nerf(p, 3, 1.8f);
			break;
		case ROTTEN_FLESH:
			Utils.nerf(p, 4, 0.8f);
			break;
		case SPIDER_EYE:
			Utils.nerf(p, 2, 3.2f);
			break;
		case COOKED_BEEF:
			Utils.nerf(p, 8, 12.8f);
			break;
		default:
			break;
		}
	}
	
}
