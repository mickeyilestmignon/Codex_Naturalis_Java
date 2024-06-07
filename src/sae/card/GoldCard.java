package sae.card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sae.enums.Corner;
import sae.player.Player;

public class GoldCard implements PlayableCard {
	
	private final List<Corner> frontCorners; 
	private final Corner kingdom;
	private final char pointsCondition; // Condition à respecter pour gagner les points
	private final int points;
	private final Map<Corner, Integer> resourcesCost;
	private boolean side; // true -> Front, false -> Back
	private final String image; //nom du fichier image
	
	public GoldCard(List<Corner> frontCorners, Corner kingdom, char pointsCondition, int points, Map<Corner, Integer> resourcesCost, String image) {
		this.frontCorners = Objects.requireNonNull(frontCorners);
		this.kingdom = Objects.requireNonNull(kingdom);
		this.pointsCondition = pointsCondition;
		this.points = points;
		this.resourcesCost = Objects.requireNonNull(resourcesCost);
		this.side = true;
		this.image = Objects.requireNonNull(image);
	}
	
	public Map<Corner, Integer> ressourceCost() {
		return resourcesCost;
	}
	
	@Override
	public List<Corner> frontCorners() {
		return frontCorners;
	}
	
	@Override
	public Corner kingdom() {
		return kingdom;
	}
	
	@Override
	public boolean side() {
		return side;
		}
	
	@Override
	public void flip() {
		side = !(side);
	}
	
	@Override
	public Map<String, List<Corner>> backCorners() {
		var allBackResources = new HashMap<String, List<Corner>>();
		allBackResources.put("mainResource", List.of(kingdom));
		return allBackResources;
	}

	@Override
	public String image() {
		return image;
	}
	
	@Override
	public String retrieveBackImage() {
		return "Gold" + kingdom.toString() + "_back.png";
	}

	@Override
	public boolean isGold() {
		return true;
	}

	@Override
	public boolean isStarter() {
		return false;
	}

	@Override
	public int gainPoints(Player player, int numberCornersCovered) {
		Objects.requireNonNull(player);
		if (side) {
			switch (pointsCondition) {
				case 'D':
					return points;
				case 'I':
					return points * player.getCornerQuantity(Corner.INKWELL);
				case 'M':
					return points * player.getCornerQuantity(Corner.MANUSCRIPT);
				case 'Q':
					return points * player.getCornerQuantity(Corner.QUILL);
				case 'C':
					return points * numberCornersCovered;
				default:
					return 0;
			}
		}
		return 0;
	}
}
