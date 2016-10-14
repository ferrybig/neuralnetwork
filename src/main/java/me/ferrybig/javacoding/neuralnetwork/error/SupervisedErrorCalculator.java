
package me.ferrybig.javacoding.neuralnetwork.error;

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.stream.IntStream;
import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;
import me.ferrybig.javacoding.neuralnetwork.SupervisedTrainingSet;

public final class SupervisedErrorCalculator implements ErrorCalculator  {

	private final Collection<SupervisedTrainingSet> training;
	private final int dataSize;
	private final int errorAdjustment;

	public SupervisedErrorCalculator(Collection<SupervisedTrainingSet> training) {
		this.training = training;
		this.dataSize = training.size();
		this.errorAdjustment = Math.max(training.isEmpty() ? 1 : training.size()
				* training.iterator().next().in.length, 1);
	}

	@Override
	public double calculate(CompiledNetwork network, double[] outCache, double[] cache) {
		if (training instanceof RandomAccess && training instanceof List<?>) {
			double error = 0;
			int outCacheLength = outCache.length;
			List<SupervisedTrainingSet> dataList = (List<SupervisedTrainingSet>) training;
			for (int i = 0; i < dataSize; i++) {
				SupervisedTrainingSet set = dataList.get(i);
				network.resolveNetworkSequential(set.in, outCache, cache);
				for (int j = 0; j < outCacheLength; j++) {
					error += Math.abs(outCache[j] - set.out[j]);
				}
			}
			return error / errorAdjustment;
		} else {
			return training.stream().flatMapToDouble(set -> {
				network.resolveNetworkSequential(set.in, outCache, cache);
				return IntStream.range(0, outCache.length)
						.mapToDouble(j -> Math.abs(outCache[j] - set.out[j]));
			}).sum() / errorAdjustment;
		}
	}

}
