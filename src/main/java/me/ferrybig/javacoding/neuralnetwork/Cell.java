
package me.ferrybig.javacoding.neuralnetwork;

public interface Cell {

	void debug(StringBuilder builder);

	String debug();

	double getBiasWeigth();

	void setBiasWeigth(double newVal);

	int getCellNumber();

	int getDebugSize();

	int getInputSize();

	Layer getLayer();

	CompiledNetwork getNetwork();

	Cell getPreviousCell(int index);

	double getWeigth(Cell cell);

	double getWeigth(int inputIndex);

	void setWeigth(int inputIndex, double newVal);

	String getSimpleName();

}
