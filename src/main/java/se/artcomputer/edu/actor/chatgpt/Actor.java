package se.artcomputer.edu.actor.chatgpt;

import java.util.concurrent.BlockingQueue;

public interface Actor {
    String SHUTDOWN = "shutdown";

    void onMessage(Object message) throws InterruptedException;
    void processMailbox();
    BlockingQueue<Object> getMailbox();

    default void shutdown() {
        getMailbox().clear();
    }
}