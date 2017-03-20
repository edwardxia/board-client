package edu.ics.game.client;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class GameLobbyController extends Controller {

	@FXML
	private BorderPane borderPane;

	@FXML
	private TextField username;

	@FXML
	private TextField roomName;

	private JSONObject state;
	private GameStatusController gameRoomStatusController = null;
	private GameStatusController gamePlayerStatusController = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			FXMLLoader roomStatusLoader = new FXMLLoader(App.class.getResource("status.fxml"));
			borderPane.setLeft(roomStatusLoader.load());
			this.gameRoomStatusController = (GameStatusController)roomStatusLoader.getController();
			this.gameRoomStatusController.getTableView().getColumns().get(0).setText("Room");
			this.gameRoomStatusController.getTableView().setPlaceholder(new Label("No room in lobby"));
			this.gameRoomStatusController.getTableView().setRowFactory(new Callback<TableView<GameStatusController.Status>, TableRow<GameStatusController.Status>>() {  
				@Override
				public TableRow<GameStatusController.Status> call(TableView<GameStatusController.Status> tableView) {
					final TableRow<GameStatusController.Status> row = new TableRow<>();
					row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							final int index = row.getIndex();
							if (index >= 0 && index < tableView.getItems().size() && tableView.getSelectionModel().isSelected(index)) {
								tableView.getSelectionModel().clearSelection();
								event.consume();
							}
						}
					});
					return row;
				}
			});
			this.gameRoomStatusController.getTableView().getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				if (newSelection != null) {
					this.roomName.setText(newSelection.getName());
					updatePlayers(newSelection.getName());
				} else {
					updatePlayers();
				}
			});

			FXMLLoader playerStatusLoader = new FXMLLoader(App.class.getResource("status.fxml"));
			borderPane.setCenter(playerStatusLoader.load());
			this.gamePlayerStatusController = (GameStatusController)playerStatusLoader.getController();
			this.gamePlayerStatusController.getTableView().getColumns().get(0).setText("Player");
			this.gamePlayerStatusController.getTableView().setPlaceholder(new Label("No player in lobby"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateState(JSONObject data) {
		this.state = data;
		this.updateRooms();
		this.updatePlayers();
	}

	private void updateRooms() {
		try {
			gameRoomStatusController.setItems(state.getJSONArray("rooms"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updatePlayers() {
		try {
			gamePlayerStatusController.setItems(state.getJSONArray("players"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updatePlayers(String roomName) {
		try {
			JSONArray jsonRooms = state.getJSONArray("rooms");
			for (int i = 0; i < jsonRooms.length(); i++) {
				JSONObject jsonRoom = jsonRooms.getJSONObject(i);
				if (jsonRoom.getString("name").equals(roomName)) {
					gamePlayerStatusController.setItems(jsonRoom.getJSONArray("players"));
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void name() {
		String username = this.username.getText();
		if (!username.equals("")) {
			this.app.getSocket().emit("name", username);
		}
	}

	public void join() {
		String roomName = this.roomName.getText();
		if (!roomName.equals("")) {
			this.app.getSocket().emit("join", roomName);
		}
	}

	public void back() {
		app.disconnect();
	}
}
