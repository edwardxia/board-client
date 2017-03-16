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
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class GameRoomController extends Controller {
	private GamePieceFactory game = null;
	private boolean gridInitialized = false;
	private List<List<Pane>> panes = new ArrayList<>();

	@FXML
	private GridPane gridPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
			for (Class<? extends GamePieceFactory> gameClass : GamePieceFactory.AVAILABLE_GAMES) {
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

	public void initializeGrid(int height, int width) {
		if (!gridInitialized) {
			for (int i = 0 ; i < width ; i++) {
				ColumnConstraints colConstraints = new ColumnConstraints();
				colConstraints.setHgrow(Priority.SOMETIMES);
				gridPane.getColumnConstraints().add(colConstraints);
			}

			for (int i = 0 ; i < height ; i++) {
				RowConstraints rowConstraints = new RowConstraints();
				rowConstraints.setVgrow(Priority.SOMETIMES);
				gridPane.getRowConstraints().add(rowConstraints);
			}
			for (int row = 0; row < height; row++) {
				panes.add(new ArrayList<>());
				for (int column = 0; column < width; column++) {
					Pane pane = new Pane();
					pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							int column = GridPane.getColumnIndex((Node) event.getTarget());
							int row = GridPane.getRowIndex((Node) event.getTarget());
							play(row, column);
						}
					});
					gridPane.add(pane, column, row);
					panes.get(row).add(pane);
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
		debug(data);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonGame = data.getJSONObject("game");
					int height = jsonGame.getInt("height");
					int width = jsonGame.getInt("width");

					initializeGrid(height, width);
					initializeGame(jsonGame.getString("name"));

					JSONArray jsonBoard = jsonGame.getJSONArray("board");
					for (int row = 0; row < height; row++) {

						JSONArray jsonRow = jsonBoard.getJSONArray(row);
						for (int column = 0; column < width; column++) {
							panes.get(row).get(column).getChildren().clear();

							Node piece = game.createPiece(jsonRow.getInt(column), row, column);
							if (piece != null) {
								piece.setMouseTransparent(true);
								panes.get(row).get(column).getChildren().add(piece);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void debug(JSONObject data) {
		System.out.println("-- Room State --");
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
	}
}
