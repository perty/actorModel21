package se.artcomputer.edu.actor.klang;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
public class Actor { // Visibility is achieved by volatile-piggybacking of reads+writes to "on"
  public interface Fun<T, R> { R apply(T t); } // Simple Function interface for Java
  public interface Effect extends Fun<Behavior, Behavior> { } // An Effect returns a Behavior given a Behavior
  public interface Behavior extends Fun<Object, Effect> { } // A Behavior is a message (Object) which returns the behavior for the next message
  public interface Address { Address tell(Object msg); } // An Address is somewhere you can send messages
  static abstract class AtomicRunnableAddress implements Runnable, Address { protected final AtomicInteger on = new AtomicInteger(); } // Defining a composite of AtomcInteger, Runnable and Address
  public static Effect Become(final Behavior behavior) { return old -> behavior; } // Become is an Effect that returns a captured Behavior no matter what the old Behavior is
  public final static Effect Stay = old -> old; // Stay is an Effect that returns the old Behavior when applied.
  public final static Effect Die  = Become(msg -> Stay); // Die is an Effect which replaces the old Behavior with a new one which does nothing, forever.
  public static Address create(final Fun<Address, Behavior> initial, final Executor e) {
    final Address a = new AtomicRunnableAddress() {
      private final ConcurrentLinkedQueue<Object> mb = new ConcurrentLinkedQueue<>();
      private Behavior behavior = msg -> (msg instanceof Address) ? Become(initial.apply((Address)msg)) : Stay;
      public Address tell(Object msg) { if (mb.offer(msg)) async(); return this; }
      public void run() { if(on.get() == 1) { try { final Object m = mb.poll(); if(m != null) behavior = behavior.apply(m).apply(behavior); } finally { on.set(0); async(); } } }
      private void async() { if(!mb.isEmpty() && on.getAndSet(1) == 0) try { e.execute(this); } catch(RuntimeException re) { on.set(0); throw re; } }
    };
    return a.tell(a); // Make self-aware
  }
}