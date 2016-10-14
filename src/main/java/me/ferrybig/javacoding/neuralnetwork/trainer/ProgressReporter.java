
package me.ferrybig.javacoding.neuralnetwork.trainer;

public interface ProgressReporter {

	public void report(long iteration, double error);

}
