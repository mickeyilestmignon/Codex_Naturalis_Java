package sae.view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.umlv.zen5.ApplicationContext;
import sae.card.PlayableCard;
import sae.card.Point;
import sae.card.StarterCard;
import sae.controller.Globals;
import sae.enums.Corner;
import sae.player.Player;
import sae.util.ImageLoader;

public class PlayerBoardView {
	
	public static List<ClickableImage> chooseStarterCard(ApplicationContext context, StarterCard starterCard) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(starterCard);
		var imagesStarter = new ImageLoader("include/images", starterCard.frontImage(), starterCard.frontImage(), starterCard.backImage());
		
		var showStarterFront = new ClickableImage(context, imagesStarter.image(0), (float) (Globals.screenWidth*0.425 - imagesStarter.image(0).getWidth()/2 + Globals.xOffset), (float) (Globals.screenHeight*0.5 - imagesStarter.image(0).getHeight()/2 + Globals.yOffset));
		var showStarterBack = new ClickableImage(context, imagesStarter.image(1), (float) (Globals.screenWidth*0.575 - imagesStarter.image(1).getWidth()/2 + Globals.xOffset), (float) (Globals.screenHeight*0.5 - imagesStarter.image(1).getHeight()/2 + Globals.yOffset));
		
		return List.of(showStarterFront, showStarterBack);
	}
	
	public static void handBgCornerNumber(ApplicationContext context, Player player) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(player);
		// AFFICHAGE FOND DE LA MAIN
		ImageLoader fondLoader = new ImageLoader("include/bg", "handbg.png", "handbg.png");
		
		// NOMBRE RESSOURCES ET ARTEFACTS DU JOUEUR
		Graphics g = fondLoader.image(0).getGraphics();
		g.setFont(Globals.font);
		g.setColor(Color.decode("#BBA44F"));
		
		g.drawString(String.valueOf(player.getCornerQuantity(Corner.ANIMAL)), (int) (fondLoader.image(0).getWidth()*0.591), (int) (fondLoader.image(0).getHeight()*0.43));
		g.drawString(String.valueOf(player.getCornerQuantity(Corner.PLANT)), (int) (fondLoader.image(0).getWidth()*0.652), (int) (fondLoader.image(0).getHeight()*0.43));
		g.drawString(String.valueOf(player.getCornerQuantity(Corner.FUNGI)), (int) (fondLoader.image(0).getWidth()*0.712), (int) (fondLoader.image(0).getHeight()*0.43));
		g.drawString(String.valueOf(player.getCornerQuantity(Corner.INSECT)), (int) (fondLoader.image(0).getWidth()*0.777), (int) (fondLoader.image(0).getHeight()*0.43));
		
		g.drawString(String.valueOf(player.getCornerQuantity(Corner.QUILL)), (int) (fondLoader.image(0).getWidth()*0.591), (int) (fondLoader.image(0).getHeight()*0.705));
		g.drawString(String.valueOf(player.getCornerQuantity(Corner.MANUSCRIPT)), (int) (fondLoader.image(0).getWidth()*0.648), (int) (fondLoader.image(0).getHeight()*0.705));
		g.drawString(String.valueOf(player.getCornerQuantity(Corner.INKWELL)), (int) (fondLoader.image(0).getWidth()*0.705), (int) (fondLoader.image(0).getHeight()*0.705));
		
		new ClickableImage(context, fondLoader.image(0), (float) (Globals.screenWidth*0.511) - fondLoader.image(0).getWidth()*Globals.sizeCoefWidth/2, (float) (Globals.screenHeight*0.908) - fondLoader.image(0).getHeight()*Globals.sizeCoefHeight/2);
	}

	public static ArrayList<ClickableImage> getPlayerHand(ApplicationContext context, Player player, int currentPlayer, int pageNumber) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(player);
		
		boolean showSecret;
		if (currentPlayer == pageNumber) {
			showSecret = true;
		}
		else {
			showSecret = false;
		}
		
		handBgCornerNumber(context, player);
		
		ImageLoader playerCards;
		ImageLoader objective;
		ArrayList<ClickableImage> playerHand = new ArrayList<ClickableImage>();
		
		if (showSecret) {
			// AFFICHAGE OBJECTIF
			objective = new ImageLoader("include/images", player.getObjective().image(), player.getObjective().image());
			new ClickableImage(context, objective.image(0), (float) (Globals.screenWidth*0.8775) - objective.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.9175)) - objective.image(0).getHeight()/2);
			// AFFICHAGE CARTES
			for (int i = 0; i < player.getHand().size(); i++) {
				if (player.getHand().get(i).side()) {
					playerCards = new ImageLoader("include/images", player.getHand().get(i).image(), player.getHand().get(i).image());
				}
				else {
					playerCards = new ImageLoader("include/images", player.getHand().get(i).retrieveBackImage(), player.getHand().get(i).retrieveBackImage());
				}
				playerHand.add(new ClickableImage(context, playerCards.image(0), (float) (Globals.screenWidth*(0.157 + 0.164*i)) - playerCards.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.9175)) - playerCards.image(0).getHeight()/2));
			}
		}
		else {
			// AFFICHAGE OBJECTIF
			objective = new ImageLoader("include/images", player.getObjective().retrieveBackImage(), player.getObjective().retrieveBackImage());
			new ClickableImage(context, objective.image(0), (float) (Globals.screenWidth*0.825) - objective.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.9175)) - objective.image(0).getHeight()/2);
			// AFFICHAGE CARTES
			for (int i = 0; i < player.getHand().size(); i++) {
				playerCards = new ImageLoader("include/images", player.getHand().get(i).retrieveBackImage(), player.getHand().get(i).retrieveBackImage());
				playerHand.add(new ClickableImage(context, playerCards.image(0), (float) (Globals.screenWidth*(0.157 + 0.164*i)) - playerCards.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.9175)) - playerCards.image(0).getHeight()/2));
			}
		}
		
		return playerHand;
		
	}
	
	public static void showPlayerBoard(ApplicationContext context, Player player) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(player);
		for (Map.Entry<Point, PlayableCard> card : player.cardsPlaced().entrySet()) {
			ImageLoader cardImage;
			if (card.getValue().side()) {
				cardImage = new ImageLoader("include/images", card.getValue().image(), card.getValue().image());
			}
			else {
				cardImage = new ImageLoader("include/images", card.getValue().retrieveBackImage(), card.getValue().retrieveBackImage());
			}
			new ClickableImage(context, cardImage.image(0), (float) (Globals.screenWidth*0.5 + (card.getKey().i() * Globals.xDecal) - cardImage.image(0).getWidth()/2 + Globals.xOffset), (float) (Globals.screenHeight*0.5 + (card.getKey().j() * Globals.yDecal) - cardImage.image(0).getHeight()/2 + Globals.yOffset));
		}
	}
	
	public static HashMap<ClickableImage, Point> showUsablePoints(ApplicationContext context, List<Point> usablePoints) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(usablePoints);
		HashMap<ClickableImage, Point> imageToPoint = new HashMap<ClickableImage, Point>();
		for (Point point : usablePoints) {
			imageToPoint.put(new ClickableImage(context, Globals.buttons.image(19), (float) (Globals.screenWidth*0.5 + (point.i() * Globals.xDecal) - Globals.buttons.image(19).getWidth()/2 + Globals.xOffset), (float) (Globals.screenHeight*0.5 + (point.j() * Globals.yDecal) - Globals.buttons.image(19).getHeight()/2 + Globals.yOffset)), point);
		}
		return imageToPoint;
	}
}
