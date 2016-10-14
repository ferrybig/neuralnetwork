package me.ferrybig.javacoding.neuralnetwork.trainer;

import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import me.ferrybig.javacoding.neuralnetwork.error.ErrorCalculator;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import me.ferrybig.javacoding.neuralnetwork.Cell;
import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;
import me.ferrybig.javacoding.neuralnetwork.Layer;

public class RandomNetworkTrainer implements NetworkTrainer {

	private final Random random;
	private final float momentum;
	private final double maxError;
	private final long maxIterations;

	public RandomNetworkTrainer(Random random, float momentum, double maxError, long maxIterations) {
		this.random = random;
		this.momentum = momentum;
		this.maxError = maxError;
		this.maxIterations = maxIterations;
	}

	@Override
	public boolean train(CompiledNetwork network, ErrorCalculator error, ProgressReporter progress) {
		// Make variable local final for code optimalizer
		final double[] cache = new double[network.getMaxCacheSize()];
		final double[] out = new double[network.getOutputSize()];
		final double[] weigth = network.getWeigth();
		final double[] weigthClone = weigth.clone();
		final double adjustedmomentum = momentum;
		final double localMaxError = maxError;
		final long localMaxIterations = maxIterations;
		final int weigthLength = weigth.length;
		final Random localRandom = random;

		double err = error.calculate(network, out, cache);
		
		for (long i = 0; i < localMaxIterations; i++) {
			if (i % 10 == 0) {
				progress.report(i, err);
			}
			if (err <= localMaxError) {
				return true;
			}
			boolean hasChanged = false;
			
			do {
				for (int j = 0; j < weigthLength; j++) {
					if (localRandom.nextFloat() < adjustedmomentum) {
						hasChanged = true;
						double offsetChange = 2 * localRandom.nextDouble() - 1;
						double factorChange = Math.pow(10, localRandom.nextDouble());
						weigth[j] += offsetChange * factorChange;
						//System.out.println("Changed cell " + j + " to "
						//		+ weigth[j] + " from " + weigthClone[j]);
					}
				}
			} while (!hasChanged);
			double newerr = error.calculate(network, out, cache);

			if (newerr <= err) {
				//System.out.println("Improved generation!");
				System.arraycopy(weigth, 0, weigthClone, 0, weigthLength);
				err = newerr;
				continue;
			}
			//System.out.println("Failed improvimg generation!");
			System.arraycopy(weigthClone, 0, weigth, 0, weigthLength);
		}
		return false;
	}

	private void optimalizeNetwork(CompiledNetwork network) {
		network.getLayers().stream().skip(1).map(Layer::getCells)
				.flatMap(Collection::stream).forEach(this::optimalizeCell);
	}

	private void optimalizeCell(Cell cell) {
		DoubleStream outputs = DoubleStream.concat(IntStream.range(0, cell.getInputSize())
				.mapToDouble(cell::getWeigth),
				DoubleStream.of(cell.getBiasWeigth()));
		DoubleSummaryStatistics stats = outputs.summaryStatistics();
		if(stats.getCount() > 1 && (stats.getMin() > 3 || stats.getMax() < 3)) {
			// this cell is always positive or negative
			IntStream.range(0, cell.getInputSize()).forEach(i->cell.setWeigth(i, 0));
			cell.setBiasWeigth(stats.getSum());
		}
	}

}
