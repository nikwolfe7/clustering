package clustering;

public interface ClusteringAlgorithm {

	void initialize(ClusterDataSet dataset);

	void setConvergenceCriteria(double minDiff, int maxIterations);

	void doClustering();
	
	void printResultsToFile(String filename);

}
