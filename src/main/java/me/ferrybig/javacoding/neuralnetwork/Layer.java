
package me.ferrybig.javacoding.neuralnetwork;

import java.util.List;

public interface Layer {

	void debug(StringBuilder builder);

	String debug();

	Cell getCell(int index);

	List<Cell> getCells();

	int getDebugSize();

	int getIndex();

	CompiledNetwork getNetwork();

	Layer getPrevious();

	int getSize();

}
