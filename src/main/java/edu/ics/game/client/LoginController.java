package edu.ics.game.client;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController extends Controller {

	@FXML
	private TextField hostname;

	@FXML
	private TextField port;

	@FXML
	private ChoiceBox<String> namespace;

	@FXML
	private Label error;

	@FXML
	private Button login;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.namespace.setItems(FXCollections.observableArrayList(
				GamePieceFactory.AVAILABLE_GAMES.stream()
				.map(el -> el.getSimpleName())
				.collect(Collectors.toList())
				));
		this.namespace.setValue(this.namespace.getItems().get(0));
	}

	public void login() {
		String hostname = this.hostname.getText();
		if (hostname.equals("")) {
			this.error.setText("Please enter a hostname.");
		}

		String port = this.port.getText();
		if (port.equals("")) {
			this.error.setText("Please enter a port.");
		}

		String URL = "http://" + hostname + ":" + port + "/" + namespace.getValue();

		this.error.setText("");
		this.login.setDisable(true);

		this.app.connect(URL);
	}
	
	public void setError(String error) {
		this.error.setText(error);
		this.login.setDisable(false);
	}
}
