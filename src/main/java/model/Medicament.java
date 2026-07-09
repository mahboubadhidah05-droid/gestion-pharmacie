package model;

/**
 * Classe représentant un médicament avec son stock.
 */
public class Medicament {

    private int id;
    private String nom;
    private String dosage;
    private int stock;
    private double prix;

    public Medicament(int id, String nom, String dosage,
                      int stock, double prix) {
        this.id = id;
        this.setNom(nom);
        this.setDosage(dosage);
        this.stock = stock;
        this.prix = prix;
    }

    public int getId() { return id; }
    public int getStock() { return stock; }
    public double getPrix() { return prix; }

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}
}
