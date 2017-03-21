package edu.ics.game.client;

import java.net.URISyntaxException;

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
		this.loadLogin();
		this.primaryStage.show();
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

	public void loadLogin() {
		loadScene("login.fxml");
		primaryStage.setTitle("Login");
	}

	public void loadGameLobby() {
		loadScene("lobby.fxml");
	}

	public void loadGameRoom() {
		loadScene("room.fxml");
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
						socket.emit("state");
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
				if (primaryController.getClass().equals(LoginController.class)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							loadGameLobby();
							((GameLobbyController)primaryController).updateState((JSONObject)args[0]);
						}
					});
				} else if (primaryController.getClass().equals(GameLobbyController.class)) {
					((GameLobbyController)primaryController).updateState((JSONObject)args[0]);
				}
			}
		}).on("room", new Emitter.Listener() {
			public void call(Object... args) {
				if (primaryController.getClass().equals(GameLobbyController.class)) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							loadGameRoom();
							((GameRoomController)primaryController).updateState((JSONObject)args[0]);
						}
					});
				} else if (primaryController.getClass().equals(GameRoomController.class)) {
					((GameRoomController)primaryController).updateState((JSONObject)args[0]);
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
			loadLogin();
		} catch (Exception e) {}
	}

	public Socket getSocket() {
		return socket;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
}
