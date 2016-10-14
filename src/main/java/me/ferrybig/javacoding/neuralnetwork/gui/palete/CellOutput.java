
package me.ferrybig.javacoding.neuralnetwork.gui.palete;

import java.awt.Color;

public enum CellOutput implements PaleteColor<CellOutput> {
	POSITIVE_OUTPUT (ColorUtilities.setBrigtness(Color.green, 0.9f)),
	NEGATIVE_OUTPUT (ColorUtilities.setBrigtness(Color.red, 0.9f)),
	STRONG_POSITIVE_OUTPUT (ColorUtilities.setBrigtness(Color.green, 0.8f)),
	STRONG_NEGATIVE_OUTPUT (ColorUtilities.setBrigtness(Color.red, 0.8f)),
	NEUTRAL_OUTPUT (Color.white);

	private final Color defaultColor;

	private CellOutput(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	@Override
	public Color getDefaultColor() {
		return defaultColor;
	}
}
