package se.artcomputer.edu.actor.chatgpt;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class EchoActor implements Actor {
    public static final Object SHUTDOWN = new Object() {
        @Override
        public String toString() {
            return "SHUTDOWN";
        }
    };

    private final BlockingQueue<Object> mailbox = new LinkedBlockingQueue<>();

    @Override
    public void onMessage(Object message) throws InterruptedException {
        System.out.println("Received: " + message);
        Thread.sleep(500);
        System.out.println("Done!");
    }

    @Override
    public void processMailbox() {
        try {
            Object message = mailbox.take();
            onMessage(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public BlockingQueue<Object> getMailbox() {
        return mailbox;
    }
}
