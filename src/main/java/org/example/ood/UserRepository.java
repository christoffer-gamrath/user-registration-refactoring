package org.example.ood;

public interface UserRepository {
    boolean exists(String username);

    void save(User user);
}
