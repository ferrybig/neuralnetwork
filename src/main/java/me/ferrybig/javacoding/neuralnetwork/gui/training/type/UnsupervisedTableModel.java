
package me.ferrybig.javacoding.neuralnetwork.gui.training.type;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;

public class UnsupervisedTableModel extends AbstractTableModel {

	private final int inputSize;
	private final List<boolean[]> data = new ArrayList<>();

	public UnsupervisedTableModel(CompiledNetwork network) {
		this.inputSize = network.getInputSize();
	}

	@Override
	public int getColumnCount() {
		return inputSize;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		super.setValueAt(aValue, rowIndex, columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex)[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Boolean.class;
	}

	@Override
	public String getColumnName(int column) {
		return "IN" + column;
	}

}