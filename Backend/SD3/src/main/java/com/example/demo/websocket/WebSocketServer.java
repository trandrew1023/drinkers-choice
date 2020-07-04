package com.example.demo.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Component;

@ServerEndpoint("/websocket/{username}")
@Component
public class WebSocketServer {
	private static Map<Session, String> sessionUsernameMap = new HashMap<>();
	private static Map<String, Session> usernameSessionMap = new HashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException {
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		// Handle new messages
		String username = sessionUsernameMap.get(session);

		if (message.startsWith("@"))
		{
			String destUsername = message.split(" ")[0].substring(1); // don't do this in your code!
			sendMessageToParticularUser(destUsername,message);
			sendMessageToParticularUser(username,message);
		} else
		{
			broadcast(message);
		}
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		String username = sessionUsernameMap.get(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {

	}

	private void sendMessageToParticularUser(String username, String message) {
		try {
			usernameSessionMap.get(username).getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void broadcast(String message) throws IOException {
		sessionUsernameMap.forEach((session, username) -> {
			synchronized (session) {
				try {
					session.getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}

//
//@ServerEndpoint("/websocket/{username}")
//@Component
//public class WebSocketServer {
//	private Session session;
//	private static Set<WebSocketServer> chatEndpoints = new CopyOnWriteArraySet<>();
//	private static Map<String, String> users = new HashMap();
//
//	@OnOpen
//	public void onOpen(Session session, @PathParam("username") String username) throws IOException {
//		this.session = session;
//		chatEndpoints.add(this);
//		users.put(session.getId(), username);
//	}
//
//	@OnMessage
//	public void onMessage(Session session, String message) throws IOException {
//		String username = users.get(session.getId());
//		String echo = message;
//		broadcast(echo);
//	}
//
//	@OnClose
//	public void onClose(Session session) throws IOException {
//		String username = users.get(session.getId());
//		chatEndpoints.remove(this);
//	}
//
//	@OnError
//	public void onError(Session session, Throwable throwable) {
//		// Do error handling here
//	}
//
//	private void sendMessageToPArticularUser(Session session, String message) {
//
//		try {
//			session.getBasicRemote().sendText(message);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static void broadcast(String message) throws IOException {
//
//		chatEndpoints.forEach(endpoint -> {
//			synchronized (endpoint) {
//				try {
//					endpoint.session.getBasicRemote().sendText(message);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//
//}
//
//package com.example.demo.websocket;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//import javax.websocket.OnClose;
//import javax.websocket.OnError;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//
//import org.apache.logging.log4j.message.Message;
//import org.springframework.stereotype.Component;
//
//@ServerEndpoint("/websocket/{chatroom}")
//@Component
//public class WebSocketServer {
//	private Session session;
//	private static Set<WebSocketServer> chatEndpoints = new CopyOnWriteArraySet<>();
//	private static Map<String, String> users = new HashMap();
//	private static Map<String, List<Session>> chatrooms = new HashMap();
//
//	@OnOpen
//	public void onOpen(Session session, @PathParam("chatroom") String chatroom) throws IOException {
//		this.session = session;
//		chatEndpoints.add(this);
//		users.put(session.getId(), chatroom);
//		if(chatrooms.containsKey(chatroom)) {
//			chatrooms.get(chatroom).add(session);
//		}
//		else {
//			List<Session> temp = new ArrayList<>();
//			temp.add(session);
//			chatrooms.put(chatroom, temp);
//		}
//	}
//
//	@OnMessage
//	public void onMessage(Session session, String message) throws IOException {
//		sendMessageToChatroom(session, message);
//	}
//
//	@OnClose
//	public void onClose(Session session) throws IOException {
//		chatEndpoints.remove(this);
//		users.remove(this);
//	}
//
//	@OnError
//	public void onError(Session session, Throwable throwable) throws IOException {
//		session.getBasicRemote().sendText("ERROR BOI");
//	}
//
//	private void sendMessageToChatroom(Session session, String message) throws IOException {
//		List<Session> temp = chatrooms.get(users.get(session.getId()));
//		for(int i = 0; i < temp.size(); i++) {
//			temp.get(i).getBasicRemote().sendText(message);
//		}
//	}
//}
