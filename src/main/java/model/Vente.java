package model;

import java.util.Date;

/**
 * Classe représentant une vente de médicament.
 */
public class Vente {

    private int id;
    private int idMedicament;
    private int quantite;
    private Date date;

    public Vente(int id, int idMedicament,
                 int quantite, Date date) {
        this.setId(id);
        this.setIdMedicament(idMedicament);
        this.setQuantite(quantite);
        this.setDate(date);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdMedicament() {
		return idMedicament;
	}

	public void setIdMedicament(int idMedicament) {
		this.idMedicament = idMedicament;
	}

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
