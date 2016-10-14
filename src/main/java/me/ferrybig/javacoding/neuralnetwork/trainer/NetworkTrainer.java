
package me.ferrybig.javacoding.neuralnetwork.trainer;

import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;
import me.ferrybig.javacoding.neuralnetwork.error.ErrorCalculator;

public interface NetworkTrainer {

	boolean train(CompiledNetwork network, ErrorCalculator error, ProgressReporter progress);

}
