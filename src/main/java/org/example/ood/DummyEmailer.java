package org.example.ood;

class DummyEmailer implements Emailer {
    @Override
    public void send(String to, String from, String message) {
        // Do nothing
    }
}
