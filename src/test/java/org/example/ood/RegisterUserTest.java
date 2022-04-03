package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class RegisterUserTest {
    @RegisterExtension
    final JUnit5Mockery context = new JUnit5Mockery();

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
    void registrationFailsIfValidationFails() {
        context.checking(new Expectations() {{
            allowing(userValidator).isValid("a", "b", "c"); will(returnValue(false));
            oneOf(listener).onFailure();
        }});
        registerUser.execute("a", "b", "c");
    }
}
