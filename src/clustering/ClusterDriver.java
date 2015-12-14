package clustering;

public class ClusterDriver {

	public static void main(String[] args) {
		DistanceMetric metric = new EuclideanDistance();
		ClusterDataSet aggregation = new ClusterDataSet("data/Aggregation.csv");
		ClusterDataSet bridge = new ClusterDataSet("data/Bridge.csv");
		ClusterDataSet compound = new ClusterDataSet("data/Compound.csv");
		ClusterDataSet flame = new ClusterDataSet("data/Flame.csv");
		ClusterDataSet jain = new ClusterDataSet("data/Jain.csv");
		ClusterDataSet spiral = new ClusterDataSet("data/Spiral.csv");
		ClusterDataSet twoDiamonds = new ClusterDataSet("data/TwoDiamonds.csv");
		
		doKMeansClustering(metric, aggregation);
		doSpectralClustering(metric, aggregation);
		
//		doKMeansClustering(metric, bridge);
//		doSpectralClustering(metric, bridge);
//		
//		doKMeansClustering(metric, compound);
//		doSpectralClustering(metric, compound);
//		
//		doKMeansClustering(metric, flame);
//		doSpectralClustering(metric, flame);
//		
//		doKMeansClustering(metric, jain);
//		doSpectralClustering(metric, jain);
//		
//		doKMeansClustering(metric, spiral);
//		doSpectralClustering(metric, spiral);
//		
//		doKMeansClustering(metric, twoDiamonds);
//		doSpectralClustering(metric, twoDiamonds);
	}
	
	private static void doKMeansClustering(DistanceMetric metric, ClusterDataSet dataSet) {
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm kMeansClusterer = new KMeansClustering(K, metric);
		kMeansClusterer.setConvergenceCriteria(1.0e-10, 1000);
		kMeansClusterer.doClustering(dataSet);
	}
	
	private static void doSpectralClustering(DistanceMetric metric, ClusterDataSet dataSet) {
    int K = dataSet.getIdealNumClusters();
    ClusteringAlgorithm spectralClusterer = new SpectralClustering(K, metric);
    spectralClusterer.setConvergenceCriteria(1.0e-10, 1000);
    spectralClusterer.doClustering(dataSet);
  }
	
	

}
