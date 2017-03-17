package edu.ics.game.client;

import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class App extends Application {
	private Stage primaryStage;
	private Controller primaryController;
	private Socket socket = null;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setOnCloseRequest(event -> {
			try {
				this.socket.disconnect();
			} catch (Exception e) {}
			System.exit(0);
		});
		primaryStage.setResizable(false);

		this.primaryStage = primaryStage;
		this.showLogin();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Controller loadScene(String fxml) {
		try {
			FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
			primaryStage.setScene(new Scene(loader.load()));
			primaryStage.sizeToScene();
			primaryStage.centerOnScreen();
			primaryController = (Controller)loader.getController();
			primaryController.setApp(this);
			return primaryController;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void showLogin() {
		loadScene("login.fxml");
		primaryStage.setTitle("Login");
		primaryStage.show();
	}

	public void showGameLobby() {
		loadScene("lobby.fxml");
		socket.emit("state");
		primaryStage.show();
	}

	public void showGameRoom(String roomName) {
		loadScene("room.fxml");
		socket.emit("state", roomName);
		primaryStage.show();
	}

	public void connect(String URL) {
		try {
			socket = IO.socket(URL);
		} catch (URISyntaxException e) {
			if (primaryController.getClass().equals(LoginController.class)) {
				((LoginController)primaryController).setError("Invalid hostname or port.");
			}
			return;
		}
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			public void call(Object... args) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						showGameLobby();
					}
				});	
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
				if (primaryController.getClass().equals(GameLobbyController.class)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								primaryStage.setTitle(((JSONObject)args[0]).getString("name") + " - Lobby");
							} catch (JSONException e) {
								primaryStage.setTitle("Game - Lobby");
							}
						}
					});

					((GameLobbyController)primaryController).updateState((JSONObject)args[0]);
				}
			}
		}).on("room", new Emitter.Listener() {
			public void call(Object... args) {
				if (primaryController.getClass().equals(GameRoomController.class)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								String title = ((JSONObject)args[0]).getJSONObject("game").getString("name") + " - Room - " + ((JSONObject)args[0]).getString("name");
								primaryStage.setTitle(title);
							} catch (JSONException e) {
								primaryStage.setTitle("Game - Room");
							}
						}
					});

					((GameRoomController)primaryController).updateState((JSONObject)args[0]);
				} else if (primaryController.getClass().equals(GameLobbyController.class)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								showGameRoom(((JSONObject)args[0]).getString("name"));
							} catch (JSONException e) {}

							((GameRoomController)primaryController).updateState((JSONObject)args[0]);
						}
					});					
				}
			}
		}).on(Socket.EVENT_CONNECT_ERROR,  new Emitter.Listener() {
			public void call(Object... args) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (primaryController.getClass().equals(LoginController.class)) {
							((LoginController)primaryController).setError("Connection Error.");
						}
					}
				});
			}
		});
		this.socket.connect();
	}

	public void disconnect() {
		try {
			this.socket.disconnect();
			showLogin();
		} catch (Exception e) {}
	}

	public Socket getSocket() {
		return socket;
	}
}
