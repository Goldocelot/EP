package be.goldocelot.ep.object;

public class Contrat {

	private boolean demandeur;
	private boolean accepter;
	private boolean changer;
	private String id;
	private String close;
	
	public Contrat(boolean demandeur, String id, String close, boolean changer, boolean accepter) {
		setDemandeur(demandeur);
		setId(id);
		setClose(close);
		setChanger(changer);
		setAccepter(accepter);
	}

	public boolean isDemandeur() {
		return demandeur;
	}

	public void setDemandeur(boolean demandeur) {
		this.demandeur = demandeur;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public boolean isAccepter() {
		return accepter;
	}

	public void setAccepter(boolean accepter) {
		this.accepter = accepter;
	}

	public boolean isChanger() {
		return changer;
	}

	public void setChanger(boolean changer) {
		this.changer = changer;
	}
	
	public String toString() {
		String conString = "§7[§aId§7]§r "+getId()+"\n"
						  +"§7[§aTerme§7]§r "+getClose()+"\n"
						  +"§7[§aRole§7]§r ";
		if(demandeur) conString+= "Demandeur";
		else conString+= "Receveur";
		conString+="\n§7[§aStatut§7]§r ";
		if(accepter) conString+= "Accepter";
		else conString+= "En attente";
		return conString;
	}
}
