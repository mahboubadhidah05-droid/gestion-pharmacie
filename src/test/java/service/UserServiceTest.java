package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.UserDAO;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        setUserService(new UserService(userDAO));
    }

    @Test
    void consulterProfil_loginExistant_doitAppelerLeDao() {
        when(userDAO.getProfil("mahbouba")).thenReturn(new String[]{"Ben Salah", "Mahbouba"});

        UserService.consulterProfil("mahbouba");

        verify(userDAO).getProfil("mahbouba");
    }

    @Test
    void consulterProfil_loginInconnu_neDoitPasPlanter() {
        when(userDAO.getProfil("inconnu")).thenReturn(null);

        UserService.consulterProfil("inconnu");

        verify(userDAO).getProfil("inconnu");
    }

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}