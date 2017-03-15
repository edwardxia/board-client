package edu.ics.game.client;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class GameLobbyController extends Controller {

	@FXML
	private TableView<Status> roomTable;

	@FXML
	private TableColumn<Status, String> roomNameColumn;

	@FXML
	private TableColumn<Status, String> roomStatusColumn;

	@FXML
	private TableView<Status> playerTable;

	@FXML
	private TableColumn<Status, String> playerNameColumn;

	@FXML
	private TableColumn<Status, String> playerStatusColumn;

	@FXML
	private TextField username;
	
	@FXML
	private TextField roomName;

	private JSONObject state;
	private final ObservableList<Status> rooms = FXCollections.observableArrayList();
	private final ObservableList<Status> players = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		roomNameColumn.setCellValueFactory(new PropertyValueFactory<Status,String>("name"));
		roomStatusColumn.setCellValueFactory(new PropertyValueFactory<Status,String>("status"));
		roomTable.setItems(rooms);
		roomTable.setRowFactory(new Callback<TableView<Status>, TableRow<Status>>() {  
			@Override
			public TableRow<Status> call(TableView<Status> tableView) {
	            final TableRow<Status> row = new TableRow<>();
	            row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {  
	                @Override  
	                public void handle(MouseEvent event) {  
	                    final int index = row.getIndex();  
	                    if (index >= 0 && index < tableView.getItems().size() && tableView.getSelectionModel().isSelected(index)  ) {
	                        tableView.getSelectionModel().clearSelection();
	                        event.consume();  
	                    }  
	                }  
	            }); 
	            return row;  
			}  
	    });
		roomTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				this.roomName.setText(newSelection.getName());
				updatePlayers(newSelection.getName());
			} else {
				updatePlayers();
			}
		});

		playerNameColumn.setCellValueFactory(new PropertyValueFactory<Status,String>("name"));
		playerStatusColumn.setCellValueFactory(new PropertyValueFactory<Status,String>("status"));
		playerTable.setItems(players);
	}

	public void updateState(JSONObject data) {
		this.state = data;
		updateRooms();
		updatePlayers();
	}

	private void updateRooms() {
		rooms.clear();

		try {
			JSONArray jsonRooms = state.getJSONArray("rooms");
			for (int i = 0; i < jsonRooms.length(); i++) {
				rooms.add(new Status(jsonRooms.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updatePlayers() {
		updatePlayers(null);
	}

	private void updatePlayers(String roomName) {
		players.clear();

		if (roomName == null) {
			try {
				JSONArray jsonPlayers = state.getJSONArray("players");
				for (int i = 0; i < jsonPlayers.length(); i++) {
					players.add(new Status(jsonPlayers.getJSONObject(i)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				JSONArray jsonRooms = state.getJSONArray("rooms");
				for (int i = 0; i < jsonRooms.length(); i++) {
					JSONObject jsonRoom = jsonRooms.getJSONObject(i);
					if (jsonRoom.getString("name").equals(roomName)) {
						JSONArray jsonPlayers = jsonRoom.getJSONArray("players");
						for (int j = 0; j < jsonPlayers.length(); j++) {
							players.add(new Status(jsonPlayers.getJSONObject(j)));
						}
						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
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

	public static class Status {
		private SimpleStringProperty name;
		private SimpleStringProperty status;

		private Status(JSONObject object) {
			try {
				this.name = new SimpleStringProperty(object.getString("name"));
				this.status = new SimpleStringProperty(object.getString("status"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		private Status(String name, String status) {
			this.name = new SimpleStringProperty(name);
			this.status = new SimpleStringProperty(status);
		}

		public String getName() {
			return name.get();
		}

		public String getStatus() {
			return status.get();
		}
	}
}
