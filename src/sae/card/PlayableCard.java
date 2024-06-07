package sae.card;

import java.util.List;
import java.util.Map;

import sae.enums.Corner;
import sae.player.Player;

public interface PlayableCard extends Card {
	
	public List<Corner> frontCorners();
	public Map<String, List<Corner>> backCorners(); // PERMANENT OU SUR LES BORDS
	public boolean side();
	public void flip();
	public int gainPoints(Player player,  int numberCornersCovered);
	public boolean isGold();
	public boolean isStarter();
	public Corner kingdom();

}
