package org.example.ood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    private final RegisterUser registerUser = new RegisterUser();

    @Test
    void givenValidUsernameAndPasswordThenTheUserIsRegistered() {
        assertEquals(true, registerUser.execute("username", "password"));
    }

    @Test
    void givenEmptyUsernameThenRegistrationFails() {
        assertEquals(false, registerUser.execute("", "password"));
    }

    private static class RegisterUser {
        public boolean execute(String username, String password) {
            if ("".equals(username)) {
                return false;
            }
            return true;
        }
    }
}
