
package me.ferrybig.javacoding.neuralnetwork.gui.palete;

import java.awt.Color;

public class ColorUtilities {
	private ColorUtilities() {
		assert false;
	}

	public static Color setBrigtness(Color orig, double shade) {
		if(shade >= 1) {
			return Color.white;
		}
		if(shade <= 0) {
			return Color.black;
		}
		int newShade = (int) Math.round(shade * (255 * 3));

		assert newShade >= 0;
		assert newShade <= 255 * 3;
		int origShade = orig.getRed() + orig.getBlue() + orig.getGreen();
		int change = newShade - origShade;
		if(change == 0) {
			return orig;
		}
		int overflow = 0;
		int newBlue = orig.getBlue();
		int newRed = orig.getRed();
		int newGreen = orig.getGreen();
		while(change > 2 || change < -2) {
			{
				int remaining = change % 3;
				change -= remaining;
				overflow += remaining;
			}
			{
				int offset = change / 3;
				change -= offset;
				newBlue += offset;
				if(newBlue > 255) {
					overflow += newBlue - 255;
					newBlue = 255;
				} else if(newBlue < 0) {
					overflow += newBlue;
					newBlue = 0;
				}
			}
			{
				int offset = change / 2;
				change -= offset;
				newGreen += offset;
				if(newGreen > 255) {
					overflow += newGreen - 255;
					newGreen = 255;
				} else if(newGreen < 0) {
					overflow += newGreen;
					newGreen = 0;
				}
			}
			{
				int offset = change;
				change -= offset;
				newRed += offset;
				if(newRed > 255) {
					overflow += newRed - 255;
					newRed = 255;
				} else if(newRed < 0) {
					overflow += newRed;
					newRed = 0;
				}
			}
			{
				change += overflow;
				overflow = 0;
			}
		}
		return new Color(newRed, newGreen, newBlue);
	}
}
