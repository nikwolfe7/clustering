package clustering;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class SpectralClustering implements ClusteringAlgorithm {

	private int K;
	private DistanceMetric distMetric;
	private RealMatrix affinityMatrix;

	public SpectralClustering(int k, DistanceMetric metric) {
		this.K = k;
		this.distMetric = metric;
		this.affinityMatrix = new Array2DRowRealMatrix();
	}

	@Override
	public void doClustering(ClusterDataSet dataset) {
		/* Construct Affinity Matrix */

	}

	@Override
	public void setConvergenceCriteria(double minDiff, int maxIterations) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(ClusterDataSet dataset) {
		// TODO Auto-generated method stub

	}

}
