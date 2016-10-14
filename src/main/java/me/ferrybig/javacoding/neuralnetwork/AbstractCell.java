package me.ferrybig.javacoding.neuralnetwork;

import java.util.Objects;

public abstract class AbstractCell implements Cell {

	protected final int cellNumber;
	protected final Layer layer;
	protected final CompiledNetwork network;

	public AbstractCell(int cellNumber, Layer layer, CompiledNetwork network) {
		this.cellNumber = cellNumber;
		this.layer = layer;
		this.network = network;
	}

	@Override
	public int getDebugSize() {
		return 6 + 2 + 6 + 3 + 1 + (getLayer().getIndex() == 0 ? 0
				: (getInputSize() + 1) * (1 + 3 + 5 + 2 + 17 + 1));
	}

	@Override
	public void debug(StringBuilder builder) {
		builder.ensureCapacity(getDebugSize());
		builder.append("Layer ");
		builder.append(String.format("%2d", getLayer().getIndex()));
		builder.append(" Cell ");
		builder.append(String.format("%3d", getCellNumber()));
		builder.append('\n');
		if (getLayer().getIndex() == 0) {
			return;
		}
		Layer previous = getLayer().getPrevious();
		for (int i = 0; i <= getInputSize(); i++) {
			if (i == getInputSize()) {
				builder.append(" ");
				builder.append("  ");
				builder.append(" BIAS");
			} else {
				builder.append("L");
				builder.append(String.format("%2d", previous.getIndex()));
				builder.append(" C");
				builder.append(String.format("%3d", i));
			}
			builder.append(": ");
			builder.append(String.format("%+17.10E", getWeigth(i)));
			builder.append('\n');

		}
	}

	@Override
	public String debug() {
		StringBuilder builder = new StringBuilder(getDebugSize());
		debug(builder);
		return builder.toString();
	}

	@Override
	public int getCellNumber() {
		return cellNumber;
	}

	@Override
	public Cell getPreviousCell(int index) {
		return this.getLayer().getPrevious().getCell(index);
	}

	@Override
	public double getWeigth(Cell cell) {
		if (!cell.getNetwork().equals(this.getNetwork())) {
			throw new IllegalArgumentException("Cells do not belong to the same network");
		}
		int layerIndexMinusOne = this.getLayer().getIndex() - 1;
		if (layerIndexMinusOne < 0) {
			throw new IllegalArgumentException("Input cells don't have weigths");
		}
		if (cell.getLayer().getIndex() != layerIndexMinusOne) {
			throw new IllegalArgumentException("Cell is not in the layer below our layer");
		}
		return getWeigth(cell.getCellNumber());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + this.cellNumber;
		hash = 37 * hash + Objects.hashCode(getLayer());
		return hash;
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
		final AbstractCell other = (AbstractCell) obj;
		if (this.cellNumber != other.cellNumber) {
			return false;
		}
		if (!Objects.equals(getLayer(), other.getLayer())) {
			return false;
		}
		return true;
	}

	@Override
	public Layer getLayer() {
		return layer;
	}

	@Override
	public CompiledNetwork getNetwork() {
		return network;
	}
	
	@Override
	public String getSimpleName() {
		return "L" + getLayer().getIndex() + " C" + getCellNumber();
	}

}
