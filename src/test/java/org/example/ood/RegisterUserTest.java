package org.example.ood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    private final RegisterUser registerUser = new RegisterUser();

    @Test
    void thatsNotActuallySuccess() {
        assertEquals(false, registerUser.execute("username"));
    }

    private static class RegisterUser {
        public boolean execute(String username) {
            return false;
        }
    }
}
