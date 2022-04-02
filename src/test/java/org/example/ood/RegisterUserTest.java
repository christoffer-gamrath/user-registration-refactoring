package org.example.ood;

import org.junit.jupiter.api.Test;

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

    @Test
    void givenUserWithSameUsernameExistsThenRegistrationFails() {
        users.save(new User("existinguser", "irrelevantPassword"));
        assertEquals(false, registerUser.execute("existinguser", "password"));
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
            if (users.exists(username)) {
                return false;
            }
            users.save(new User(username, password));
            return true;
        }
    }

    private static class UserRepository {
        private final List<User> users = new ArrayList<>();

        boolean exists(String username) {
            return users
                .stream()
                .anyMatch(u -> u.username.equals(username));
        }

        void save(User user) {
            users.add(user);
        }
    }

    public record User(String username, String password) {
    }
}
