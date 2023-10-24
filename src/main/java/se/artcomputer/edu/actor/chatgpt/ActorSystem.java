package se.artcomputer.edu.actor.chatgpt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ActorSystem {
    private final Map<Actor, ExecutorService> actorExecutors = new ConcurrentHashMap<>();

    public void send(Actor actor, Object message) {
        ExecutorService executorService = actorExecutors.computeIfAbsent(actor, a -> Executors.newVirtualThreadPerTaskExecutor());
        if (!executorService.isShutdown()) {
            if (message == Actor.SHUTDOWN) {
                executorService.shutdown();
            } else {
                actor.getMailbox().add(message);
                actor.processMailbox();
            }
        }
        if (activeExecutors() <= 0) {
            shutdown();
        }
    }

    public void shutdown() {
        for (ExecutorService executor : actorExecutors.values()) {
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS); // Wait indefinitely
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private long activeExecutors() {
        return actorExecutors.values().stream().filter(executorService -> !executorService.isShutdown()).count();
    }
}
