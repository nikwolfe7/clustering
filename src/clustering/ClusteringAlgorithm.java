package clustering;

import java.util.List;

public interface ClusteringAlgorithm {

	void initialize(ClusterDataSet dataset);

	void setConvergenceCriteria(double minDiff, int maxIterations);

	void doClustering();
	
	void printResultsToFile(String filename);

	List<Cluster> getKClusters();

}
