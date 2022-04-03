package org.example.ood;

class RegisterUser {
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
