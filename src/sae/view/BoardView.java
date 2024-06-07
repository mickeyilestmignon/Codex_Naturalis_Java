package sae.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.KeyboardKey;
import fr.umlv.zen5.Event.Action;
import sae.card.Card;
import sae.card.Objective;
import sae.card.PlayableCard;
import sae.card.StarterCard;
import sae.controller.Game;
import sae.controller.Globals;
import sae.player.Player;
import sae.util.ImageLoader;

public class BoardView {
	
	public static void clearZone(ApplicationContext context, float x, float y, float width, float height) {
		Objects.requireNonNull(context);
		context.renderFrame(graphics -> {
			graphics.setColor(Globals.backgroundColor);
			graphics.fill(new Rectangle2D.Float(x, y, width, height));
		});
	}

	public static void clear(ApplicationContext context) {
		Objects.requireNonNull(context);
		context.renderFrame(graphics -> {
			graphics.setColor(Globals.backgroundColor);
			graphics.fill(new Rectangle2D.Float(0, 0, Globals.screenWidth, Globals.screenHeight));
		});
	}
	
	public static void textZonePlayerName(ApplicationContext context, int playerNumber) {
		Objects.requireNonNull(context);
		new ClickableImage(context, Globals.texts.image(playerNumber), (float) (Globals.screenWidth/2 - Globals.texts.image(playerNumber).getWidth()/2), Globals.screenHeight/3 - Globals.texts.image(playerNumber).getHeight()/2);
	}
	
	public static void showStartingCards(ApplicationContext context, List<PlayableCard> cardList) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(cardList);
		var cards = new ImageLoader("include/images", cardList.get(0).image(), cardList.get(0).image(), cardList.get(1).image(), cardList.get(2).image());
		for (int i = 0; i < cardList.size(); i++) {
			new ClickableImage(context, cards.image(i), (float) (Globals.screenWidth*(0.35 + 0.15*i)) - cards.image(i).getWidth()/2, (float) (Globals.screenHeight*0.3) - cards.image(i).getHeight()/2);
		}
	}
	
	public static void showStarterCard(ApplicationContext context, StarterCard starterCard) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(starterCard);
		var cards = new ImageLoader("include/images", starterCard.image(), starterCard.frontImage(), starterCard.backImage());
		new ClickableImage(context, cards.image(0), (float) (Globals.screenWidth*0.425) - cards.image(0).getWidth()/2, (float) (Globals.screenHeight*0.5) - cards.image(0).getHeight()/2);
		new ClickableImage(context, cards.image(1), (float) (Globals.screenWidth*0.575) - cards.image(1).getWidth()/2, (float) (Globals.screenHeight*0.5) - cards.image(1).getHeight()/2);
	}
	
	public static Objective showObjectiveCardChoice(ApplicationContext context, Objective obj1, Objective obj2) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(obj1);
		Objects.requireNonNull(obj2);
		var imageObj1 = new ImageLoader("include/images", obj1.image(), obj1.image()).image(0);
		var imageObj2 = new ImageLoader("include/images", obj2.image(), obj2.image()).image(0);
		
		var showObj1 = new ClickableImage(context, imageObj1, (float) (Globals.screenWidth*0.425 - imageObj1.getWidth()/2), (float) (Globals.screenHeight*0.8 - imageObj1.getHeight()/2));
		var showObj2 = new ClickableImage(context, imageObj2, (float) (Globals.screenWidth*0.575 - imageObj2.getWidth()/2), (float) (Globals.screenHeight*0.8 - imageObj2.getHeight()/2));
		
		while (true) {
			
			var event = context.pollOrWaitEvent(10);
			if (event == null) {continue;}
			var action = event.getAction();
			
			if (action == Action.POINTER_DOWN) {
				var location = event.getLocation();
				float x = (float) location.getX();
				float y = (float) location.getY();
				
				if (showObj1.isClicked(x, y)) {
					return obj1;
				}
				else if (showObj2.isClicked(x, y)) {
					return obj2;
				}
			}
			
			if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.Q) {
				System.out.println("Vous avez quitté le jeu.");
				context.exit(0);
			}
		}
	}
	
	public static List<Player> preMainPage(ApplicationContext context, int numberPlayers, int player, ArrayList<String> nameList, List<Player> playerList) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(nameList);
		Objects.requireNonNull(playerList);
		if (player >= numberPlayers) {
			return playerList;
		}
		var playerClickableImage = new ClickableImage(context, Globals.buttons.image(player+10), Globals.screenWidth/2 - Globals.buttons.image(player+10).getWidth()/2, (float) (Globals.screenHeight*(0.35 + 0.1*player)) - Globals.buttons.image(player+10).getHeight()/2);
		for (int i = 0; i < numberPlayers; i++) {
			if (i != player) {
				new ClickableImage(context, Globals.texts.image(i+7), Globals.screenWidth/2 - Globals.texts.image(i+7).getWidth()/2, (float) (Globals.screenHeight*(0.35 + 0.1*i)) - Globals.texts.image(i+7).getHeight()/2);
			}
		}
		
		while (true) {
			
			var event = context.pollOrWaitEvent(10);
			if (event == null) {continue;}
			var action = event.getAction();
			
			if (action == Action.POINTER_DOWN) {
				var location = event.getLocation();
				float x = (float) location.getX();
				float y = (float) location.getY();
				
				if (playerClickableImage.isClicked(x, y)) {
					playerList.add(selectObjectiveAndStarter(context, numberPlayers, player, nameList));
					clear(context);
					return preMainPage(context, numberPlayers, player+1, nameList, playerList);
				}
			}
			
			if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.Q) {
				System.out.println("Vous avez quitté le jeu.");
				context.exit(0);
			}
		}
	}
	
	public static Player selectObjectiveAndStarter(ApplicationContext context, int numberPlayers, int player, ArrayList<String> nameList) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(nameList);
		
		String pseudo = nameList.get(player);
		
		clear(context);
		
		var voici_main = new ImageLoader("include/texts", "voici_main.png", "voici_main.png");
		new ClickableImage(context, voici_main.image(0), (float) (Globals.screenWidth/2) - voici_main.image(0).getWidth()/2, (float) (Globals.screenHeight*0.15) - voici_main.image(0).getHeight()/2);
		
		var hand = Card.generateStartingCards(); // Starters
		showStartingCards(context, hand);
		
		var choisir_obj = new ImageLoader("include/texts", "choisir_obj.png", "choisir_obj.png");
		new ClickableImage(context, choisir_obj.image(0), (float) (Globals.screenWidth/2) - choisir_obj.image(0).getWidth()/2, (float) (Globals.screenHeight*0.65) - choisir_obj.image(0).getHeight()/2);
		StarterCard starterCard = Card.pickStartingCard();
		showStarterCard(context, starterCard);
		
		var objectiveChoosen = BoardView.chooseObjectiveFromTwo(context);
		
		return new Player(pseudo, hand, starterCard, objectiveChoosen);
	}
	
	public static void showCurrentPlayerInfo(ApplicationContext context, Game game, int currentPlayer) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(game);
		ImageLoader text = new ImageLoader("include/texts", "JC1.png", "JC1.png", "JC2.png", "JC3.png", "JC4.png");
		Graphics g = text.image(currentPlayer-1).getGraphics();
		g.setFont(Globals.font);
		g.setColor(Color.decode("#BBA44F"));
		
		g.drawString(game.players().get(currentPlayer-1).pseudo(), 220, 120); // PSEUDO
		g.drawString(String.valueOf(game.players().get(currentPlayer-1).points()), 200, 185); // POINTS
		
		new ClickableImage(context, text.image(currentPlayer-1), (float) (Globals.screenWidth*0.825) -text.image(currentPlayer-1).getWidth()/2, (float) (Globals.screenHeight*(0.085)) - text.image(currentPlayer-1).getHeight()/2);
	}
	
	public static ArrayList<ClickableImage> showPlayersTab(ApplicationContext context, Game game, int pageNumber, int currentPlayer) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(game);
		
		// JOUEUR COURANT
		showCurrentPlayerInfo(context, game, currentPlayer);
		
		ArrayList<ClickableImage> playersTab = new ArrayList<ClickableImage>();
		for (int i = 1; i < game.players().size()+1; i++) {
			if (i != pageNumber+1) {
				playersTab.add(new ClickableImage(context, Globals.buttons.image(i), (float) (Globals.screenWidth*(0.05 + 0.075*i)) - Globals.buttons.image(i).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.buttons.image(i).getHeight()/2));
			}
			else {
				playersTab.add(new ClickableImage(context, Globals.buttons.image(i+5), (float) (Globals.screenWidth*(0.05 + 0.075*i)) - Globals.buttons.image(i+5).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.buttons.image(i+5).getHeight()/2));
			}
		}
		// new ClickableImage(context, Globals.elements.image(0), (float) (Globals.screenWidth*0.15) -Globals.elements.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.55)) - Globals.elements.image(0).getHeight()/2);
		// new ClickableImage(context, Globals.texts.image(11), (float) (Globals.screenWidth*0.640) - Globals.texts.image(11).getWidth()/2, (float) (Globals.screenHeight*(0.175)) - Globals.texts.image(11).getHeight()/2);
		return playersTab;
	}
	
	public static void showGameTrail(ApplicationContext context, Game game) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(game);
		ImageLoader map = new ImageLoader("include/images", "map.png", "map.png");
		
		// ROUGE JAUNE BLEU VERT
		for (int i = 0; i < game.players().size(); i++) {
			Graphics g = map.image(0).getGraphics();
			g.setFont(Globals.fontTrial);
			
			if (i == 0) {
				g.setColor(Color.decode("#BB271A"));
			}
			else if (i == 1) {
				g.setColor(Color.decode("#B89130"));
			}
			else if (i == 2) {
				g.setColor(Color.decode("#4979D1"));
			}
			else {
				g.setColor(Color.decode("#78A55A"));
			}
			
			int points = game.players().get(i).points();
			String round = "";
			if (points > 29) {
				round = "+";
			}
			g.drawString("J"+(i+1)+round, 68 + (i == 1 || i == 3 ? 35 : 0) + ((points%30)%4)*116, 80 + (i == 2 || i == 3 ? 35 : 0) + ((int)(points%30)/4)*106);
		}
		
		new ClickableImage(context, map.image(0), (float) (Globals.screenWidth*0.1625) -map.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.575)) - map.image(0).getHeight()/2);
	}

	public static Objective chooseObjectiveFromTwo(ApplicationContext context) {
		Objects.requireNonNull(context);
		Objective obj1 = (Objective) Card.pickRandomtObjectiveCard();
		Objective obj2 = (Objective) Card.pickRandomtObjectiveCard();
		while (obj1.equals(obj2)) {
			obj2 = (Objective) Card.pickRandomtObjectiveCard();
		}
		
		Objective choosenObj = showObjectiveCardChoice(context, obj1, obj2);
		
		Globals.objectiveDeck.removeCard(choosenObj);
		return choosenObj;
	}
	
	public static void gameStatus(ApplicationContext context, Game game) {
		if (game.lastTurn()) {
			new ClickableImage(context, Globals.texts.image(28), (float) (Globals.screenWidth*0.5) -Globals.texts.image(28).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.texts.image(28).getHeight()/2);
		}
		else if (game.playerFinished()) {
			new ClickableImage(context, Globals.texts.image(27), (float) (Globals.screenWidth*0.5) -Globals.texts.image(27).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.texts.image(27).getHeight()/2);
		}
		
	}
}
