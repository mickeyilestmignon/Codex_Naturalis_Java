package sae.card;

import java.util.Objects;

import sae.controller.Globals;

public class Pile {
	 
	private final String cardType;
	private PlayableCard hiddenCard;
	private PlayableCard revealedCard1;
	private PlayableCard revealedCard2;
	private boolean isEmpty;
	
	public Pile(String cardType) {
		Objects.requireNonNull(cardType);
		this.hiddenCard = (PlayableCard) Card.pickRandomCard(cardType);
		Globals.playableDeck.removeCard(cardType, hiddenCard);
		this.cardType = cardType;
		
		this.revealedCard1 = (PlayableCard) Card.pickRandomCard(cardType);
		Globals.playableDeck.removeCard(cardType, revealedCard1);
		
		this.revealedCard2 = (PlayableCard) Card.pickRandomCard(cardType);
		Globals.playableDeck.removeCard(cardType, revealedCard2);
		
		this.isEmpty = false;
	}
	
	public void updateHiddenCard() { // QUAND LE JOUEUR PIOCHE UNE CARTE DE LA PILE (NON VISIBLE)
		hiddenCard = (PlayableCard)Card.pickRandomCard(cardType);
		if (hiddenCard != null) {
			Globals.playableDeck.removeCard(cardType, hiddenCard);
		}
	}
	
	public void updateRevealedCard1() {
		revealedCard1 = hiddenCard;
		updateHiddenCard();
	}
	
	public void updateRevealedCard2() {
		revealedCard2 = hiddenCard;
		updateHiddenCard();
	}
	
	public PlayableCard hiddenCard() {
		return hiddenCard;
	}
	
	public PlayableCard revealedCard1() {
		return revealedCard1;
	}
	
	public PlayableCard revealedCard2() {
		return revealedCard2;
	}
	
	public boolean isEmpty() {
		return isEmpty;
	}
	
	public void setIsEmpty() {
		isEmpty = true;
	}
}