package be.goldocelot.ep.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import be.goldocelot.ep.object.Batiment;
import be.goldocelot.ep.object.Contrat;
import be.goldocelot.ep.object.Entreprise;
import be.goldocelot.ep.object.Gerant;
import be.goldocelot.ep.utils.FondNecessaire;
import be.goldocelot.ep.utils.Utils;

public class Cmd implements CommandExecutor, FondNecessaire{
	
	private Save save;
	private YamlConfiguration yml;
	private Player p;
	private Map<Player, Boolean> edit = new HashMap<>();
	private Map<Player, String> name = new HashMap<>();
	private Map<Player, Location> b1 = new HashMap<>();
	private Map<Player, Location> b2 = new HashMap<>();
	private Map<Player, Boolean> validate = new HashMap<>();
	private Map<Player, Integer> superficie = new HashMap<>();
	
	public Cmd(Save save) {
		this.save = save;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		this.yml = save.getNewConfiguration();
		if(sender instanceof Player) {
			this.p = (Player) sender;
			switch (label) {
//***************************************************************************************************************************************************************
			case "entreprise":
				if(args.length<1) {
					p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/entreprise help§4\" pour obtenir la liste des commandes liès aux entreprises.");
					return false;
				}
				switch (args[0]) {
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "create":
					if(args.length>=4) {
						String nom = args[1];
						if(!yml.isSet("Entreprise."+nom)) {
								if(!yml.isSet("Gerant."+p.getName())) {
									double fond = 0;
									try {
										fond = Double.parseDouble(args[2]);
									}catch(NumberFormatException ex) {
										p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise create §7[§aNom§7] §7[§aFond (max "+FOND_DE_DEPART+")§7] §7[§aDescription de l'entreprise§7]");
										return false;
									}
									if(fond<=FOND_DE_DEPART) {
										String type = "";
										for(int i = 3; i<args.length;i++) {
											type+=args[i]+" ";
										}
										Entreprise ent = new Entreprise(fond, nom, type, null, null);
										Gerant ger = new Gerant(ent, p, FOND_DE_DEPART-fond);
										Utils.saveAll(ent, ger, yml, save);
										p.sendMessage("§7[§6EP§7]§a Votre entreprise §r"+nom+"§a a bien été créée");
									}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise create §7[§aNom§7] §7[§aFond (max "+FOND_DE_DEPART+")§7] §7[§aDescription de l'entreprise§7]");						
							}else p.sendMessage("§7[§6EP§7]§4 Vous avez déja une entreprise !");
						}else p.sendMessage("§7[§6EP§7]§4 Ce nom est déjà prit !");
					}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise create §7[§aNom§7] §7[§aFond (max "+FOND_DE_DEPART+")§7] §7[§aDescription de l'entreprise§7]");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "rename":
					if(yml.isSet("Gerant."+sender.getName())) {
						if(args.length == 2) {
							Entreprise ent = Utils.genEntreprise(p, yml);
							Gerant ger = Utils.genGerant(p, yml);
							if(ent.getFond()>=PRIX_RENAME_ENT) {
								ent.adjusteMoney(-PRIX_RENAME_ENT);
								yml.set("Entreprise."+ent.getNom(), null);
								ent.setNom(args[1]);
								ger.setEntreprise(ent);
								Utils.saveAll(ent, ger, yml, save);
								p.sendMessage("§7[§6EP§7]§a Vous avez bien changé le nom de votre entreprise par §r"+args[1]+"§a.");
							}else p.sendMessage("§7[§6EP§7]§4 Il vous faut au minimum "+PRIX_RENAME_ENT+"$ de fond dans l'entreprise pour la renommer !");							
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise rename §7[§aNouveau Nom§7]");
					}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "changeType":
					if(yml.isSet("Gerant."+sender.getName())) {
						if(args.length >= 2) {
							Entreprise ent = Utils.genEntreprise(p, yml);
							if(ent.getFond()>=PRIX_CHANGEMENT_DE_TYPE_ENT) {
								ent.adjusteMoney(-PRIX_CHANGEMENT_DE_TYPE_ENT);
								String type = "";
								for(int i = 1; i<args.length;i++) {
									type+=args[i]+" ";
								}
								ent.setType(type);
								Utils.saveEntreprise(ent, yml, save);
								p.sendMessage("§7[§6EP§7]§a Vous avez bien changé le type de votre entreprise.");
							}else p.sendMessage("§7[§6EP§7]§4 Il vous faut au minimum "+PRIX_CHANGEMENT_DE_TYPE_ENT+"$ de fond dans l'entreprise pour changer son type !");							
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise changeType §7[§aNouvelle description de l'entreprise§7]");
					}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "sendMoney":
					if(yml.isSet("Gerant."+sender.getName())) {
						if(args.length == 2) {
							double amount = 0;
							Entreprise ent = Utils.genEntreprise(p, yml);
							Gerant ger = Utils.genGerant(p, yml);
							try {
								amount = Double.parseDouble(args[1]);
							}catch(NumberFormatException ex) {
								p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise sendMoney §7[§aMontant§7]");
								return false;
							}
							if(amount>0) {
								if(ger.getFondPersonelle()>=TAXE_ENVOIE_ARGENT*amount) {
									ger.adjusteMoney(-TAXE_ENVOIE_ARGENT*amount);
									ent.adjusteMoney(amount);
									yml.set("Capital.Fond", yml.getDouble("Capital.Argent")+TAXE_ENVOIE_ARGENT*amount-amount);
									Utils.saveAll(ent, ger, yml, save);
									p.sendMessage("§7[§6EP§7]§a Vous avez bien envoyé §r"+amount+"$§a à votre entreprise cela vous coûte §r"+amount*TAXE_ENVOIE_ARGENT+"$. (taxe)");
								}else p.sendMessage("§7[§6EP§7]§4 Il vous faut au minimum §r"+amount*TAXE_ENVOIE_ARGENT+"$§4 pour envoyer §r"+amount+"$§4 à votre entreprise ! (taxe)");			
							}else p.sendMessage("§7[§6EP§7]§4 Le montant doit être positif !");								
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise sendMoney §7[§aMontant§7]");
					}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "takeMoney":
					if(yml.isSet("Gerant."+sender.getName())) {
						if(args.length == 2) {
							double amount = 0;
							Entreprise ent = Utils.genEntreprise(p, yml);
							Gerant ger = Utils.genGerant(p, yml);
							try {
								amount = Double.parseDouble(args[1]);
							}catch(NumberFormatException ex) {
								p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise sendMoney §7[§aMontant§7]");
								return false;
							}
							if(amount>0) {
								if(ent.getFond()>=TAXE_ENVOIE_ARGENT*amount) {
									ent.adjusteMoney(-TAXE_ENVOIE_ARGENT*amount);
									ger.adjusteMoney(amount);
									yml.set("Capital.Fond", yml.getDouble("Capital.Argent")+TAXE_ENVOIE_ARGENT*amount-amount);
									Utils.saveAll(ent, ger, yml, save);
									p.sendMessage("§7[§6EP§7]§a Vous avez bien reçu §r"+amount+"$§a de votre entreprise cela lui coûte §r"+amount*TAXE_ENVOIE_ARGENT+"$§4. (taxe)");
								}else p.sendMessage("§7[§6EP§7]§4 Il vous faut au minimum §r"+amount*TAXE_ENVOIE_ARGENT+"$§4 dans votre entreprise pour reçevoir §r"+amount+"$§4 de votre entreprise ! (taxe)");			
							} else p.sendMessage("§7[§6EP§7]§4 Le montant doit être positif !");							
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise takeMoney §7[§aMontant§7]");
					}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "info":
					if(yml.isSet("Gerant."+sender.getName())) {
						if(args.length == 1) {
							Entreprise ent = Utils.genEntreprise(p, yml);
							p.sendMessage("§7[§6EP§7]§a Information de l'entreprise:\n§r"+ent);
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/entreprise info");
					}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "help":
					p.sendMessage("§7[§6EP§7]§a Voici la liste des commandes lièes aux entreprises :");
					p.sendMessage("§7[§6EP§7]§a /entreprise create §7[§aNom§7] §7[§aFond (max "+FOND_DE_DEPART+")§7] §7[§aDescription de l'entreprise§7] §r: permet de créer une entreprise.");
					p.sendMessage("§7[§6EP§7]§a /entreprise rename §7[§aNouveau Nom§7] §r: permet de renommer votre entreprise. (§a"+PRIX_RENAME_ENT+"§r$)");
					p.sendMessage("§7[§6EP§7]§a /entreprise changeType §7[§aNouvelle description de l'entreprise§7] §r: permet de changer la description de votre entreprise. (§a"+PRIX_CHANGEMENT_DE_TYPE_ENT+"§r$)");
					p.sendMessage("§7[§6EP§7]§a /entreprise sendMoney §7[§aMontant§7] §r: permet d'envoyer de l'argent à votre entreprise.");
					p.sendMessage("§7[§6EP§7]§a /entreprise takeMoney §7[§aMontant§7] §r: permet de prendre de l'argent à votre entreprise.");
					p.sendMessage("§7[§6EP§7]§a /entreprise info §r: permet de voir les informations liè à votre entreprise.");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------					
				default:
					p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/entreprise help§4\" pour obtenir la liste des commandes liès aux entreprises.");
					break;
				}
				break;
//***************************************************************************************************************************************************************
			case "batiment":
				if(yml.isSet("Gerant."+p.getName())) {
					if(args.length<1) {
						p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/batiment help§4\" pour obtenir la liste des commandes liès aux batiments.");
						return false;
					}
					switch(args[0]) {
//---------------------------------------------------------------------------------------------------------------------------------------------------------------					
					case "bureau":
						if(args.length==1) {
							if(!edit.containsKey(p)) {
								Entreprise ent = Utils.genEntreprise(p, yml);
								for(Batiment inList : ent.getBatiments()) {
									if(inList.getNom().equals("Bureau"+ent.getNom())) {
										p.sendMessage("§7[§6EP§7]§4 Votre entreprise dispose déjà d'un bureau !");
										return false;
									}
								}
								edit.put(p, true);
								name.put(p, "Bureau"+ent.getNom());
								p.sendMessage("§7[§6EP§7]§a Création de votre bureau enclanché, §r/batiment b1§a et §r/batiment b2§a pour définir les coins de votre parcelle !");
							}else p.sendMessage("§7[§6EP§7]§4 Veuillez terminer la création de votre batiment actuel !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment bureau");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "b1":
						if(args.length==1) {
							if(edit.containsKey(p) && edit.get(p)) {
								b1.put(p, p.getLocation());
								p.sendMessage("§7[§6EP§7]§a Définition du premier coin réussie !");
							}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de création de batiment en cours !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment b1");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "b2":
						if(args.length==1) {
							if(edit.containsKey(p) && edit.get(p)) {
								if(b1.containsKey(p) && b1.get(p)!=null) {
									b2.put(p, p.getLocation());
									validate.put(p, true);

									int cote1 = 1+Math.max(b1.get(p).getBlockX(), b2.get(p).getBlockX())-Math.min(b1.get(p).getBlockX(), b2.get(p).getBlockX());
									int cote2 = 1+Math.max(b1.get(p).getBlockZ(), b2.get(p).getBlockZ())-Math.min(b1.get(p).getBlockZ(), b2.get(p).getBlockZ());
									int air = cote1*cote2;
									superficie.put(p,air);
									p.sendMessage("§7[§6EP§7]§a Définition de votre batiment terminée !");
									p.sendMessage("§7[§6EP§7]§a Celui-ci à une aire de §r"+air+"§am² le créer vous coûtera §r"+air*Batiment.VALEUR+"§a$ !");
									p.sendMessage("§7[§6EP§7]§a Si ça vous convient §r/batiment validate §7[§aUtilité§7]§a sinon §r/batiment cancel§a !");
								}else p.sendMessage("§7[§6EP§7]§4 Veuillez définir le premier coin avant celui-ci !");
							}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de création de batiment en cours !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment b2");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "validate":
						if(args.length>=2) {
							if(validate.containsKey(p) && validate.get(p)) {
								String use = "";
								for(int i = 1 ; i<args.length ;i++) {
									use+=args[i]+" ";
								}
								Entreprise ent = Utils.genEntreprise(p, yml);
								List<OfflinePlayer> editeur = new ArrayList<>();
								editeur.add(p);
								Batiment bat = new Batiment(superficie.get(p), use, name.get(p), editeur);
								if(ent.getFond()>=bat.getValeur()) {
									ent.addBatiment(bat);
									ent.adjusteMoney(-bat.getValeur());
									yml.set("Capital.Fond", yml.getDouble("Capital.Fond")+bat.getValeur());
									Utils.saveEntreprise(ent, yml, save);
									Utils.saveBatiment(b1.get(p), b2.get(p), bat, yml, save);
									b1.remove(p);
									b2.remove(p);
									edit.remove(p);
									validate.remove(p);
									name.remove(p);
									superficie.remove(p);
									p.sendMessage("§7[§6EP§7]§a Création de votre batiment terminée !");
								}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas les fonds nécessaires !");
							}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de batiment à valider !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment validate §7[§aUtilité§7]");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "cancel":
						if(args.length==1) {
							if(validate.containsKey(p) && validate.get(p)) {
								validate.remove(p);
								name.remove(p);
								superficie.remove(p);
								p.sendMessage("§7[§6EP§7]§a Création de votre batiment annulée !");
							}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de batiment à valider !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment cancel");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "create":
						if(args.length==2) {
							if(!edit.containsKey(p)) {
								Entreprise ent = Utils.genEntreprise(p, yml);
								String nom = args[1]+ent.getNom();
								for(Batiment inList : ent.getBatiments()) {
									if(inList.getNom().equals(nom)) {
										p.sendMessage("§7[§6EP§7]§4 Votre entreprise dispose déjà d'un batiment avec son nom !");
										return false;
									}
								}
								edit.put(p, true);
								name.put(p, nom);
								p.sendMessage("§7[§6EP§7]§a Création de votre batiment enclanché, §r/batiment b1§a et §r/batiment b2§a pour définir les coins de votre parcelle !");
							}else p.sendMessage("§7[§6EP§7]§4 Veuillez terminer la création de votre batiment actuel !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment create §7[§aNom du batiment§7]");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "delete":
						if(args.length==2) {
							Entreprise ent = Utils.genEntreprise(p, yml);
							String nom = args[1]+ent.getNom();
							for(Batiment inList : ent.getBatiments()) {
								if(inList.getNom().equals(nom)) {
									ent.removeBatiment(inList);
									Utils.saveEntreprise(ent, yml, save);
									Utils.removeBatiment(nom, yml, save);
									p.sendMessage("§7[§6EP§7]§a Vous avez bien supprimer ce batiment !");
									return false;
								}
							}
							p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de batiment qui porte ce nom !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment delete §7[§aNom du batiment§7]");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "rename":
						if(args.length==3) {
							Entreprise ent = Utils.genEntreprise(p, yml);
							String aNom = args[1]+ent.getNom();
							String nNom = args[2]+ent.getNom();
							for(Batiment inList : ent.getBatiments()) {
								if(inList.getNom().equals(aNom)) {
									if(ent.getFond()>=PRIX_RENAME_BAT) {
										ent.adjusteMoney(-PRIX_RENAME_BAT);
										inList.setNom(nNom);
										Utils.saveEntreprise(ent, yml, save);
										Utils.saveBatiment(inList, aNom, yml, save);
										Utils.removeBatiment(aNom, yml, save);
										
										p.sendMessage("§7[§6EP§7]§a Vous avez bien renommé ce batiment !");
										return false;
									}else p.sendMessage("§7[§6EP§7]§4 Il vous faut au minimum "+PRIX_RENAME_BAT+"$ de fond dans l'entreprise pour renommer ce batiment !");		
								}
							}
							p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de batiment qui porte ce nom !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment rename §7[§aAncien nom du batiment§7] §7[§aNouveau nom du batiment§7]");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "changeUse":
						if(args.length>=3) {
							Entreprise ent = Utils.genEntreprise(p, yml);
							String nom = args[1]+ent.getNom();
							for(Batiment inList : ent.getBatiments()) {
								if(inList.getNom().equals(nom)) {
									if(ent.getFond()>=PRIX_CHANGEMENT_DE_TYPE_BAT) {
										String type = "";
										for(int i = 2; i<args.length;i++) {
											type+=args[i]+" ";
										}
										inList.setUtilité(type);
										ent.adjusteMoney(-PRIX_CHANGEMENT_DE_TYPE_BAT);
										Utils.saveEntreprise(ent, yml, save);
										Utils.saveBatiment(inList, yml, save);
										p.sendMessage("§7[§6EP§7]§a Vous avez bien changé l'utilité de ce batiment !");
										return false;
									}else p.sendMessage("§7[§6EP§7]§4 Il vous faut au minimum "+PRIX_CHANGEMENT_DE_TYPE_BAT+"$ de fond dans l'entreprise pour renommer ce batiment !");					
								}
							}
							p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de batiment qui porte ce nom !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment changeUse §7[§aNom du batiment§7] §7[§aNouvelle usage§7]");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "add":
						if(args.length == 3) {
							Entreprise ent = Utils.genEntreprise(p, yml);
							String nomBat = args[1]+ent.getNom();
							String nomP = args[2];
							for(Batiment inList : ent.getBatiments()) {
								if(inList.getNom().equals(nomBat)) {
									for(Player online : Bukkit.getOnlinePlayers()) {
										if(online.getName().equals(nomP)) {
											if(!inList.getEditeur().contains(online)) inList.addEditeur(online);
											Utils.saveBatiment(inList, yml, save);
											p.sendMessage("§7[§6EP§7]§a Vous avez ajouté ce joueur au éditeur de votre batiment !");
											return false;
										}
									}
									p.sendMessage("§7[§6EP§7]§4 Ce joueur n'est pas en ligne !");
									return false;
								}
							}
							p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de batiment qui porte ce nom !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment add §7[§aNom du batiment§7] §7[§aNom du joueur§7]");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "remove":
						if(args.length == 3) {
							Entreprise ent = Utils.genEntreprise(p, yml);
							String nomBat = args[1]+ent.getNom();
							String nomP = args[2];
							for(Batiment inList : ent.getBatiments()) {
								if(inList.getNom().equals(nomBat)) {
									for(Player online : Bukkit.getOnlinePlayers()) {
										if(online.getName().equals(nomP)) {
											if(inList.getEditeur().contains(online)) inList.removeEditeur(online);
											Utils.saveBatiment(inList, yml, save);
											p.sendMessage("§7[§6EP§7]§a Vous avez retiré ce joueur au éditeur de votre batiment !");
											return false;
										}
									}
									p.sendMessage("§7[§6EP§7]§4 Ce joueur n'est pas en ligne !");
									return false;
								}
							}
							p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de batiment qui porte ce nom !");
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/batiment remove §7[§aNom du batiment§7] §7[§aNom du joueur§7]");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					case "help":
						p.sendMessage("§7[§6EP§7]§a Voici la liste des commandes lièes aux batiments :");
						p.sendMessage("§7[§6EP§7]§a /batiment bureau §r: permet de créer votre bureau.");
						p.sendMessage("§7[§6EP§7]§a /batiment create §7[§aNom§7] §r: permet de créer un batiment.");
						p.sendMessage("§7[§6EP§7]§a /batiment rename §7[§aAncien nom du batiment§7] §7[§aNouveau nom du batiment§7] §r: permet de renommer un batiment. (§a"+PRIX_RENAME_BAT+"§r$)");
						p.sendMessage("§7[§6EP§7]§a /batiment changeUse §7[§aNom du batiment§7] §7[§aNouvelle usage§7] §r: permet de changer l'utilité d'un batiment. (§a"+PRIX_CHANGEMENT_DE_TYPE_BAT+"§r$)");
						p.sendMessage("§7[§6EP§7]§a /batiment delete §7[§aNom du batiment§7] §r: permet de supprimer un batiment.");
						p.sendMessage("§7[§6EP§7]§a /batiment add §7[§aNom du batiment§7] §7[§aNom du joueur§7] §r: permet d'ajouter un éditeur à un batiment.");
						p.sendMessage("§7[§6EP§7]§a /batiment remove §7[§aNom du batiment§7] §7[§aNom du joueur§7] §r: permet de supprimer un éditeur à un batiment.");
						break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
					default:
						p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/batiment help§4\" pour obtenir la liste des commandes liès aux batiments.");
						break;
					}
				}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
				break;
//***************************************************************************************************************************************************************				
			case "contrat":
				if(yml.isSet("Gerant."+p.getName())) {
					String nomE = yml.getString("Gerant."+p.getName()+".Entreprise");
					if(yml.isSet("Entreprise."+nomE+".Batiment")&&yml.getStringList("Entreprise."+nomE+".Batiment").contains("Bureau"+nomE)){
						String batNom = "Bureau"+nomE;
						Location loc1 = new Location(Bukkit.getWorld(yml.getString("Batiment."+batNom+".World")), yml.getInt("Batiment."+batNom+".Coord1.x"), 0, yml.getInt("Batiment."+batNom+".Coord1.z"));
						Location loc2 = new Location(Bukkit.getWorld(yml.getString("Batiment."+batNom+".World")), yml.getInt("Batiment."+batNom+".Coord2.x"), 0, yml.getInt("Batiment."+batNom+".Coord2.z"));
						if((p.getLocation().getBlockX()>=Math.min(loc1.getBlockX(), loc2.getBlockX()) && p.getLocation().getBlockX()<=Math.max(loc1.getBlockX(), loc2.getBlockX())) && 
								p.getLocation().getBlockZ()>=Math.min(loc1.getBlockZ(), loc2.getBlockZ()) && p.getLocation().getBlockZ()<=Math.max(loc1.getBlockZ(), loc2.getBlockZ())) {
							if(args.length<1) {
								p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/contrat help§4\" pour obtenir la liste des commandes liès aux contrats.");
								return false;
							}
							switch (args[0]) {
//---------------------------------------------------------------------------------------------------------------------------------------------------------------					
							case "create":
								if(args.length >= 4) {
									String id = args[1];							
									if(!yml.isSet("Contrat."+id)) {
										Player t = null;
										for(Player online : Bukkit.getOnlinePlayers()) {
											if(online.getName().equals(args[2])) {
												t = online;
												break;
											}
										}
										if(t == null) {
											p.sendMessage("§7[§6EP§7]§4 Ce joueur n'est pas en ligne !");
											return false;
										}
										if(!t.equals(p)) {
											if(yml.isSet("Gerant."+t.getName())) {
												String terme = "";
												for(int i = 3; i<args.length;i++) {
													terme+=args[i]+" ";
												}
												Contrat demC = new Contrat(true, id, terme, false, false);
												Contrat recC = new Contrat(false, id, terme, false, false);
												Entreprise demEnt = Utils.genEntreprise(p, yml);
												Entreprise recEnt = Utils.genEntreprise(t, yml);
												demEnt.addContrat(demC);
												recEnt.addContrat(recC);
												Utils.saveEntreprise(demEnt, yml, save);
												Utils.saveEntreprise(recEnt, yml, save);
												Utils.saveContrat(demEnt, recEnt, demC, yml, save);
												p.sendMessage("§7[§6EP§7]§a Vous avez bien créé un contrat !");
												t.sendMessage("§7[§6EP§7]§a Vous avez reçu un contrat !");
												t.playSound(t.getLocation(), Sound.ORB_PICKUP, 30, 30);
											}else p.sendMessage("§7[§6EP§7]§4 Ce joueur n'a pas d'entreprise !");
										}else p.sendMessage("§7[§6EP§7]§4 Vous ne pouvez pas faire un contrat avec vous même !");
									}else p.sendMessage("§7[§6EP§7]§4 Cet id de contrat est déjà prit !");
								}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/contrat create §7[§aId§7] §7[§aJoueur§7] §7[§aTerme§7]");
								break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
							case "accept":
								if(args.length == 2) {							
									String id = args[1];
									if(yml.isSet("Contrat."+id)) {
										Entreprise ent = Utils.genEntreprise(p, yml);
										if(yml.isSet("Entreprise."+ent.getNom()+".Contrat."+id)) {
											if(!yml.getBoolean("Contrat."+id+".Accepter")) {
												if(Utils.canEditContrat(ent, id, yml)) {
													Contrat con = Utils.genContrat(id, ent, yml);
													con.setAccepter(true);			
													Utils.saveContrat(con, yml, save);
													p.sendMessage("§7[§6EP§7]§a Vous avez accepté ce contrat !");
													Utils.sendContratNotification(con, ent, "§7[§6EP§7]§a Un de vos contrats a été accepté !", yml, save);
												}else p.sendMessage("§7[§6EP§7]§4 Ce n'est pas à vous d'accepter ce contrat !");
											}else p.sendMessage("§7[§6EP§7]§4 Ce contrat a déjà été accepté !");
										}else p.sendMessage("§7[§6EP§7]§4 Ce contrat ne vous concerne pas !");
									}else p.sendMessage("§7[§6EP§7]§4 Ce contrat n'existe pas !");
								}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/contrat accept §7[§aId§7]");
								break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
							case "decline":
								if(args.length == 2) {							
									String id = args[1];
									if(yml.isSet("Contrat."+id)) {
										Entreprise ent = Utils.genEntreprise(p, yml);
										if(yml.isSet("Entreprise."+ent.getNom()+".Contrat."+id)) {
											if(!yml.getBoolean("Contrat."+id+".Accepter")) {
												if(Utils.canEditContrat(ent, id, yml)) {
													Contrat con = Utils.genContrat(id, ent, yml);
													ent.removeContrat(con);
													for(String nom : yml.getConfigurationSection("Entreprise.").getKeys(false)) {
														if(ent.getNom().equals(nom)) continue;
														for(String idContrat : yml.getConfigurationSection("Entreprise."+nom+".Contrat").getKeys(false)) {
															if(id.equals(idContrat)) {
																Utils.removeContrat(con, ent.getNom(), nom, yml, save);
																p.sendMessage("§7[§6EP§7]§a Vous avez refusé ce contrat !");
																Utils.sendContratNotification(con, ent, "§7[§6EP§7]§a Un de vos contrats a été refusé !", yml, save);
																return false;
															}
														}
													}
													p.sendMessage("§7[§6EP§7]§4 Ce contrat a un problème: Impossible de trouver le 2ème possesseur du contrat !");
												}else p.sendMessage("§7[§6EP§7]§4 Ce n'est pas à vous de refuser ce contrat !");
											}else p.sendMessage("§7[§6EP§7]§4 Ce contrat a déjà été accepté !");
										}else p.sendMessage("§7[§6EP§7]§4 Ce contrat ne vous concerne pas !");
									}else p.sendMessage("§7[§6EP§7]§4 Ce contrat n'existe pas !");
								}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/contrat decline §7[§aId§7]");
								break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
							case "edit":
								if(args.length >= 3) {							
									String id = args[1];
									if(yml.isSet("Contrat."+id)) {
										Entreprise ent = Utils.genEntreprise(p, yml);
										if(yml.isSet("Entreprise."+ent.getNom()+".Contrat."+id)) {
											if(!yml.getBoolean("Contrat."+id+".Accepter")) {
												if(Utils.canEditContrat(ent, id, yml)) {
													String modif = "";
													for(int i = 2 ; i<args.length ; i++) {
														modif+=args[i]+" ";
													}
													Contrat con = Utils.genContrat(id, ent, yml);
													con.setClose(modif);
													con.setChanger(!con.isChanger());
													Utils.saveContrat(con, yml, save);
													p.sendMessage("§7[§6EP§7]§a Ce contrat a bien été modifié ");
													Utils.sendContratNotification(con, ent, "§7[§6EP§7]§a Un de vos contrats a été modifié !", yml, save);
												}else p.sendMessage("§7[§6EP§7]§4 Ce n'est pas à vous de modifier ce contrat !");
											}else p.sendMessage("§7[§6EP§7]§4 Ce contrat a déjà été accepté !");
										}else p.sendMessage("§7[§6EP§7]§4 Ce contrat ne vous concerne pas !");
									}else p.sendMessage("§7[§6EP§7]§4 Ce contrat n'existe pas !");
								}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/contrat edit §7[§aNouveau terme§7]");
								break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
							case "info":
								if(args.length == 2) {							
									String id = args[1];
									if(yml.isSet("Contrat."+id)) {
										Entreprise ent = Utils.genEntreprise(p, yml);
										if(yml.isSet("Entreprise."+ent.getNom()+".Contrat."+id)) {
											Contrat con = Utils.genContrat(id, ent, yml);
											p.sendMessage("§7[§6EP§7]§a Information du contrat:\n§r"+con);
										}else p.sendMessage("§7[§6EP§7]§4 Ce contrat ne vous concerne pas !");
									}else p.sendMessage("§7[§6EP§7]§4 Ce contrat n'existe pas !");
								}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/contrat info §7[§aId§7]");
								break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------						
							case "list":
								if(args.length == 1) {
									Entreprise ent = Utils.genEntreprise(p, yml);
									p.sendMessage("§7[§6EP§7]§a List de vos contrats:\n§r");
									for(Contrat inList : ent.getContrats()) {
										String conString = "§7[§a"+inList.getId()+"§7]§r ";
										if(inList.isAccepter()) conString+="§rContrat en cours.";
										else {
											if(Utils.canEditContrat(ent, inList.getId(), yml)) conString+="§aEn attente de votre réponse/modification.";
											else conString+="§4En attente d'une réponse/modification.";
										}
										p.sendMessage(conString);
									}
								}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/contrat list");
								break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
							case "finish":
								if(args.length == 2) {							
									String id = args[1];
									if(yml.isSet("Contrat."+id)) {
										Entreprise ent = Utils.genEntreprise(p, yml);
										if(yml.isSet("Entreprise."+ent.getNom()+".Contrat."+id)) {
											if(!yml.getBoolean("Entreprise."+ent.getNom()+".Contrat."+id)) {
												Contrat con = Utils.genContrat(id, ent, yml);
												ent.removeContrat(con);
												for(String nom : yml.getConfigurationSection("Entreprise.").getKeys(false)) {
													if(ent.getNom().equals(nom)) continue;
													for(String idContrat : yml.getConfigurationSection("Entreprise."+nom+".Contrat").getKeys(false)) {
														if(id.equals(idContrat)) {
															Utils.sendContratNotification(con, ent, "§7[§6EP§7]§a Un de vos contrats est terminé !", yml, save);
															Utils.removeContrat(con, ent.getNom(), nom, yml, save);
															p.sendMessage("§7[§6EP§7]§a Vous avez mit fin à ce contrat !");
															return false;
														}
													}
												}
												p.sendMessage("§7[§6EP§7]§4 Ce contrat a un problème: Impossible de trouver le 2ème possesseur du contrat !");
											}else p.sendMessage("§7[§6EP§7]§4 Seul le receveur d'un contrat peut indiquer qu'il est terminé !");
										}else p.sendMessage("§7[§6EP§7]§4 Ce contrat ne vous concerne pas !");
									}else p.sendMessage("§7[§6EP§7]§4 Ce contrat n'existe pas !");
								}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/contrat finish §7[§aId§7]");
								break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
							case "help":
								p.sendMessage("§7[§6EP§7]§a Voici la liste des commandes lièes aux contrats :");
								p.sendMessage("§7[§6EP§7]§a /contrat create §7[§aId§7] §7[§aJoueur§7] §7[§aTerme§7] §r: permet de créer un contrat.");
								p.sendMessage("§7[§6EP§7]§a /contrat accept §7[§aId§7] §r: permet d'accepter un contrat.");
								p.sendMessage("§7[§6EP§7]§a /contrat decline §7[§aId§7] §r: permet de refuser un contrat.");
								p.sendMessage("§7[§6EP§7]§a /contrat edit §7[§aNouveau terme§7] §r: permet de changer les termes d'un contrat.");
								p.sendMessage("§7[§6EP§7]§a /contrat info §7[§aId§7] §r: permet de voir les informations liè à un contrat.");
								p.sendMessage("§7[§6EP§7]§a /contrat list §r: permet de voir la liste de vos contrats actif.");
								p.sendMessage("§7[§6EP§7]§a /contrat finish §7[§aId§7] §r: permet de mettre fin à un contrat terminé.");
								break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
							default:
								p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/contrat help§4\" pour obtenir la liste des commandes liès aux contrats.");
								break;
							}
						}p.sendMessage("§7[§6EP§7]§4 Vous ne pouvez pas intérragir avec vos contrats en dehors de votre bureau !");				
					}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas de bureau ! (§r/batiment bureau§4)");					
				}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
				break;
//***************************************************************************************************************************************************************
			case "ep":
				if(args.length<1) {
					p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/ep help§4\" pour obtenir la liste des commandes générales.");
					return false;
				}
				switch (args[0]) {
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "sendMoney":
					if(yml.isSet("Gerant."+p.getName())) {
						if(args.length == 3) {
							double amount = 0;
							Gerant ger = Utils.genGerant(p, yml);
							try {
								amount = Double.parseDouble(args[1]);
							}catch(NumberFormatException ex) {
								p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/ep sendMoney §7[§aMontant§7] §7[§aJoueur§7]");
								return false;
							}
							if(amount>0) {
								if(ger.getFondPersonelle()>=TAXE_ENVOIE_ARGENT*amount) {
									OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]);
									if(op.isOnline()) {
										if(yml.isSet("Gerant."+op.getName())) {
											Player pTarget = (Player) op;
											Gerant gerTarget = Utils.genGerant(pTarget, yml);
											ger.adjusteMoney(-TAXE_ENVOIE_ARGENT*amount);
											gerTarget.adjusteMoney(amount);
											yml.set("Capital.Fond", yml.getDouble("Capital.Argent")+TAXE_ENVOIE_ARGENT*amount-amount);
											Utils.saveGerant(ger, yml, save);
											Utils.saveGerant(gerTarget, yml, save);
											p.sendMessage("§7[§6EP§7]§a Vous avez bien envoyé §r"+amount+"$§a à "+pTarget.getName()+" cela vous coûte §r"+amount*TAXE_ENVOIE_ARGENT+"$. (taxe)");
										}else p.sendMessage("§7[§6EP§7]§4 La cible n'a pas d'entreprise !");
									}else p.sendMessage("§7[§6EP§7]§4 Ce joueur n'est pas en ligne !");
								}else p.sendMessage("§7[§6EP§7]§4 Il vous faut au minimum §r"+amount*TAXE_ENVOIE_ARGENT+"$§4 pour envoyer §r"+amount+"$§4 à un joueur ! (taxe)");			
							}else p.sendMessage("§7[§6EP§7]§4 Le montant doit être positif !");								
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/ep sendMoney §7[§aMontant§7] §7[§aJoueur§7]");
					}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				case "info":
					if(yml.isSet("Gerant."+sender.getName())) {
						if(args.length == 1) {
							Gerant ger = Utils.genGerant(p, yml);
							p.sendMessage("§7[§6EP§7]§a Voici vos informations:\n§r"+ger);
						}else p.sendMessage("§7[§6EP§7]§4 Mauvaise utlisation de la commande: §a/ep info");
					}else p.sendMessage("§7[§6EP§7]§4 Vous n'avez pas d'entreprise ! (§r/entreprise create§4)");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------				
				case "help":
					p.sendMessage("§7[§6EP§7]§a Voici la liste des commandes du plugin :");
					p.sendMessage("§7[§6EP§7]§a /entreprise help §r: permet de voir la liste des commandes lièes aux entreprises.");
					p.sendMessage("§7[§6EP§7]§a /contrat help §r: permet de voir la liste des commandes lièes aux contrats.");
					p.sendMessage("§7[§6EP§7]§a /batiment help §r: permet de voir la liste des commandes lièes aux batiments.");
					p.sendMessage("§7[§6EP§7]§a /ep sendMoney §7[§aMontant§7] §7[§aJoueur§7] §r: permet d'envoyer de l'argent à un autre joueur.");
					p.sendMessage("§7[§6EP§7]§a /ep info §r: permet de voir les informations vous concernant.");
					break;
//---------------------------------------------------------------------------------------------------------------------------------------------------------------
				default:
					p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/ep help§4\" pour obtenir la liste des commandes générales.");
					break;
				}				
			break;
//***************************************************************************************************************************************************************
			default:
				p.sendMessage("§7[§6EP§7]§4 Cette commande n'existe pas, utilisé \"§a/ep help§4\" pour obtenir la liste des commandes générales");
				break;
			}
		}else sender.sendMessage("§7[§6EP§7]§4 Seul les joueurs peuvent utiliser ces commandes.");
		return false;
	}
}