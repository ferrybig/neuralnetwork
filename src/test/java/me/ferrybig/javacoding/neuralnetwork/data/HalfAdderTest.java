
package me.ferrybig.javacoding.neuralnetwork.data;

import me.ferrybig.javacoding.neuralnetwork.NeuralTest;
import me.ferrybig.javacoding.neuralnetwork.SupervisedTrainingSet;

public class HalfAdderTest extends NeuralTest {

	@Override
	protected int[] getLayers() {
		return new int[]{2,3,2};
	}

	@Override
	protected SupervisedTrainingSet[] getTestData() {
		return new SupervisedTrainingSet[]{
			new SupervisedTrainingSet(new double[]{0,0}, new double[]{0,0}),
			new SupervisedTrainingSet(new double[]{1,0}, new double[]{0,1}),
			new SupervisedTrainingSet(new double[]{0,1}, new double[]{0,1}),
			new SupervisedTrainingSet(new double[]{1,1}, new double[]{1,0}),
		};
	}

}
