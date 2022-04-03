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
    private final RegisterUser registerUser = new RegisterUser(users, emailer);

    @Test
    void givenValidUsernameAndPasswordThenTheUserIsRegisteredAndItSendsTheUserAWelcomeEmail() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
            oneOf(users).save(new User("username", "securepassword", "user@example.com"));
            oneOf(emailer).send("user@example.com", "us@example.org", "Welcome, username! Let me explain at length how to get started using this service! ...");
        }});
        assertEquals(true, registerUser.execute("username", "securepassword", "user@example.com"));
    }

    @Test
    void givenEmptyUsernameThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
        }});
        assertEquals(false, registerUser.execute("", "securepassword", "user@example.com"));
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenEmptyPasswordThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
        }});
        assertEquals(false, registerUser.execute("username", "", "user@example.com"));
        assertEquals(false, users.exists("username"));
    }

    @Test
    void givenTooShortPasswordThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
        }});
        assertEquals(false, registerUser.execute("username", "short", "user@example.com"));
    }

    @Test
    void givenEmptyEmailThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("username"); will(returnValue(false));
        }});
        assertEquals(false, registerUser.execute("username", "securepassword", ""));
    }

    @Test
    void givenUserWithSameUsernameExistsThenRegistrationFails() {
        context.checking(new Expectations() {{
            allowing(users).exists("existinguser"); will(returnValue(true));
        }});
        assertEquals(false, registerUser.execute("existinguser", "securepassword", "user@example.com"));
    }
}
