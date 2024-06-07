package sae.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import sae.card.Card;
import sae.card.Objective;
import sae.card.Pile;
import sae.player.Player;

public class Game {
	
	private final List<Player> players;
	private final Objective commonObjective1;
	private final Objective commonObjective2;
	private final Pile resourcePile;
	private final Pile goldPile;
	private boolean playerFinished;
	private boolean lastTurn;
	
	public Game(List<Player> playerList) {
		Objects.requireNonNull(playerList);
		this.players = Collections.unmodifiableList(playerList);
		
		// OBJECTIFS COMMUNS
		this.commonObjective1 = (Objective) Card.pickRandomtObjectiveCard();
		Globals.objectiveDeck.removeCard(commonObjective1);
		
		this.commonObjective2 = (Objective) Card.pickRandomtObjectiveCard();
		Globals.objectiveDeck.removeCard(commonObjective2);
		
		// INITIALISATION DES PILES
		this.resourcePile = new Pile("resourceCard");
		this.goldPile = new Pile("goldCard");
		this.playerFinished = false;
		this.lastTurn = false;
	}
	
	public List<Player> players() {
		return players;
	}
	
	public boolean allPlayersEmptyHands() {
		for (Player player : players) {
			if (player.getHand().size() > 0) {
				return false;
			}
		}
		return true;
	}
	
	public Objective commonObjective1() {
		return commonObjective1;
	}
	
	public Objective commonObjective2() {
		return commonObjective2;
	}
	
	public Pile resourcePile() {
		return resourcePile;
	}
	
	public Pile goldPile() {
		return goldPile;
	}
	
	public boolean playerFinished() {
		return playerFinished;
	}
	
	public void setPlayerFinished() {
		playerFinished = true;
	}
	
	public boolean lastTurn() {
		return lastTurn;
	}
	
	public void setLastTurn() {
		lastTurn = true;
	}
}