package se.artcomputer.edu.actor.chatgpt;

public class EchoTest {
    public static void main(String[] args) {
        ActorSystem system = new ActorSystem();
        Actor actor = new EchoActor();

        system.send(actor, "Hello, Actor!");
        system.send(actor, EchoActor.SHUTDOWN);

        system.shutdown(); 
    }
}
