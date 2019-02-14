package be.goldocelot.ep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import be.goldocelot.ep.minecraft.Cmd;
import be.goldocelot.ep.minecraft.Event;
import be.goldocelot.ep.minecraft.Save;
import be.goldocelot.ep.utils.Utils;

public class Main extends JavaPlugin{
	
	private Save save;
	private final double POURCENT_MAX=0.2;
	private final double POURCENT_MIN=0.05;
	private final double AJOUT=1000./10.;
	@Override
	public void onEnable() {
		save = new Save(this);
		save.initFolder();
		save.initFile();
		Bukkit.getPluginManager().registerEvents(new Event(save), this);
		getCommand("entreprise").setExecutor(new Cmd(save));
		getCommand("contrat").setExecutor(new Cmd(save));
		getCommand("batiment").setExecutor(new Cmd(save));
		getCommand("ep").setExecutor(new Cmd(save));
		
		for(World w : Bukkit.getWorlds()) {
			w.setTime(0l);
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				YamlConfiguration yml = save.getNewConfiguration();
				Map<OfflinePlayer, Double> fond = new HashMap<>();
				Map<OfflinePlayer, Double> pPourcent = new HashMap<>();
				Map<OfflinePlayer, Double> toTaxe = new HashMap<>();				
				double capital = yml.getDouble("Capital.Fond");
				double total = 0;
				double taxe = AJOUT;
				for(String name : yml.getConfigurationSection("Gerant.").getKeys(false)) {
					@SuppressWarnings("deprecation")
					OfflinePlayer p = Bukkit.getOfflinePlayer(name);
					double pFond = yml.getDouble("Gerant."+name+".Fond")+yml.getDouble("Entreprise."+yml.getString("Gerant."+name+".Entreprise")+".Fond");
					total+=pFond;
					fond.put(p, pFond);
				}
				double pourcent = 1-(capital/(total+capital));
				if(pourcent>POURCENT_MAX) {
					pourcent = POURCENT_MAX;
				}else if(pourcent<POURCENT_MIN) {
					pourcent = POURCENT_MIN;
				}
				for(String name : yml.getConfigurationSection("Gerant.").getKeys(false)) {
					@SuppressWarnings("deprecation")
					OfflinePlayer p = Bukkit.getOfflinePlayer(name);
					toTaxe.put(p, fond.get(p)*pourcent);
					pPourcent.put(p, (1d-(fond.get(p)/total))/(fond.size()==1 ? 1 : fond.size()-1));
					fond.put(p, fond.get(p)-toTaxe.get(p));
					taxe+=toTaxe.get(p);
				}
				for(String name : yml.getConfigurationSection("Gerant.").getKeys(false)) {
					@SuppressWarnings("deprecation")
					OfflinePlayer p = Bukkit.getOfflinePlayer(name);
					fond.put(p, Utils.arrondir(fond.get(p)+taxe*pPourcent.get(p)));
					double valeur = Utils.arrondir(taxe*pPourcent.get(p)-toTaxe.get(p));
					if(yml.getDouble("Entreprise."+yml.getString("Gerant."+name+".Entreprise")+".Fond")<-fond.get(p)) {
						fond.put(p, fond.get(p)-yml.getDouble("Entreprise."+yml.getString("Gerant."+name+".Entreprise")+".Fond"));
						yml.set("Entreprise."+yml.getString("Gerant."+name+".Entreprise")+".Fond", 0);
						if(yml.getDouble("Gerant."+name+".Fond")<-fond.get(p)) {
							yml.set("Gerant."+name+".Fond", 0);
						}else {
							yml.set("Gerant."+name+".Fond", fond.get(p));
						}
					}else {
						yml.set("Entreprise."+yml.getString("Gerant."+name+".Entreprise")+".Fond", fond.get(p)-yml.getDouble("Gerant."+name+".Fond"));
					}
					if(p.isOnline()) {
						Player player = (Player) p;
						player.sendMessage("§7[§6EP§7]§a Les taxes sont arrivé vos fonds on été modifié de §r"+valeur+" §a!");
					}else {
						List<String> notifications = new ArrayList<>();
						if(yml.contains("Notification."+name)) {
							for(String notification : yml.getStringList("Notification."+name)) {
								notifications.add(notification);
							}
						}								
						notifications.add("§7[§6EP§7]§a Les taxes sont arrivé vos fonds on été modifié de §r"+valeur+" §a!");
						yml.set("Notification."+name, notifications);
						try {
							yml.save(save.getFile());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, 1728000L/10L, 1728000L/10L);
	}
	
	/*
		- Commande pour ce débloquer d'un bâtiment
		- Bar de fun ???
	 */
}
