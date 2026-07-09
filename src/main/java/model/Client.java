package model;

/**
 * Classe représentant un client de la pharmacie.
 */
public class Client {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String adresse;

    public Client(int id, String nom, String prenom,
                  String email, String adresse) {
        this.setId(id);
        this.setNom(nom);
        this.setPrenom(prenom);
        this.setEmail(email);
        this.setAdresse(adresse);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
}

