package server.websocket;

import java.util.Vector;
import java.util.Map;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final Map<Integer, ConcurrentHashMap<Session, Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        ConcurrentHashMap<Session, Session> gameSessions = connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>());
        gameSessions.put(session, session);

    }

    public void remove(Integer gameID, Session session) {
        ConcurrentHashMap<Session, Session> gameSessions = connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>());
        gameSessions.remove(session);
    }

//    public void broadcast(Session excludeSession, Notification notification) throws IOException {
//        String msg = notification.toString();
//        for (Session c : connections.values()) {
//            if (c.isOpen()) {
//                if (!c.equals(excludeSession)) {
//                    c.getRemote().sendString(msg);
//                }
//            }
//        }
//    }
}