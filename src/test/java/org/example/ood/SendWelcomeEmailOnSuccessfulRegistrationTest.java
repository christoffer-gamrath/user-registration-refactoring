package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class SendWelcomeEmailOnSuccessfulRegistrationTest {
    @RegisterExtension
    final JUnit5Mockery context = new JUnit5Mockery();

    private final Emailer emailer = context.mock(Emailer.class);

    @Test
    void sendWelcomeEmailOnSuccessfulRegistration() {
        context.checking(new Expectations() {{
            oneOf(emailer).send("user@example.com", "us@example.org", "Welcome, username! Let me explain at length how to get started using this service! ...");
        }});
        final var welcomeEmailer = new SendWelcomeEmailOnSuccessfulRegistration(emailer);
        welcomeEmailer.onSuccess(new User("username", "", "user@example.com"));
    }
}
