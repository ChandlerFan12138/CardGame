package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * In the user’s browser, the game is running in an infinite loop, where there is around a 1 second delay 
 * between each loop. Its during each loop that the UI acts on the commands that have been sent to it. A 
 * heartbeat event is fired at the end of each loop iteration. As with all events this is received by the Game 
 * Actor, which you can use to trigger game logic.
 * 
 * { 
 *   String messageType = “heartbeat”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 * @author Shiyu Ren
 */
public class Heartbeat implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		if(gameState.player1Deck.size() == 0) {
			gameState.isPlayerTurn = false;
			BasicCommands.addPlayer1Notification(out, "Player's deck is empty!", 2);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} 
			BasicCommands.addPlayer1Notification(out, "AI win!", 10);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} 
		}else if (gameState.AIDeck.size() == 0){
			gameState.isPlayerTurn = false;
			BasicCommands.addPlayer1Notification(out, "AI's deck is empty!", 2);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} 
			BasicCommands.addPlayer1Notification(out, "Player win!", 10);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} 
		}
		
	}
}
