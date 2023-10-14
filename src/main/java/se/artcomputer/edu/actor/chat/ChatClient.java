package se.artcomputer.edu.actor.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

public interface ChatClient {
 
    String host = "localhost";
    int portNumber = 4444;
    TypedActor.System system = new TypedActor.System();
 
    sealed interface ClientProtocol { }
    record Message(String user, String text) implements ClientProtocol {}
    record LineRead(String payload) implements ClientProtocol {}
 
    static void main(String[] args) throws IOException {
        var userName = args[0];
 
        var socket = new Socket(host, portNumber);
        var channel = new ChannelActors(socket);
        TypedActor.Address<ChannelActors.WriteLine> writer = system.actorOf(self -> channel::writer);
        TypedActor.Address<ClientProtocol> client = system.actorOf(self -> msg -> client(writer, msg));
        ChannelActors.Reader<ClientProtocol> reader = channel.reader(client, LineRead::new);
        reader.start(system.actorOf(self -> msg -> reader.read(self)));
 
        out.printf("Login............... %s\n", userName);
 
        var scann = new Scanner(in);
        while (true) {
            if (scann.nextLine() instanceof String line && !line.isBlank()) {
                client.tell(new Message(userName, line));
            }
        }
    }
 
    static TypedActor.Effect<ClientProtocol> client(TypedActor.Address<ChannelActors.WriteLine> writer, ClientProtocol msg) {
        var mapper = new ObjectMapper();
 
        try {
            switch (msg) {
                case Message m -> {
                    var jsonMsg = mapper.writeValueAsString(m);
                    writer.tell(new ChannelActors.WriteLine(jsonMsg));
                }
                case LineRead(var payload) -> {
                    switch (mapper.readValue(payload.trim(), Message.class)) {
                        case Message(var user, var text) -> out.printf("%s > %s\n", user, text);
                    }
                }
            }
            return TypedActor.Stay();
        } catch(JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}