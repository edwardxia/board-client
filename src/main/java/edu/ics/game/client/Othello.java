package edu.ics.game.client;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Othello extends GamePieceFactory {
	public static final String style = "-fx-border-color: black; -fx-background-color: green;";
	
	public Node createPiece(int piece, int row, int column) {
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
}