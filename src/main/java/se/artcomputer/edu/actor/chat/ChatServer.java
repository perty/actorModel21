package se.artcomputer.edu.actor.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public interface ChatServer {
 
    sealed interface ClientManagerProtocol { }
    record ClientConnected(TypedActor.Address<ChannelActors.WriteLine> addr) implements ClientManagerProtocol { }
    record LineRead(String payload) implements ClientManagerProtocol {}
 
    TypedActor.System system = new TypedActor.System();
    int PORT = 4444;
 
    static void main(String... args) throws IOException {
        var serverSocket = new ServerSocket(PORT);
        out.printf("Server started at %s.\n", serverSocket.getLocalSocketAddress());
 
        TypedActor.Address<ClientManagerProtocol> clientManager =
                system.actorOf(self -> ChatServer::clientManager);
 
        while (true) {
            var socket = serverSocket.accept();
            var channel = new ChannelActors(socket);
            ChannelActors.Reader<ClientManagerProtocol> reader =
                    channel.reader(clientManager, LineRead::new);
            reader.start(system.actorOf(self -> msg -> reader.read(self)));
            TypedActor.Address<ChannelActors.WriteLine> writer = system.actorOf(self -> channel::writer);
            clientManager.tell(new ClientConnected(writer));
        }
    }
 
    static TypedActor.Effect<ClientManagerProtocol> clientManager(ClientManagerProtocol msg) {
        return clientManager(msg, new ArrayList<>());
    }
 
    static TypedActor.Effect<ClientManagerProtocol> clientManager(ClientManagerProtocol msg, List<TypedActor.Address<ChannelActors.WriteLine>> clients) {
        return switch (msg) {
            case ClientConnected(var address) -> {
                clients.add(address);
                yield TypedActor.Become(m -> clientManager(m, clients));
            }
            case LineRead(var payload) -> {
                clients.forEach(client -> client.tell(new ChannelActors.WriteLine(payload)));
                yield TypedActor.Stay();
            }
        };
    }
}