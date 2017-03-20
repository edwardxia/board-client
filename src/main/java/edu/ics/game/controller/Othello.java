package edu.ics.game.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Othello extends Game {
	public static final String style = "-fx-border-color: black; -fx-background-color: green;";

	public Node createPiece(int piece, int column, int row) {
		switch (piece) {
		case 0:
			Circle black = new Circle(30, 30, 25);
			black.setFill(Color.BLACK);
			return black;
		case 1:
			Circle white = new Circle(30, 30, 25);
			white.setFill(Color.WHITE);
			return white;
		default:
			Circle transparent = new Circle(30, 30, 25);
			transparent.setFill(Color.TRANSPARENT);
			return transparent;
		}
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
			return "Black (" + jsonPlayers.getJSONObject(0).getString("name") + "): " + score[0]+ " vs. White (" + jsonPlayers.getJSONObject(1).getString("name") + "): " + score[1];
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}