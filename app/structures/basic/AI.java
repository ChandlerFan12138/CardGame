package structures.basic;

import commands.BasicCommands;
import structures.GameState;
import akka.actor.ActorRef;

public class AI extends Player{

	public AI() {
		super();

	}
	public AI(int i, int j) {
		// TODO Auto-generated constructor stub
		super();
	}
public void playCard(ActorRef out, GameState gameState, int tilex, int tiley) {
		//play a card
		if(gameState.selectedCard.play(out, gameState, tilex, tiley)) {
			//delete the used card in the hand
			
			//cost mana 
			this.setMana(mana-gameState.selectedCard.getManacost());
			BasicCommands.setPlayer2Mana(out, this);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			//delete the selectedCard in the gameState
			gameState.selectedCard = null;
		}else {
			BasicCommands.addPlayer1Notification(out, "Incorrect Selected Unit", 2);
		}
	}
	
}

