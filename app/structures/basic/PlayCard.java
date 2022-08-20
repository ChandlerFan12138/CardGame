package structures.basic;

import akka.actor.ActorRef;
import structures.GameState;

/*PlayCard interface @Yunyi
 * 
 * this interface has a method for cards using
 * when a card has been used successfully, return true
 */

public interface PlayCard {
	//the way of using unit cards and spell cards
	public boolean play(ActorRef out, GameState gameState, int tilex, int tiley);
}
