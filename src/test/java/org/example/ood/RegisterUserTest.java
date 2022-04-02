package org.example.ood;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    private final UserRepository users = new UserRepository();
    private final RegisterUser registerUser = new RegisterUser(users);

    @Test
    void givenValidUsernameAndPasswordThenTheUserIsRegistered() {
        assertEquals(true, registerUser.execute("username", "password"));
        assertEquals(true, users.exists("username"));
    }

    @Test
    void givenEmptyUsernameThenRegistrationFails() {
        assertEquals(false, registerUser.execute("", "password"));
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenEmptyPasswordThenRegistrationFails() {
        assertEquals(false, registerUser.execute("username", ""));
        assertEquals(false, users.exists("username"));
    }

    private static class RegisterUser {
        private final UserRepository users;

        public RegisterUser(UserRepository users) {
            this.users = users;
        }

        public boolean execute(String username, String password) {
            if ("".equals(username) || "".equals(password)) {
                return false;
            }
            users.save(username);
            return true;
        }
    }

    private static class UserRepository {
        private final List<String> users = new ArrayList<>();

        boolean exists(String username) {
            return users.contains(username);
        }

        void save(String username) {
            users.add(username);
        }
    }
}
