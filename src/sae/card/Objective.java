package sae.card;

import sae.controller.Game;

public interface Objective extends Card {

	public String objectiveType();
	public String image();
	public int calculatePlayerPoints(Game game, int playerNumber);

}