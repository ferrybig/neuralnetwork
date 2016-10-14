
package me.ferrybig.javacoding.neuralnetwork.gui.palete;

import java.awt.Color;

public enum LineColor implements PaleteColor<LineColor> {
	POSITIVE_OUTPUT (ColorUtilities.setBrigtness(Color.green, 0.4f)),
	NEGATIVE_OUTPUT (ColorUtilities.setBrigtness(Color.red, 0.4f)),
	NEUTRAL_OUTPUT (ColorUtilities.setBrigtness(Color.gray, 0.4f)),
	POSITIVE_OUTPUT_SELECTED (ColorUtilities.setBrigtness(Color.green, 0.4f)),
	NEGATIVE_OUTPUT_SELECTED (ColorUtilities.setBrigtness(Color.red, 0.4f)),
	NEUTRAL_OUTPUT_SELECTED (ColorUtilities.setBrigtness(Color.gray, 0.4f)),
	POSITIVE_OUTPUT_UNSELECTED (ColorUtilities.setBrigtness(Color.green, 0.9f)),
	NEGATIVE_OUTPUT_UNSELECTED (ColorUtilities.setBrigtness(Color.red, 0.9f)),
	NEUTRAL_OUTPUT_UNSELECTED (ColorUtilities.setBrigtness(Color.gray, 0.9f)),
	;

	private final Color defaultColor;

	private LineColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	@Override
	public Color getDefaultColor() {
		return defaultColor;
	}
}
