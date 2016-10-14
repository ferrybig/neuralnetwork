package me.ferrybig.javacoding.neuralnetwork.gui.palete;

import java.awt.Color;
import java.util.Map;

public interface PaleteColor<T> {

	public default Color getFromMap(Map<? super T, ? extends Color> map) {
		Color r = map.get(this);
		if (r == null) {
			return getDefaultColor();
		}
		return r;
	}

	public Color getDefaultColor();
}
