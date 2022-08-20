
import events.TileClicked;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Heartbeat;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;


public class HeartbeatTest {
	@Test
	public void heartbeat() {
		
		// First override the alt tell variable so we can issue commands without a running front-end
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		// As we are not starting the front-end, we have no GameActor, so lets manually create
		// the components we want to test
		GameState gameState = new GameState(); // create state storage
		Heartbeat heartbeatProcessor =  new Heartbeat(); // create an initalize event processor
		
		assertFalse(gameState.HeartBeat); // check we have not initalized
		
		// lets simulate recieveing an initalize message
		ObjectNode eventMessage = Json.newObject(); // create a dummy message
		Initalize initalizeProcessor =  new Initalize();
		initalizeProcessor.processEvent(null, gameState, eventMessage);
		heartbeatProcessor.processEvent(null, gameState, eventMessage); // send it to the initalize event processor
		
		assertTrue(gameState.HeartBeat); // check that this updated the game state
		
	}

}
