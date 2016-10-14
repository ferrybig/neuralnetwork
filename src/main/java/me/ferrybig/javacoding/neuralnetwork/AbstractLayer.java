
package me.ferrybig.javacoding.neuralnetwork;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractLayer implements Layer {

	protected transient SoftReference<List<Cell>> cells;
	protected final int index;
	private final CompiledNetwork network;

	public AbstractLayer(int index, CompiledNetwork network) {
		this.index = index;
		this.network = network;
	}

	protected List<Cell> checkCellList() {
		if (cells != null) {
			List<Cell> cellList = this.cells.get();
			if (cellList != null) {
				return cellList;
			}
		}
		return null;
	}

	@Override
	public void debug(StringBuilder builder) {
		builder.ensureCapacity(getDebugSize());
		builder.append(String.format("--- Layer %2d ---", getIndex()));
		builder.append('\n');
		for (Cell cell : this.getCells()) {
			cell.debug(builder);
		}
		builder.append('\n');
	}

	@Override
	public String debug() {
		StringBuilder builder = new StringBuilder(getDebugSize());
		debug(builder);
		return builder.toString();
	}

	@Override
	public int getDebugSize() {
		int size = 18;
		for (Cell cell : this.getCells()) {
			size += cell.getDebugSize();
		}
		return size;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractLayer other = (AbstractLayer) obj;
		if (this.index != other.index) {
			return false;
		}
		if (!Objects.equals(this.network, other.network)) {
			return false;
		}
		return true;
	}

	protected abstract Cell createCell(int index);

	@Override
	public Cell getCell(int index) {
		List<Cell> cellList = checkCellList();
		if (cellList != null) {
			return cellList.get(index);
		}
		return createCell(index);
	}

	@Override
	public List<Cell> getCells() {
		List<Cell> cellList = checkCellList();
		if (cellList != null) {
			return cellList;
		}
		int size = getSize();
		cellList = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			cellList.add(createCell(i));
		}
		cellList = Collections.unmodifiableList(cellList);
		cells = new SoftReference<>(cellList);
		return cellList;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public CompiledNetwork getNetwork() {
		return network;
	}

	@Override
	public Layer getPrevious() {
		if (index == 0) {
			throw new IllegalStateException("Input cells don't have a previous layer");
		}
		return getNetwork().getLayer(index - 1);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + Objects.hashCode(this.network);
		hash = 29 * hash + this.index;
		return hash;
	}

}
