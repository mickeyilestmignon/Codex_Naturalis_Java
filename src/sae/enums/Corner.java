package sae.enums;

public enum Corner {
	
	QUILL, MANUSCRIPT, INKWELL, ANIMAL, PLANT, FUNGI, INSECT, INVISIBLE, EMPTY;
	
	public static Corner convertToCorner(String string) {
		return Corner.valueOf(string.toUpperCase());
	}

}