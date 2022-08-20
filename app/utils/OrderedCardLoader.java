package utils;

import java.util.ArrayList;
import java.util.List;

import structures.basic.*;

/**
 * This is a utility class that provides methods for loading the decks for each
 * player, as the deck ordering is fixed. 
 * @author Richard
 *
 */
public class OrderedCardLoader {

	/**
	 * Returns all of the cards in the human player's deck in order
	 * @return
	 */
	public static List<Card> getPlayer1Cards() {
	
		List<Card> cardsInDeck = new ArrayList<Card>(20);
		
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger, 0, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_pureblade_enforcer, 1, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 2, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_knight, 3, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_truestrike, 4, SpellCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_azure_herald, 5, Azure_Herald.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_ironcliff_guardian, 6, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_azurite_lion, 7, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_sundrop_elixir, 8, SpellCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, 9, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_knight, 10, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 11, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger, 12, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_pureblade_enforcer, 13, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_truestrike, 14, SpellCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_azure_herald, 15, Azure_Herald.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_ironcliff_guardian, 16, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_azurite_lion, 17, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_sundrop_elixir, 18, SpellCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, 19, UnitCard.class));
		
		return cardsInDeck;
	}
	
	
	/**
	 * Returns all of the cards in the human player's deck in order
	 * @return
	 */
	public static List<Card> getPlayer2Cards() {
	
		List<Card> cardsInDeck = new ArrayList<Card>(20);
		
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_rock_pulveriser, 20, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_bloodshard_golem, 21, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_staff_of_ykir, 22, SpellCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_blaze_hound, 23, Blaze_Hound.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_windshrike, 24, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_pyromancer, 25, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_serpenti, 26, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_entropic_decay, 27, SpellCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_planar_scout, 28, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, 29, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_rock_pulveriser, 30, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_bloodshard_golem, 31, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_staff_of_ykir, 32, SpellCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_blaze_hound, 33, Blaze_Hound.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_windshrike, 34, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_pyromancer, 35, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_serpenti, 36, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_entropic_decay, 37, SpellCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_planar_scout, 38, UnitCard.class));
		cardsInDeck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, 39, UnitCard.class));
		
		return cardsInDeck;
	}
	
}
