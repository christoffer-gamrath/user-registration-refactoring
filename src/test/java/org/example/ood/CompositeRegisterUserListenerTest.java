package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class CompositeRegisterUserListenerTest {
    @RegisterExtension
    final JUnit5Mockery context = new JUnit5Mockery();

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
}
