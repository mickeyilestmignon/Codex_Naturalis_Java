package sae.card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sae.enums.Corner;
import sae.player.Player;

public class ResourceCard implements PlayableCard {
	
	private final List<Corner> frontCorners;
	private final Corner kingdom;
	private boolean side; // true -> Front, false -> Back
	private final String image;
	private final int point;
	
	public ResourceCard(List<Corner> frontCorners, Corner kingdom, String image, int point) {
		this.frontCorners = Objects.requireNonNull(frontCorners);
		this.kingdom = Objects.requireNonNull(kingdom);
		this.side = true;
		this.image = Objects.requireNonNull(image);
		this.point = point;
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
		allbackCorners.put("mainResource", List.of(kingdom));
		return allbackCorners;
	}
	
	@Override
	public String image() {
		return image;
	}
	
	@Override
	public String retrieveBackImage() {
		return "Resource" + kingdom.toString() + "_back.png";
	}

	@Override
	public boolean isGold() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Corner kingdom() {
		// TODO Auto-generated method stub
		return kingdom;
	}

	@Override
	public boolean isStarter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int gainPoints(Player player, int numberCornersCovered) {
		Objects.requireNonNull(player);
		if (side) {
			return point;
		}
		return 0;
	}

}
