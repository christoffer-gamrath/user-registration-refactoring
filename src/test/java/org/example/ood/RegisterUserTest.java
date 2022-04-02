package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    @RegisterExtension
    final JUnit5Mockery context = new JUnit5Mockery();

    private final Emailer emailer = context.mock(Emailer.class);
    private final UserRepository users = new UserRepository();
    private final RegisterUser registerUser = new RegisterUser(users, emailer);

    @Test
    void givenValidUsernameAndPasswordThenTheUserIsRegisteredAndItSendsTheUserAWelcomeEmail() {
        context.checking(new Expectations() {{
            oneOf(emailer).send("user@example.com", "us@example.org", "Welcome, username! Let me explain at length how to get started using this service! ...");
        }});
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
        users.save(new User("existinguser", "irrelevantPassword", "irrelevant@example.com"));
        assertEquals(false, registerUser.execute("existinguser", "securepassword", "user@example.com"));
    }

    private static class RegisterUser {
        private final UserRepository users;
        private final Emailer emailer;

        public RegisterUser(UserRepository users, Emailer emailer) {
            this.users = users;
            this.emailer = emailer;
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
            users.save(new User(username, password, email));
            emailer.send(email, "us@example.org", String.format("Welcome, username! Let me explain at length how to get started using this service! ..."));
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

    public record User(String username, String password, String email) {
    }

    public interface Emailer {
        void send(String to, String from, String message);
    }
}
