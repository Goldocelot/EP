package be.goldocelot.ep.object;

import java.util.ArrayList;
import java.util.List;

import be.goldocelot.ep.utils.MoneyChange;

public class Entreprise implements MoneyChange{
	
	private double fond;
	private String nom;
	private String type;
	private List<Batiment> batiments;
	private List<Contrat> contrats;
	
	public Entreprise(double fond, String nom, String type, List<Batiment> batiments, List<Contrat> contrats) {
		setFond(fond);
		setNom(nom);
		setType(type);
		setBatiments(batiments);
		setContrats(contrats);
	}

	public void adjusteMoney(double toAdd) {
		this.fond+=toAdd;
	}
	
	public double getFond() {
		return fond;
	}

	public void setFond(double fond) {
		if(fond >= 0) {
			this.fond = fond;	
			}
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
			this.type = type;
	}
	
	public List<Batiment> getBatiments() {
		return batiments;
	}

	public void setBatiments(List<Batiment> batiments) {
		if(batiments != null) {
			this.batiments = batiments;
		}else {
			List<Batiment> bat = new ArrayList<>();
			this.batiments = bat;
		}	
	}
	
	public void addBatiment(Batiment batiment) {
		this.batiments.add(batiment);
	}
	
	public void removeBatiment(Batiment batiment) {
		if(this.batiments.contains(batiment)) {
			this.batiments.remove(batiment);
		}
	}
	public List<Contrat> getContrats() {
		return contrats;
	}

	public void setContrats(List<Contrat> contrats) {
		if(contrats != null) {
			this.contrats = contrats;
		}else {
			List<Contrat> cont = new ArrayList<>();
			this.contrats = cont;
		}
	}
	
	
	public void addContrat(Contrat contrat) {
		this.contrats.add(contrat);
	}
	
	public void removeContrat(Contrat contrat) {
		if(this.contrats.contains(contrat)) {
			this.contrats.remove(contrat);
		}
	}
	
	public boolean equals(Object o) {
		if(o instanceof Entreprise) {
			Entreprise temp = (Entreprise) o;
			return temp.getNom().equals(this.getNom());
		}
		return false;
	}
	
	public String toString() {
		String EntrepriseTS = "§7[§aNom§7]§r "+this.getNom()+"\n"+
				"§7[§aFond§7]§r "+this.getFond()+"$\n"+
				"§7[§aType§7]§r "+this.getType()+"\n"+
				"§7[§aBatiments§7]§r ";
		for(Batiment inList : this.getBatiments()) {
			EntrepriseTS=EntrepriseTS+"\n"+inList.toString(this)+"\n";
		}
		return EntrepriseTS;
	}

}
