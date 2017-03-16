package edu.ics.game.client;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class TicTacToe extends GamePieceFactory {
	public Node createPiece(int piece, int row, int column) {
		Group group = new Group();
		
		// A quick hack to ensure all have equal size.
		Rectangle transparent = new Rectangle(0, 0, 160, 160);
		transparent.setFill(Color.TRANSPARENT);
		group.getChildren().add(transparent);

		switch (piece) {
		case 0:
			Line line1 = new Line(30, 30, 130, 130);
			line1.setStrokeWidth(5);
			Line line2 = new Line(30, 130, 130, 30);
			line2.setStrokeWidth(5);
			group.getChildren().addAll(line1, line2);
			break;
		case 1:
			Circle white = new Circle(80, 80, 60);
			white.setStroke(Color.BLACK);
			white.setStrokeWidth(5);
			white.setFill(Color.TRANSPARENT);
			group.getChildren().add(white);
			break;
		}

		return group;
	}
}
