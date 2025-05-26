/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.mycompany.registraionloginapp.LoginService;
import com.mycompany.registraionloginapp.RegistrationService;
import com.mycompany.registraionloginapp.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author muano
 */
public class RegistrationLoginAppTest {

    RegistrationService registration = new RegistrationService();
    LoginService login = new LoginService();

    // 1. Username is correctly formatted
    @Test
    public void testUsernameCorrectlyFormatted() {
        String username = "Kyl_1";
        assertTrue(registration.checkUserName(username));
    }

    // 2. Username is incorrectly formatted
    @Test
    public void testUsernameIncorrectlyFormatted() {
        String username = "Kyle!!!!!!!";
        assertFalse(registration.checkUserName(username));
    }

    // 3. Password meets complexity
    @Test
    public void testPasswordMeetsComplexity() {
        String password = "Ch&&sec@ke99!";
        assertTrue(registration.checkPasswordComplexity(password));
    }

    // 4. Password does not meet complexity
    @Test
    public void testPasswordDoesNotMeetComplexity() {
        String password = "password";
        assertFalse(registration.checkPasswordComplexity(password));
    }

    // 5. Cell phone number correctly formatted
    @Test
    public void testPhoneNumberCorrectlyFormatted() {
        String phone = "+27828968976";
        assertTrue(registration.checkCellPhoneNumber(phone));
    }

    // 6. Cell phone number incorrectly formatted
    @Test
    public void testPhoneNumberIncorrectlyFormatted() {
        String phone = "08966553";
        assertFalse(registration.checkCellPhoneNumber(phone));
    }

    // 7. Login success returns true
    @Test
    public void testLoginSuccess() {
        User user = new User("John", "Doe", "KY1_1", "Ch&&sec@ke99!", "+27828968976");
        login.setUser(user);
        assertTrue(login.logUser("KY1_1", "Ch&&sec@ke99!"));
    }
    // 8. Login success returns true
    @Test
    public void testLoginFail() {
        User user = new User("John", "Doe", "KY1_1", "Ch&&sec@ke99!", "+27828968976");
        login.setUser(user);
        assertFalse(login.logUser("wrongUser", "wrongPass"));
    }
    
    
}
