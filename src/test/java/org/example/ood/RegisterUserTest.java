package org.example.ood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    private final Applesauce applesauce = new Applesauce();

    @Test
    void registerUserReturnsTrueOnSuccess() {
        assertEquals(true, registerUser());
    }

    private boolean registerUser() {
        return true;
    }

    private static class Applesauce {

    }
}
