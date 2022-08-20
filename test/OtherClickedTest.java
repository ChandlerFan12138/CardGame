
import events.OtherClicked;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;


public class OtherClickedTest {
	@Test
	public void checkotherclicked() {
		Initalize initalizeProcessor =  new Initalize();

		// First override the alt tell variable so we can issue commands without a running front-end
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell; // specify that the alternative tell should be used
		
		// As we are not starting the front-end, we have no GameActor, so lets manually create
		// the components we want to test
		GameState gameState = new GameState(); // create state storage
		OtherClicked otherclickedProcessor =  new OtherClicked(); // create an initalize event processor
		
		assertFalse(gameState.OtherClicked); // check we have not initalized
		
		// lets simulate recieveing an initalize message
		ObjectNode eventMessage = Json.newObject(); // create a dummy message
		initalizeProcessor.processEvent(null, gameState, eventMessage);
		otherclickedProcessor.processEvent(null, gameState, eventMessage); // send it to the initalize event processor
		
		assertTrue(gameState.OtherClicked); // check that this updated the game state
		
	
		
	}
}
