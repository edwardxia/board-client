package edu.ics.game.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import javafx.scene.Node;

public abstract class Game {
	public static final List<Class<? extends Game>> AVAILABLE_GAMES = new ArrayList<>(Arrays.asList(
			Checkers.class,
			Othello.class,
			TicTacToe.class
			));

	public abstract Node createPiece(int piece, int column, int row);
	public abstract String createMessage(JSONObject state);
}