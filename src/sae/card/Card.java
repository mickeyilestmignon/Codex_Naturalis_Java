package sae.card;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import sae.controller.Globals;
import sae.enums.Corner;
import sae.util.RandInt;

public interface Card {
	
	public String image();
	public String retrieveBackImage();
	
	/**
	 * @param path, chemin d'accès de deck.txt
	 * @return renvoie une map de parsedLine (types de cartes) associées à une liste de toutes ces cartes.
	 * @throws IOException
	 */
	public static void generateDeck() throws IOException {
		Path path = Path.of("./include", "deck.txt");
		//ObjectiveDeck deck = new ObjectiveDeck();
		try (var reader = Files.newBufferedReader(path);) {
			String line;
			int lineNumber = 0;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				insertLineToCard(line, lineNumber);
			}
		}
	}
	
	private static void insertLineToCard(String line, int lineNumber) { // Transforme les lignes en cartes et les ajoute dans le Deck
		Objects.requireNonNull(line);
		String[] parsedLine = line.split(" ");
		String cardType = parsedLine[0];
		switch (cardType) {
			case "StarterCard":
				Globals.playableDeck.addStarterCard(lineToStarterCard(parsedLine, lineNumber));
				break;
			case "ResourceCard":
				Globals.playableDeck.addResourceCard(lineToResourceCard(parsedLine, lineNumber));
				break;
			case "GoldCard":
				Globals.playableDeck.addGoldCard(lineToGoldCard(parsedLine, lineNumber));
				break;
			case "Objective":
				Globals.objectiveDeck.addObjectiveCard(lineToObjective(parsedLine, lineNumber));
				break;
		}
		
	}
	
	// FACTORY DES CARTES ------------------------------------------------------------------------------------------------------------------------
	
	private static StarterCard lineToStarterCard(String[] parsedLine, int lineNumber) {
		Objects.requireNonNull(parsedLine);
		ArrayList<Corner> frontCornersList = new ArrayList<Corner>(List.of(Corner.convertToCorner(parsedLine[2]), Corner.convertToCorner(parsedLine[3]), Corner.convertToCorner(parsedLine[4]), Corner.convertToCorner(parsedLine[5])));
		ArrayList<Corner> backCornersList = new ArrayList<Corner>(List.of(Corner.convertToCorner(parsedLine[7]), Corner.convertToCorner(parsedLine[8]), Corner.convertToCorner(parsedLine[9]), Corner.convertToCorner(parsedLine[10])));
		ArrayList<Corner> permanentResourcesList = new ArrayList<Corner>();
		for (int i = 12; i < parsedLine.length; i++) {
			permanentResourcesList.add(Corner.convertToCorner(parsedLine[i]));
		}
		StarterCard starterCard = new StarterCard(frontCornersList, backCornersList, permanentResourcesList, "card"+lineNumber+".png", "card"+lineNumber+"_back.png"); // AJOUTER IMAGE BACK
		return starterCard;
	}
	
	private static ResourceCard lineToResourceCard(String[] parsedLine, int lineNumber) {
		Objects.requireNonNull(parsedLine);
		ArrayList<Corner> frontCornersList = new ArrayList<Corner>(List.of(Corner.convertToCorner(parsedLine[2]), Corner.convertToCorner(parsedLine[3]), Corner.convertToCorner(parsedLine[4]), Corner.convertToCorner(parsedLine[5])));
		int point = 0;
		if (parsedLine[parsedLine.length - 1].equals("D:1")) {
			point = 1;
		}
		ResourceCard resourceCard = new ResourceCard(frontCornersList, Corner.convertToCorner(parsedLine[7]), "card"+lineNumber+".png", point);
		return resourceCard;
	}
	
	private static GoldCard lineToGoldCard(String[] parsedLine, int lineNumber) {
		Objects.requireNonNull(parsedLine);
		ArrayList<Corner> frontCornersList = new ArrayList<Corner>(List.of(Corner.convertToCorner(parsedLine[2]), Corner.convertToCorner(parsedLine[3]), Corner.convertToCorner(parsedLine[4]), Corner.convertToCorner(parsedLine[5])));
		HashMap<Corner, Integer> resourcesCost = new HashMap<Corner, Integer>();
		int i = 9;
		while (!parsedLine[i].equals("Scoring")) {
			String[] cost = parsedLine[i].split(":");
			resourcesCost.put(Corner.convertToCorner(cost[0]), Integer.valueOf(cost[1]));
			i++;
		}
		String[] pointsCondition = parsedLine[parsedLine.length - 1].split(":");
		return new GoldCard(frontCornersList, Corner.convertToCorner(parsedLine[7]), pointsCondition[0].charAt(0), Integer.valueOf(pointsCondition[1]).intValue(), resourcesCost, "card"+lineNumber+".png");
	}

	private static Objective lineToObjective(String[] parsedLine, int lineNumber) {
		Objects.requireNonNull(parsedLine);
		if (parsedLine[1].equals("Pattern")) {
			return lineToObjectivePattern(parsedLine, lineNumber);
		} else {
			return lineToObjectiveRA(parsedLine, lineNumber);
		}
	}
	
	private static ObjectivePattern lineToObjectivePattern(String[] parsedLine, int lineNumber) {
		Objects.requireNonNull(parsedLine);
		return new ObjectivePattern(parsedLine[1], lineToCondition(parsedLine), Integer.valueOf(parsedLine[8].split(":")[1]), "card"+lineNumber+".png");
	}
	
	private static LinkedHashMap<Point, Corner> lineToCondition(String[] parsedLine) {
		Objects.requireNonNull(parsedLine);
		Point point = new Point(0, 0);
		LinkedHashMap<Point, Corner> conditions = new LinkedHashMap<Point, Corner>();
		Point currentPoint = new Point(0, 0);
		for (int i = 2; i < 7; i++) {
			if (i % 2 == 0) {
				conditions.put(point, Corner.convertToCorner(parsedLine[i]));
			} else {
				point = Globals.positionToPoint.get(parsedLine[i]).sum(currentPoint);
				currentPoint = point;
			}
		}
		return conditions;
	}
	
	private static ObjectiveRA lineToObjectiveRA(String[] parsedLine, int lineNumber) {
		Objects.requireNonNull(parsedLine);
		int i = 2;
		while (!parsedLine[i].equals("Scoring")) {
			i++;
		}
		return new ObjectiveRA(parsedLine[1], Corner.convertToCorner(parsedLine[2]), i-2, Integer.valueOf(parsedLine[parsedLine.length - 1].split(":")[1]), "card"+lineNumber+".png");
	}
	
	// PLAYER -----------------------------------------------------------------------------------------------------------------------------------------
	
	public static PlayableCard pickRandomCard(String cardType) {
		Objects.requireNonNull(cardType);
		int length = Globals.playableDeck.get(cardType).size();
		int rand = RandInt.randInt(0, length);
		
		if (length <= 0) {
			return null;
		}
		PlayableCard pickedCard = Globals.playableDeck.get(cardType).get(rand);
		
		return pickedCard;
	}
	
	public static Card pickRandomtObjectiveCard() {
		int length = Globals.objectiveDeck.get("objective").size();
		int rand = RandInt.randInt(0, length);
		
		Card pickedCard = Globals.objectiveDeck.get("objective").get(rand);
		
		return pickedCard;
	}
	
	
	public static StarterCard pickStartingCard() {
		StarterCard pickedCard = (StarterCard) pickRandomCard("starterCard");
		Globals.playableDeck.removeCard("starterCard", pickedCard);
		return pickedCard;
	}
	
	public static List<PlayableCard> generateStartingCards() {
		List<PlayableCard> startingCards = new ArrayList<PlayableCard>();
		
		PlayableCard card1 = Card.pickRandomCard("resourceCard");
		PlayableCard card2 = Card.pickRandomCard("resourceCard");
		PlayableCard card3 = Card.pickRandomCard("goldCard");
		
		startingCards.add(card1);
		startingCards.add(card2);
		startingCards.add(card3);
		
		Globals.playableDeck.removeCard("resourceCard", card1);
		Globals.playableDeck.removeCard("resourceCard", card2);
		Globals.playableDeck.removeCard("goldCard", card3);
		
		return startingCards;
	}
}
