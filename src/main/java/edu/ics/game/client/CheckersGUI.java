package edu.ics.game.client;

import java.util.List;
import io.socket.client.Socket;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CheckersGUI extends GameGridGUI{
	
	private Color colorOfBackground = Color.WHITE;
	private Color colorOfX = Color.BLACK;
	private Color colorOfCircle = Color.BLACK;
	private Color colorOfGrid = Color.BLACK;
	
	public CheckersGUI(Socket socket){
		super(socket);
		displayGameWindow("Checkers", 10, 10, colorOfBackground, colorOfGrid);
	}

	public void displayGameWindow(String gameName, int numOfCols, int numOfRows, Color backgroundColor, Color gridColor){
		// Creates a window to display the entire game window
		Stage window = new Stage();
		window.setTitle(gameName);

		BorderPane windowLayout = new BorderPane();

		Canvas canvas = createCanvas(backgroundColor);
		//Must Have creatGrid to create checkersboard
		createGrid(numOfCols, numOfRows, gridColor);
		displayCheckersBoard();
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
					displayCircle(index, Color.RED);
				} else if (t == 1) {
					displayCircle(index, Color.BLACK);
				}
			}
		}
	}
	
	public void displayCheckersBoard(){
		Color color = Color.RED;
		Color firstRowColor = Color.RED;
		for(int i = 0; i < numOfRows; i++){
			for(int j = 0; j < numOfCols; j++){
				Coordinates index = new Coordinates(i,j);
				if ((i + j) % 2 == 1){
					displayBox(index, Color.RED);
				}else{
					displayBox(index, Color.BLACK);
				}
			}
		}
	}
	
	public void displayBox(Coordinates index, Color color){
		GraphicsContext gc = canvas.getGraphicsContext2D();
		//gc.setStroke(color);
		gc.setFill(color);
		Coordinates topLeft = getTopLeftCorner(index);
		gc.fillRect(topLeft.row, topLeft.column, boxWidth, boxHeight);
	}
	
	public Color alternateColor(Color color){
		if (color == Color.RED){
			return Color.BLACK;
		}else{
			return Color.RED;
		}
	}
	
	public void displayCircle(Coordinates index, Color color){
		///Display's a circle token
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setStroke(color);
		Coordinates topLeft = getTopLeftCorner(index);
		gc.strokeOval(topLeft.row, topLeft.column, boxWidth, boxHeight);
	}
}