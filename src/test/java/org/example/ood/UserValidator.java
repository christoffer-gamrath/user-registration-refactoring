package org.example.ood;

public interface UserValidator {
    boolean isValid(String username, String password, String email);
}
