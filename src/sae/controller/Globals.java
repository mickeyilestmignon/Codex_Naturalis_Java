package sae.controller;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sae.card.ObjectiveDeck;
import sae.card.PlayableDeck;
import sae.card.Point;
import sae.util.ImageLoader;

public class Globals {
	
	public static ObjectiveDeck objectiveDeck = new ObjectiveDeck();
	public static PlayableDeck playableDeck = new PlayableDeck(); // UNIQUEMENT LES PLAYABLECARDS
	public static final HashMap<String, Point> positionToPoint = new HashMap<String, Point>() {{
		put("Top", new Point(0, -2)); put("TopRight", new Point(1, -1)); put("Right", new Point(2, 0)); put("BottomRight", new Point(1, 1)); put("Bottom", new Point(0, 2)); put("BottomLeft", new Point(-1, 1));
		put("Left", new Point(-2, 0)); put("TopLeft", new Point(-1, -1));
	}};
	
	//images
	public static final ImageLoader buttons = new ImageLoader("include/buttons", "one.png", "map.png", "one.png", "two.png", "three.png", "four.png", "map_hover.png", "one_hover.png", "two_hover.png", "three_hover.png", "four_hover.png", 
			"voirMainJ1.png", "voirMainJ2.png", "voirMainJ3.png", "voirMainJ4.png", "gauche.png", "droite.png", "bas.png", "haut.png", "next.png", "blank.png", "flip.png", "whowon.png");
	public static final ImageLoader texts = new ImageLoader("include/texts", "Nombre_de_joueurs.png", "Nombre_de_joueurs.png", "input name1.png", "input name2.png", "input name3.png", "input name4.png", "voici_main.png", "choisir_obj.png", 
			"voirMainJ1_.png", "voirMainJ2_.png", "voirMainJ3_.png", "voirMainJ4_.png", "obj_commun.png", "cartes_ressources.png", "cartes_gold.png", "JC1.png", "JC2.png", "JC3.png", "JC4.png", "J1PO.png", "J2PO.png", "J3PO.png", "J4PO.png", "points_gagnes_obj.png",
			"JC1V.png", "JC2V.png", "JC3V.png", "JC4V.png", "20points.png", "lastTurn.png");
	public static final ImageLoader elements = new ImageLoader("include/images", "map.png", "map.png");
	
	//styles
	public static final Color backgroundColor = Color.decode("#c7bda3");
	public static Font font = new Font("Arial", Font.BOLD, 40);
	public static Font fontTrial = new Font("Arial", Font.BOLD, 25);
	
	//values
	public static float screenWidth = 0;
	public static float screenHeight = 0;
	public static float sizeCoefWidth = 1;
	public static float sizeCoefHeight = 1;
	public static int xOffset = 0; // POUR LE DEPLACEMENT DE LA ZONE DE JEU
	public static int yOffset = 0;
	public static final int cardHeight = 200;
	public static final int cardWidth = 300;
	public static final double cornerHeight = 85;
	public static final double cornerWidth = 69.42;
	public static double xDecal = cardWidth - cornerWidth - 3;
	public static double yDecal = cardHeight - cornerHeight + 5;
	
	//other
	public static final HashMap<Integer,Integer> cornerToCorner = new HashMap<Integer, Integer>() {{
		put(0, 3); put(1, 2); put(2, 1); put(3, 0);
	}};
	public static final List<Point> directions = List.of(new Point(-1,-1), new Point(-1,1), new Point(1,-1), new Point(1,1));
	
}