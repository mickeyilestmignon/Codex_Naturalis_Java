package sae.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import fr.umlv.zen5.ApplicationContext;
import sae.controller.Globals;

public record ClickableImage(ApplicationContext context, BufferedImage image, float x, float y) {
	
	public ClickableImage {
		Objects.requireNonNull(context);
		Objects.requireNonNull(image);
		drawImage(context, image, x, y);
	}
	
	private static void drawImage(ApplicationContext context, BufferedImage image, float x, float y) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(image);
		var width = image.getWidth() / Globals.sizeCoefWidth;
		var height = image.getHeight() / Globals.sizeCoefHeight;
		var scale = Math.min(image.getWidth() / width, image.getHeight()/ height);
		var transform = new AffineTransform(scale, 0, 0, scale, x + (image.getWidth() - scale * width) / 2, y + (image.getHeight() - scale * height) / 2);
		context.renderFrame(graphics -> {
			graphics.drawImage(image, transform, null);
		});
	}
	
	public void clearImage(ApplicationContext context) {
		Objects.requireNonNull(context);
		context.renderFrame(graphics -> {
			graphics.setColor(Globals.backgroundColor);
			graphics.fill(new Rectangle2D.Float(x, y, image.getWidth(), image.getHeight()));
		});
	}
	
	public boolean isClicked(float Cx, float Cy) {
		return x <= Cx && Cx <= x+image.getWidth() && y <= Cy && Cy <= y+image.getHeight();
	}

}
