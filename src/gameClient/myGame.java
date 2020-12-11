package gameClient;

import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import api.*;
import gameClient.util.Point3D;

public class myGame {
	public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS=EPS2;
	private DWGraph_Algo algo;
	private LinkedList<CL_Agent> ash;
	private LinkedList<CL_Pokemon> poke;
//	private LinkedList<String> _info;  //maybe to do
	private int numA;
	
	public myGame(game_service game) {
		stGRP(game);
		stPOKE(game);

		numOfAgent(game);
		
		
		Iterator<node_data> moveNode = null;
		Iterator<CL_Pokemon> movep = poke.iterator() ;
		//add agent by "numA" - add by find the location for one of the "poke"
		for(int i = 0 ; i < this.numA ; i++) {
			if(movep.hasNext()) {
				
				int src = movep.next().get_edge().getSrc();
				game.addAgent(src);
			}
			else if(moveNode == null){
				moveNode = this.algo.getGraph().getV().iterator();
				game.addAgent(moveNode.next().getKey());
			}
			else {
				game.addAgent(moveNode.next().getKey());
			}
		} 
		stAGE(game);
	}
	
	public void stGRP(game_service game) {
		
		DWGraph_DS grp =new DWGraph_DS();
		

		try {
			JSONObject first = new JSONObject(game.getGraph());
			JSONArray nodes = first.getJSONArray("Nodes");
			JSONArray edges = first.getJSONArray("Edges");
			for(int i = 0 ; i < nodes.length() ; i++) {
				JSONObject temp = nodes.getJSONObject(i);
				int id = temp.getInt("id");
				GeoLocation pos =new  GeoLocation(temp.getString("pos"));
				Nodes f = new Nodes(id, pos);
				grp.addNode(f);
			}
			for(int i = 0 ; i < edges.length() ; i++) {
				JSONObject temp = edges.getJSONObject(i);
				int src = temp.getInt("src");
				int dest = temp.getInt("dest");
				double w = temp.getDouble("w");
				grp.connect(src, dest, w);
			}
			this.algo = new DWGraph_Algo();
			this.algo.init(grp);

		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void stPOKE(game_service game) {
		this.poke = new  LinkedList<CL_Pokemon>();
		try {
			JSONObject first = new JSONObject(game.getPokemons());
			JSONArray second = first.getJSONArray("Pokemons");
			for(int i = 0 ; i < second.length() ; i++) {
				JSONObject third = second.getJSONObject(i);
				JSONObject fourth = third.getJSONObject("Pokemon");
				int t = fourth.getInt("type");
				double v = fourth.getDouble("value");
				String p = fourth.getString("pos");
				CL_Pokemon f = new CL_Pokemon(new Point3D(p), t, v, 0, null);
				updateEdge(f,algo.getGraph());
				this.poke.add(f);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//check if it work??? 
	public void stAGE(game_service game) {
		//check if it work??? 
		ash =new LinkedList<CL_Agent>();
		try {
			JSONObject ttt = new JSONObject(game.getAgents());
			JSONArray ags = ttt.getJSONArray("Agents");
			for(int i=0;i<ags.length();i++) {
				CL_Agent c = new CL_Agent(algo.getGraph(),0);
				c.update(ags.get(i).toString());
				ash.add(c);
			}
			//= getJSONArray("Agents");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void numOfAgent(game_service game) {
		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			numA = ttt.getInt("agents");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public LinkedList<node_data> NearestPoke(CL_Agent a) {
		double dist=-1;
		int shortEnd=-1;
		int start = a.getSrcNode();
		Iterator<CL_Pokemon> moves = this.poke.iterator() ;

		while(moves.hasNext()) {
			CL_Pokemon pok = moves.next();
			int  p = pok.get_edge().getSrc();
			double end = this.algo.shortestPathDist(start, p);	
			if(dist == -1 || (end != -1 && end < dist)){
				dist = end;
				shortEnd=p;
				a.set_curr_fruit(pok);
			}
		}
		int src = a.get_curr_fruit().get_edge().getSrc();
		int dest = a.get_curr_fruit().get_edge().getDest();
		LinkedList<node_data> q = (LinkedList<node_data>) algo.shortestPath(a.getID(), src);
		q.add(algo.getGraph().getNode(dest));
		return q;	

	}
	
	public static void updateEdge(CL_Pokemon fr, directed_weighted_graph g) {
		//	oop_edge_data ans = null;
		Iterator<node_data> itr = g.getV().iterator();
		while(itr.hasNext()) {
			node_data v = itr.next();
			Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
			while(iter.hasNext()) {
				edge_data e = iter.next();
				boolean f = isOnEdge(fr.getLocation(), e,fr.getType(), g);
				if(f) {fr.set_edge(e);}
			}
		}
	}

	private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest ) {

		boolean ans = false;
		double dist = src.distance(dest);
		double d1 = src.distance(p) + p.distance(dest);
		if(dist>d1-EPS2) {ans = true;}
		return ans;
	}
	private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
		geo_location src = g.getNode(s).getLocation();
		geo_location dest = g.getNode(d).getLocation();
		return isOnEdge(p,src,dest);
	}
	private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
		int src = g.getNode(e.getSrc()).getKey();
		int dest = g.getNode(e.getDest()).getKey();
		if(type<0 && dest>src) {return false;}
		if(type>0 && src>dest) {return false;}
		return isOnEdge(p,src, dest, g);
	}	
	
	public void updateAge(CL_Agent a,int src , int dest) {
		edge_data e = algo.getGraph().getEdge(src, dest);
		a.setNextNode(dest);
	}

	////////////////////////////////////////////////
	/////////////getters & setters//////////////////
	////////////////////////////////////////////////
	public directed_weighted_graph getGrp() {
		return algo.getGraph();
	}

	
	public DWGraph_Algo getAlgo() {
		return this.algo;
	}
	
	public void setGrp(directed_weighted_graph grp) {
		this.algo.init(grp);
	}

	public LinkedList<CL_Agent> getAsh() {
		return ash;
	}

	public void setAsh(LinkedList<CL_Agent> ash) {
		this.ash = ash;
	}

	public LinkedList<CL_Pokemon> getPoke() {
		return poke;
	}

	public void setPoke(LinkedList<CL_Pokemon> poke) {
		this.poke = poke;
	}

	public int getNumA() {
		return numA;
	}

	public void setNumA(int numA) {
		this.numA = numA;
	}
}
