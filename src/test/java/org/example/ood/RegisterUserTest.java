package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    @RegisterExtension
    final JUnit5Mockery context = new JUnit5Mockery();

    private final Emailer emailer = context.mock(Emailer.class);
    private final UserRepository users = context.mock(UserRepository.class);
    private final RegisterUser.Listener listener = context.mock(RegisterUser.Listener.class);
    private final RegisterUser registerUser = new RegisterUser(users, emailer, listener);

    @Test
    void givenValidUsernameAndPasswordThenTheUserIsRegisteredAndItSendsTheUserAWelcomeEmail() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
            oneOf(users).save(new User("username", "securepassword", "user@example.com"));
            oneOf(emailer).send("user@example.com", "us@example.org", "Welcome, username! Let me explain at length how to get started using this service! ...");
            oneOf(listener).onSuccess();
        }});
        registerUser.execute("username", "securepassword", "user@example.com");
    }

    @Test
    void givenEmptyUsernameThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("", "securepassword", "user@example.com");
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenEmptyPasswordThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("username", "", "user@example.com");
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenTooShortPasswordThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
            oneOf(listener).onFailure();
        }});
         registerUser.execute("username", "short", "user@example.com");
    }

    @Test
    void givenEmptyEmailThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
            oneOf(listener).onFailure();
        }});
         registerUser.execute("username", "securepassword", "");
    }

    @Test
    void givenUserWithSameUsernameExistsThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("existinguser"); will(returnValue(true));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("existinguser", "securepassword", "user@example.com");
    }

    private static class RegisterUser {
        private final UserRepository users;
        private final Emailer emailer;
        private final Listener listener;
        private static final String welcomeMessage = "Welcome, %s! Let me explain at length how to get started using this service! ...";

        public RegisterUser(UserRepository users, Emailer emailer, Listener listener) {
            this.users = users;
            this.emailer = emailer;
            this.listener = listener;
        }

        public void execute(String username, String password, String email) {
            if ("".equals(username) || "".equals(password) || "".equals(email)) {
                listener.onFailure();
                return;
            }
            if (users.exists(username)) {
                listener.onFailure();
                return;
            }
            if (password.length() < 14) {
                listener.onFailure();
                return;
            }
            users.save(new User(username, password, email));
            sendWelcomeEmail(username, email);
            listener.onSuccess();
        }

        private void sendWelcomeEmail(String username, String email) {
            emailer.send(email, "us@example.org", String.format(welcomeMessage, username));
        }

        public interface Listener {
            void onSuccess();
            void onFailure();
        }
    }

    public interface UserRepository {
        boolean exists(String username);

        void save(RegisterUserTest.User user);
    }

    public record User(String username, String password, String email) {
    }

    public interface Emailer {
        void send(String to, String from, String message);
    }
}
