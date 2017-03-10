package edu.ics.game.client;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Client {
	private Socket socket;
	
	public Client() {
		try {
			socket = IO.socket("http://localhost:3000/TicTacToe");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			public void call(Object... args) {
				try {
					socket.emit("set", new JSONObject().put("name", "Someone"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				socket.emit("join", "room1");
				socket.emit("list", "rooms");
				socket.emit("leave", "room1");


//				socket.emit("joinRoom", "room2");
//				socket.emit("leaveRoom", "room2");
//				socket.disconnect();
			}
		}).on("message", new Emitter.Listener() {
			public void call(Object... args) {
				System.out.println((String)args[0]);
			}
		}).on("rooms", new Emitter.Listener() {
			public void call(Object... args) {
				JSONObject data = (JSONObject)args[0];
				System.out.println(data.toString());
			}
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			public void call(Object... args) {}
		});
		
		socket.connect();

	}
}
