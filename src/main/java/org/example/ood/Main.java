package org.example.ood;

public class Main {
    public static void main(String[] args) {
        final var registerUser = new RegisterUser(
            new InMemoryUserRepository(),
            new DummyEmailer()
        );
        final var success = registerUser.execute("username", "securepassword", "email@example.com");
        if (success) {
            System.out.printf("User registered\n");
        } else {
            System.out.printf("User registration failed\n");
        }
    }
}
