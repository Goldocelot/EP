package be.goldocelot.ep.object;

import org.bukkit.entity.Player;

import be.goldocelot.ep.utils.MoneyChange;

public class Gerant implements MoneyChange{
	private Entreprise entreprise;
	private Player p;
	private double fondPersonelle;
	
	public Gerant(Entreprise entreprise, Player p, double fondPersonelle) {
		setEntreprise(entreprise);
		setP(p);
		setFondPersonelle(fondPersonelle);
	}

	public Entreprise getEntreprise() {
		return entreprise;
	}

	public void setEntreprise(Entreprise entreprise) {
		this.entreprise = entreprise;
	}

	public Player getP() {
		return p;
	}

	public void setP(Player p) {
		this.p = p;
	}

	public double getFondPersonelle() {
		return fondPersonelle;
	}

	public void setFondPersonelle(double fondPersonelle) {
		if(fondPersonelle>0) {
			this.fondPersonelle = fondPersonelle;
		}
	}
	
	public void adjusteMoney(double toAdd) {
		this.fondPersonelle+=toAdd;
	}
	
	public String toString() {
		return "§7[§aNom§7]§r "+this.getP().getName()+"\n"
				+"§7[§aFond§7]§r "+this.getFondPersonelle()+"$\n"
				+"§7[§aEntreprise§7]§r "+this.getEntreprise().getNom()+"\n";		
	}

}
