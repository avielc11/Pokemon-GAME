package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import gameClient.*;
import gameClient.util.*;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class Ex2 {

	/**
	 * start the project. 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		game_service g = Game_Server_Ex2.getServer(0);

		//build myGame
		myGame game = new myGame(g);
		System.out.println("start is good");
		HashMap<Integer, LinkedList<node_data>> list = new HashMap<Integer, LinkedList<node_data>>();

		
//		build array of list before the game start
		
		Iterator<CL_Agent> movea = game.getAsh().iterator();
		while(movea.hasNext()) {
			CL_Agent tempA = movea.next();
			LinkedList<node_data> q = game.NearestPoke(tempA);
			list.put(tempA.getID(), q);
		}
		System.out.println("start 1 is good");
		//start the game
		g.startGame();
		int i= 0;
		while(g.isRunning()) {
			game.stPOKE(g);

			movea =game.getAsh().iterator();
			while(movea.hasNext()) {
				CL_Agent t = movea.next();
				if(list.get(t.getID())==null) {
					LinkedList<node_data> l = game.NearestPoke(t);
					list.get(t.getID()).addAll(l);
				}				
				if(list.get(t.getID())!=null && t.get_curr_edge()==null) {
					node_data node = list.get(t.getID()).poll();
					g.chooseNextEdge(t.getID(), node.getKey());
				}
			}

			//move
			g.move();
		}
	}

	//draw the graph by "grp" and locate the pokemon by "poke"  on the graph
	/** 
	 * Frame frame = new Frame(game);
	 * Panel panel = new Panel();
	 * frame.add(panel);
	 * frame.setVisible(true);
	 */






}