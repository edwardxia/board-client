package edu.ics.game.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.Node;

public abstract class GamePieceFactory {
	public static final List<Class<? extends GamePieceFactory>> AVAILABLE_GAMES = new ArrayList<>(Arrays.asList(
			TicTacToe.class,
			Othello.class,
			Checkers.class
			));

	public abstract Node createPiece(int piece, int column, int row);
}
