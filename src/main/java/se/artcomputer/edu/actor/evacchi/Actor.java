package se.artcomputer.edu.actor.evacchi;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import static java.lang.System.out;

public interface Actor {
    interface Behavior extends Function<Object, Effect> {}
    interface Effect extends Function<Behavior, Behavior> {}
    interface Address { Address tell(Object msg); }

    static Effect Become(Behavior like) { return old -> like; }
    static Effect Stay = old -> old;
    static Effect Die = Become(msg -> { out.println("Dropping msg [" + msg + "] due to severe case of death."); return Stay; });

    record System(ExecutorService executorService) {
        public Address actorOf(Function<Address, Behavior> initial) {
            abstract class AtomicRunnableAddress implements Address, Runnable
                { final AtomicInteger on = new AtomicInteger(0); }
            var addr = new AtomicRunnableAddress() {
                final ConcurrentLinkedQueue<Object> mb = new ConcurrentLinkedQueue<>();
                Behavior behavior = m -> (m instanceof Address self) ? Become(initial.apply(self)) : Stay;
                public Address tell(Object msg) { mb.offer(msg); async(); return this; }
                public void run() {
                    try { if (on.get() == 1) { var m = mb.poll(); if (m!=null) { behavior = behavior.apply(m).apply(behavior); } }}
                    finally { on.set(0); async(); }}
                void async() {
                    if (!mb.isEmpty() && on.compareAndSet(0, 1)) {
                        try { executorService.execute(this); }
                        catch (Throwable t) { on.set(0); throw t; }}}
            };
            return addr.tell(addr);
        }
    }
}