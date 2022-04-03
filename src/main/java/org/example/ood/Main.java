package org.example.ood;

public class Main {
    public static void main(String[] args) {
        final var users = new InMemoryUserRepository();
        final var listener = new CompositeRegisterUserListener(
            new SendWelcomeEmailOnSuccessfulRegistration(new DummyEmailer()),
            new LogUserRegistrations()
        );
        final var registerUser = new RegisterUser(
            users,
            listener,
            new UserValidatorImpl(users)
        );

        registerUser.execute("username", "securepassword", "email@example.com");
    }
}
