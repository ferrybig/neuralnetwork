package me.ferrybig.javacoding.neuralnetwork;

import me.ferrybig.javacoding.neuralnetwork.trainer.RandomNetworkTrainer;
import me.ferrybig.javacoding.neuralnetwork.error.SupervisedErrorCalculator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public abstract class NeuralTest {

	protected abstract int[] getLayers();

	protected Random[] getRandomInstances() {
		return new Random[]{new Random(1234), new Random("ferrybig".hashCode()),
			new Random(4321), new Random("neural".hashCode()), new Random(1234885678912L)};
	}

	protected abstract SupervisedTrainingSet[] getTestData();

	protected CompiledNetwork constructNetwork() {
		return new CompiledNetwork(getLayers());
	}

	protected int getMaxTestIterations() {
		return 10_000;
	}

	protected double getTargetError() {
		return 0.0001;
	}

	protected float getMomentum() {
		return 0.1f;
	}

	@Test
	public void testNetwork() {
		CompiledNetwork best = null;
		int successCount = 0;
		int failedCount = 0;
		double[] out = null;
		double[] cache = null;
		for (Random random : getRandomInstances()) {
			CompiledNetwork network = constructNetwork();

			if(out == null) {
				out = new double[network.getOutputSize()];
				cache = new double[network.getMinCacheSize()];
			}
			RandomNetworkTrainer trainer = new RandomNetworkTrainer(random, getMomentum(),
					getTargetError(), getMaxTestIterations());

			List<SupervisedTrainingSet> training = new ArrayList<>(Arrays.asList(getTestData()));
			SupervisedErrorCalculator errorCalculator = new SupervisedErrorCalculator(training);

			boolean success = trainer.train(network, errorCalculator, (i, err) -> {
				if ((i % (Math.pow(10, (int) Math.log10(i)) * 5) == 0) || (i == 0)) {
					System.out.println("Iteration " + i + " error " + err);
				}
			});

			if (success) {
				System.out.println(getClass().getSimpleName() + "> Succeded iteration");
				best = network;
				successCount++;
			} else {
				System.out.println(getClass().getSimpleName() + "> Failed iteration");
				failedCount++;
			}
			System.out.println(getClass().getSimpleName() + "> Final error: " +
					errorCalculator.calculate(network, out, cache));

		}
		if (best == null) {
			Assert.fail("No solution found for the problem " + getClass().getSimpleName());
			return;
		}
		for (SupervisedTrainingSet set : getTestData()) {
			best.resolveNetworkSequential(set.in, out, cache);
			System.err.println(this.getClass().getSimpleName() + "> " + printArray(set.in) + " --> " + printArray(out));
		}
		System.out.println(best.debug());
		System.err.println(this.getClass().getSimpleName() + "> Succeeded " + successCount + " times, failed " + failedCount + " times!");
	}

	private static String printArray(double[] arr) {
		return Arrays.stream(arr).mapToObj(d -> String.format("%.4f", d)).collect(Collectors.joining(", ", "[", "]"));
	}
}
