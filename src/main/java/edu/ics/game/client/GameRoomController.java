package edu.ics.game.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameRoomController extends Controller {
	private Game game = null;
	private boolean gridInitialized = false;
	private List<List<Pane>> panes = new ArrayList<>();
	private GameStatusController gameStatusController = null;
	private int playerIndex = -1;

	@FXML
	private BorderPane borderPane;

	@FXML
	private GridPane gridPane;

	@FXML
	private Label message;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			FXMLLoader loader = new FXMLLoader(App.class.getResource("status.fxml"));
			borderPane.setCenter(loader.load());
			this.gameStatusController = (GameStatusController)loader.getController();
			BorderPane.setMargin(this.gameStatusController.getTableView(), new Insets(0, 0, 0, 20));
			this.gameStatusController.getTableView().getColumns().get(0).setText("Players");
			this.gameStatusController.getTableView().setPlaceholder(new Label("No player in room"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void leave() {
		this.app.getSocket().emit("leave");
		this.app.showGameLobby();
	}

	public void hold() {
		this.app.getSocket().emit("wait");
	}

	public void ready() {
		this.app.getSocket().emit("ready");
	}

	public void initializeGame(String name) {
		if (game == null) {
			for (Class<? extends Game> gameClass : Game.AVAILABLE_GAMES) {
				if (gameClass.getSimpleName().equals(name)) {
					try {
						game = gameClass.newInstance();
						String style = (String) gameClass.getField("style").get(null);
						gridPane.setStyle(style);
					} catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
					}
				}
			}
		}
	}

	public void initializeGrid(int columns, int rows) {
		if (!gridInitialized) {
			for (int i = 0 ; i < columns ; i++) {
				ColumnConstraints colConstraints = new ColumnConstraints();
				colConstraints.setHgrow(Priority.SOMETIMES);
				gridPane.getColumnConstraints().add(colConstraints);
			}

			for (int i = 0 ; i < rows ; i++) {
				RowConstraints rowConstraints = new RowConstraints();
				rowConstraints.setVgrow(Priority.SOMETIMES);
				gridPane.getRowConstraints().add(rowConstraints);
			}
			for (int column = 0; column < columns; column++) {
				panes.add(new ArrayList<>());
				for (int row = 0; row < rows; row++) {
					Pane pane = new Pane();
					pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							int column = GridPane.getColumnIndex((Node) event.getTarget());
							int row = GridPane.getRowIndex((Node) event.getTarget());
							play(column, row);
						}
					});
					gridPane.add(pane, column, row);
					panes.get(column).add(pane);
				}
			}
			gridInitialized = true;
		}
	}
	public void play(int... args) {
		JSONObject moveData = new JSONObject();
		JSONArray move = new JSONArray();
		for (int i = 0; i < args.length; i++) {
			move.put(args[i]);
		}
		try {
			moveData.put("move", move);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		app.getSocket().emit("play", moveData);
	}

	public void updateState(JSONObject data) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonGame = data.getJSONObject("game");
					int rows = jsonGame.getInt("rows");
					int columns = jsonGame.getInt("columns");

					initializeGrid(columns, rows);
					initializeGame(jsonGame.getString("name"));

					JSONArray jsonBoard = jsonGame.getJSONArray("board");
					for (int column = 0; column < columns; column++) {

						JSONArray jsonRow = jsonBoard.getJSONArray(column);
						for (int row = 0; row < rows; row++) {

							panes.get(column).get(row).getChildren().clear();

							Node piece = game.createPiece(jsonRow.getInt(row), column, row);
							if (piece != null) {
								piece.setMouseTransparent(true);
								panes.get(column).get(row).getChildren().add(piece);
							}
						}
					}

					if (gameStatusController != null) {
						gameStatusController.updateItems(data.getJSONArray("players"));
					}

					if (data.has("playerIndex")) {
						playerIndex = data.getInt("playerIndex");
					}

					message.setText(game.createMessage(data));

					String alertMessage;
					if (jsonGame.getBoolean("ended") && playerIndex >= 0) {
						if (jsonGame.getInt("winner") < 0) {
							alertMessage = "It's a tie!";
						} else if (jsonGame.getInt("winner") == playerIndex) {
							alertMessage = "You win!";
						} else {
							alertMessage = "You lose.";
						}
						AlertBox.display("Game Over", alertMessage);
						playerIndex = -1;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static class AlertBox {
		public static void display(String title, String message){
			Stage window = new Stage();
			window.setResizable(false);
			window.setMinWidth(200);
			window.initModality(Modality.APPLICATION_MODAL);
			window.setTitle(title);

			Label label = new Label();
			label.setText(message);
			Button closeButton = new Button("Ok");
			closeButton.setOnAction(e -> window.close());

			VBox layout = new VBox(10);
			layout.setPadding(new Insets(20));
			layout.getChildren().addAll(label, closeButton);
			layout.setAlignment(Pos.CENTER);

			Scene scene = new Scene(layout);
			window.setScene(scene);
			window.showAndWait();
		}
	}
}
