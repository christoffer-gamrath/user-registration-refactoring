package org.example.ood;

class UserValidatorImpl implements UserValidator {
    private final UserRepository users;

    public UserValidatorImpl(UserRepository users) {
        this.users = users;
    }

    @Override
    public boolean isValid(String username, String password, String email) {
        if ("".equals(username) || "".equals(password) || "".equals(email)) {
            return false;
        } else if (users.exists(username)) {
            return false;
        } else if (password.length() < 14) {
            return false;
        }
        return true;
    }
}
