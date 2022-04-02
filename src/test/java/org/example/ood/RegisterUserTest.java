package org.example.ood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    private final RegisterUser registerUser = new RegisterUser();

    @Test
    void registerUserReturnsTrueOnSuccess() {
        assertEquals(true, registerUser.registerUser());
    }

    private static class RegisterUser {
        public boolean registerUser() {
            return true;
        }
    }
}
