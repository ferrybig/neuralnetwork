
package me.ferrybig.javacoding.neuralnetwork.data;

import me.ferrybig.javacoding.neuralnetwork.NeuralTest;
import me.ferrybig.javacoding.neuralnetwork.SupervisedTrainingSet;

public class TrueGateTest  extends NeuralTest {

	@Override
	protected int[] getLayers() {
		return new int[]{1,1};
	}

	@Override
	protected SupervisedTrainingSet[] getTestData() {
		return new SupervisedTrainingSet[]{
			new SupervisedTrainingSet(new double[]{1}, new double[]{1}),
			new SupervisedTrainingSet(new double[]{0}, new double[]{1}),
		};
	}

}
