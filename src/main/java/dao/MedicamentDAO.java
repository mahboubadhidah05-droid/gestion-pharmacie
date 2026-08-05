package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.MedicamentResponse;
import dto.MedicamentVenteInfo;
import dto.NomMedicamentResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

public class MedicamentDAO {

private static final String TABLE_MEDICAMENT =
        "medicament";

private static final String COL_ID =
        "id_medicament";

private static final String COL_NOM =
        "nom";

private static final String COL_STOCK =
        "stock";

private static final String WHERE_ID =
        " WHERE " + COL_ID + " = ?";

private static final String CONTEXTE_MEDICAMENT =
        "du médicament avec l'ID : ";

public void ajouterMedicament(
        String nom,
        String dosage,
        int stock,
        double prix,
        int seuil,
        String datePeremption,
        boolean conventionneCnam,
        double tauxRemboursement,
        String codeBarre,
        String forme,
        String fabricant) {

    String sql =
            "INSERT INTO "
            + TABLE_MEDICAMENT
            + " (nom, dosage, stock, prix, seuil_critique, date_peremption,"
            + " conventionne_cnam, taux_remboursement, code_barre,"
            + " forme, fabricant)"
            + " VALUES(?,?,?,?,?,?,?,?,?,?,?)";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, nom);
        statement.setString(2, dosage);
        statement.setInt(3, stock);
        statement.setDouble(4, prix);
        statement.setInt(5, seuil);
        statement.setString(6, datePeremption);
        statement.setBoolean(7, conventionneCnam);
        statement.setDouble(8, tauxRemboursement);
        statement.setString(9, codeBarre);
        statement.setString(10, forme);
        statement.setString(11, fabricant);

        statement.executeUpdate();

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de l'ajout du médicament : "
                        + nom
                        + ", dosage : "
                        + dosage,
                exception
        );
    }
}

public int getStock(int idMed) {

    String sql =
            "SELECT "
            + COL_STOCK
            + " FROM "
            + TABLE_MEDICAMENT
            + WHERE_ID;

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, idMed);

        try (ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {
                return result.getInt(COL_STOCK);
            }
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la récupération du stock "
                        + CONTEXTE_MEDICAMENT
                        + idMed,
                exception
        );
    }

    return -1;
}

public String getDatePeremption(int idMed) {

    String sql =
            "SELECT date_peremption FROM "
            + TABLE_MEDICAMENT
            + WHERE_ID;

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, idMed);

        try (ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {
                return result.getString("date_peremption");
            }
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la récupération de la date de péremption "
                        + CONTEXTE_MEDICAMENT
                        + idMed,
                exception
        );
    }

    return null;
}

/**
 * Sous-ensemble des infos nécessaires à VenteService pour calculer
 * le remboursement CNAM au moment d'une vente.
 */
public MedicamentVenteInfo getInfosVente(int idMed) {

    String sql =
            "SELECT prix, conventionne_cnam, taux_remboursement FROM "
            + TABLE_MEDICAMENT
            + WHERE_ID;

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, idMed);

        try (ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {

                return new MedicamentVenteInfo(
                        result.getDouble("prix"),
                        result.getBoolean("conventionne_cnam"),
                        result.getDouble("taux_remboursement")
                );
            }
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la récupération des infos de vente "
                        + CONTEXTE_MEDICAMENT
                        + idMed,
                exception
        );
    }

    return null;
}

public List<MedicamentResponse> listerMedicaments() {

    String sql =
            "SELECT * FROM "
            + TABLE_MEDICAMENT
            + " ORDER BY "
            + COL_ID;

    List<MedicamentResponse> medicaments =
            new ArrayList<>();

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql);
         ResultSet result =
                 statement.executeQuery()) {

        while (result.next()) {

            medicaments.add(
                    new MedicamentResponse(
                            result.getInt(COL_ID),
                            result.getString(COL_NOM),
                            result.getString("dosage"),
                            result.getInt(COL_STOCK),
                            result.getDouble("prix"),
                            result.getInt("seuil_critique"),
                            result.getString("date_peremption"),
                            0,
                            result.getBoolean("conventionne_cnam"),
                            result.getDouble("taux_remboursement"),
                            result.getString("code_barre"),
                            result.getString("forme"),
                            result.getString("fabricant")
                    )
            );
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la récupération de la liste "
                        + "des médicaments",
                exception
        );
    }

    return medicaments;
}

/**
 * Recherche par autocomplétion : tous les médicaments dont le nom
 * commence par {@code debut} (insensible à la casse), avec assez
 * d'infos (dosage, forme, fabricant) pour les distinguer visuellement
 * s'ils partagent le même nom.
 */
public List<dto.MedicamentAutocompleteResponse> rechercherParDebutNom(
        String debut) {

    String sql =
            "SELECT "
            + COL_ID
            + ", "
            + COL_NOM
            + ", dosage, forme, fabricant FROM "
            + TABLE_MEDICAMENT
            + " WHERE LOWER("
            + COL_NOM
            + ") LIKE LOWER(?) ORDER BY "
            + COL_NOM
            + " LIMIT 10";

    List<dto.MedicamentAutocompleteResponse> resultats =
            new ArrayList<>();

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, debut + "%");

        try (ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                resultats.add(
                        new dto.MedicamentAutocompleteResponse(
                                result.getInt(COL_ID),
                                result.getString(COL_NOM),
                                result.getString("dosage"),
                                result.getString("forme"),
                                result.getString("fabricant")
                        )
                );
            }
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la recherche par autocomplétion pour : "
                        + debut,
                exception
        );
    }

    return resultats;
}

/**
 * Retrouve l'ID d'un médicament à partir de son code-barres
 * (recherche par scan lors d'une vente).
 *
 * @return l'ID trouvé, ou -1 si aucun médicament ne correspond.
 */
public int getIdParCodeBarre(String codeBarre) {

    String sql =
            "SELECT "
            + COL_ID
            + " FROM "
            + TABLE_MEDICAMENT
            + " WHERE code_barre = ?";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, codeBarre);

        try (ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {
                return result.getInt(COL_ID);
            }
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la recherche du médicament "
                        + "par code-barres : "
                        + codeBarre,
                exception
        );
    }

    return -1;
}

/**
 * Résumé minimal (nom, dosage, stock) d'un médicament par son ID —
 * utilisé pour confirmer visuellement le produit après un scan.
 */
public dto.MedicamentScanResponse getResumeParId(int id) {

    String sql =
            "SELECT "
            + COL_NOM
            + ", dosage, "
            + COL_STOCK
            + " FROM "
            + TABLE_MEDICAMENT
            + WHERE_ID;

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, id);

        try (ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {

                return new dto.MedicamentScanResponse(
                        id,
                        result.getString(COL_NOM),
                        result.getString("dosage"),
                        result.getInt(COL_STOCK)
                );
            }
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la récupération du résumé "
                        + CONTEXTE_MEDICAMENT
                        + id,
                exception
        );
    }

    return null;
}

public List<NomMedicamentResponse> listerNoms() {

    String sql =
            "SELECT " + COL_NOM + ", dosage FROM "
            + TABLE_MEDICAMENT
            + " ORDER BY " + COL_NOM;

    List<NomMedicamentResponse> noms =
            new ArrayList<>();

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql);
         ResultSet result =
                 statement.executeQuery()) {

        while (result.next()) {

            noms.add(
                    new NomMedicamentResponse(
                            result.getString(COL_NOM),
                            result.getString("dosage")
                    )
            );
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la récupération des noms "
                        + "de médicaments",
                exception
        );
    }

    return noms;
}

public void updateStock(
        int idMed,
        int quantite) {

    String sql =
            "UPDATE "
            + TABLE_MEDICAMENT
            + " SET "
            + COL_STOCK
            + "=?"
            + WHERE_ID;

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, quantite);
        statement.setInt(2, idMed);

        statement.executeUpdate();

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la mise à jour du stock "
                        + CONTEXTE_MEDICAMENT
                        + idMed
                        + ", nouvelle quantité : "
                        + quantite,
                exception
        );
    }
}

public String stockCritique(
        int idMed) {

    String sql =
            "SELECT * FROM "
            + TABLE_MEDICAMENT
            + WHERE_ID
            + " AND "
            + COL_STOCK
            + " <= seuil_critique";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, idMed);

        try (ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {

                return String.format(
                        "Médicament en stock critique : %s (ID %d) "
                                + "| Stock actuel = %d",
                        result.getString(COL_NOM),
                        result.getInt(COL_ID),
                        result.getInt(COL_STOCK)
                );
            }
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la vérification du stock critique "
                        + CONTEXTE_MEDICAMENT
                        + idMed,
                exception
        );
    }

    return null;
}

public int getIdMedicamentParNomEtDosage(
        String nom,
        String dosage) {

    String sql =
            "SELECT "
            + COL_ID
            + " FROM "
            + TABLE_MEDICAMENT
            + " WHERE LOWER(TRIM("
            + COL_NOM
            + ")) = LOWER(TRIM(?)) AND LOWER(TRIM(dosage)) = LOWER(TRIM(?))";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, nom);
        statement.setString(2, dosage);

        try (ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {
                return result.getInt(COL_ID);
            }
        }

    } catch (SQLException exception) {

        throw new AccesDonneesException(
                "Échec de la recherche du médicament "
                        + "avec le nom : "
                        + nom
                        + " et le dosage : "
                        + dosage,
                exception
        );
    }

    return -1;
}

}