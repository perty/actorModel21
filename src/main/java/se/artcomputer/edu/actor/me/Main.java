package se.artcomputer.edu.actor.me;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: NumberOfActors");
            System.exit(2);
        }
        long numberOfActors = Long.parseLong(args[0]);
        System.out.printf("Starting %d actors.%n", numberOfActors);
        for (int n = 0; n < numberOfActors; n++) {
            Actor actor = new Actor(n);
            Thread.startVirtualThread(actor);
            actor.message(new Actor.Message("Message for actor %d".formatted(n)));
            actor.message(Actor.SHUTDOWN);
        }
        System.out.println("Done starting actors");
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
