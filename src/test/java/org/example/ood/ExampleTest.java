package org.example.ood;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class ExampleTest {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final Runnable runnable = context.mock(Runnable.class);

    @Test
    void jmockWorks() {
        context.checking(new Expectations() {{
            this.oneOf(runnable).run();
        }});
        runnable.run();
    }
}
