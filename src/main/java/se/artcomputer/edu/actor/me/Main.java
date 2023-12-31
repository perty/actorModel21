package se.artcomputer.edu.actor.me;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: NumberOfActors");
            System.exit(2);
        }
        long numberOfActors = Long.parseLong(args[0]);
        System.out.printf("Starting %d actors.%n", numberOfActors);
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> actorFutures = new ArrayList<>();
        List<Actor> actors = new ArrayList<>();
        try {
            for (int n = 0; n < numberOfActors; n++) {
                Actor actor = new Actor(n);
                actors.add(actor);
                actorFutures.add(executorService.submit(actor));
            }
            System.out.println("Done starting actors. Sending messages.");
            for (Actor actor: actors) {
                actor.message(new Actor.Message("Message for actor %d".formatted(actor.getNumber())));
                actor.message(Actor.SHUTDOWN);
            }
            for (long done = 0; done < numberOfActors; done = numberOfDoneActors(actorFutures)) {
                System.out.printf(">>>>>>>> %d actors are done <<<<<<<<<<%n", done);
                Thread.sleep(1_000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static long numberOfDoneActors(List<Future<?>> actorFutures) {
        return actorFutures.stream().filter(Future::isDone).count();
    }
}
