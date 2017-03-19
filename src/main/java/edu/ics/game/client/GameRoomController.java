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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class GameRoomController extends Controller {
	private Game game = null;
	private boolean gridInitialized = false;
	private List<List<Pane>> panes = new ArrayList<>();
	private GameStatusController gameStatusController = null;
	@SuppressWarnings("unused")
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
					int bCount = 0;
					int wCount = 0;

					initializeGrid(columns, rows);
					initializeGame(jsonGame.getString("name"));
					JSONArray jsonBoard = jsonGame.getJSONArray("board");
					System.out.println(jsonGame);
					for (int column = 0; column < columns; column++) {

						JSONArray jsonRow = jsonBoard.getJSONArray(column);
						for (int row = 0; row < rows; row++) {

							panes.get(column).get(row).getChildren().clear();
							Node piece = game.createPiece(jsonRow.getInt(row), column, row);
							if (piece != null) {
								piece.setMouseTransparent(true);
								panes.get(column).get(row).getChildren().add(piece);
							}
							if (data.getJSONObject("game").getString("name").equals("Othello")){
								if (jsonRow.getInt(row) == 0) {
									bCount += 1;
								} else if (jsonRow.getInt(row) == 1) {
									wCount += 1;
								}
							}
							if (data.getJSONObject("game").getString("name").equals("Checkers")){
								if (jsonRow.getInt(row) == 0 || jsonRow.getInt(row) == 4 || jsonRow.getInt(row) == 1) {
									bCount += 1;
								} else if (jsonRow.getInt(row) == 2 || jsonRow.getInt(row) == 6 || jsonRow.getInt(row) == 3) {
									wCount += 1;
								}
							}
							
						}
					}
					
					updateScore(data.getJSONObject("game").getString("name"), data, bCount, wCount);

					if (gameStatusController != null) {
						gameStatusController.updateItems(data.getJSONArray("players"));
					}
					if (data.has("playerIndex")) {
						playerIndex = data.getInt("playerIndex");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void updateScore(String game, JSONObject data, int bScore, int wScore) {
		try {
			if (game.equals("Othello")) {
				JSONArray players = data.getJSONArray("players");
				if (players.length() == 2) {
					message.setText(((players.getJSONObject(0).getString("name"))) + ": " + bScore+ "   " + 
							players.getJSONObject(1).getString("name") + ": " + wScore);
				} else {
					message.setText("WAITING");
				}
			}
			if (game.equals("Checkers")) {
				JSONArray players = data.getJSONArray("players");
				if (players.length() == 2) {
					message.setText(((players.getJSONObject(0).getString("name"))) + ": " + bScore+ "   " + 
							players.getJSONObject(1).getString("name") + ": " + wScore);
				} else {
					message.setText("WAITING");
				}
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
