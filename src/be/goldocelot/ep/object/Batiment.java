package be.goldocelot.ep.object;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Batiment {
	public final static double VALEUR=1;
	private int superficie;
	private double valeur;
	private String utilit�;
	private String nom;
	private List<OfflinePlayer> editeur;

	public Batiment(int superficie, String utilit�, String nom, List<OfflinePlayer> editeur) {
		setSuperficie(superficie);
		setValeur(valeur);
		setUtilit�(utilit�);
		setNom(nom);
		setEditeur(editeur);
	}

	public int getSuperficie() {
		return superficie;
	}

	public void setSuperficie(int superficie) {
		if(superficie>0) {
			this.superficie = superficie;
		}
	}

	public double getValeur() {
		return valeur;
	}

	public void setValeur(double valeur) {
		this.valeur=superficie*VALEUR;
	}

	public String getUtilit�() {
		return utilit�;
	}

	public void setUtilit�(String utilit�) {
		this.utilit� = utilit�;
	}
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}	

	public List<OfflinePlayer> getEditeur() {
		return editeur;
	}

	public void setEditeur(List<OfflinePlayer> editeur) {
		this.editeur = editeur;
	}
	
	public void addEditeur(Player p) {
		editeur.add(p);
	}
	
	public void removeEditeur(Player p) {
		if(editeur.contains(p)) {
			editeur.remove(p);
		}
	}
	
	public boolean equals(Object o) {
		if(o instanceof Batiment) {
			Batiment temp = (Batiment) o;
			return temp.getUtilit�().equals(this.getUtilit�());
		}
		return false;
	}
	
	public String toString(Entreprise ent) {
		String nom = this.getNom().substring(0, this.nom.length()-ent.getNom().length());
		return "\n�7[�aNom�7]�r "+nom+"\n"+
				"-�7[�aUtilit�7]�r "+this.getUtilit�()+"\n"+
				"-�7[�aValeur�7]�r "+this.getValeur()+"$\n"+
				"-�7[�aSuperficie�7]�r "+this.getSuperficie()+"m�\n";
	}

}
