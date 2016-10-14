
package me.ferrybig.javacoding.neuralnetwork;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractNetwork implements Network {

	protected transient SoftReference<List<Layer>> layers;

	protected List<Layer> checkLayerList() {
		if (layers != null) {
			List<Layer> layerList = layers.get();
			if (layerList != null) {
				return layerList;
			}
		}
		return null;
	}

	protected abstract Layer createLayer(int index);

	@Override
	public void debug(StringBuilder builder) {
		builder.ensureCapacity(getDebugSize());
		int layerCount = getLayerCount();
		for (int i = 0; i < layerCount; i++) {
			if (i == 0) {
				builder.append("input ");
			} else if (i == layerCount - 1) {
				builder.append("output");
			} else {
				builder.append("hidden");
			}
			builder.append(" layer with ");
			builder.append(String.format("%3d", getLayer(i).getSize()));
			builder.append(" neurons");
			builder.append('\n');
		}
		builder.append('\n');
		for (Layer layer : this.getLayers()) {
			layer.debug(builder);
		}
	}

	@Override
	public String debug() {
		StringBuilder builder = new StringBuilder(getDebugSize());
		debug(builder);
		return builder.toString();
	}

	@Override
	public int getDebugSize() {
		int size = 1;
		for (Layer layer : this.getLayers()) {
			size += layer.getDebugSize();
			size += 6 + 12 + 3 + 8 + 1;
		}
		return size;
	}


	@Override
	public Layer getLayer(int index) {
		List<Layer> cellList = checkLayerList();
		if (cellList != null) {
			return cellList.get(index);
		}
		return createLayer(index);
	}


	@Override
	public List<Layer> getLayers() {
		List<Layer> layerList = checkLayerList();
		if (layerList != null) {
			return layerList;
		}
		int layerCount = getLayerCount();
		layerList = new ArrayList<>(layerCount);
		for (int i = 0; i < layerCount; i++) {
			layerList.add(createLayer(i));
		}
		layerList = Collections.unmodifiableList(layerList);
		layers = new SoftReference<>(layerList);
		return layerList;
	}


	/**
	 *
	 * @param input
	 * @param output
	 * @deprecated Method is inefficient as it requires remaking of the cache
	 * index, make one per thread and reuse them
	 */
	@Deprecated
	@Override
	public void resolveNetworkSequential(double[] input, double[] output) {
		resolveNetworkSequential(input, 0, input.length, output, 0, output.length);
	}

	/**
	 *
	 * @param input
	 * @param inputIndex
	 * @param inputLength
	 * @param output
	 * @param outputIndex
	 * @param outputLength
	 * @deprecated Method is inefficient as it requires remaking of the cache
	 * index, make one per thread and reuse them
	 */
	@Deprecated
	@Override
	public void resolveNetworkSequential(double[] input, int inputIndex, int inputLength, double[] output, int outputIndex, int outputLength) {
		resolveNetworkSequential(input, 0, input.length, output, 0, output.length, new double[getMinCacheSize()], 0, getMinCacheSize());
	}

	@Override
	public void resolveNetworkSequential(double[] input, double[] output, double[] cache) {
		resolveNetworkSequential(input, 0, input.length, output, 0, output.length, cache, 0, cache.length);
	}


}
