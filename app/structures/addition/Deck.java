package structures.addition;
import java.util.ArrayList;
import java.util.List;

import structures.basic.*;

/* 
 * Deck @Shiyu
 * 
 */

public class Deck {
	List<Card> playerDeck = new ArrayList<Card>(20);
	
	public List<Card> getPlayerDeck() {
		return playerDeck;
	}

	public void setPlayerDeck(List<Card> playerDeck) {
		this.playerDeck = playerDeck;
	}
	
	
	
}
