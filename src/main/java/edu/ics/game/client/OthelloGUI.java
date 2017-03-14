package edu.ics.game.client;
import java.util.List;

import io.socket.client.Socket;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class OthelloGUI extends GameGridGUI {

	private Color colorOfBackground = Color.GREEN;
	private Color player1Color = Color.BLACK;
	private Color player2Color = Color.WHITE;
	private Color colorOfGrid = Color.BROWN;

	public OthelloGUI(Socket socket){
		super(socket);
		displayGameWindow("Othello", 8, 8, colorOfBackground, colorOfGrid);
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

//		//Making Reset button
		// Unecessary because not in sync with server side
//		Button resetButton = new Button("Reset Game");
//		//Assign function to the button;
//		resetButton.setOnAction(e -> {
//			reset(backgroundColor, gridColor);
//			createGrid(numOfCols, numOfRows, gridColor);
//		});
//
//		///Place button on the top side of window layout
//		windowLayout.setTop(resetButton);
//		windowLayout.setAlignment(resetButton, Pos.TOP_CENTER);

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
		for (int row = 0; row < board.size(); row++) {
			for (int column = 0; column < board.get(row).size(); column++) {
				int t = board.get(row).get(column);
				Coordinates index = new Coordinates (row, column);
				if (t == 0) {
					displayCircle(index, player1Color);
				} else if (t == 1) {
					displayCircle(index, player2Color);
				}
			}
		}
	}
	
	public void displayCircle(Coordinates index, Color player){
		///Display's a circle token
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(player);
		Coordinates topLeft = getTopLeftCorner(index);
		gc.fillOval(topLeft.row, topLeft.column, boxWidth, boxHeight);
	}
}