package edu.ics.game.client;

import javafx.fxml.Initializable;

public abstract class Controller implements Initializable {
	protected App app;

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}
}
