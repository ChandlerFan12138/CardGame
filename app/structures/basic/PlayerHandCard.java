package structures.basic;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

public class PlayerHandCard {
	
	private ArrayList<Card> playerHandCard;
	
	public PlayerHandCard() {
		this.playerHandCard = new ArrayList<Card>();
	}
	
	//initialize the players' Hand at the beginning
	public void initalizePlayerHandCard() {
		
	}
	
	//add card in players' hand
	public void addCard(Card card) {
		playerHandCard.add(card);
	}
	//remove card from players' hand
	public void removeCard(Card card) {
		
	}
	
	//play card
	public void cardPlay(ActorRef out, int position) {
		//After clicking a card, delete the card in hand
		playerHandCard.remove(position-1);
		BasicCommands.deleteCard(out, position);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//reload the order of card
		for(int i = position-1; i< playerHandCard.size(); i++) {
			playerHandCard.set(position-1, playerHandCard.get(position));
		}
		playerHandCard.remove(position);
		
	}
}
