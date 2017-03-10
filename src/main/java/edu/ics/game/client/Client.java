package edu.ics.game.client;

import java.net.URISyntaxException;

import org.json.JSONArray;
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
				socket.emit("state");
				socket.emit("name", "Someone");
				socket.emit("join", "room1");
				socket.emit("ready");
				
				int[] move = {1, 1};
				JSONObject data = new JSONObject();
				try {
					data.put("move", new JSONArray(move));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				socket.emit("play", data);
//				socket.emit("wait");
//				socket.emit("leave", "room1");
//				socket.emit("state");
//				socket.disconnect();
			}
		}).on("message", new Emitter.Listener() {
			public void call(Object... args) {
				System.out.println("-- System Message --");
				System.out.println((String)args[0]);
			}
		}).on("chat", new Emitter.Listener() {
			public void call(Object... args) {
				System.out.println("-- Chat Message --");
				JSONObject data = (JSONObject)args[0];
				System.out.println(data.toString());			
			}
		}).on("lobby", new Emitter.Listener() {
			public void call(Object... args) {
				System.out.println("-- Lobby State --");
				JSONArray data = (JSONArray)args[0];
				System.out.println(data.toString());
			}
		}).on("room", new Emitter.Listener() {
			public void call(Object... args) {
				System.out.println("-- Room State --");
				JSONObject data = (JSONObject)args[0];
				System.out.println(data.toString());
			}
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			public void call(Object... args) {}
		});
		
		socket.connect();

	}
}
