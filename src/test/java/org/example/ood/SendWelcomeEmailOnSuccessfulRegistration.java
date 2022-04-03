package org.example.ood;

class SendWelcomeEmailOnSuccessfulRegistration implements RegisterUser.Listener {
    private static final String welcomeMessage = "Welcome, %s! Let me explain at length how to get started using this service! ...";
    private final Emailer emailer;

    public SendWelcomeEmailOnSuccessfulRegistration(Emailer emailer) {
        this.emailer = emailer;
    }

    private void sendWelcomeEmail(User user) {
        emailer.send(user.email(), "us@example.org", String.format(welcomeMessage, user.username()));
    }

    @Override
    public void onSuccess(User user) {
        sendWelcomeEmail(user);
    }

    @Override
    public void onFailure() {
    }
}
