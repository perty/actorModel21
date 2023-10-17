# Actor Model in Java

## The Klang minimalist gist

In folder `src/main/java/se/artcomputer/edu/actor` there is a gist from 
[Viktor Klang](https://gist.github.com/viktorklang), a member of Akka project. I have removed some lint.

It is a minimalist Actor implementation. It is for reference only.

## Evacchi

From this [blog post](https://evacchi.github.io/posts/2021/10/12/learn-you-an-actor-system-for-great-good-with-java-17-records-switch-expressions-and-jbang/)
by [Edoardo Vacchi](https://gist.github.com/evacchi).


## Actor model with Chat example

Adapted from the blogs [ACTORS AND VIRTUAL THREADS, A MATCH MADE IN HEAVEN?](https://www.javaadvent.com/2022/12/actors-and-virtual-threads-a-match-made-in-heaven.html)
and [Type You An Actor Runtime For Greater Good!](https://evacchi.github.io/posts/2022/02/13/type-you-an-actor-runtime-for-greater-good-with-java-17-records-switch-expressions-and-jbang/)

Demonstrates the use of Virtual Threads to implement an Actor model.

An Actor is a routine and a message queue.

The System puts a message in the Actor's queue. Some time later the Actor gets the chance 
to read the message and applies the routine.

The routine is called Behavior and takes a message and creates an Effect. 

An Effect is the transition to the new state of the Actor, expressed as a new Behavior.

Effect: current Behavior -> new Behavior.

Standard Effects are Stay and Die. Stay means no change while Die is a shutdown of the
Actor.

## Usage

1. Build with `mvn package`.
1. Start the server with `sh start-server.sh`.
1. Start clients with `sh start-client.sh` _username_.