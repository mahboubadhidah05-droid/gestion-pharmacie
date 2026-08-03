package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.LotResponse;
import exception.AccesDonneesException;
import utils.DBConnection;

/**
 * DAO de gestion des lots de stock (chaque lot a sa propre quantité
 * et sa propre date de péremption, contrairement au champ unique
 * medicament.date_peremption qui ne peut représenter qu'un seul lot).
 */
public class LotMedicamentDAO {

    private static final String INSERT_LOT =
            "INSERT INTO lot_medicament "
            + "(id_medicament, quantite, date_peremption) "
            + "VALUES (?, ?, ?)";

    private static final String SELECT_LOTS_DISPONIBLES =
            "SELECT * FROM lot_medicament "
            + "WHERE id_medicament=? AND quantite > 0 "
            + "ORDER BY "
            + "CASE WHEN date_peremption IS NULL THEN 1 ELSE 0 END, "
            + "date_peremption ASC, id_lot ASC";

    private static final String SELECT_LOTS_EXPIRES =
            "SELECT * FROM lot_medicament "
            + "WHERE id_medicament=? AND quantite > 0 "
            + "AND date_peremption IS NOT NULL AND date_peremption < ?";

    private static final String UPDATE_QUANTITE_LOT =
            "UPDATE lot_medicament SET quantite=? WHERE id_lot=?";

    private static final String SELECT_DATE_PLUS_PROCHE =
            "SELECT MIN(date_peremption) AS date_min FROM lot_medicament "
            + "WHERE id_medicament=? AND quantite > 0 "
            + "AND date_peremption IS NOT NULL";

    private static final String SELECT_QUANTITE_PERIMEE =
            "SELECT COALESCE(SUM(quantite), 0) AS total FROM lot_medicament "
            + "WHERE id_medicament=? AND quantite > 0 "
            + "AND date_peremption IS NOT NULL AND date_peremption < ?";


    public int ajouterLot(
            int idMedicament,
            int quantite,
            String datePeremption) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_LOT,
                             java.sql.Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(1, idMedicament);
            statement.setInt(2, quantite);
            statement.setString(3, datePeremption);

            statement.executeUpdate();

            try (ResultSet cles = statement.getGeneratedKeys()) {

                if (cles.next()) {
                    return cles.getInt(1);
                }
            }

            return -1;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de l'ajout du lot pour le médicament ID="
                            + idMedicament,
                    exception
            );
        }
    }

    /**
     * Retourne les lots avec du stock restant, triés du plus proche
     * de la péremption au plus lointain (les lots sans date en dernier)
     * — utilisé pour la déduction FIFO lors d'une vente.
     */
    public List<LotResponse> getLotsDisponibles(int idMedicament) {
        return executerRequeteLots(SELECT_LOTS_DISPONIBLES, idMedicament, null);
    }

    /**
     * Retourne les lots dont la date de péremption est strictement
     * dépassée par rapport à la date fournie (format AAAA-MM-JJ).
     */
    public List<LotResponse> getLotsExpires(
            int idMedicament,
            String dateDuJour) {

        return executerRequeteLots(
                SELECT_LOTS_EXPIRES, idMedicament, dateDuJour
        );
    }

    private List<LotResponse> executerRequeteLots(
            String sql,
            int idMedicament,
            String dateOptionnelle) {

        List<LotResponse> lots = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idMedicament);

            if (dateOptionnelle != null) {
                statement.setString(2, dateOptionnelle);
            }

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    lots.add(new LotResponse(
                            result.getInt("id_lot"),
                            result.getInt("id_medicament"),
                            result.getInt("quantite"),
                            result.getString("date_peremption")
                    ));
                }
            }

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération des lots pour le médicament ID="
                            + idMedicament,
                    exception
            );
        }

        return lots;
    }

    public void mettreAJourQuantiteLot(int idLot, int nouvelleQuantite) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(UPDATE_QUANTITE_LOT)) {

            statement.setInt(1, nouvelleQuantite);
            statement.setInt(2, idLot);

            statement.executeUpdate();

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la mise à jour du lot ID=" + idLot,
                    exception
            );
        }
    }

    /**
     * Date de péremption la plus proche parmi les lots restants
     * (null si aucun lot n'a de date, ou aucun stock disponible).
     */
    public String getDatePeremptionLaPlusProche(int idMedicament) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_DATE_PLUS_PROCHE)) {

            statement.setInt(1, idMedicament);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getString("date_min");
                }
            }

            return null;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération de la date de péremption "
                            + "la plus proche pour le médicament ID="
                            + idMedicament,
                    exception
            );
        }
    }

    /**
     * Quantité totale des lots réellement périmés (date dépassée par
     * rapport à la date fournie) et ayant encore du stock — permet de
     * savoir combien d'unités sur le stock total sont concernées,
     * plutôt que de laisser croire que tout le stock est périmé.
     */
    public int getQuantitePerimee(int idMedicament, String dateDuJour) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_QUANTITE_PERIMEE)) {

            statement.setInt(1, idMedicament);
            statement.setString(2, dateDuJour);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt("total");
                }
            }

            return 0;

        } catch (SQLException exception) {

            throw new AccesDonneesException(
                    "Échec de la récupération de la quantité périmée "
                            + "pour le médicament ID=" + idMedicament,
                    exception
            );
        }
    }
}