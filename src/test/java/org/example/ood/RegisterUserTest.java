package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RegisterUserTest {
    @RegisterExtension
    final JUnit5Mockery context = new JUnit5Mockery();

    private final Emailer emailer = context.mock(Emailer.class);
    private final UserRepository users = context.mock(UserRepository.class);
    private final RegisterUser.Listener listener = context.mock(RegisterUser.Listener.class);
    private final UserValidator userValidator = context.mock(UserValidator.class);
    private final RegisterUser registerUser = new RegisterUser(users, listener, userValidator);

    @Test
    void givenValidUsernameAndPasswordThenTheUserIsRegisteredAndItSendsTheUserAWelcomeEmail() {
        context.checking(new Expectations() {{
            allowing(userValidator).isValid("username", "securepassword", "user@example.com"); will(returnValue(true));
            final var user = new User("username", "securepassword", "user@example.com");
            oneOf(users).save(user);
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
    void registrationFailsIfValidationFails() {
        context.checking(new Expectations() {{
            allowing(userValidator).isValid("a", "b", "c"); will(returnValue(false));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("a", "b", "c");
    }

    @Test
    void emptyUsernameIsInvalid() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
        }});
        final var userValidator = new UserValidatorImpl(users);
        assertFalse(userValidator.isValid("", "securepassword", "user@example.com"));
    }

    @Test
    void emptyPasswordIsInvalid() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
        }});
        final var userValidator = new UserValidatorImpl(users);
        assertFalse(userValidator.isValid("username", "", "user@example.com"));
    }

    @Test
    void shortPasswordIsInvalid() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
        }});
        final var userValidator = new UserValidatorImpl(users);
        assertFalse(userValidator.isValid("username", "short", "user@example.com"));
    }

    @Test
    void emptyEmailIsInvalid() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
        }});
        final var userValidator = new UserValidatorImpl(users);
        assertFalse(userValidator.isValid("username", "securepassword", ""));
    }

    @Test
    void userIsInvalidIfUserWithSameUsernameAlreadyExists() {
        context.checking(new Expectations() {{
            allowing(users).exists("existinguser"); will(returnValue(true));
        }});
        final var userValidator = new UserValidatorImpl(users);
        assertFalse(userValidator.isValid("existinguser", "securepassword", "user@example.com"));
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
        private final UserValidator validator;

        public RegisterUser(UserRepository users, Listener listener, UserValidator validator) {
            this.users = users;
            this.listener = listener;
            this.validator = validator;
        }

        public void execute(String username, String password, String email) {
            if (!validator.isValid(username, password, email)) {
                listener.onFailure();
                return;
            }
            final var user = new User(username, password, email);
            users.save(user);
            listener.onSuccess(user);
        }

        public interface Listener {
            void onSuccess(User user);

            void onFailure();
        }
    }

    public interface UserValidator {
        boolean isValid(String username, String password, String email);
    }

    private static class UserValidatorImpl implements UserValidator {
        private final UserRepository users;

        public UserValidatorImpl(UserRepository users) {
            this.users = users;
        }

        @Override
        public boolean isValid(String username, String password, String email) {
            if ("".equals(username) || "".equals(password) || "".equals(email)) {
                return false;
            } else if (users.exists(username)) {
                return false;
            } else if (password.length() < 14) {
                return false;
            }
            return true;
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
