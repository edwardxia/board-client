package edu.ics.game.client;

import java.net.URISyntaxException;

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
				socket.emit("setName", "Test user");
				socket.emit("joinRoom", "my room");
				socket.emit("joinRoom", "another room");
				socket.emit("leaveRoom", "another room");

				// socket.disconnect();
			}
		}).on("message", new Emitter.Listener() {
			public void call(Object... args) {
				System.out.println("message received");
			}
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			public void call(Object... args) {}
		});
		
		socket.connect();

	}
}
