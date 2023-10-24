package se.artcomputer.edu.actor.chatgpt;

public class PingPongTest {
    public static void main(String[] args) {
        ActorSystem system = new ActorSystem();

        PingPongActor actorLeft = new PingPongActor(system, "Left");
        PingPongActor actorRight = new PingPongActor(system, "Right");
        actorLeft.setBuddyActor(actorRight);
        actorRight.setBuddyActor(actorLeft);

        system.send(actorLeft, "ping");
    }
}
