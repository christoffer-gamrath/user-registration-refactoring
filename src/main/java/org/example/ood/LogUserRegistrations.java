package org.example.ood;

class LogUserRegistrations implements RegisterUser.Listener {
    @Override
    public void onSuccess(User user) {
        System.out.printf("User '%s' registered\n", user.username());
    }

    @Override
    public void onFailure() {
        System.out.printf("User registration failed\n");
    }
}
