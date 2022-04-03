package org.example.ood;

import java.util.ArrayList;
import java.util.List;

class InMemoryUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public boolean exists(String username) {
        return users
            .stream()
            .anyMatch(u -> u.username().equals(username));
    }

    @Override
    public void save(User user) {
        users.add(user);
    }
}
