package utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class DBConnectionTest {

    @Test
    void validatePasswordDoitAccepterMotDePasseValide() {

        assertDoesNotThrow(
                () -> DBConnection.validatePassword(
                        "mot-de-passe-test"
                )
        );
    }

    @Test
    void validatePasswordDoitRefuserNull()
            throws SQLException {

        assertThrows(
                SQLException.class,
                () -> DBConnection.validatePassword(null)
        );
    }

    @Test
    void validatePasswordDoitRefuserChaineVide()
            throws SQLException {

        assertThrows(
                SQLException.class,
                () -> DBConnection.validatePassword("")
        );
    }

    @Test
    void validatePasswordDoitRefuserEspaces()
            throws SQLException {

        assertThrows(
                SQLException.class,
                () -> DBConnection.validatePassword("   ")
        );
    }

    @Test
    void constructeurPriveDoitLeverException()
            throws Exception {

        Constructor<DBConnection> constructeur =
                DBConnection.class.getDeclaredConstructor();

        constructeur.setAccessible(true);

        assertThrows(
                Exception.class,
                constructeur::newInstance
        );
    }
}