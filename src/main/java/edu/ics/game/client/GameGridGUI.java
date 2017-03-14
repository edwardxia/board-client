package edu.ics.game.client;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Window;

public abstract class GameGridGUI {
	protected Window gameBoard;
	protected int numOfCols;
	protected int numOfRows;
	protected int boxWidth;
	protected int boxHeight;
	protected Socket socket;
	protected Canvas canvas;

	protected GameGridGUI(Socket socket) {
		//Temporary for now. Need to move to the lobby interface 
		this.socket = socket;
	}
	public abstract void updateBoard(List<List<Integer>> board);
		//With the gameboard array update how the board looks like with the gui
	public abstract void displayGameWindow(String gameName, int numOfCols, int numOfRows, Color backgroundColor, Color gridColor);
		//Create the window for the desired game

	public Canvas createCanvas(Color color){
		//Create canvas
		this.canvas = new Canvas(500, 500);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(color);
		gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
		return canvas;
	}

	public void createGrid(int numOfCols, int numOfRows, Color gridColor){
		//Draws the grid onto the canvas
		this.numOfCols = numOfCols;
		this.numOfRows = numOfRows;
		boxWidth = (int)canvas.getWidth()/numOfCols;
		boxHeight = (int)canvas.getHeight()/numOfRows;
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		//Drawing the lines  
		gc.setStroke(gridColor);
		gc.setLineWidth(5);

		//Draw the Column Lines
		double colRatio = canvas.getWidth() / numOfCols;
		for(int i = 0; i < numOfCols-1; i++ ){
			double distance = colRatio * (i+1);
			gc.strokeLine( distance,0, distance, canvas.getHeight());
		}

		//Draw the Row Lines
		double rowRatio = canvas.getWidth() / numOfCols;
		for(int i = 0; i < numOfCols-1; i++ ){
			double distance = rowRatio * (i+1);
			gc.strokeLine( 0,distance, canvas.getWidth(), distance);
		}
	}

	public List<List<Integer>> getBoardState(JSONObject data){
		//Extract the gameboard array from the Server's json object.
		List<List<Integer>> board = new ArrayList<>();
		try {
			JSONArray jsonBoard = data.getJSONObject("game").getJSONArray("board");
			for (int i = 0; i < jsonBoard.length(); i++) {
				board.add(new ArrayList<>());
				JSONArray jsonRow = jsonBoard.getJSONArray(i);
				for (int j = 0; j < jsonRow.length(); j++) {
					board.get(i).add(jsonBoard.getJSONArray(i).getInt(j));
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return board;
	}

	public void sendInput(MouseEvent e){
		// Transfer a chosen box input to the server.
		JSONObject moveData = new JSONObject();
		Coordinates index = getIndexOfBox(e);
		List<Integer> move = new ArrayList<Integer>(Arrays.asList(index.row, index.column));
		try {
			moveData.put("move", new JSONArray(move));
		} catch (JSONException err) {
			err.printStackTrace();
		}

		this.socket.emit("play", moveData);
	}

	public void reset (Color backgroundColor, Color gridColor){
		//Remake an empty grid
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(backgroundColor);
		gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
		createGrid(numOfCols, numOfRows, gridColor);
	}

	public Coordinates getIndexOfBox(MouseEvent e){
		// Returns the Coordinates of the location of the box. ex) row : 0 col: 1
		int row = -1;
		int col = -1;
		for (int i = 0; i< numOfRows; i++){
			if((i * boxHeight) < ((int)e.getY()) && ((int) e.getY()) < ((i+1) * boxHeight)){
				row = i;
			}
		}
		for (int i = 0; i < numOfCols; i++){
			if((i * boxWidth) < ((int)e.getX()) && ((int) e.getX()) < ((i+1) * boxWidth)){
				col = i;
			}
		}
		Coordinates index = new Coordinates(row, col);
		return index;
	}

	public Coordinates getTopLeftCorner(Coordinates index){
		//Gets the coordinates of the top left of a chosen box in the grid of a canvas. Ex) row: 124 col : 234
		int x = index.column * (boxWidth);
		int y = index.row * (boxHeight);
		Coordinates topLeft = new Coordinates(x,y);
		return topLeft;
	}

	public Coordinates getTopRightCorner(Coordinates index){
		//Gets the coordinate of the top right of a box in the grid of a canvas. Ex) row: 124 col : 234
		int x = (index.column + 1) * (boxWidth);
		int y = index.row * (boxHeight);
		Coordinates topRight = new Coordinates(x,y);
		return topRight;
	}

	public Coordinates getBottomRightCorner(Coordinates index){
		//Gets the coordinate of the bottom right of a box in the grid of a canvas. Ex) row: 124 col : 234
		int x = (index.column + 1) * (boxWidth);
		int y = (index.row + 1) * (boxHeight);
		Coordinates bottomRight = new Coordinates(x,y);
		return bottomRight;
	} 

	public Coordinates getBottomLeftCorner(Coordinates coordinates){
		//Gets the coordinate of the bottom left of a box in the grid of a canvas. Ex) row: 124 col : 234
		int x = coordinates.column * (boxWidth);
		int y = (coordinates.row + 1) * (boxHeight);
		Coordinates bottomLeft = new Coordinates(x,y);
		return bottomLeft;
	} 

	public Coordinates getCenter(Coordinates coordinates){
		//Gets the coordinate of the center of a box in the grid of a canvas. Ex) row: 124 col : 234
		int x = (coordinates.column + 1) * (boxWidth/2);
		int y = (coordinates.row + 1) * (boxHeight/2);
		Coordinates center = new Coordinates(x,y);
		return center;
	}

}
