package sae.card;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import sae.controller.Game;
import sae.enums.Corner;
import sae.player.Player;

public class ObjectivePattern implements Objective {

	private final String objectiveType; // Pattern, Resource, Artifact
	private final Map<Point, Corner> conditions; // Pattern à respecter
	private final int points;
	private final String image; //nom du fichier image
	
	public ObjectivePattern(String objectiveType, LinkedHashMap<Point, Corner> conditions, int points, String image) {
		this.objectiveType = Objects.requireNonNull(objectiveType);
		this.conditions = Objects.requireNonNull(conditions);
		this.points = points;
		this.image = Objects.requireNonNull(image);
	}
	
	@Override
	public String objectiveType() {
		return objectiveType;
	}
	
	@Override
	public String image() {
		return image;
	}
	
	@Override
	public String retrieveBackImage() {
		return "Objective_back.png";
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof ObjectivePattern objPattern
			&& image.equals(objPattern.image)
			&& objectiveType.equals(objPattern.objectiveType)
			&& conditions.equals(objPattern.conditions)
			&& points == objPattern.points;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(image, objectiveType, conditions, points);
	}
	
	@Override
	public int calculatePlayerPoints(Game game, int playerNumber) {
		Objects.requireNonNull(game);
		
		Player player = game.players().get(playerNumber);
		int objectivePoints = 0;
		ArrayList<Point> lockedPoints = new ArrayList<Point>();
		
		for (Point point : player.cardsPlaced().keySet()) {
			ArrayList<Point> tempPoints = new ArrayList<Point>();
			int step = 0;
			for (Point pointCondition : conditions.keySet()) {
				
				// POINT SUR LA BOARD + POINT DE LA CONDITION
				Point currentPoint = point.sum(pointCondition);
				if (!player.cardsPlaced().containsKey(currentPoint)) {
					break;
				}
				
				// SI CA CORRESPOND
				if (player.cardsPlaced().get(currentPoint).kingdom() == conditions.get(pointCondition) && !lockedPoints.contains(currentPoint)) {
					tempPoints.add(currentPoint);
					step += 1;
				}
				
				// SI ON A VERIFIE LES 3 CONDITIONS, ON AJOUTE LES POINTS
				if (step == 3) {
					tempPoints.add(currentPoint);
					lockedPoints.addAll(tempPoints);
					objectivePoints += points;
				}
			}
		}
		return objectivePoints;
	}
}