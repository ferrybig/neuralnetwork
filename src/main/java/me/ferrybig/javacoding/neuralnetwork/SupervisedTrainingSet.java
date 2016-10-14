
package me.ferrybig.javacoding.neuralnetwork;

import java.util.Arrays;

public class SupervisedTrainingSet extends UnsupervisedTrainingSet {
	public double[] out;

	public SupervisedTrainingSet(double[] in, double[] out) {
		super(in);
		this.out = out;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Arrays.hashCode(this.in);
		hash = 31 * hash + Arrays.hashCode(this.out);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SupervisedTrainingSet other = (SupervisedTrainingSet) obj;
		if (!Arrays.equals(this.in, other.in)) {
			return false;
		}
		if (!Arrays.equals(this.out, other.out)) {
			return false;
		}
		return true;
	}

	public double[] getOut() {
		return out;
	}

	public void setOut(double[] out) {
		this.out = out;
	}


	@Override
	public String toString() {
		return "TrainingSet{" + "in=" + Arrays.toString(in) + ", out=" + Arrays.toString(out) + '}';
	}

}
