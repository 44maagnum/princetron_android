package princeTron.Engine;

import java.util.ArrayList;
import java.util.HashMap;

import princeTron.UserInterface.Arena;

import android.os.Handler;
import android.os.Message;

import android.util.Log;

public class GameEngine extends princeTron.Network.NetworkGame {
	
	private static final int TRUE = 1;
	private static final int FALSE = 0;
	// array of players' turns. Indexed by player id
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	// number of tics since game started
	public Integer numTics = 0;
	private boolean isReady = false;
	private int myId = -1;
	// for collision detection - the proper way is mysteriously not working
	private HashMap<Integer, ArrayList<Integer>> visitedMap;

	private Handler handler;

	public static final int X_SCALE = 100;
	public static final int Y_SCALE = 100;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final long mMoveDelay = 100;

	public GameEngine(Handler handler) {
		visitedMap = new HashMap<Integer, ArrayList<Integer>>();
		this.handler = handler;
		visitedMap = new HashMap<Integer, ArrayList<Integer>>();
		players = new HashMap<Integer, Player>();
		numTics = 0;
		myId = -1;
	}
	
	public void passInvitation(String username) {
		Message msg = handler.obtainMessage(princeTron.UserInterface.Arena.INVITED);
		msg.obj = username;
		msg.sendToTarget();
	}
	
	public void passLogin(String[] otherUsers) {
		Log.i("GameEngine", "in passLogin()");
		Message msg = handler.obtainMessage(princeTron.UserInterface.Arena.LOGGED_IN);
		Log.i("GameEngine", ""+msg);
		msg.obj = otherUsers;
		msg.sendToTarget();
	}
	
	public void passLobbyUpdate(String name, boolean hasEntered) {
		Message msg = handler.obtainMessage(princeTron.UserInterface.Arena.LOBBY_UPDATE);
		msg.obj = name;
		if (hasEntered) msg.arg1 = TRUE;
		else msg.arg1 = FALSE;
		msg.sendToTarget();
	}
	
	// initializes the game, and the informs the UI
	// the player id's are with respect to the initial X values, 
	// and then Y values to break a tie
	public void passEnterArena(Coordinate[] starts, int[] dirs, String[] names, int myId) {
		visitedMap = new HashMap<Integer, ArrayList<Integer>>();
		players = new HashMap<Integer, Player>();
		Log.i("GameEngine", ""+starts.length);
		for (int i = 0; i < starts.length; i++) {
				Player p = new Player(starts[i], dirs[i], i, names[i]);
				players.put(i, p);
		}
		this.myId = myId;
		Log.i("GameEngine", "myId: " + myId);
		Message msg = handler.obtainMessage(Arena.IN_ARENA);
		msg.arg1 = myId;
		msg.sendToTarget();
		Thread.yield();
	}

	// check that the new branch worked
	
	// steps all the snakes forwards, returns true if there was a collision
	// on the local snake
	public synchronized Coordinate update(boolean toReturn) {
		//Log.i("GameEngine", "visited: " + visitedMap.size());
		for (Integer i : players.keySet()) {
			Player player = players.get(i);
			if (!player.hasStopped()) {
				player.stepForward();
				Coordinate current = player.currentPoint();
				ArrayList<Integer> yList = visitedMap.get(current.x);
				if (yList == null) {
					yList = new ArrayList<Integer>();
				}
				if (player.getId() == myId && yList.contains(current.y)) {
					Log.i("GameEngine", "Crash location: "+current);
					Log.i("GameEngine", "crash!");
					if (toReturn) return current; // collision
				}
				else {
					yList.add(current.y);
				}
				if ((player.getId() == myId) && (current.x > 100 || current.y > 100 
						|| current.x < 0 || current.y < 0)) {
					Log.i("GameEngine", "off the edge!");
					Log.i("GameEngine", "Crash location: "+current);
					if (toReturn) return current; // off the edge
				}
				visitedMap.put(current.x, yList);
			}
			players.put(i, player);
		}
		numTics++;
		return null;
	}

	public synchronized ArrayList<Player> getPlayers() {
		ArrayList<Player> toReturn = new ArrayList<Player>();
		for (Integer i : players.keySet()) {
			toReturn.add(players.get(i));
		}
		return toReturn;
	}

	// called by the UI when the player turns. argument is true if 
	// left turn, false otherwise
	public synchronized void turn(boolean isLeft) {
		Log.i("GameEngine", "turning in gameEngine");
		Player player = players.get(myId);
		player.turn(isLeft, numTics);
		players.put(myId, player);
		Log.i("GameEngine", "finishing turn in gameEngine");
	}
	
	@Override
	public synchronized Coordinate opponentTurn(int playerId, int time, boolean isLeft) {
		int oldNumTics = numTics;
		if (time > numTics) {
			Player player = players.get(playerId);
			player.turn(isLeft, time);
			players.put(playerId, player);
			return null;
		}
		for (Integer i : players.keySet()) {
			Player p = players.get(i);
			while (p.numTics > time) {
				ArrayList<Coordinate> removed = p.stepBackward(1);
				for (Coordinate c : removed) {
					ArrayList<Integer> yList = visitedMap.get(c.x);
					if (yList == null) yList = new ArrayList<Integer>();
					yList.remove((Integer) c.y);
					visitedMap.put(c.x, yList);
				}
			}
			players.put(i, p);
		}
		Player player = players.get(playerId);
		player.turn(isLeft, time);
		players.put(playerId, player);
		numTics = time;
		Coordinate toReturn = null;
		for (; numTics < oldNumTics; ) {
			Coordinate c = update(true);
			if (c != null) {
				toReturn = c;
			}
		}
		return toReturn;
	}

	public Iterable<Player> getTrails() {
		return players.values();
	}

	public boolean isReady() {
		return isReady;
	}
	
	public void startGame() {
		Message msg = handler.obtainMessage(Arena.PLAYING);
		msg.sendToTarget();
		Thread.yield();
	}
	
	public void endGame() {
		Message msg = handler.obtainMessage(Arena.IN_LOBBY);
		String report = "";
		for (Player p : players.values()) {
			if (!p.hasLost) {
				report += p.name + " wins! :)\n";
			}
			else {
				report += p.name + " loses :(\n";
			}
		}
		msg.obj = report;
		msg.sendToTarget();
	}

	public void gameResult(int playerId, boolean isWin) {
		for (Player player : players.values()) {
			Log.i("GameEngine", "player " + player.getId() + "loc: " + player.currentPoint());
			if (player.getId() == playerId) {
				if (!isWin) {
					player.hasLost = true;
				}
				player.stop();
				Message msg = handler.obtainMessage(Arena.PLAYER_CRASH);
				msg.obj = player.name;
				msg.sendToTarget();
			}
		}
	}
}
