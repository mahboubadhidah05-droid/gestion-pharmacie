package exception;

/**
 * Exception levée quand une opération sur la base de données échoue.
 * Non vérifiée : elle traverse les couches Service et Contrôleur
 * jusqu'au gestionnaire global qui la transforme en réponse 500.
 */
public class AccesDonneesException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccesDonneesException(String message, Throwable cause) {
        super(message, cause);
    }
}