package be.goldocelot.ep.utils;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import be.goldocelot.ep.Main;
import be.goldocelot.ep.minecraft.Event;
import be.goldocelot.ep.minecraft.Save;
import be.goldocelot.ep.object.Batiment;
import be.goldocelot.ep.object.Contrat;
import be.goldocelot.ep.object.Entreprise;
import be.goldocelot.ep.object.Gerant;

public class Utils {

	public static Entreprise genEntreprise(Player p, YamlConfiguration yml) {
		List<Batiment> batArray = new ArrayList<>();
		List<Contrat> conArray = new ArrayList<>();
		String nom = yml.getString("Gerant."+p.getName()+".Entreprise");
		Entreprise ent = new Entreprise(yml.getDouble("Entreprise."+nom+".Fond"), nom, yml.getString("Entreprise."+nom+".Type"), batArray, conArray);
		
		if(yml.isSet("Entreprise."+nom+".Batiment")) {
			for(String inList : yml.getStringList("Entreprise."+nom+".Batiment")) {
				Batiment bat = genBatiment(inList, yml);
				ent.addBatiment(bat);
			}
		}
		
		if(yml.isSet("Entreprise."+nom+".Contrat")) {
			for(String inList : yml.getConfigurationSection("Entreprise."+nom+".Contrat").getKeys(false)) {
				Contrat c = genContrat(inList, ent, yml);
				ent.addContrat(c);
			}
		}		
		
		return new Entreprise(yml.getDouble("Entreprise."+nom+".Fond"), nom, yml.getString("Entreprise."+nom+".Type"), batArray, conArray);
	}
	
	public static Gerant genGerant(Player p, YamlConfiguration yml) {
		return new Gerant(genEntreprise(p, yml), p, yml.getDouble("Gerant."+p.getName()+".Fond"));
	}
	
	public static Contrat genContrat(String id, Entreprise ent, YamlConfiguration yml) {
		return new Contrat(yml.getBoolean("Entreprise."+ent.getNom()+".Contrat."+id), id, yml.getString("Contrat."+id+".Terme"), yml.getBoolean("Contrat."+id+".Changer"), yml.getBoolean("Contrat."+id+".Accepter"));
	}
	
	public static void saveEntreprise(Entreprise ent, YamlConfiguration yml, Save save) {
		yml.set("Entreprise."+ent.getNom()+".Fond", ent.getFond());
		yml.set("Entreprise."+ent.getNom()+".Type", ent.getType());
		List<String> batName = new ArrayList<>();
		for(Batiment bat : ent.getBatiments()) {
			batName.add(bat.getNom());
		}		
		yml.set("Entreprise."+ent.getNom()+".Batiment", batName);
		for(Contrat inList : ent.getContrats()) {
			yml.set("Entreprise."+ent.getNom()+".Contrat."+inList.getId(), inList.isDemandeur());
		}
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveGerant(Gerant ger, YamlConfiguration yml, Save save) {
		yml.set("Gerant."+ger.getP().getName()+".Entreprise", ger.getEntreprise().getNom());
		yml.set("Gerant."+ger.getP().getName()+".Fond", ger.getFondPersonelle());
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveContrat(Entreprise dem, Entreprise rec, Contrat con, YamlConfiguration yml, Save save) {
		yml.set("Contrat."+con.getId()+".Terme", con.getClose());
		yml.set("Contrat."+con.getId()+".Accepter", con.isAccepter());
		yml.set("Contrat."+con.getId()+".Changer", con.isChanger());
		yml.set("Entreprise."+dem.getNom()+".Contrat."+con.getId(), true);
		yml.set("Entreprise."+rec.getNom()+".Contrat."+con.getId(), false);
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveContrat(Contrat con, YamlConfiguration yml, Save save) {
		yml.set("Contrat."+con.getId()+".Terme", con.getClose());
		yml.set("Contrat."+con.getId()+".Accepter", con.isAccepter());
		yml.set("Contrat."+con.getId()+".Changer", con.isChanger());
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeContrat(Contrat con, String nom1, String nom2, YamlConfiguration yml, Save save) {
		yml.set("Contrat."+con.getId(), null);
		yml.set("Entreprise."+nom1+".Contrat."+con.getId(), null);
		yml.set("Entreprise."+nom2+".Contrat."+con.getId(), null);
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean canEditContrat(Entreprise ent, String id, YamlConfiguration yml) {
		return (yml.getBoolean("Entreprise."+ent.getNom()+".Contrat."+id) && yml.getBoolean("Contrat."+id+".Changer")) || 
				  (!yml.getBoolean("Entreprise."+ent.getNom()+".Contrat."+id) && !yml.getBoolean("Contrat."+id+".Changer"));
	}
	
	public static void sendContratNotification(Contrat con, Entreprise ent, String notif, YamlConfiguration yml, Save save) {
		for(String entreprise : yml.getConfigurationSection("Entreprise.").getKeys(false)) {
			if(!ent.getNom().equals(entreprise)) {
				for(String id : yml.getConfigurationSection("Entreprise."+entreprise+".Contrat").getKeys(false)) {
					if(con.getId().equals(id)) {
						for(String gerant : yml.getConfigurationSection("Gerant.").getKeys(false)) {
							if(yml.getString("Gerant."+gerant+".Entreprise").equals(entreprise)) {
								@SuppressWarnings("deprecation")
								OfflinePlayer oP = Bukkit.getOfflinePlayer(gerant);
								if(oP.isOnline()) {
									Player p = (Player) oP;
									p.sendMessage(notif);
									p.playSound(p.getLocation(), Sound.ORB_PICKUP, 30, 30);
								}else {
									List<String> notifications = new ArrayList<>();
									if(yml.contains("Notification."+gerant)) {
										for(String notification : yml.getConfigurationSection("Notification."+gerant).getKeys(false)) {
											notifications.add(notification);
										}
									}								
									notifications.add(notif);
									yml.set("Notification."+gerant, notifications);
									try {
										yml.save(save.getFile());
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	public static Batiment genBatiment(String name, YamlConfiguration yml) {
		List<OfflinePlayer> editeur = new ArrayList<>();
		for(String nom : yml.getStringList("Batiment."+name+".Editeur")) {
			editeur.add(Bukkit.getPlayer(nom));
		}
		return new Batiment(yml.getInt("Batiment."+name+".Superficie"), yml.getString("Batiment."+name+".Utilité"), name, editeur);
	}
	
	public static void saveBatiment(Location location, Location location2,Batiment bat, YamlConfiguration yml, Save save) {
		yml.set("Batiment."+bat.getNom()+".Coord1.x", location.getBlockX());
		yml.set("Batiment."+bat.getNom()+".Coord2.x", location2.getBlockX());
		yml.set("Batiment."+bat.getNom()+".Coord1.z", location.getBlockZ());
		yml.set("Batiment."+bat.getNom()+".Coord2.z", location2.getBlockZ());
		yml.set("Batiment."+bat.getNom()+".World", location.getWorld().getName());
		yml.set("Batiment."+bat.getNom()+".Superficie", bat.getSuperficie());
		yml.set("Batiment."+bat.getNom()+".Utilité", bat.getUtilité());
		List<String> nomEditeur = new ArrayList<>();
		for(OfflinePlayer editeur : bat.getEditeur()) {
			nomEditeur.add(editeur.getName());
		}
		yml.set("Batiment."+bat.getNom()+".Editeur", nomEditeur);
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveBatiment(Batiment bat, String aNom, YamlConfiguration yml, Save save) {
		yml.set("Batiment."+bat.getNom()+".Coord1.x", yml.get("Batiment."+aNom+".Coord1.x"));
		yml.set("Batiment."+bat.getNom()+".Coord2.x", yml.get("Batiment."+aNom+".Coord2.x"));
		yml.set("Batiment."+bat.getNom()+".Coord1.z", yml.get("Batiment."+aNom+".Coord1.z"));
		yml.set("Batiment."+bat.getNom()+".Coord2.z", yml.get("Batiment."+aNom+".Coord2.z"));
		yml.set("Batiment."+bat.getNom()+".World", yml.get("Batiment."+aNom+".World"));
		yml.set("Batiment."+bat.getNom()+".Superficie", bat.getSuperficie());
		yml.set("Batiment."+bat.getNom()+".Utilité", bat.getUtilité());
		List<String> nomEditeur = new ArrayList<>();
		for(OfflinePlayer editeur : bat.getEditeur()) {
			nomEditeur.add(editeur.getName());
		}
		yml.set("Batiment."+bat.getNom()+".Editeur", nomEditeur);
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveBatiment(Batiment bat, YamlConfiguration yml, Save save) {
		yml.set("Batiment."+bat.getNom()+".Superficie", bat.getSuperficie());
		yml.set("Batiment."+bat.getNom()+".Utilité", bat.getUtilité());
		List<String> nomEditeur = new ArrayList<>();
		for(OfflinePlayer editeur : bat.getEditeur()) {
			nomEditeur.add(editeur.getName());
		}
		yml.set("Batiment."+bat.getNom()+".Editeur", nomEditeur);
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeBatiment(String nom,YamlConfiguration yml, Save save) {
		yml.set("Batiment."+nom, null);
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveAll(Entreprise ent, Gerant ger,YamlConfiguration yml, Save save) {
		saveEntreprise(ent, yml, save);
		saveGerant(ger, yml, save);
		try {
			yml.save(save.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void nerf(Player p,int food, float saturation) {
		p.setFoodLevel(p.getFoodLevel()-(int)(food*Event.FOOD_NERF));
		p.setSaturation(p.getSaturation()-saturation*Event.FOOD_NERF);
	}
	
	public static double arrondir(Double nombre) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		return Double.parseDouble(df.format(nombre).replace(',', '.'));
	}
	
	public static void taxation(Main main, YamlConfiguration yml, Save save) {
		for(World w : Bukkit.getWorlds()) {
			w.setTime(0l);
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
			
			@Override
			public void run() {
				YamlConfiguration yml = save.getNewConfiguration();
				Map<OfflinePlayer, Double> fond = new HashMap<>();
				Map<OfflinePlayer, Double> pPourcent = new HashMap<>();
				Map<OfflinePlayer, Double> toTaxe = new HashMap<>();				
				double capital = yml.getDouble("Capital.Fond");
				double total = 0;
				double taxe = Main.AJOUT;
				for(String name : yml.getConfigurationSection("Gerant.").getKeys(false)) {
					@SuppressWarnings("deprecation")
					OfflinePlayer p = Bukkit.getOfflinePlayer(name);
					double pFond = yml.getDouble("Gerant."+name+".Fond")+yml.getDouble("Entreprise."+yml.getString("Gerant."+name+".Entreprise")+".Fond");
					total+=pFond;
					fond.put(p, pFond);
				}
				double pourcent = 1-(capital/(total+capital));
				if(pourcent>Main.POURCENT_MAX) {
					pourcent = Main.POURCENT_MAX;
				}else if(pourcent<Main.POURCENT_MIN) {
					pourcent = Main.POURCENT_MIN;
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
}
