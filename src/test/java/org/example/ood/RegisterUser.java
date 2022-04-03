package org.example.ood;

class RegisterUser {
    private final UserRepository users;
    private final Emailer emailer;
    private static final String welcomeMessage = "Welcome, %s! Let me explain at length how to get started using this service! ...";

    public RegisterUser(UserRepository users, Emailer emailer) {
        this.users = users;
        this.emailer = emailer;
    }

    public boolean execute(String username, String password, String email) {
        if ("".equals(username) || "".equals(password) || "".equals(email)) {
            return false;
        }
        if (users.exists(username)) {
            return false;
        }
        if (password.length() < 14) {
            return false;
        }
        users.save(new User(username, password, email));
        emailer.send(email, "us@example.org", String.format(welcomeMessage, username));
        return true;
    }
}
