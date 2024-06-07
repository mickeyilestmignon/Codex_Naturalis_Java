package sae.util;

public class RandInt {
	
	public static int randInt(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

}
