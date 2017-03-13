package edu.ics.game.client;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TicTacToeGUI extends GameGridGUI {

	private Color colorOfBackground = Color.GREEN;
	private Color colorOfX = Color.WHITE;
	private Color colorOfCircle = Color.RED;
	private Color colorOfGrid = Color.YELLOW;

	public TicTacToeGUI(Socket socket){
		super(socket);
		displayGameWindow("Tic Tac Toe", 8, 8, colorOfBackground, colorOfGrid);
	}

	public void displayGameWindow(String gameName, int numOfCols, int numOfRows, Color backgroundColor, Color gridColor){
		// Creates a window to display the entire game window
		Stage window = new Stage();
		window.setTitle(gameName);

		BorderPane windowLayout = new BorderPane();

		Canvas canvas = createCanvas(backgroundColor);
		createGrid(numOfCols, numOfRows, gridColor);
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			sendInput(e);
		});

		//Making Reset button
		Button resetButton = new Button("Reset Game");
		//Assign function to the button;
		resetButton.setOnAction(e -> {
			reset(backgroundColor, gridColor);
			createGrid(numOfCols, numOfRows, gridColor);
		});

		///Place button on the top side of window layout
		windowLayout.setTop(resetButton);
		windowLayout.setAlignment(resetButton, Pos.TOP_CENTER);

		//Add the Grid background to layout
		windowLayout.setCenter(canvas);

		//Put the layout in windows content 
		Scene gridWindow = new Scene(windowLayout, 600, 600);

		//Attach Scene to the window
		window.setScene(gridWindow);
		gameBoard = window;
		window.show();
	}

	public void reset() {
		super.reset(this.colorOfBackground, this.colorOfGrid);
	}
	
	public void updateBoard(List<List<Integer>> board){
		this.reset();
		GraphicsContext gc = canvas.getGraphicsContext2D();
		for (int row = 0; row < board.size(); row++) {
			for (int column = 0; column < board.get(row).size(); column++) {
				int t = board.get(row).get(column);
				if (t == 0) {
					displayX(row, column);
				} else if (t == 1) {
					displayCircle(row, column);
				}
			}
		}

	}

	public void displayCircle(int row, int col){
		///Display's a circle token
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setStroke(colorOfCircle);
		List<Integer> coordinates = new ArrayList<Integer>();
		coordinates.add(col);
		coordinates.add(row);
		List<Integer> topLeft = getTopLeftCorner(coordinates);
		gc.strokeOval(topLeft.get(0), topLeft.get(1), boxWidth, boxHeight);
	}

	public void displayX(int row, int col){
		//Display's a X token
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setStroke(colorOfX);
		List<Integer> coordinates = new ArrayList<Integer>();
		coordinates.add(col);
		coordinates.add(row);
		List<Integer> topLeft = getTopLeftCorner(coordinates);
		List<Integer> bottomRight = getBottomRightCorner(coordinates);
		gc.strokeLine(topLeft.get(0), topLeft.get(1), bottomRight.get(0), bottomRight.get(1));
		List<Integer> topRight = getTopRightCorner(coordinates);
		List<Integer> bottomLeft = getBottomLeftCorner(coordinates);
		gc.strokeLine(topRight.get(0), topRight.get(1), bottomLeft.get(0), bottomLeft.get(1));

	}

}
