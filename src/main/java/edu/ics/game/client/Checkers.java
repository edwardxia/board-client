package edu.ics.game.client;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Checkers extends GamePieceFactory{
	public Node createPiece(int piece, int column, int row) {
		Group group = new Group();
		Rectangle background = new Rectangle(0, 0, 60, 60);
		background.setFill(((column + row) % 2) == 0 ? Color.WHITE : Color.GRAY);
		group.getChildren().add(background);
		
		switch (piece) {
		case 0:
		case 2:
			Circle white = new Circle(30, 30, 25);
			white.setFill(Color.WHITE);
			group.getChildren().add(white);
			break;
		case 1:
		case 3:
			Circle black = new Circle(30, 30, 25);
			black.setFill(Color.BLACK);
			group.getChildren().add(black);
			break;
		}
		return group;
	}
}
