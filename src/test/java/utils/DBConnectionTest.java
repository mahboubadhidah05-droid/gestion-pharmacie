package utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;


class DBConnectionTest {


    @Test
    void constructeurPrive_leveException()
            throws Exception {

        Constructor<DBConnection> constructor =
                DBConnection.class.getDeclaredConstructor();

        constructor.setAccessible(true);


        InvocationTargetException exception =
                assertThrows(
                        InvocationTargetException.class,
                        constructor::newInstance
                );


        assertInstanceOf(
                IllegalStateException.class,
                exception.getCause()
        );


        assertEquals(
                "Utility class",
                exception.getCause().getMessage()
        );
    }


    @Test
    void validatePassword_null_leveSQLException() {

        SQLException exception =
                assertThrows(
                        SQLException.class,
                        () -> DBConnection.validatePassword(null)
                );


        assertEquals(
                "Database password not configured",
                exception.getMessage()
        );
    }


    @Test
    void validatePassword_vide_leveSQLException() {

        SQLException exception =
                assertThrows(
                        SQLException.class,
                        () -> DBConnection.validatePassword("   ")
                );


        assertEquals(
                "Database password not configured",
                exception.getMessage()
        );
    }


    @Test
    void validatePassword_valide_neLevePasException() {

        assertDoesNotThrow(() ->
                DBConnection.validatePassword(
                        "motDePasseValide"
                )
        );
    }


    @Test
    void getConnection_succes_retourneConnexion()
            throws SQLException {


        Connection mockConnection =
                mock(Connection.class);


        try (MockedStatic<DriverManager> mockedDriver =
                     mockStatic(DriverManager.class)) {


            mockedDriver.when(() ->
                    DriverManager.getConnection(
                            anyString(),
                            anyString(),
                            anyString()
                    )
            ).thenReturn(mockConnection);



            Connection result =
                    DBConnection.getConnection();



            assertNotNull(result);

            assertSame(
                    mockConnection,
                    result
            );
        }
    }
}