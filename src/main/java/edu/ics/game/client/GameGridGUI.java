package edu.ics.game.client;
import java.util.ArrayList;
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
		this.socket = socket;
	}
	public abstract void updateBoard(List<List<Integer>> board);
	public abstract void displayGameWindow(String gameName, int numOfCols, int numOfRows, Color backgroundColor, Color gridColor);
	//Testing for now
	int player = 1;
	//
	//Testing Purposes
	public void switchTurn(){
		if (player ==1 ){
			player = 2;
		}else{
			player = 1;
		}
	}
	//

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
		boxWidth = ((int)canvas.getWidth())/numOfCols;
		boxHeight = ((int)canvas.getHeight())/numOfRows;
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
		JSONObject moveData = new JSONObject();
		List<Integer> move = getIndexOfBox(e);
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

	public List<Integer> getIndexOfBox(MouseEvent e){
		List<Integer> coordinates = new ArrayList<Integer>();
		for (int i = 0; i< numOfRows; i++){
			if((i * boxHeight) < ((int)e.getY()) && ((int) e.getY()) < ((i+1) * boxHeight)){
				coordinates.add(i);
			}
		}
		for (int i = 0; i < numOfCols; i++){
			if((i * boxWidth) < ((int)e.getX()) && ((int) e.getX()) < ((i+1) * boxWidth)){
				coordinates.add(i);
			}
		}
		return coordinates;
	}

	public List<Integer> getTopLeftCorner(List<Integer> coordinates){
		//Gets the coordinate of the top left of a box in the grid.
		List<Integer> result = new ArrayList<Integer>();
		int x = coordinates.get(0) * (boxWidth);
		int y = coordinates.get(1) * (boxHeight);
		result.add(x);
		result.add(y);
		return result;
	}

	public List<Integer> getTopRightCorner(List<Integer> coordinates){
		//Gets the coordinate of the top right of a box in the grid.
		List<Integer> result = new ArrayList<Integer>();
		int x = (coordinates.get(0) + 1) * (boxWidth);
		int y = coordinates.get(1) * (boxHeight);
		result.add(x);
		result.add(y);
		return result;
	}

	public List<Integer> getBottomRightCorner(List<Integer> coordinates){
		//Gets the coordinate of the bottom right of a box in the grid.
		List<Integer> result = new ArrayList<Integer>();
		int x = (coordinates.get(0) + 1) * (boxWidth);
		int y = (coordinates.get(1) + 1) * (boxHeight);
		result.add(x);
		result.add(y);
		return result;
	} 

	public List<Integer> getBottomLeftCorner(List<Integer> coordinates){
		//Gets the coordinate of the bottom left of a box in the grid.
		List<Integer> result = new ArrayList<Integer>();
		int x = coordinates.get(0) * (boxWidth);
		int y = (coordinates.get(1) + 1) * (boxHeight);
		result.add(x);
		result.add(y);
		return result;
	} 

	public List<Integer> getCenter(List<Integer> coordinates){
		//Gets the coordinate of the center of a box in the grid.
		List<Integer> result = new ArrayList<Integer>();
		int x = (coordinates.get(0) + 1) * (boxWidth/2);
		int y = (coordinates.get(1) + 1) * (boxHeight/2);
		result.add(x);
		result.add(y);
		return result;
	}

}
