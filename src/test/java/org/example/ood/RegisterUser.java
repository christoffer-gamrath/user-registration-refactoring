package org.example.ood;

class RegisterUser {
    private final RegisterUserTest.UserRepository users;
    private final Listener listener;
    private final RegisterUserTest.UserValidator validator;

    public RegisterUser(RegisterUserTest.UserRepository users, Listener listener, RegisterUserTest.UserValidator validator) {
        this.users = users;
        this.listener = listener;
        this.validator = validator;
    }

    public void execute(String username, String password, String email) {
        if (!validator.isValid(username, password, email)) {
            listener.onFailure();
            return;
        }
        final var user = new RegisterUserTest.User(username, password, email);
        users.save(user);
        listener.onSuccess(user);
    }

    public interface Listener {
        void onSuccess(RegisterUserTest.User user);

        void onFailure();
    }
}
