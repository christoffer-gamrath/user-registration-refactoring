package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserTest {
    @RegisterExtension
    final JUnit5Mockery context = new JUnit5Mockery();

    private final Emailer emailer = context.mock(Emailer.class);
    private final UserRepository users = context.mock(UserRepository.class);
    private final RegisterUser.Listener listener = context.mock(RegisterUser.Listener.class);
    private final RegisterUser registerUser = new RegisterUser(users, listener, new SendWelcomeEmailOnSuccessfulRegistration(emailer));

    @Test
    void givenValidUsernameAndPasswordThenTheUserIsRegisteredAndItSendsTheUserAWelcomeEmail() {
        context.checking(new Expectations() {{
            allowing(users).exists("username");
            will(returnValue(false));
            final var user = new User("username", "securepassword", "user@example.com");
            oneOf(users).save(user);
            oneOf(emailer).send("user@example.com", "us@example.org", "Welcome, username! Let me explain at length how to get started using this service! ...");
            oneOf(listener).onSuccess(user);
        }});
        registerUser.execute("username", "securepassword", "user@example.com");
    }

    @Test
    void sendWelcomeEmailOnSuccessfulRegistration() {
        context.checking(new Expectations() {{
            oneOf(emailer).send("user@example.com", "us@example.org", "Welcome, username! Let me explain at length how to get started using this service! ...");
        }});
        final var welcomeEmailer = new SendWelcomeEmailOnSuccessfulRegistration(emailer);
        welcomeEmailer.onSuccess(new User("username", "", "user@example.com"));
    }

    @Test
    void givenEmptyUsernameThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username");
            will(returnValue(false));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("", "securepassword", "user@example.com");
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenEmptyPasswordThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username");
            will(returnValue(false));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("username", "", "user@example.com");
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenTooShortPasswordThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username");
            will(returnValue(false));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("username", "short", "user@example.com");
    }

    @Test
    void givenEmptyEmailThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username");
            will(returnValue(false));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("username", "securepassword", "");
    }

    @Test
    void givenUserWithSameUsernameExistsThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("existinguser");
            will(returnValue(true));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("existinguser", "securepassword", "user@example.com");
    }

    @Test
    void compositeListener() {
        final var listenerA = context.mock(RegisterUser.Listener.class, "listenerA");
        final var listenerB = context.mock(RegisterUser.Listener.class, "listenerB");
        final var expectedUser = new User("user", "", "email");
        context.checking(new Expectations() {{
            oneOf(listenerA).onSuccess(expectedUser);
            oneOf(listenerB).onSuccess(expectedUser);
        }});

        final var composite = new CompositeRegisterUserListener(listenerA, listenerB);
        composite.onSuccess(expectedUser);
    }

    private static class RegisterUser {
        private final UserRepository users;
        private final Listener listener;
        private final SendWelcomeEmailOnSuccessfulRegistration welcomeEmailer;

        public RegisterUser(UserRepository users, Listener listener, SendWelcomeEmailOnSuccessfulRegistration welcomeEmailer) {
            this.users = users;
            this.listener = listener;
            this.welcomeEmailer = welcomeEmailer;
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
            final var user = new User(username, password, email);
            users.save(user);
            welcomeEmailer.onSuccess(user);
            listener.onSuccess(user);
        }

        public interface Listener {
            void onSuccess(User user);

            void onFailure();
        }
    }

    private static class SendWelcomeEmailOnSuccessfulRegistration implements RegisterUser.Listener {
        private static final String welcomeMessage = "Welcome, %s! Let me explain at length how to get started using this service! ...";
        private final Emailer emailer;

        public SendWelcomeEmailOnSuccessfulRegistration(Emailer emailer) {
            this.emailer = emailer;
        }

        private void sendWelcomeEmail(User user) {
            emailer.send(user.email, "us@example.org", String.format(welcomeMessage, user.username));
        }

        @Override
        public void onSuccess(User user) {
            sendWelcomeEmail(user);
        }

        @Override
        public void onFailure() {
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

    private static class CompositeRegisterUserListener implements RegisterUser.Listener {
        private final List<RegisterUser.Listener> listeners = new ArrayList<>();

        public CompositeRegisterUserListener(RegisterUser.Listener... listeners) {
            this.listeners.addAll(Arrays.asList(listeners));
        }

        @Override
        public void onSuccess(User user) {
            listeners.forEach(l -> l.onSuccess(user));
        }

        @Override
        public void onFailure() {
            listeners.forEach(RegisterUser.Listener::onFailure);
        }
    }
}
