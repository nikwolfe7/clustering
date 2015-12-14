package clustering;

public interface ClusteringAlgorithm {

	void initialize(ClusterDataSet dataset);

	void doClustering(ClusterDataSet dataset);

	void setConvergenceCriteria(double minDiff, int maxIterations);

}
