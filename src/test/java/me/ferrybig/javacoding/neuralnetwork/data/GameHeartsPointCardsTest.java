package me.ferrybig.javacoding.neuralnetwork.data;

import me.ferrybig.javacoding.neuralnetwork.NeuralTest;
import me.ferrybig.javacoding.neuralnetwork.SupervisedTrainingSet;

/**
 * This tests the ability to learn the points in the game hearts. In the test
 * data, the first 3 bits specify the card, in the order [7, 8, 9, 10, J, Q, K,
 * A], and the last 2 bits specify the type of the card, [diamonds, hearts,
 * clubs, spades]
 *
 * This network needs to remember the amount of points the input card are, and
 * relay that information to the output using only 3 hidden cells.
 *
 * @author Fernando
 */
public class GameHeartsPointCardsTest extends NeuralTest {

	@Override
	protected int[] getLayers() {
		return new int[]{5, 3};
	}

	@Override
	protected float getMomentum() {
		return super.getMomentum() / 10;
	}

	@Override
	protected int getMaxTestIterations() {
		return super.getMaxTestIterations() * 1;
	}

	@Override
	protected double getTargetError() {
		return super.getTargetError();
	}

	@Override
	protected SupervisedTrainingSet[] getTestData() {
		return new SupervisedTrainingSet[]{
			// diamonds

			new SupervisedTrainingSet(new double[]{0, 0, 0, 0, 0}, new double[]{0, 0, 0}), // 7
			new SupervisedTrainingSet(new double[]{1, 0, 0, 0, 0}, new double[]{0, 0, 0}), // 8
			new SupervisedTrainingSet(new double[]{0, 1, 0, 0, 0}, new double[]{0, 0, 0}), // 9
			new SupervisedTrainingSet(new double[]{1, 1, 0, 0, 0}, new double[]{0, 0, 0}), // 10
			new SupervisedTrainingSet(new double[]{0, 0, 1, 0, 0}, new double[]{0, 0, 0}), // J
			new SupervisedTrainingSet(new double[]{1, 0, 1, 0, 0}, new double[]{0, 0, 0}), // Q
			new SupervisedTrainingSet(new double[]{0, 1, 1, 0, 0}, new double[]{0, 0, 0}), // K
			new SupervisedTrainingSet(new double[]{1, 1, 1, 0, 0}, new double[]{0, 0, 0}), // A

			// hearts
			new SupervisedTrainingSet(new double[]{0, 0, 0, 1, 0}, new double[]{1, 0, 0}), // 7
			new SupervisedTrainingSet(new double[]{1, 0, 0, 1, 0}, new double[]{1, 0, 0}), // 8
			new SupervisedTrainingSet(new double[]{0, 1, 0, 1, 0}, new double[]{1, 0, 0}), // 9
			new SupervisedTrainingSet(new double[]{1, 1, 0, 1, 0}, new double[]{1, 0, 0}), // 10
			new SupervisedTrainingSet(new double[]{0, 0, 1, 1, 0}, new double[]{1, 0, 0}), // J
			new SupervisedTrainingSet(new double[]{1, 0, 1, 1, 0}, new double[]{1, 0, 0}), // Q
			new SupervisedTrainingSet(new double[]{0, 1, 1, 1, 0}, new double[]{1, 0, 0}), // K
			new SupervisedTrainingSet(new double[]{1, 1, 1, 1, 0}, new double[]{1, 0, 0}), // A

			// clubs
			new SupervisedTrainingSet(new double[]{0, 0, 0, 0, 1}, new double[]{0, 0, 0}), // 7
			new SupervisedTrainingSet(new double[]{1, 0, 0, 0, 1}, new double[]{0, 0, 0}), // 8
			new SupervisedTrainingSet(new double[]{0, 1, 0, 0, 1}, new double[]{0, 0, 0}), // 9
			new SupervisedTrainingSet(new double[]{1, 1, 0, 0, 1}, new double[]{0, 0, 0}), // 10
			new SupervisedTrainingSet(new double[]{0, 0, 1, 0, 1}, new double[]{0, 1, 0}), // J
			new SupervisedTrainingSet(new double[]{1, 0, 1, 0, 1}, new double[]{0, 0, 0}), // Q
			new SupervisedTrainingSet(new double[]{0, 1, 1, 0, 1}, new double[]{0, 0, 0}), // K
			new SupervisedTrainingSet(new double[]{1, 1, 1, 0, 1}, new double[]{0, 0, 0}), // A

			// Spades:
			new SupervisedTrainingSet(new double[]{0, 0, 0, 1, 1}, new double[]{0, 0, 0}), // 7
			new SupervisedTrainingSet(new double[]{1, 0, 0, 1, 1}, new double[]{0, 0, 0}), // 8
			new SupervisedTrainingSet(new double[]{0, 1, 0, 1, 1}, new double[]{0, 0, 0}), // 9
			new SupervisedTrainingSet(new double[]{1, 1, 0, 1, 1}, new double[]{0, 0, 0}), // 10
			new SupervisedTrainingSet(new double[]{0, 0, 1, 1, 1}, new double[]{0, 0, 0}), // J
			new SupervisedTrainingSet(new double[]{1, 0, 1, 1, 1}, new double[]{1, 0, 1}), // Q
			new SupervisedTrainingSet(new double[]{0, 1, 1, 1, 1}, new double[]{0, 0, 0}), // K
			new SupervisedTrainingSet(new double[]{1, 1, 1, 1, 1}, new double[]{0, 0, 0}), // A
		};
	}

}
