package sae.controller;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import fr.umlv.zen5.*;
import fr.umlv.zen5.Event.Action;
import sae.card.Card;
import sae.card.PlayableCard;
import sae.card.Point;
import sae.player.Player;
import sae.util.ImageLoader;
import sae.view.BoardView;
import sae.view.ClickableImage;
import sae.view.PlayerBoardView;

public class gameController {
	
	public gameController() {
	}
	
	public static void playerPage(ApplicationContext context, Game game, int currentPlayer, int pageNumber, boolean cardPlaced, boolean cardPicked) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(game);
		
		BoardView.clear(context);
		
		// SI UN JOUEUR A 20 POINTS OU SI C'EST LE DERNIER TOUR
		BoardView.gameStatus(context, game);
		
		// SI C'EST SA PAGE ET QU'AUCUNE CARTE N'EST PLACEE, CHOISIR LE STARTER
		boolean starterToChoose = false;
		List<ClickableImage> starterImagesList = null;
		if (currentPlayer-1 == pageNumber && !game.players().get(currentPlayer-1).oneCardPlaced()) {
			starterImagesList = PlayerBoardView.chooseStarterCard(context, game.players().get(currentPlayer-1).starterCard());
			starterToChoose = true;
		}
		// SINON ON AFFICHE LES CARTES
		if (!starterToChoose) {
			PlayerBoardView.showPlayerBoard(context, game.players().get(pageNumber));
		}
		
		// MOVE BUTTONS
		var gauche = new ClickableImage(context, Globals.buttons.image(14), (float) (Globals.screenWidth*0.02) - Globals.buttons.image(14).getWidth()/2, (float) (Globals.screenHeight*(0.5)) - Globals.buttons.image(14).getHeight()/2);
		var droite = new ClickableImage(context, Globals.buttons.image(15), (float) (Globals.screenWidth*0.985) - Globals.buttons.image(15).getWidth()/2, (float) (Globals.screenHeight*(0.5)) - Globals.buttons.image(15).getHeight()/2);
		var bas = new ClickableImage(context, Globals.buttons.image(16), (float) (Globals.screenWidth*0.5) - Globals.buttons.image(15).getWidth()/2, (float) (Globals.screenHeight*(0.775)) - Globals.buttons.image(14).getHeight()/2);
		var haut = new ClickableImage(context, Globals.buttons.image(17), (float) (Globals.screenWidth*0.5) - Globals.buttons.image(15).getWidth()/2, (float) (Globals.screenHeight*(0.15)) - Globals.buttons.image(14).getHeight()/2);
		
		// MAP
		ClickableImage map;
		if (pageNumber == -1) {
			map = new ClickableImage(context, Globals.buttons.image(5), (float) (Globals.screenWidth*0.05) - Globals.buttons.image(5).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.buttons.image(5).getHeight()/2);
		}
		else {
			map = new ClickableImage(context, Globals.buttons.image(0), (float) (Globals.screenWidth*0.05) - Globals.buttons.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.buttons.image(0).getHeight()/2);
		}
		
		// AFFICHAGE PLAYERS TAB
		List<ClickableImage> playersTab = BoardView.showPlayersTab(context, game, pageNumber, currentPlayer);
		
		// AFFICHAGE MAIN
		ArrayList<ClickableImage> playerHand = PlayerBoardView.getPlayerHand(context, game.players().get(pageNumber), currentPlayer-1, pageNumber);
		
		while (true) {
			
			var event = context.pollOrWaitEvent(10);
			if (event == null) {continue;}
			var action = event.getAction();
			
			if (action == Action.POINTER_DOWN) {
				var location = event.getLocation();
				float x = (float) location.getX();
				float y = (float) location.getY();
				
				// CHANGER DE PAGE JOUEUR
				for (int i = 0; i < game.players().size(); i++) {
					if (playersTab.get(i).isClicked(x, y)) {
						playerPage(context, game, currentPlayer, i, cardPlaced, cardPicked);
					}
				}
				
				// REVENIR A LA MAIN PAGE
				if (map.isClicked(x, y)) {
					mainPage(context, game, currentPlayer, pageNumber, cardPlaced, cardPicked);
				}
				
				// DEPLACER LA ZONE DE JEU
				if (gauche.isClicked(x, y)) {
					Globals.xOffset += 100;
					playerPage(context, game, currentPlayer, pageNumber, cardPlaced, cardPicked);
				}
				else if (droite.isClicked(x, y)) {
					Globals.xOffset -= 100;
					playerPage(context, game, currentPlayer, pageNumber, cardPlaced, cardPicked);
				}
				else if (bas.isClicked(x, y)) {
					Globals.yOffset -= 100;
					playerPage(context, game, currentPlayer, pageNumber, cardPlaced, cardPicked);
				}
				else if (haut.isClicked(x, y)) {
					Globals.yOffset += 100;
					playerPage(context, game, currentPlayer, pageNumber, cardPlaced, cardPicked);
				}
				
				// CHOIX DU STARTER
				if (!(starterImagesList == null)) {
					if (starterToChoose) {
						if (starterImagesList.get(0).isClicked(x, y)) {
							game.players().get(currentPlayer-1).placeCard(new Point(0, 0), game.players().get(currentPlayer-1).starterCard());
							playerPage(context, game, currentPlayer, pageNumber, true, true); // POUR DECLENCHER LE PROCHAIN JOUEUR
						}
						else if (starterImagesList.get(1).isClicked(x, y)) {
							game.players().get(currentPlayer-1).starterCard().flip();
							game.players().get(currentPlayer-1).placeCard(new Point(0, 0), game.players().get(currentPlayer-1).starterCard());
							playerPage(context, game, currentPlayer, pageNumber, true, true);
						}
					}
				}
				
				// PLACER CARTE
				if (!cardPlaced && currentPlayer-1 == pageNumber && !starterToChoose) {
					for (int i = 0; i < playerHand.size(); i++) {
						if (playerHand.get(i).isClicked(x, y)) {
							
							// AFFICHAGE GRISE OU ON PEUT PLACER
							List<Point> usablePoints = game.players().get(currentPlayer-1).whereToPlace(game.players().get(currentPlayer-1).getHand().get(i));
							HashMap<ClickableImage, Point> imageToPoint = PlayerBoardView.showUsablePoints(context, usablePoints);
							
							// BOUTON FLIP CARTE
							var flipButton = new ClickableImage(context, Globals.buttons.image(20), (float) (Globals.screenWidth*0.505 - Globals.buttons.image(20).getWidth()/2), (float) (Globals.screenHeight*0.68 - Globals.buttons.image(20).getHeight()/2));
							
							while (true) {
								
								var event2 = context.pollOrWaitEvent(10);
								if (event2 == null) {continue;}
								var action2 = event2.getAction();
								
								if (action2 == Action.POINTER_DOWN) {
									var location2 = event2.getLocation();
									float x2 = (float) location2.getX();
									float y2 = (float) location2.getY();
									
									// ATTENTE DU CLICK
									for (ClickableImage image : imageToPoint.keySet()) {
										if (image.isClicked(x2, y2)) {
											game.players().get(currentPlayer-1).placeCard(imageToPoint.get(image), game.players().get(currentPlayer-1).getHand().get(i)); // LA PLACER SUR LA BOARD
											int numberCornersCovered = game.players().get(currentPlayer-1).removeCornersCovered(imageToPoint.get(image)); // ENLEVER LES CORNERS RECOUVERTS
											game.players().get(currentPlayer-1).addPoints(game.players().get(currentPlayer-1).getHand().get(i), numberCornersCovered); // AJOUTER LES POINTS
											game.players().get(currentPlayer-1).removeCardFromHand(game.players().get(currentPlayer-1).getHand().get(i)); // LA RETIRER DE LA MAIN
											boolean emptyPiles = game.resourcePile().isEmpty() && game.goldPile().isEmpty();
											playerPage(context, game, currentPlayer, pageNumber, true, emptyPiles);
										}
									}
									
									if (flipButton.isClicked(x2, y2)) {
										game.players().get(currentPlayer-1).getHand().get(i).flip();
									}
									
									playerPage(context, game, currentPlayer, pageNumber, false, false);
								}
							}
						}
					}
				}
			}
			
			if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.Q) {
				System.out.println("Vous avez quitté le jeu.");
				context.exit(0);
				return;
			}
			if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.P) {
				addObjectivePointsPage(context, game);
			}
		}
	}
	
	public static void mainPage(ApplicationContext context, Game game, int currentPlayer, int shownBoard, boolean cardPlaced, boolean cardPicked) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(game);
		
		BoardView.clear(context);
		
		// SI UN JOUEUR A 20 POINTS OU SI C'EST LE DERNIER TOUR
		BoardView.gameStatus(context, game);
		
		// CYCLE DU JOUEUR, SI LA CARTE A ETE PLACEE ET A PIOCHE
		ClickableImage nextPlayer = null;
		if (cardPlaced && cardPicked) {
			nextPlayer = new ClickableImage(context, Globals.buttons.image(18), (float) (Globals.screenWidth*0.8) -Globals.buttons.image(18).getWidth()/2, (float) (Globals.screenHeight*(0.95)) - Globals.buttons.image(18).getHeight()/2);
		}
		
		// JOUEUR COURANT
		BoardView.showCurrentPlayerInfo(context, game, currentPlayer);
		
		// PISTE DE JEU
		BoardView.showGameTrail(context, game);
		
		new ClickableImage(context, Globals.buttons.image(5), (float) (Globals.screenWidth*0.05) - Globals.buttons.image(5).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.buttons.image(5).getHeight()/2);
		// PLAYERS TAB
		ArrayList<ClickableImage> playersTab = new ArrayList<ClickableImage>();
		for (int i = 1; i < game.players().size()+1; i++) {
			playersTab.add(new ClickableImage(context, Globals.buttons.image(i), (float) (Globals.screenWidth*(0.05 + 0.075*i)) - Globals.buttons.image(i).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.buttons.image(i).getHeight()/2));
		}
		
		// OBJECTIFS COMMUNS
		new ClickableImage(context, Globals.texts.image(11), (float) (Globals.screenWidth*0.640) - Globals.texts.image(11).getWidth()/2, (float) (Globals.screenHeight*(0.175)) - Globals.texts.image(11).getHeight()/2);
		var objCommun = new ImageLoader("include/images", game.commonObjective1().image(), game.commonObjective1().image(), game.commonObjective2().image());
		new ClickableImage(context, objCommun.image(0), (float) (Globals.screenWidth*0.715) - objCommun.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.325)) - objCommun.image(0).getHeight()/2);
		new ClickableImage(context, objCommun.image(1), (float) (Globals.screenWidth*0.565) - objCommun.image(1).getWidth()/2, (float) (Globals.screenHeight*(0.325)) - objCommun.image(1).getHeight()/2);
		
		// RESSOURCE
		new ClickableImage(context, Globals.texts.image(12), (float) (Globals.screenWidth*0.465) - Globals.texts.image(12).getWidth()/2, (float) (Globals.screenHeight*(0.45)) - Globals.texts.image(12).getHeight()/2);
		
		PlayableCard resourcePile1 = game.resourcePile().revealedCard1();
		ClickableImage resourcePile1ClickableImage = null;
		if (resourcePile1 != null) {
			ImageLoader resourcePile1Image = new ImageLoader("include/images", resourcePile1.image(), resourcePile1.image());
			resourcePile1ClickableImage = new ClickableImage(context, resourcePile1Image.image(0), (float) (Globals.screenWidth*0.390) -resourcePile1Image.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.6)) - resourcePile1Image.image(0).getHeight()/2);
		}
		
		PlayableCard resourcePile2 = game.resourcePile().revealedCard2();
		ClickableImage resourcePile2ClickableImage = null;
		if (resourcePile2 != null) {
			ImageLoader resourcePile2Image = new ImageLoader("include/images", resourcePile2.image(), resourcePile2.image());
			resourcePile2ClickableImage = new ClickableImage(context, resourcePile2Image.image(0), (float) (Globals.screenWidth*0.540) -resourcePile2Image.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.6)) - resourcePile2Image.image(0).getHeight()/2);
		}
		
		PlayableCard resourcePileHidden = game.resourcePile().hiddenCard();
		ClickableImage resourcePileHiddenClickableImage = null;
		if (resourcePileHidden != null) {
			ImageLoader resourcePileHiddenImage = new ImageLoader("include/images", resourcePileHidden.retrieveBackImage(), resourcePileHidden.retrieveBackImage());
			resourcePileHiddenClickableImage = new ClickableImage(context, resourcePileHiddenImage.image(0), (float) (Globals.screenWidth*0.465) -resourcePileHiddenImage.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.8)) - resourcePileHiddenImage.image(0).getHeight()/2);
		}
		
		// GOLD
		new ClickableImage(context, Globals.texts.image(13), (float) (Globals.screenWidth*0.815) - Globals.texts.image(13).getWidth()/2, (float) (Globals.screenHeight*(0.45)) - Globals.texts.image(13).getHeight()/2);
		
		PlayableCard goldPile1 = game.goldPile().revealedCard1();
		ClickableImage goldPile1ClickableImage = null;
		if (goldPile1 != null) {
			ImageLoader goldPile1Image = new ImageLoader("include/images", goldPile1.image(), goldPile1.image());
			goldPile1ClickableImage = new ClickableImage(context, goldPile1Image.image(0), (float) (Globals.screenWidth*0.740) -goldPile1Image.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.6)) - goldPile1Image.image(0).getHeight()/2);
		}
		
		PlayableCard goldPile2 = game.goldPile().revealedCard2();
		ClickableImage goldPile2ClickableImage = null;
		if (goldPile2 != null) {
			ImageLoader goldPile2Image = new ImageLoader("include/images", goldPile2.image(), goldPile2.image());
			goldPile2ClickableImage = new ClickableImage(context, goldPile2Image.image(0), (float) (Globals.screenWidth*0.890) -goldPile2Image.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.6)) - goldPile2Image.image(0).getHeight()/2);
		}
		
		PlayableCard goldPileHidden = game.goldPile().hiddenCard();
		ClickableImage goldPileHiddenClickableImage = null;
		if (goldPileHidden != null) {
			ImageLoader goldPileHiddenImage = new ImageLoader("include/images", goldPileHidden.retrieveBackImage(), goldPileHidden.retrieveBackImage());
			goldPileHiddenClickableImage = new ClickableImage(context, goldPileHiddenImage.image(0), (float) (Globals.screenWidth*0.815) -goldPileHiddenImage.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.8)) - goldPileHiddenImage.image(0).getHeight()/2);
		}
		
		// TESTER SI TOUTES LES CARTES SONT NULL
		if (resourcePile1 == null && resourcePile2 == null && resourcePileHidden == null) {
			game.resourcePile().setIsEmpty();
		}
		if (goldPile1 == null && goldPile2 == null && goldPileHidden == null) {
			game.goldPile().setIsEmpty();
		}
		
		while (true) {
			
			var event = context.pollOrWaitEvent(10);
			if (event == null) {continue;}
			var action = event.getAction();
			
			if (action == Action.POINTER_DOWN) {
				var location = event.getLocation();
				float x = (float) location.getX();
				float y = (float) location.getY();
				
				// PLAYERS TAB
				for (int i = 0; i < game.players().size(); i++) {
					if (playersTab.get(i).isClicked(x, y)) {
						playerPage(context, game, currentPlayer, i, cardPlaced, cardPicked);
					}
				}
				
				// PIOCHE, N'AFFICHE RIEN SI LES PILES SONT VIDES
				if (cardPlaced && !cardPicked) {
					if (resourcePile1 != null) {
						if (resourcePile1ClickableImage.isClicked(x, y)) {
							game.players().get(currentPlayer-1).addCardToHand(game.resourcePile().revealedCard1());
							game.resourcePile().updateRevealedCard1();
							mainPage(context, game, currentPlayer, shownBoard, true, true);
						}
					}
					if (resourcePile2 != null) {
						if (resourcePile2ClickableImage.isClicked(x, y)) {
							game.players().get(currentPlayer-1).addCardToHand(game.resourcePile().revealedCard2());
							game.resourcePile().updateRevealedCard2();
							mainPage(context, game, currentPlayer, shownBoard, true, true);
						}
					}
					if (resourcePileHidden != null) {
						if (resourcePileHiddenClickableImage.isClicked(x, y)) {
							game.players().get(currentPlayer-1).addCardToHand(game.resourcePile().hiddenCard());
							game.resourcePile().updateHiddenCard();
							mainPage(context, game, currentPlayer, shownBoard, true, true);
						}
					}
					if (goldPile1 != null) {
						if (goldPile1ClickableImage.isClicked(x, y)) {
							game.players().get(currentPlayer-1).addCardToHand(game.goldPile().revealedCard1());
							game.goldPile().updateRevealedCard1();
							mainPage(context, game, currentPlayer, shownBoard, true, true);
						}
					}
					if (goldPile2 != null) {
						if (goldPile2ClickableImage.isClicked(x, y)) {
							game.players().get(currentPlayer-1).addCardToHand(game.goldPile().revealedCard2());
							game.goldPile().updateRevealedCard2();
							mainPage(context, game, currentPlayer, shownBoard, true, true);
						}
					}
					if (goldPileHidden != null) {
						if (goldPileHiddenClickableImage.isClicked(x, y)) {
							game.players().get(currentPlayer-1).addCardToHand(game.goldPile().hiddenCard());
							game.goldPile().updateHiddenCard();
							mainPage(context, game, currentPlayer, shownBoard, true, true);
						}
					}
				}
				
				// BOUTON PROCHAIN JOUEUR, VERIFICATION CONDITIONS FIN DE PARTIE
				if (!(nextPlayer == null)) {
					if (nextPlayer.isClicked(x, y)) {
						
						// SI LES JOUEURS N'ONT PLUS DE CARTES DANS LEUR MAIN
						if (game.allPlayersEmptyHands()) {
							addObjectivePointsPage(context, game);
						}
						
						// SI UN JOUEUR DEPASSE LES 20 POINTS, ON LAISSE FINIR CE TOUR
						if (game.players().get(currentPlayer-1).points() >= 20) {
							game.setPlayerFinished();
						}
						if (currentPlayer < game.players().size()) {
							currentPlayer += 1;
						}
						else {
							currentPlayer = 1;
							
							// SI C'ETAIT LE DERNIER TOUR, ON FINIT LA PARTIE
							if (game.lastTurn()) {
								addObjectivePointsPage(context, game);
							}
							
							// SI UN JOUEUR A FINI, ON LANCE LE DERNIER TOUR
							if (game.playerFinished()) {
								game.setLastTurn();
							}
						}
						mainPage(context, game, currentPlayer, shownBoard, false, false);
					}
				}
			}
			
			if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.Q) {
				System.out.println("Vous avez quitté le jeu.");
				context.exit(0);
				return;
			}
		}
	}
	
	public static void addObjectivePointsPage(ApplicationContext context, Game game) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(game);
		
		BoardView.clear(context);
		
		new ClickableImage(context, Globals.texts.image(22), (float) (Globals.screenWidth*0.5) -Globals.texts.image(22).getWidth()/2, (float) (Globals.screenHeight*(0.075)) - Globals.texts.image(22).getHeight()/2);
		
		// NOMBRE DE POINTS QUE CHAQUE JOUEUR A GAGNE PAR OBJECTIFS [COMMUN 1, COMMUN 2, PERSONNEL]
		for (int i = 0; i < game.players().size(); i++) {
			List<Integer> playerPoints = game.players().get(i).getPointsFromObjectives(game, i);
			ImageLoader objectiveImages = new ImageLoader("include/images", game.commonObjective1().image(), game.commonObjective1().image(), game.commonObjective2().image(), game.players().get(i).objective().image());
			
			ImageLoader text = new ImageLoader("include/texts", "J1PO.png", "J1PO.png", "J2PO.png", "J3PO.png", "J4PO.png");
			Graphics g = text.image(i).getGraphics();
			g.setFont(Globals.font);
			g.setColor(Color.decode("#BBA44F"));
			
			g.drawString(String.valueOf(playerPoints.get(0)), 800, 55); // POINTS OBJECTIF COMMUN 1
			g.drawString(String.valueOf(playerPoints.get(1)), 1200, 55); // POINTS OBJECTIF COMMUN 2
			g.drawString(String.valueOf(playerPoints.get(2)), 1590, 55); // POINTS OBJECTIF PERSONNEL
			
			// OBJECTIF COMMUN 1
			new ClickableImage(context, objectiveImages.image(0), (float) (Globals.screenWidth*0.4) -objectiveImages.image(0).getWidth()/2, (float) (Globals.screenHeight*(0.225 + (0.175 * i))) - objectiveImages.image(0).getHeight()/2);
			// OBJECTIF COMMUN 2
			new ClickableImage(context, objectiveImages.image(1), (float) (Globals.screenWidth*0.6) -objectiveImages.image(1).getWidth()/2, (float) (Globals.screenHeight*(0.225 + (0.175 * i))) - objectiveImages.image(1).getHeight()/2);
			// OBJECTIF PERSONNEL
			new ClickableImage(context, objectiveImages.image(2), (float) (Globals.screenWidth*0.8) -objectiveImages.image(2).getWidth()/2, (float) (Globals.screenHeight*(0.225 + (0.175 * i))) - objectiveImages.image(2).getHeight()/2);
			
			new ClickableImage(context, text.image(i), (float) (Globals.screenWidth*0.525) -text.image(i).getWidth()/2, (float) (Globals.screenHeight*(0.225 + (0.175 * i))) - text.image(i).getHeight()/2);
		}
		
		var whowon = new ClickableImage(context, Globals.buttons.image(21), (float) (Globals.screenWidth*0.5) -Globals.buttons.image(21).getWidth()/2, (float) (Globals.screenHeight*(0.925)) - Globals.buttons.image(21).getHeight()/2);
		
		while (true) {
			
			var event = context.pollOrWaitEvent(10);
			if (event == null) {continue;}
			var action = event.getAction();
			
			if (action == Action.POINTER_DOWN) {
				var location = event.getLocation();
				float x = (float) location.getX();
				float y = (float) location.getY();
				
				if (whowon.isClicked(x, y)) {
					endPage(context, game);
				}
				
			}
			
			if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.Q) {
				System.out.println("Vous avez quitté le jeu.");
				context.exit(0);
				return;
			}
		}
	}
	
	public static void endPage(ApplicationContext context, Game game) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(game);
		
		BoardView.clear(context);
		
		ImageLoader text = new ImageLoader("include/texts", "JC1.png", "JC1.png", "JC2.png", "JC3.png", "JC4.png");
		
		// DANS LE CAS OU IL Y A UNE EGALITE
		int max = game.players().get(0).points();
		for (int i = 1; i < game.players().size(); i++) {
			if (game.players().get(i).points() > max) {
				max = game.players().get(i).points();
			}
		}
		
		for (int i = 0; i < game.players().size(); i++) {
			if (game.players().get(i).points() == max) {
				new ClickableImage(context, Globals.texts.image(23+i), (float) (Globals.screenWidth*0.5) -Globals.texts.image(23+i).getWidth()/2, (float) (Globals.screenHeight*(0.1+(0.1*i))) - Globals.texts.image(23+i).getHeight()/2);
			}
		}
		
		for (int i = 0; i < game.players().size(); i++) {
			Graphics g = text.image(i).getGraphics();
			g.setFont(Globals.font);
			g.setColor(Color.decode("#BBA44F"));
			
			g.drawString(game.players().get(i).pseudo(), 220, 120); // PSEUDO
			g.drawString(String.valueOf(game.players().get(i).points()), 200, 185); // POINTS
			
			new ClickableImage(context, text.image(i), (float) (Globals.screenWidth*(i == 0 || i == 2 ? 0.325 : 0.675)) -text.image(i).getWidth()/2, (float) (Globals.screenHeight*(i == 0 || i == 1 ? 0.55 : 0.85)) - text.image(i).getHeight()/2);
		}
		
		while (true) {
			
			var event = context.pollOrWaitEvent(10);
			if (event == null) {continue;}
			var action = event.getAction();
			
			if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.Q) {
				System.out.println("Vous avez quitté le jeu.");
				context.exit(0);
				return;
			}
		}
	}
	
	public static String enterName(ApplicationContext context, int i) {
		Objects.requireNonNull(context);
		
		new ClickableImage(context, Globals.texts.image(i+1), (float) (Globals.screenWidth/2 - Globals.texts.image(i+1).getWidth()/2), Globals.screenHeight/3 - Globals.texts.image(i+1).getHeight()/2);
		String name = "";
		
		while (true) {
			var event = context.pollOrWaitEvent(10);
			if (event == null) {continue;}
			
			var action = event.getAction();
			if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.UNDEFINED) {
				BoardView.clear(context);
				return name;
			}
			// ENTREE DES PSEUDOS
			else if (action == Action.KEY_PRESSED) {
				name += event.getKey().toString();
			}
		}
	}
	
	public static void playerNames(ApplicationContext context, int numberPlayers) {
		Objects.requireNonNull(context);
		
		ArrayList<String> nameList = new ArrayList<String>();
		for (int i = 0; i < numberPlayers; i++) {
			nameList.add(gameController.enterName(context, i));
		}
		var playerList = new ArrayList<Player>();
		BoardView.preMainPage(context, numberPlayers, 0, nameList, playerList);
		Game game = new Game(playerList);
		mainPage(context, game, 1, 1, false, false);
	}
 	
	public static void main(String[] args) throws IOException {
			Application.run(Globals.backgroundColor,  context -> {
				
				var screenInfo = context.getScreenInfo();
				Globals.screenWidth = screenInfo.getWidth(); // 2048
				Globals.screenHeight = screenInfo.getHeight(); // 1152
				Globals.sizeCoefWidth = Globals.screenWidth / 2048;
				Globals.sizeCoefHeight = Globals.screenHeight / 1152;
				
				// DECALAGE DES CARTES
				Globals.xDecal *= Globals.sizeCoefWidth;
				Globals.yDecal *= Globals.sizeCoefHeight;
				
				try {
					Card.generateDeck();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// BOUTONS CHOIX NOMBRE DE JOUEURS
				new ClickableImage(context, Globals.texts.image(0), (float) (Globals.screenWidth/2 - Globals.texts.image(0).getWidth()/2), Globals.screenHeight/3 - Globals.texts.image(0).getHeight()/2);
				var onePlayer = new ClickableImage(context, Globals.buttons.image(1), (float) (Globals.screenWidth*0.35 - Globals.buttons.image(1).getWidth()/2), Globals.screenHeight/2 - Globals.buttons.image(1).getHeight()/2);
				var twoPlayer = new ClickableImage(context, Globals.buttons.image(2), (float) (Globals.screenWidth*0.45 - Globals.buttons.image(2).getWidth()/2), Globals.screenHeight/2 - Globals.buttons.image(2).getHeight()/2);
				var threePlayer = new ClickableImage(context, Globals.buttons.image(3), (float) (Globals.screenWidth*0.55 - Globals.buttons.image(3).getWidth()/2), Globals.screenHeight/2 - Globals.buttons.image(3).getHeight()/2);
				var fourPlayer = new ClickableImage(context, Globals.buttons.image(4), (float) (Globals.screenWidth*0.65 - Globals.buttons.image(4).getWidth()/2), Globals.screenHeight/2 - Globals.buttons.image(4).getHeight()/2);
				
				while (true) {
					
					var event = context.pollOrWaitEvent(10);
					if (event == null) {continue;}
					var action = event.getAction();
					
					if (action == Action.POINTER_DOWN) {
						var location = event.getLocation();
						float x = (float) location.getX();
						float y = (float) location.getY();
						
						if (onePlayer.isClicked(x, y)) {
							BoardView.clear(context);
							playerNames(context, 1);
						}
						else if (twoPlayer.isClicked(x, y)) {
							BoardView.clear(context);
							playerNames(context, 2);
						}
						else if (threePlayer.isClicked(x, y)) {
							BoardView.clear(context);
							playerNames(context, 3);
						}
						else if (fourPlayer.isClicked(x, y)) {
							BoardView.clear(context);
							playerNames(context, 4);
						}
					}
					
					if (action == Action.KEY_PRESSED && event.getKey() == KeyboardKey.Q) {
						System.out.println("Vous avez quitté le jeu.");
						context.exit(0);
						return;
					}
				}
			});
	}
}
