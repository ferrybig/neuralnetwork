
package me.ferrybig.javacoding.neuralnetwork.error;

import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;

public interface ErrorCalculator {

	double calculate(CompiledNetwork network, double[] outCache, double[] cache);

}
