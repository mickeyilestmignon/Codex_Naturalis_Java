package sae.card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sae.enums.Corner;
import sae.player.Player;

public class StarterCard implements PlayableCard {
	
	private final List<Corner> frontCorners;
	private final List<Corner> backCorners;
	private final List<Corner> permanentResources;
	private boolean side; // true -> Front, false -> Back
	private final String frontImage;
	private final String backImage;
	
	public StarterCard (List<Corner> frontCorners, List<Corner> backCorners, List<Corner> permanentResources, String frontImage, String backImage) {
		this.frontCorners = Objects.requireNonNull(frontCorners);
		this.backCorners = Objects.requireNonNull(backCorners);
		this.permanentResources = Objects.requireNonNull(permanentResources);
		this.side = true;
		this.frontImage = Objects.requireNonNull(frontImage);
		this.backImage = Objects.requireNonNull(backImage);
	}
	
	@Override
	public List<Corner> frontCorners() {
		return frontCorners;
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
		var allbackCorners = new HashMap<String, List<Corner>>();
		allbackCorners.put("backCorners", backCorners);
		allbackCorners.put("permanentResources", permanentResources);
		return allbackCorners;
	}
	
	@Override
	public String image() {
		if (side) {
			return frontImage;
		} else {
			return backImage;
		}
	}
	
	public String frontImage() {
		return frontImage;
	}
	
	public String backImage() {
		return backImage;
	}

	@Override
	public String retrieveBackImage() {
		return backImage;
	}

	@Override
	public boolean isGold() {
		return false;
	}

	@Override
	public Corner kingdom() {
		return null;
	}

	@Override
	public boolean isStarter() {
		return true;
	}

	@Override
	public int gainPoints(Player player, int numberCornersCovered) {
		Objects.requireNonNull(player);
		return 0;
	}
}
