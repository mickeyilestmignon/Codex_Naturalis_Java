package sae.card;

import java.util.Objects;

import sae.controller.Game;
import sae.enums.Corner;
import sae.player.Player;

public class ObjectiveRA implements Objective {

	private final String objectiveType; // Pattern, Resource, Artifact
	private final Corner typeRA; // Type de ressource ou artefact
	private final int quantityRA; // Quantité à avoir sur le plateau
	private final int points;
	private final String image;
	
	public ObjectiveRA(String objectiveType, Corner typeRA, int quantityRA, int points, String image) {
		this.objectiveType = Objects.requireNonNull(objectiveType);
		this.typeRA = Objects.requireNonNull(typeRA);
		this.quantityRA = quantityRA;
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
		return o instanceof ObjectiveRA objRA
			&& image.equals(objRA.image)
			&& objectiveType.equals(objRA.objectiveType)
			&& typeRA == objRA.typeRA
			&& quantityRA == objRA.quantityRA
			&& points == objRA.points;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(image, objectiveType, typeRA, quantityRA, points);
	}
	
	public int calculatePlayerPoints(Game game, int playerNumber) {
		Objects.requireNonNull(game);
		Player player = game.players().get(playerNumber); //Joueur correspondant au numéro
		int pointsGained = player.getCornerQuantity(typeRA)/quantityRA * points; //Calcul des points gagnés
		return pointsGained;
	}

}