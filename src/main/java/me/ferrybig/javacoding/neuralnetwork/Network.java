
package me.ferrybig.javacoding.neuralnetwork;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public interface Network {

	void debug(StringBuilder builder);

	String debug();

	int getDebugSize();

	int getInputSize();

	Layer getLayer(int index);

	int getLayerCount();

	List<Layer> getLayers();

	int getMaxCacheSize();

	int getMinCacheSize();

	int getOutputSize();

	/**
	 *
	 * @param input
	 * @param output
	 * @deprecated Method is inefficient as it requires remaking of the cache
	 * index, make one per thread and reuse it
	 */
	@Deprecated
	void resolveNetworkSequential(double[] input, double[] output);

	/**
	 *
	 * @param input
	 * @param inputIndex
	 * @param inputLength
	 * @param output
	 * @param outputIndex
	 * @param outputLength
	 * @deprecated Method is inefficient as it requires remaking of the cache
	 * index, make one per thread and reuse it
	 */
	@Deprecated
	void resolveNetworkSequential(double[] input, int inputIndex, int inputLength, double[] output, int outputIndex, int outputLength);

	void resolveNetworkSequential(double[] input, double[] output, double[] cache);

	void resolveNetworkSequential(double[] input, int inputIndex, int inputLength, double[] output, int outputIndex, int outputLength, double[] cache, int cacheIndex, int cacheLength);

	void write(DataOutput out) throws IOException;

}
