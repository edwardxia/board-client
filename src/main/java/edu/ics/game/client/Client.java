package edu.ics.game.client;

import java.net.URISyntaxException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Client {
	private Socket socket;
	private Scanner scanner;
	private Thread playThread = null;

	public Client() {
		scanner = new Scanner(System.in);

		try {
			socket = IO.socket("http://localhost:3000/TicTacToe");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			public void call(Object... args) {
				System.out.println("Please enter your name: ");
				socket.emit("name", scanner.nextLine());
				socket.emit("state");
				socket.emit("join", "room1");
				socket.emit("ready");
				//					socket.emit("wait");
				//					socket.emit("leave", "room1");
				//					socket.emit("state");
				//					socket.disconnect();
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

				try {
					if (data.get("status").equals("PLAYING")) {
						if (playThread == null) {
							playThread = new Thread(() -> {
								play();
							});
							playThread.start();
						}
					} else if (playThread != null) {
						playThread.interrupt();
						playThread = null;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			public void call(Object... args) {}
		});

		socket.connect();
	}
	
	public void play() {
		// In GUI this will not be a while loop,
		// but an event listener
		while (true) {
			System.out.println("input x[space]y: ");
			
			int x = -1, y = -1;
			x = scanner.nextInt();
			y = scanner.nextInt();
			scanner.nextLine();
			
			int[] move = {x, y};
			JSONObject movedata = new JSONObject();
			try {
				movedata.put("move", new JSONArray(move));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			socket.emit("play", movedata);
		}
	}
}
