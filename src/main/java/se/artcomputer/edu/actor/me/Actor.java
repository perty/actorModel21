package se.artcomputer.edu.actor.me;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Actor implements Runnable {
    public static final Message SHUTDOWN = new Message("__SHUTDOWN__");
    private final BlockingQueue<Message> mailBox;
    private final int number;

    public Actor(int number) {
        this.number = number;
        mailBox = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        try {
            while (true) {
                log("Checking the mailbox");
                Message message = mailBox.take();
                log("Got message '%s'".formatted(message.payload()));
                processMessage(message);
            }
        } catch (InterruptedException exception) {
            log("Was interrupted");
        }
    }

    private void processMessage(Message message) throws InterruptedException {
        log("Processing message '%s'".formatted(message.payload()));
        if (message.equals(SHUTDOWN)) {
            throw new InterruptedException("SHUTDOWN");
        }
        Thread.sleep(500);
        log("Done processing message '%s'".formatted(message.payload()));
    }

    private void log(String logEntry) {
        System.out.printf("%d: %s%n", number, logEntry);
    }

    public void message(Message message) {
        mailBox.add(message);
    }

    public record Message(String payload) {

    }
}
