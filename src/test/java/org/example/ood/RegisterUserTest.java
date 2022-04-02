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
        assertEquals(true, registerUser.execute("username", "securepassword", "user@example.com"));
        assertEquals(true, users.exists("username"));
    }

    @Test
    void givenEmptyUsernameThenRegistrationFails() {
        assertEquals(false, registerUser.execute("", "securepassword", "user@example.com"));
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenEmptyPasswordThenRegistrationFails() {
        assertEquals(false, registerUser.execute("username", "", "user@example.com"));
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenTooShortPasswordThenRegistrationFails() {
        assertEquals(false, registerUser.execute("username", "short", "user@example.com"));
    }

    @Test
    void givenEmptyEmailThenRegistrationFails() {
        assertEquals(false, registerUser.execute("username", "securepassword", ""));
    }

    @Test
    void givenUserWithSameUsernameExistsThenRegistrationFails() {
        users.save(new User("existinguser", "irrelevantPassword"));
        assertEquals(false, registerUser.execute("existinguser", "securepassword", "user@example.com"));
    }

    private static class RegisterUser {
        private final UserRepository users;

        public RegisterUser(UserRepository users) {
            this.users = users;
        }

        public boolean execute(String username, String password, String email) {
            if ("".equals(username) || "".equals(password) || "".equals(email)) {
                return false;
            }
            if (users.exists(username)) {
                return false;
            }
            if (password.length() < 14) {
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
