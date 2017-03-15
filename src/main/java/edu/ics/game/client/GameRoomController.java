package edu.ics.game.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameRoomController extends Controller {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void updateState(JSONObject data) {
		System.out.println("-- Room State --");
		System.out.println(data.toString());
		System.out.println();

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

		int width = 0;
		for (int i = 0; i < board.size(); i++) {
			width = board.get(i).size();

			for (int j = 0; j < board.get(i).size(); j++) {
				int t = board.get(i).get(j);
				if (t == -1) {
					System.out.printf("  ");
				} else if (t == 0) {
					System.out.printf(" x");
				} else if (t == 1) {
					System.out.printf(" o");
				}
			}
			System.out.println("| " + i);
		}
		System.out.println(String.format("%0" + (width * 2) + "d", 0).replace("0", "-"));
		for (int i = 0; i < width; i++) {
			System.out.printf("%2d", i);
		}
		System.out.println();		
	}
}
