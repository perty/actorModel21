package se.artcomputer.edu.actor.chat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import static java.lang.System.out;

public interface TypedActor {
    interface Effect<T> {
        Behavior<T> transition(Behavior<T> next);
    }

    interface Behavior<T> {
        Effect<T> receive(T o);
    }

    interface Address<T> {
        Address<T> tell(T msg);
    }

    static <T> Effect<T> Become(Behavior<T> next) {
        return current -> next;
    }

    static <T> Effect<T> Stay() {
        return current -> current;
    }

    static <T> Effect<T> Die() {
        return Become(msg -> {
            out.println("Dropping msg [" + msg + "] due to severe case of death.");
            return Stay();
        });
    }

    record System() {
        private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        public <T> Address<T> actorOf(Function<Address<T>, Behavior<T>> initial) {
            var addr = new RunnableAddress<>(initial);
            executorService.execute(addr);
            return addr;
        }
    }

    class RunnableAddress<T> implements Address<T>, Runnable {

        final Function<Address<T>, Behavior<T>> initial;
        final LinkedBlockingQueue<T> mailbox = new LinkedBlockingQueue<>();

        RunnableAddress(Function<Address<T>, Behavior<T>> initial) {
            this.initial = initial;
        }

        public Address<T> tell(T msg) {
            mailbox.offer(msg);
            return this;
        }

        public void run() {
            Behavior<T> behavior = initial.apply(this);
            while (true) {
                try {
                    T message = mailbox.take();
                    Effect<T> effect = behavior.receive(message);
                    behavior = effect.transition(behavior);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}