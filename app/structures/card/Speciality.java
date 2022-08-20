package structures.card;
import akka.actor.ActorRef;
import commands.BasicCommands;
import com.fasterxml.jackson.databind.JsonNode;
import structures.GameState;
import structures.basic.*;



public interface Speciality {
	public void ability(ActorRef out, GameState gameState, JsonNode message);
}
