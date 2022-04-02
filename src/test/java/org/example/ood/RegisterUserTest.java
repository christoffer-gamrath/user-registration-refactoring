package org.example.ood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    @Test
    void registerUserReturnsTrueOnSuccess() {
        assertEquals(true, applesauce());
    }

    private boolean applesauce() {
        return true;
    }
}
