
package me.ferrybig.javacoding.neuralnetwork;

public class Validaters {
	private void rangeCheckForArray(Object[] array, int fromIndex, int length) {
		if (fromIndex < 0) // Negative input
		{
			throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
		}
		if (fromIndex + length > array.length) // Larger than array
		{
			throw new IndexOutOfBoundsException("length = " + length + ", array.length = "
					+ array.length + ", fromIndex = " + fromIndex);
		}
		if (fromIndex > fromIndex + length) // Overflow
		{
			throw new IllegalArgumentException("length = " + length + ", array.length = "
					+ array.length + ", fromIndex = " + fromIndex);
		}
	}

	private void rangeCheckForArray(double[] array, int fromIndex, int length) {
		if (fromIndex < 0) // Negative input
		{
			throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
		}
		if (fromIndex + length > array.length) // Larger than array
		{
			throw new IndexOutOfBoundsException("length = " + length + ", array.length = "
					+ array.length + ", fromIndex = " + fromIndex);
		}
		if (fromIndex > fromIndex + length) // Overflow
		{
			throw new IllegalArgumentException("length = " + length + ", array.length = "
					+ array.length + ", fromIndex = " + fromIndex);
		}
	}
}
