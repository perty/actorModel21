//JAVA 17
//JAVAC_OPTIONS --enable-preview --release 17
//JAVA_OPTIONS  --enable-preview
//REPOS jitpack=https://jitpack.io/
//DEPS com.github.evacchi:min-java-actors:main-SNAPSHOT

package se.artcomputer.edu.actor.evacchi;

import java.util.concurrent.Executors;

import static java.lang.System.*;
import static se.artcomputer.edu.actor.evacchi.Actor.*;

record Ping(Address sender) {
}

record Pong(Address sender) {
}

record DeadlyPong(Address sender) {
}

public class PingPong {
    public static void main(String... args) {
        new PingPong().run();
    }

    void run() {
        var actorSystem = new Actor.System(Executors.newCachedThreadPool());
        var ponger = actorSystem.actorOf(self -> msg -> pongerBehavior(self, msg, 0));
        var pinger = actorSystem.actorOf(self -> msg -> pingerBehavior(self, msg));
        ponger.tell(new Ping(pinger));
    }

    Effect pongerBehavior(Address self, Object msg, int counter) {
//        return switch (msg) {
//            case Ping p && counter < 10 -> {
//                out.println("ping! 👉");
//                p.sender().tell(new Pong(self));
//                yield Become(m -> pongerBehavior(self, m, counter + 1));
//            }
//            case Ping p -> {
//                out.println("ping! 💀");
//                p.sender().tell(new DeadlyPong(self));
//                yield Die;
//            }
//            default -> Stay;
//        };
        if (msg instanceof Ping p) {
            if (counter < 10) {
                out.println("ping! 👉");
                p.sender().tell(new Pong(self));
                return Become(m -> pongerBehavior(self, m, counter + 1));
            }
            out.println("ping! 💀");
            p.sender().tell(new DeadlyPong(self));
            return Die;
        }
        return Stay;
    }

    Effect pingerBehavior(Address self, Object msg) {
        return switch (msg) {
            case Pong p -> {
                out.println("pong! 👈");
                p.sender().tell(new Ping(self));
                yield Stay;
            }
            case DeadlyPong p -> {
                out.println("pong! 😵");
                p.sender().tell(new Ping(self));
                yield Die;
            }
            default -> Stay;
        };
    }
}
