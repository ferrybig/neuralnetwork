
package me.ferrybig.javacoding.neuralnetwork.gui.training.type;

import javax.swing.table.AbstractTableModel;
import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;

final class SupervisedTableModel extends AbstractTableModel {

	private final boolean[][] outputList;
	private final int outputSize;
	private final int inputSize;

	public SupervisedTableModel(CompiledNetwork network) {
		this.inputSize = network.getInputSize();
		this.outputSize = network.getOutputSize();
		this.outputList = new boolean[this.inputSize > 31 ? Integer.MAX_VALUE : 1 << this.inputSize][this.outputSize];
	}

	@Override
	public int getColumnCount() {
		return outputSize + inputSize;
	}

	@Override
	public int getRowCount() {
		return this.inputSize > 31 ? Integer.MAX_VALUE : 1 << this.inputSize;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < inputSize) {
			return (1 << columnIndex & rowIndex) != 0;
		}
		return outputList[rowIndex][columnIndex - inputSize];
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		outputList[rowIndex][columnIndex - inputSize] = (boolean) aValue;
		this.fireTableCellUpdated(rowIndex, columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex >= inputSize;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Boolean.class;
	}

	@Override
	public String getColumnName(int column) {
		return column < inputSize ? "IN" + column : "OUT" + (column - inputSize);
	}

}
