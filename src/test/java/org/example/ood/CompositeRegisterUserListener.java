package org.example.ood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CompositeRegisterUserListener implements RegisterUser.Listener {
    private final List<RegisterUser.Listener> listeners = new ArrayList<>();

    public CompositeRegisterUserListener(RegisterUser.Listener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    @Override
    public void onSuccess(User user) {
        listeners.forEach(l -> l.onSuccess(user));
    }

    @Override
    public void onFailure() {
        listeners.forEach(RegisterUser.Listener::onFailure);
    }
}
