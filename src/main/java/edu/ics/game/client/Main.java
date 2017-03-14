package edu.ics.game.client;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

	Stage window;
	private Socket socket;
	private GameGridGUI game = null;

	public static void main(String[] args) {
		launch(args);
	}	

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			socket = IO.socket("http://localhost:3000/TicTacToe");
			// This demo client also support TicTacToe
			// Edit the URL above to play TicTacToe
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		game = new TicTacToeGUI(socket);
		
		System.out.println("Starting");
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			public void call(Object... args) {
				System.out.println("Connected");

				socket.emit("name", "Player 1");
				// `name` server event takes a string as parameter
				// # it updates players' name on server

				socket.emit("state");
				// `state` server event will request lobby state from the server
				// # the response is fetched in "lobby" event on the client

				// socket.emit("state", "roomName");
				// # `state` server event can optionally takes a string as parameter
				// # it will request the state of the give room name
				// # the response if fetched in "room" event on the client

				socket.emit("join", "room1");
				// `join` server event takes a string as roomName
				// # It add the user to the room. A new room will be created if not exist.

				// # User will be automatically removed from previous room when joining a new room.
				// # This behavior is for the simplicity of this assignment, and can be turned off if necessary.

				socket.emit("ready");
				// # By default, when user join a room they will be in "WAITING" state.
				// # This changes the user to "READY" state.
				// # It does not have any effect on user in the "PLAYING" or "WATCHING" state.

				// socket.emit("wait");
				// # This changes the user back to "WAITING" state.
				// # It does not have any effect on user in the "PLAYING" or "WATCHING" state.

				// # When enough players are in "READY" state, the game starts automatically
				// # The server will emit a "room" event to all players once it starts.

				// socket.emit("play", moveData);
				// # This will send a move to the server.
				// # moveData is a `JSONObject`: {"move": [x, y], "room": "roomName"}
				// # "room" is optional as only one room at a time is supported right now.
				// # For TicTacToe "move" is just [x, y], and for Checkers it can be [oldX, oldY, newX, newY].
				// # See the "play()" function at the bottom of this file for an example.

				// # Players can input whenever they want. Server will count who's turn and ignore any invalid requests.
				// # All players will receive "room" event with the updated game state once any player places a move.
				// # The GUI should redraw accordingly.

				// # Then game is ended, "room"'s "state.game.ended" will be true.
				// # "state.game.winner" will be the index of winning player, or -1 if it is tie.
				// # Users in "PLAYING" state will be changed to "WAITING" state.

				// socket.emit("leave");
				// # `leave` server event will remove user from all rooms.

				// socket.emit("leave", "roomName");
				// # It optionally takes a roomName, so that user is only removed from that room.

				// socket.disconnect();
				// # This will disconnect user from the server.
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
				System.out.println();

				List<List<Integer>> board = new ArrayList<>();

				try {
					JSONArray jsonBoard = data.getJSONObject("game").getJSONArray("board");
					for (int i = 0; i < jsonBoard.length(); i++) {
						board.add(new ArrayList<>());
						JSONArray jsonRow = jsonBoard.getJSONArray(i);
						for (int j = 0; j < jsonRow.length(); j++) {
							board.get(i).add(jsonBoard.getJSONArray(i).getInt(j));
						}
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				int width = 0;
				for (int i = 0; i < board.size(); i++) {
					width = board.get(i).size();

					for (int j = 0; j < board.get(i).size(); j++) {
						int t = board.get(i).get(j);
						if (t == -1) {
							System.out.printf("  ");
						} else if (t == 0) {
							System.out.printf(" x");
						} else if (t == 1) {
							System.out.printf(" o");
						}
					}
					System.out.println("| " + i);
				}
				System.out.println(String.format("%0" + (width * 2) + "d", 0).replace("0", "-"));
				for (int i = 0; i < width; i++) {
					System.out.printf("%2d", i);
				}
				System.out.println();

				game.updateBoard(board);
			}
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			public void call(Object... args) {}
		});

		socket.connect();
	}
}
