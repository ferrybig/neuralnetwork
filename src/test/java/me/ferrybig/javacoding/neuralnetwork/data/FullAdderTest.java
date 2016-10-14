
package me.ferrybig.javacoding.neuralnetwork.data;

import me.ferrybig.javacoding.neuralnetwork.NeuralTest;
import me.ferrybig.javacoding.neuralnetwork.SupervisedTrainingSet;

public class FullAdderTest extends NeuralTest {

	@Override
	protected int[] getLayers() {
		return new int[]{3, 4, 4, 2};
	}

	@Override
	protected SupervisedTrainingSet[] getTestData() {
		return new SupervisedTrainingSet[]{
		    new SupervisedTrainingSet(new double[]{0, 0, 0}, new double[]{0, 0}),
		    new SupervisedTrainingSet(new double[]{1, 0, 0}, new double[]{0, 1}),
		    new SupervisedTrainingSet(new double[]{0, 1, 0}, new double[]{0, 1}),
		    new SupervisedTrainingSet(new double[]{1, 1, 0}, new double[]{1, 0}),
		    new SupervisedTrainingSet(new double[]{0, 0, 1}, new double[]{0, 1}),
		    new SupervisedTrainingSet(new double[]{1, 0, 1}, new double[]{1, 0}),
		    new SupervisedTrainingSet(new double[]{0, 1, 1}, new double[]{1, 0}),
		    new SupervisedTrainingSet(new double[]{1, 1, 1}, new double[]{1, 1}),
		};
	}

}
