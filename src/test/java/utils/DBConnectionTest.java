package utils;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * Tests unitaires pour {@link DBConnection}.
 *
 * IMPORTANT : pour que testGetConnection_Succes passe, la variable
 * d'environnement DB_PASSWORD doit être définie AVANT le lancement des tests
 * (PASSWORD est un static final chargé une seule fois par la JVM). Ajouter
 * dans le pom.xml :
 *
 * <plugin>
 *   <groupId>org.apache.maven.plugins</groupId>
 *   <artifactId>maven-surefire-plugin</artifactId>
 *   <configuration>
 *     <environmentVariables>
 *       <DB_PASSWORD>test-password-ci</DB_PASSWORD>
 *     </environmentVariables>
 *   </configuration>
 * </plugin>
 *
 * Ce n'est pas un vrai secret, uniquement une valeur factice utilisée pour
 * que la classe passe la validation pendant les tests.
 */
class DBConnectionTest {

    @Test
    void testConstructeurPrive_LeveIllegalStateException() throws NoSuchMethodException {
        Constructor<DBConnection> constructor = DBConnection.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception =
                assertThrows(InvocationTargetException.class, constructor::newInstance);

        assertInstanceOf(IllegalStateException.class, exception.getCause());
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void testValidatePassword_Null_LeveSQLException() {
        SQLException exception =
                assertThrows(SQLException.class, () -> DBConnection.validatePassword(null));

        assertEquals("Database password not configured", exception.getMessage());
    }

    @Test
    void testValidatePassword_Vide_LeveSQLException() {
        SQLException exception =
                assertThrows(SQLException.class, () -> DBConnection.validatePassword("   "));

        assertEquals("Database password not configured", exception.getMessage());
    }

    @Test
    void testValidatePassword_Valide_NeLeveRien() {
        assertDoesNotThrow(() -> DBConnection.validatePassword("motDePasseValide"));
    }

    @Test
    void testGetConnection_Succes() throws SQLException {
        Connection mockConnection = mock(Connection.class);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() ->
                    DriverManager.getConnection(anyString(), anyString(), anyString())
            ).thenReturn(mockConnection);

            Connection result = DBConnection.getConnection();

            assertNotNull(result);
            assertSame(mockConnection, result);
        }
    }
}