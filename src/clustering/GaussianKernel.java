package clustering;

public class GaussianKernel implements DistanceMetric {

	private double sigma;
	private DistanceMetric euclideanDist;
	
	public GaussianKernel(double sig) {
		this.sigma = sig;
		this.euclideanDist = new EuclideanDistance();
	}

	@Override
	public double getDistance(double[] inst1, double[] inst2) {
		double dist = euclideanDist.getDistance(inst1, inst2);
		double denom = 2 * Math.pow(sigma, 2);
		return Math.exp(dist/denom);
	}

	@Override
	public double getDistance(DataInstance inst1, DataInstance inst2) {
		return getDistance(inst1.getDataVector(), inst2.getDataVector());
	}

}
