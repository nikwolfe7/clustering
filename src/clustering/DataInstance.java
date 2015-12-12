package clustering;

public class DataInstance {

	private double[] input;
	private double[] output;

	/**
	 * Assumes we have truth values first from left to right, then input value
	 * 
	 * @param inputDimension
	 * @param outputDimension
	 * @param vector
	 */
	public DataInstance(int inputDimension, int outputDimension, double[] vector) {
		this.input = new double[inputDimension];
		this.output = new double[outputDimension];
		System.arraycopy(vector, outputDimension, input, 0, inputDimension);
		System.arraycopy(vector, 0, output, 0, outputDimension);
	}
	
	public void replaceInputVector(double[] vector) {
		input = vector;
	}

	public double[] getInputVector() {
		return input;
	}
	
	public int getInputDimension() {
		return input.length;
	}
	
	public void replaceOutputTruthValue(double[] vector) {
		output = vector;
	}

	public double[] getOutputTruthValue() {
		return output;
	}
	
	public int getOutputDimension() {
		return output.length;
	}

}
