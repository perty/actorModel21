package se.artcomputer.edu.actor.chat;

import java.io.*;
import java.net.Socket;
import java.util.function.Function;

class ChannelActors {
    record WriteLine(String payload) {
    }

    record PerformReadLine() {
    }

    final BufferedReader in;
    final PrintWriter out;

    ChannelActors(Socket socket) {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    final class Reader<T> {
        final Function<String, T> fn;
        final TypedActor.Address<T> addr;

        public Reader(TypedActor.Address<T> addr, Function<String, T> fn) {
            this.fn = fn;
            this.addr = addr;
        }

        void start(TypedActor.Address<PerformReadLine> readActor) {
            readActor.tell(new PerformReadLine());
        }

        TypedActor.Effect<PerformReadLine> read(TypedActor.Address<PerformReadLine> self) {
            try {
                return switch (in.readLine()) {
                    case null -> {
                        yield TypedActor.Die();
                    }
                    case String line -> {
                        addr.tell(fn.apply(line));
                        self.tell(new PerformReadLine());
                        yield TypedActor.Stay();
                    }
                };
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    <T> Reader<T> reader(TypedActor.Address<T> addr, Function<String, T> fn) {
        return new Reader<>(addr, fn);
    }

    TypedActor.Effect<WriteLine> writer(WriteLine wl) {
        out.println(wl.payload());
        return TypedActor.Stay();
    }
}