package be.goldocelot.ep;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import be.goldocelot.ep.minecraft.Cmd;
import be.goldocelot.ep.minecraft.Event;
import be.goldocelot.ep.minecraft.Save;
import be.goldocelot.ep.utils.Utils;

public class Main extends JavaPlugin{
	
	private Save save;
	public static final double POURCENT_MAX=0.2;
	public static final double POURCENT_MIN=0.05;
	public static final double AJOUT=1000./10.;
	
	@Override
	public void onEnable() {
		save = new Save(this);
		save.initFolder();
		save.initFile();
		YamlConfiguration yml= save.getNewConfiguration();
		Bukkit.getPluginManager().registerEvents(new Event(save), this);
		getCommand("entreprise").setExecutor(new Cmd(save));
		getCommand("contrat").setExecutor(new Cmd(save));
		getCommand("batiment").setExecutor(new Cmd(save));
		getCommand("ep").setExecutor(new Cmd(save));
		
		Utils.taxation(this, yml, save);
	}
	
	/*
		- Commande pour ce débloquer d'un bâtiment
		- Bar de fun ???
	 */
}
