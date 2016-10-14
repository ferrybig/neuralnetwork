
package me.ferrybig.javacoding.neuralnetwork.gui.training.type;

import javax.swing.JComponent;
import me.ferrybig.javacoding.neuralnetwork.error.ErrorCalculator;

public interface Training {

	public JComponent getComponent();

	public ErrorCalculator getErrorCalculator();
}
