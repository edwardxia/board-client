package edu.ics.game.client;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class GameStatusController implements Initializable {

	@FXML
	private TableView<Status> tableView;

	@FXML
	private TableColumn<Status, String> nameColumn;

	@FXML
	private TableColumn<Status, String> statusColumn;

	private final ObservableList<Status> items = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameColumn.setCellValueFactory(new PropertyValueFactory<Status,String>("name"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<Status,String>("status"));
		tableView.setItems(this.items);
	}

	public TableView<Status> getTableView() {
		return tableView;
	}

	public void updateItems(JSONArray items) {
		this.items.clear();

		try {
			for (int i = 0; i < items.length(); i++) {
				this.items.add(new Status(items.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
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

		public void setStatus(String status) {
			this.status = new SimpleStringProperty(status);
		}

		public String getStatus() {
			return status.get();
		}
	}
}
