package me.ferrybig.javacoding.neuralnetwork;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Fernando
 */
public class CompiledNetworkTest {

	public CompiledNetworkTest() {
	}

	@Test
	public void testSequentionalCalculationMatchesWithMinAndMaxCacheDualLayer() {
		CompiledNetwork network = new CompiledNetwork(new double[]{1, 2, 3, 4}, new int[]{1, 1, 1});
		double[] smallCache = new double[network.getMinCacheSize()];
		double[] bigCache = new double[network.getMaxCacheSize()];
		double[] in = new double[]{1};
		double[] outSmall = new double[]{1};
		double[] outBig = new double[]{1};
		network.resolveNetworkSequential(in, outSmall, smallCache);
		network.resolveNetworkSequential(in, outBig, bigCache);

		Assert.assertTrue(Arrays.toString(outSmall) + " does not match " + Arrays.toString(outBig),
				Arrays.equals(outSmall, outBig));
	}

	@Test
	public void debugWorksWithoutExceptions() {
		CompiledNetwork network = new CompiledNetwork(new int[]{10, 10, 10, 10, 10});
		network.debug();
	}
}
