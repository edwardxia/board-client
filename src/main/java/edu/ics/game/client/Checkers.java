package edu.ics.game.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class Checkers extends Game {
	public Node createPiece(int piece, int column, int row) {
		StackPane pane = new StackPane();
		Rectangle background = new Rectangle(0, 0, 60, 60);
		background.setFill(((column + row) % 2) == 0 ? Color.WHITE : Color.GRAY);
		pane.getChildren().add(background);

		if (piece >=0 && piece < 8) {
			Circle circle = new Circle(30, 30, 25);
			if (piece / 4 == 1) {
				circle.setStrokeWidth(3);
			} else {
				circle.setStrokeWidth(0);
			}

			if ((piece % 4) / 2 == 0) {
				circle.setFill(Color.WHITE);
				circle.setStroke(Color.BLACK);
			} else {
				circle.setFill(Color.BLACK);
				circle.setStroke(Color.WHITE);
			}

			pane.getChildren().add(circle);

			if ((piece % 4) % 2 == 1) {
				Text text = new Text("♔");
				text.setFont(new Font(32));
				text.setFill(((piece % 4) / 2 == 0) ? Color.BLACK : Color.WHITE);
				text.setBoundsType(TextBoundsType.VISUAL);
				pane.getChildren().add(text);
			}
		}

		return pane;
	}

	public String createMessage(JSONObject state) {
		try {
			if (!state.getString("status").equals("PLAYING")) {
				return null;
			}
			JSONArray jsonPlayers = state.getJSONArray("players");
			JSONArray jsonScore = state.getJSONObject("game").getJSONArray("score");
			int[] score = new int[jsonScore.length()];
			for (int i = 0; i < jsonScore.length(); i++) {
				score[i] = jsonScore.getInt(i);
			}
			return "White (" + jsonPlayers.getJSONObject(0).getString("name") + "): " + score[0]+ " vs. Black (" + jsonPlayers.getJSONObject(1).getString("name") + "): " + score[1];
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
