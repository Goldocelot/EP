package be.goldocelot.ep.minecraft;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import be.goldocelot.ep.Main;

public class Save {

	private Main main;
	private File file;
	
	public Save(Main main) {
		this.main = main;
	}
	
	public void initFolder() {
		if(!this.main.getDataFolder().exists()) this.main.getDataFolder().mkdirs();
	}
	
	public void initFile() {
		this.file = new File(this.main.getDataFolder(), "save.yml");
		
		
		if(!this.file.exists()) {
			try {
				this.file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteFile() {
		this.file = new File(this.main.getDataFolder(), "save.yml");
		
		if(this.file.exists()) {
			this.file.delete();
		}
	}
	
	public File getFile() {
		return this.file;
	}
	
	public YamlConfiguration getNewConfiguration() {
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.load(this.file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		} 
		return config;
	}
}