package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorImplTest {
    @RegisterExtension
    final JUnit5Mockery context = new JUnit5Mockery();

    private final UserRepository users = context.mock(UserRepository.class);

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
}
