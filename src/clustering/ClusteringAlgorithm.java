package clustering;

public interface ClusteringAlgorithm {

  void doClustering(ClusterDataSet dataset);

  void setConvergenceCriteria(double minDiff, int maxIterations);

}
