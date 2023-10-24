package se.artcomputer.edu.actor.chatgpt;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PingPongActor implements Actor {
    private final BlockingQueue<Object> mailbox = new LinkedBlockingQueue<>();
    private final ActorSystem system;
    private final String name;

    private int messageCount;
    private Actor buddyActor;

    public void setBuddyActor(Actor buddyActor) {
        this.buddyActor = buddyActor;
        messageCount = 0;
    }

    public PingPongActor(ActorSystem system, String name) {
        this.system = system;
        this.name = name;
    }

    @Override
    public void onMessage(Object message) {
        messageCount++;
        System.out.println(name + " received: " + message + ". Messages " + messageCount);
        if (messageCount > 10) {
            system.send(buddyActor, Actor.SHUTDOWN);
        }
        switch (message.toString()) {
            case "pong" -> system.send(buddyActor, "ping");
            case "ping" -> system.send(buddyActor, "pong");
            case Actor.SHUTDOWN -> shutdown();
        }
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

    @Override
    public String toString() {
        return name;
    }
}
