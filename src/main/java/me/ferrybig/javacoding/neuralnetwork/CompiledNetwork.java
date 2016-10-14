package me.ferrybig.javacoding.neuralnetwork;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.lang.Validate;

public final class CompiledNetwork extends AbstractNetwork {

	private final double[] weigth;
	private final int[] layerSize;
	/**
	 * Output caches larger than this don't benefit from space optimalization
	 */
	private final int maxCacheSize;
	/**
	 * Minimal size for the output layer
	 */
	private final int minCacheSize;

	public CompiledNetwork(int[] layerSize) {
		this(generateEmptyWeigthGrid(layerSize), layerSize);
	}

	public CompiledNetwork(double[] weigth, int[] layerSize) {
		this.weigth = weigth.clone();
		this.layerSize = layerSize.clone();
		int weigthSize = 0;
		int outputSize = layerSize[0];
		int minOutputSize = 0;
		int oldSize = layerSize[0];
		for (int i = 1; i < this.layerSize.length; i++) {
			int size = this.layerSize[i];
			Validate.isTrue(size > 0, "LayerSize contains an non positive size at index ", i);
			if (size + oldSize > minOutputSize) {
				minOutputSize = size + oldSize;
			}
			outputSize += size;
			// Add neurons themselves
			weigthSize += size * oldSize;
			// Add bias for the neurons
			weigthSize += size;
			oldSize = size;
			Validate.isTrue(outputSize >= 0,
					"LayerSize to large to represent a an integer, overflow detected to ", outputSize);
			Validate.isTrue(weigthSize >= 0,
					"LayerSize to large to represent a an integer, overflow detected to ", weigthSize);
		}
		Validate.isTrue(weigth.length == weigthSize, "Length of weight array does not match expected size: ", weigthSize);
		maxCacheSize = outputSize;
		minCacheSize = minOutputSize;
	}

	@Override
	public int getLayerCount() {
		return layerSize.length;
	}

	public double[] getWeigth() {
		return this.weigth;
	}

	@Override
	public int getInputSize() {
		return layerSize[0];
	}

	@Override
	public int getOutputSize() {
		return layerSize[layerSize.length - 1];
	}

	@Override
	public void resolveNetworkSequential(
			double[] input, int inputIndex, int inputLength,
			double[] output, int outputIndex, int outputLength,
			double[] cache, int cacheIndex, int cacheLength) {
		//System.out.println("------");
		System.arraycopy(input, inputIndex, cache, cacheIndex, inputLength);
		int weigthIndex = 0;
		if (cacheLength >= maxCacheSize) {
			// Use simple method for calculation since we have the space
			int lastRunningIndex = 0;
			int runningIndex = layerSize[0];
			for (int layer = 1; layer < layerSize.length; layer++) {
				int prevLayer = layer - 1;
				int thisSize = layerSize[layer];
				int prevSize = layerSize[prevLayer];

				resolveLayerSequential(weigthIndex, runningIndex,
						lastRunningIndex, prevSize, thisSize, cache);
				weigthIndex += thisSize * (prevSize + 1);
				lastRunningIndex = runningIndex;
				runningIndex += thisSize;
			}
			System.arraycopy(cache, lastRunningIndex, output, outputIndex, outputLength);
		} else if (cacheLength >= minCacheSize) {
			// We have less space to deal with, use a less space using method that requires more calculations
			boolean writingToEndOfArray = true;
			for (int layer = 1; layer < layerSize.length; layer++) {
				int prevLayer = layer - 1;
				int thisSize = layerSize[layer];
				int prevSize = layerSize[prevLayer];

				int readingIndex;
				int writingIndex;
				// Branch should be optimalized for flip-flop conditions
				if (writingToEndOfArray) {
					writingIndex = cacheIndex + cacheLength - thisSize;
					readingIndex = cacheIndex;
				} else {
					readingIndex = cacheIndex + cacheLength - prevSize;
					writingIndex = cacheIndex;
				}
				assert prevSize > 0;
				assert thisSize > 0;

				assert readingIndex + prevSize <= writingIndex || writingIndex == 0;
				assert readingIndex + prevSize <= cacheIndex + cacheLength;
				assert readingIndex >= cacheIndex;
				assert writingIndex + thisSize <= readingIndex || readingIndex == 0;
				assert writingIndex + thisSize <= cacheIndex + cacheLength;
				assert writingIndex >= cacheIndex;
				assert writingIndex != readingIndex;

				//System.out.println("Reading from " + readingIndex + " writing to " + writingIndex + " cache " + cache.length);
				resolveLayerSequential(weigthIndex, writingIndex,
						readingIndex, prevSize, thisSize, cache);

				writingToEndOfArray = !writingToEndOfArray;
				weigthIndex += thisSize * (prevSize + 1);
			}
			int copyIndex;
			if (writingToEndOfArray) {
				copyIndex = cacheIndex;
			} else {
				copyIndex = cacheIndex + cacheLength - outputLength;
			}
			System.arraycopy(cache, copyIndex, output, outputIndex, outputLength);
		} else {
			throw new IllegalArgumentException("Cache size to small for this operation, minCacheSize = " + minCacheSize);
		}
		//System.out.println(Arrays.toString(input) + " --> " + Arrays.toString(output));
	}

	private void resolveLayerSequential(int weigthIndex, int outputLayerIndex,
			int previousOutputLayerStart, int previousLayerSize, int layerSize,
			double[] cache) {
		int previousLayerSizeIncludingBias = previousLayerSize + 1;
		for (int outputIndex = 0; outputIndex < layerSize; outputIndex++) {
			int weigthStartingIndex = weigthIndex
					+ outputIndex * previousLayerSizeIncludingBias;

			// Bias is stored in the last weigth value and is always 1
			double out = weigth[weigthStartingIndex + previousLayerSize]
					* 1;
			//System.out.println("---");
			//System.out.println("Bias weigth: " +  weigth[weigthStartingIndex + previousLayerSize]);
			//System.out.println("Bias first calculation: " + out);
			for (int inputIndex = 0; inputIndex < previousLayerSize; inputIndex++) {
				out += weigth[weigthStartingIndex + inputIndex]
						* cache[previousOutputLayerStart + inputIndex];
				//System.out.println("Cell input: " + cache[previousOutputLayerStart + inputIndex]);
				//System.out.println("Cell weigth: " + weigth[weigthStartingIndex + inputIndex]);
			}
			//System.out.println("Output: " + sigmoid(out));
			cache[outputLayerIndex + outputIndex] = sigmoid(out);
		}
	}

//	/**
//	 *
//	 * @param weigthIndex
//	 * @param outputLayerIndex
//	 * @param previousLayerSize
//	 * @param layerSize
//	 * @param threads
//	 * @see ExecutorService#invokeAll(java.util.Collection)
//	 * @return
//	 */
//	private List<Callable<?>> resolveLayerParalel(int weigthIndex, int outputLayerIndex,
//			int previousOutputLayerStart, int previousLayerSize, int layerSize, int threads) {
//		int perThread = (int) Math.ceil(layerSize / (double) threads);
//		List<Callable<?>> callables = new ArrayList<>(threads);
//		for (int outputIndex = 0; outputIndex < layerSize; outputIndex += perThread) {
//			int batchSize;
//			if (outputIndex + perThread > layerSize) {
//				batchSize = layerSize - outputIndex;
//			} else {
//				batchSize = perThread;
//			}
//			int previousLayerSizeIncludingBias = previousLayerSize + 1;
//			int weigthStartingIndex = weigthIndex + outputIndex * previousLayerSizeIncludingBias;
//			int outputLayerStart = outputLayerIndex + outputIndex;
//			callables.add(() -> {
//				resolveLayerSequential(weigthStartingIndex, outputLayerStart,
//						previousOutputLayerStart, previousLayerSize, batchSize);
//				return null;
//			});
//		}
//		return callables;
//
//	}
	public static double sigmoid(double in) {
		return 1 / (1 + Math.pow(Math.E, -in));
	}

	//
	//	private void writeObject(ObjectOutputStream out)
	//			throws IOException {
	//		write(out);
	//	}
	//
	//	private void readObject(ObjectInputStream in)
	//			throws IOException, ClassNotFoundException {
	//		try {
	//			Field weigthField = this.getClass().getDeclaredField("weigth");
	//			Field sizeField = this.getClass().getDeclaredField("layerSize");
	//			Field outputField = this.getClass().getDeclaredField("outputLayer");
	//			throw new IOException("Not yet implemented");
	//		} catch (NoSuchFieldException | SecurityException ex) {
	//			throw new IOException("Cannot load network, reflection problem", ex);
	//		}
	//
	//	}
	public static CompiledNetwork read(DataInput in) throws IOException {
		int version = in.readInt();
		if (version == 0 || version == 1) {
			int sizeArraySize = in.readInt();
			int[] sizeArray = new int[sizeArraySize];
			readSizeArray(sizeArray, in);
			int weigthSize = Arrays.stream(sizeArray).sum();
			double[] weigth = new double[weigthSize];
			readWeigthArray(weigth, in);
			return new CompiledNetwork(weigth, sizeArray);
		} else {
			throw new IOException("Unknown network type"); // MalformatNetworkException
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(0);
		out.writeInt(layerSize.length);
		for (int size : layerSize) {
			out.writeInt(size);
		}
		for (double w : weigth) {
			out.writeDouble(w);
		}
	}

	private static void readSizeArray(int[] arr, DataInput in) throws IOException {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = in.readInt();
		}
	}

	private static void readWeigthArray(double[] arr, DataInput in) throws IOException {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = in.readDouble();
		}
	}

	private static void readWeigthArray(float[] arr, DataInput in) throws IOException {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = in.readFloat();
		}
	}

	public CompiledNetwork copy() {
		return new CompiledNetwork(weigth.clone(), layerSize);
	}

	protected static double[] generateEmptyWeigthGrid(int[] layerSize) {
		int weigthSize = 0;
		int oldSize = layerSize[0];
		for (int i = 1; i < layerSize.length; i++) {
			int size = layerSize[i];
			Validate.isTrue(size > 0, "LayerSize contains an non positive size at index ", i);
			// Add neurons themselves
			weigthSize += size * oldSize;
			// Add bias for the neurons
			weigthSize += size;
			oldSize = size;
			Validate.isTrue(weigthSize >= 0,
					"LayerSize to large to represent a an integer, overflow detected to ", weigthSize);
		}
		return new double[weigthSize];
	}

	@Override
	protected Layer createLayer(int index) {
		Validate.isTrue(!(index < 0 || index >= getLayerCount()), "Layer index out of range: ", index);
		int weigthIndex = 0;
		int oldSize = layerSize[0];
		for (int i = 1; i < index; i++) {
			int size = layerSize[i];
			Validate.isTrue(size > 0, "LayerSize contains an non positive size at index ", i);
			// Add neurons themselves
			weigthIndex += size * oldSize;
			// Add bias for the neurons
			weigthIndex += size;
			oldSize = size;
		}
		return new CompiledLayer(index, layerSize[index], index <= 0 ? 0 : layerSize[index - 1], weigth, weigthIndex);
	}

	@Override
	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	@Override
	public int getMinCacheSize() {
		return minCacheSize;
	}


	private class CompiledLayer extends AbstractLayer {

		private final int thisSize;
		private final int prevSize;
		private final double[] weights;
		private final int weightStart;

		public CompiledLayer(int index, int thisSize, int prevSize, double[] weights, int weightStart) {
			super(index, CompiledNetwork.this);
			this.thisSize = thisSize;
			this.prevSize = prevSize;
			this.weights = weights;
			this.weightStart = weightStart;
		}

		@Override
		protected Cell createCell(int index) {
			return new CompiledCell(index, weights, weightStart + (prevSize + 1) * index, prevSize, thisSize);
		}

		@Override
		public int getSize() {
			return thisSize;
		}

		private final class CompiledCell extends AbstractCell {

			private final double[] weigth;
			private final int weigthIndex;
			private final int prevLayerSize;
			private final int thisLayerSize;

			public CompiledCell(int cellNumber, double[] weigth, int weigthIndex, int prevLayerSize, int thisLayerSize) {
				super(cellNumber, CompiledLayer.this, CompiledNetwork.this);
				this.weigth = weigth;
				this.weigthIndex = weigthIndex;
				this.prevLayerSize = prevLayerSize;
				this.thisLayerSize = thisLayerSize;
			}

			@Override
			public int getInputSize() {
				return prevLayerSize;
			}

			@Override
			public double getWeigth(int inputIndex) {
				if (inputIndex < 0) {
					throw new IllegalArgumentException("Input index to low");
				}
				if (inputIndex > prevLayerSize) {
					throw new IllegalArgumentException("Input index to high");
				}
				return weigth[weigthIndex + inputIndex];
			}

			@Override
			public double getBiasWeigth() {
				return getWeigth(getInputSize());
			}

			@Override
			public void setBiasWeigth(double newVal) {
				setWeigth(getInputSize(), newVal);
			}

			@Override
			public void setWeigth(int inputIndex, double newVal) {
				if (inputIndex < 0) {
					throw new IllegalArgumentException("Input index to low");
				}
				if (inputIndex > prevLayerSize) {
					throw new IllegalArgumentException("Input index to high");
				}
				weigth[weigthIndex + inputIndex] = newVal;
			}

		}
	}

}
