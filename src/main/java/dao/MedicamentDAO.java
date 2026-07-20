package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.MedicamentResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

public class MedicamentDAO {

```
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
        int seuil) {

    String sql =
            "INSERT INTO "
            + TABLE_MEDICAMENT
            + " (nom, dosage, stock, prix, seuil_critique)"
            + " VALUES(?,?,?,?,?)";

    try (Connection connection =
                 DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setString(1, nom);
        statement.setString(2, dosage);
        statement.setInt(3, stock);
        statement.setDouble(4, prix);
        statement.setInt(5, seuil);

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
                            result.getInt("seuil_critique")
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
            + " WHERE "
            + COL_NOM
            + "=? AND dosage=?";

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
```

}
