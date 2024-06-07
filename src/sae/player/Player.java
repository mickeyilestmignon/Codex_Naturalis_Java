package sae.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sae.card.Card;
import sae.card.GoldCard;
import sae.card.Objective;
import sae.card.PlayableCard;
import sae.card.Point;
import sae.card.StarterCard;
import sae.controller.Game;
import sae.enums.Corner;

public class Player {
	
	private final String pseudo;
	private int points;
	private final Map<Corner, Integer> cornerQuantity; // QUANTITE POUR CHAQUE CORNER
	private final List<PlayableCard> hand; // 2 Resource, 1 Gold
	private final LinkedHashMap<Point, PlayableCard> cardsPlaced;
	private final StarterCard starterCard;
	private final Objective objective;
	
	private Player(String pseudo, int points, List<PlayableCard> hand, LinkedHashMap<Point, PlayableCard> cardsPlaced, StarterCard starterCard, Objective objective, Map<Corner, Integer> cornerNumber) {
		this.pseudo = Objects.requireNonNull(pseudo);
		this.hand = Objects.requireNonNull(hand);
		this.cardsPlaced = Objects.requireNonNull(cardsPlaced);
		this.starterCard = Objects.requireNonNull(starterCard);
		this.objective = Objects.requireNonNull(objective);
		this.cornerQuantity = Objects.requireNonNull(cornerNumber);
		this.points = points;
	}
	
	public Player(String pseudo, List<PlayableCard> hand, StarterCard starterCard, Objective objective) {
		this(pseudo, 0, hand, new LinkedHashMap<Point, PlayableCard>(), starterCard, objective, Player.generateEmptyCornerQuantity());
	}
	
	public void addCardToHand(PlayableCard card) {
		Objects.requireNonNull(card);
		hand.add(card);
	}
	
	public void addCardToHand(String cardType) {
		Objects.requireNonNull(cardType);
		hand.add(Card.pickRandomCard(cardType));
	}
	
	public int getCornerQuantity(Corner corner) {
		return cornerQuantity.get(corner);
	}
	
	public List<Point> whereToPlace(PlayableCard card) {
		Objects.requireNonNull(card);
		ArrayList<Point> usablePoints = new ArrayList<Point>();
		for (Map.Entry<Point, PlayableCard> entry : cardsPlaced.entrySet()) {
			List<Point> pointList = List.of(new Point(entry.getKey().i()-1, entry.getKey().j()-1), new Point(entry.getKey().i()+1, entry.getKey().j()-1), new Point(entry.getKey().i()-1, entry.getKey().j()+1), new Point(entry.getKey().i()+1, entry.getKey().j()+1)); // HAUTDROITE, HAUTGAUCHE, BASTDROITE, BASGAUCHE
			for (int i = 0; i < pointList.size(); i++) {
				if (!cardsPlaced.keySet().contains(pointList.get(i)) && !usablePoints.contains(pointList.get(i)) && (entry.getValue().side() ? entry.getValue().frontCorners().get(i) != Corner.INVISIBLE : entry.getValue().isStarter() ? entry.getValue().backCorners().get("backCorners").get(i) != Corner.INVISIBLE : true) && (card.isGold() && card.side() ? canPlaceGoldCard((GoldCard) card) : true)) {
					usablePoints.add(pointList.get(i));
				}
			}
		}
		// NE PAS QU'UN CARTE SOIT SUR UN CORNER INVISIBLE
		ArrayList<Point> deletePoints = new ArrayList<Point>();
		for (int i = 0; i < usablePoints.size(); i++) { //vérification sur chaque emplacement de carte possible
			
			//HAUTGAUCHE
			var p1 = new Point(usablePoints.get(i).i()-1, usablePoints.get(i).j()-1);
			//HAUTDROITE
			var p2 = new Point(usablePoints.get(i).i()+1, usablePoints.get(i).j()-1);
			//BASGAUCHE
			var p3 = new Point(usablePoints.get(i).i()-1, usablePoints.get(i).j()+1);
			//BASDROITE
			var p4 = new Point(usablePoints.get(i).i()+1, usablePoints.get(i).j()+1);
			
			if (removePointIfOnInvisible(usablePoints, i, p1, 3)) {deletePoints.add(usablePoints.get(i));}
			else if (removePointIfOnInvisible(usablePoints, i, p2, 2)) {deletePoints.add(usablePoints.get(i));}
			else if (removePointIfOnInvisible(usablePoints, i, p3, 1)) {deletePoints.add(usablePoints.get(i));}
			else if (removePointIfOnInvisible(usablePoints, i, p4, 0)) {deletePoints.add(usablePoints.get(i));}
			
		}
		for (Point point : deletePoints) {
			usablePoints.remove(point);
		}
		return Collections.unmodifiableList(usablePoints);
	}
	
	private boolean removePointIfOnInvisible(List<Point> usablePoints, int i, Point p, int emplacement) {
		Objects.requireNonNull(usablePoints);
		Objects.requireNonNull(p);
		if (cardsPlaced.containsKey(p)) {
			if (cardsPlaced.get(p).side() ? cardsPlaced.get(p).frontCorners().get(emplacement) == Corner.INVISIBLE : cardsPlaced.get(p).isStarter() ? cardsPlaced.get(p).backCorners().get("backCorners").get(emplacement) == Corner.INVISIBLE : false) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canPlaceGoldCard(GoldCard card) {
		Objects.requireNonNull(card);
		for (Map.Entry<Corner, Integer> entry : card.ressourceCost().entrySet()) {
			// COUT DE LA GOLDCARD
			if (cornerQuantity.get(entry.getKey()) < entry.getValue()) {
				return false;
			}
		}
		return true;
	}
	
	private void addCardValue(PlayableCard card) {
		Objects.requireNonNull(card);
		// AJOUTER LES BORDS
		if (card.side()) {
			for (Corner corner : card.frontCorners()) {
				if (corner != Corner.EMPTY && corner != Corner.INVISIBLE) {
					cornerQuantity.put(corner, cornerQuantity.get(corner) + 1);
				}
			}
			if (card.isGold()) {
				cornerQuantity.put(((GoldCard) card).kingdom(), cornerQuantity.get(((GoldCard) card).kingdom()) + 1);
			}
		}
		else {
			for (Map.Entry<String, List<Corner>> corner : card.backCorners().entrySet()) {
				for (Corner corner2 : corner.getValue()) {
					if (corner2 != Corner.EMPTY && corner2 != Corner.INVISIBLE) {
						cornerQuantity.put(corner2, cornerQuantity.get(corner2) + 1);
					}
				}
			}
		}
	}
	
	public int removeCornersCovered(Point point) {
		Objects.requireNonNull(point);
		int cpt = 0;
		if (cardsPlaced.containsKey(point)) {
			//HAUTGAUCHE
			var p1 = new Point(point.i()-1, point.j()-1);
			cpt += deleteCorner(p1, 3);
			//HAUTDROITE
			var p2 = new Point(point.i()+1, point.j()-1);
			cpt += deleteCorner(p2, 2);
			//BASGAUCHE
			var p3 = new Point(point.i()-1, point.j()+1);
			cpt += deleteCorner(p3, 1);
			//BASDROITE
			var p4 = new Point(point.i()+1, point.j()+1);
			cpt += deleteCorner(p4, 0);
		}
		return cpt;
	}
	
	private int deleteCorner(Point point, int emplacement) {
		// SUPPRIMER LES CORNERS RECOUVERTS
		if (cardsPlaced.containsKey(point)) {
			Corner deleteCorner = (cardsPlaced.get(point).side() ? cardsPlaced.get(point).frontCorners().get(emplacement) : cardsPlaced.get(point).isStarter() ? cardsPlaced.get(point).backCorners().get("backCorners").get(emplacement) : Corner.EMPTY);
			if (deleteCorner != Corner.EMPTY && deleteCorner != Corner.INVISIBLE) {
				cornerQuantity.put(deleteCorner, cornerQuantity.get(deleteCorner) - 1);
			}
			return 1;
		}
		return 0;
	}
	
	public int points() {
		return points;
	}
	
	public String pseudo() {
		return pseudo;
	}
	
	public void addPoints(PlayableCard card, int numberCornersCovered) {
		Objects.requireNonNull(card);
		points += card.gainPoints(this, numberCornersCovered);
	}
	
	public boolean oneCardPlaced() {
		return (cardsPlaced.size() > 0);
	}
	
	public Map<Point, PlayableCard> cardsPlaced() {
		return cardsPlaced;
	}
	
	public void placeCard(Point point, PlayableCard card) {
		Objects.requireNonNull(point);
		Objects.requireNonNull(card);
		addCardValue(card);
		cardsPlaced.put(point, card);
	}
	
	public void removeCardFromHand(Card card) {
		Objects.requireNonNull(card);
		hand.remove(card);
	}
	
	public Objective getObjective() {
		return objective;
	}
	
	public List<PlayableCard> getHand() {
		return hand;
	}
	
	public StarterCard starterCard() {
		return starterCard;
	}
	
	public Objective objective() {
		return objective;
	}
	
	@Override
	public String toString() {
		var s = new StringBuilder();
		s.append("[pseudo = ").append(pseudo).append(", points = ").append(points);
		s.append(", starterCard = ").append(starterCard).append(", objective = ").append(objective);
		s.append(", hand = ").append(hand).append("]");
		return s.toString();
	}
	
	public List<Integer> getPointsFromObjectives(Game game, int playerNumber) {
		List<Integer> pointsFromObjectives = new ArrayList<Integer>();
		pointsFromObjectives.add(game.commonObjective1().calculatePlayerPoints(game, playerNumber)); //0 : Objectif commun 1
		pointsFromObjectives.add(game.commonObjective2().calculatePlayerPoints(game, playerNumber)); //1 : Ojectif commun 2
		pointsFromObjectives.add(game.players().get(playerNumber).objective.calculatePlayerPoints(game, playerNumber)); //2 : Objectif personnel
		for (Integer pts : pointsFromObjectives) {
			points += pts;
		}
		return pointsFromObjectives;
	}

	public static Map<Corner, Integer> generateEmptyCornerQuantity() {
		HashMap<Corner, Integer> cornerNumber = new HashMap<Corner,Integer>();
		cornerNumber.put(Corner.QUILL, 0);
		cornerNumber.put(Corner.MANUSCRIPT, 0);
		cornerNumber.put(Corner.INKWELL, 0);
		cornerNumber.put(Corner.ANIMAL, 0);
		cornerNumber.put(Corner.PLANT, 0);
		cornerNumber.put(Corner.FUNGI, 0);
		cornerNumber.put(Corner.INSECT, 0);
		return cornerNumber;
	}
}
