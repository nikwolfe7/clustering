package clustering;

public class DataInstance {

	private double[] data;
	private double[] label;

	/**
	 * Assumes we have truth values first from left to right, then input value
	 * 
	 * @param dataDimension
	 * @param labelDimension
	 * @param vector
	 */
	public DataInstance(int dataDimension, int labelDimension, double[] vector) {
		this.data = new double[dataDimension];
		this.label = new double[labelDimension];
		System.arraycopy(vector, labelDimension, data, 0, dataDimension);
		System.arraycopy(vector, 0, label, 0, labelDimension);
	}
	
	public void replaceDataVector(double[] vector) {
		data = vector;
	}

	public double[] getDataVector() {
		return data;
	}
	
	public int getDataDimension() {
		return data.length;
	}
	
	public void replaceLabelValue(double[] vector) {
		label = vector;
	}

	public double[] getLabelValue() {
		return label;
	}
	
	public int getLabelValueDimension() {
		return label.length;
	}

}
