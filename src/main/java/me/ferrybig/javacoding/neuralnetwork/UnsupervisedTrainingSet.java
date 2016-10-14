
package me.ferrybig.javacoding.neuralnetwork;

import java.util.Arrays;

public class UnsupervisedTrainingSet {

	public UnsupervisedTrainingSet(double[] in) {
		this.in = in;
	}

	public double[] in;

	public double[] getIn() {
		return in;
	}

	public void setIn(double[] in) {
		this.in = in;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 41 * hash + Arrays.hashCode(this.in);
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
		final UnsupervisedTrainingSet other = (UnsupervisedTrainingSet) obj;
		if (!Arrays.equals(this.in, other.in)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UnsupervisedTrainingSet{" + "out=" + Arrays.toString(in) + '}';
	}

}
