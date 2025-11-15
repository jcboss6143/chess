package server.websocket;

import java.util.Vector;
import java.util.Map;

import com.google.gson.Gson;
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
        ConcurrentHashMap<Session, Session> gameSessions = connections.get(gameID);
        gameSessions.remove(session);
    }

    public void broadcast(Integer gameID, Session session, ServerMessage notification, boolean excludeSession) throws IOException {
        String msg = new Gson().toJson(notification);
        if (session != null && !excludeSession) {
            session.getRemote().sendString(msg);
            return;
        }
        ConcurrentHashMap<Session, Session> gameSessions = connections.get(gameID);
        for (Session c : gameSessions.values()) {
            if (c.isOpen()) {
                if (session == null) {
                    c.getRemote().sendString(msg);
                }
                else if (!c.equals(session)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}