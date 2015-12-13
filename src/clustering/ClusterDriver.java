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
		
		doClustering(metric, aggregation);
		doClustering(metric, bridge);
		doClustering(metric, compound);
		doClustering(metric, flame);
		doClustering(metric, jain);
		doClustering(metric, spiral);
		doClustering(metric, twoDiamonds);
	}
	
	private static void doClustering(DistanceMetric metric, ClusterDataSet dataSet) {
		int K = dataSet.getIdealNumClusters();
		KMeansClustering kMeansClusterer = new KMeansClustering(K, metric);
		kMeansClusterer.setConvergenceCriteria(1.0e-10, 1000);
		kMeansClusterer.doKMeansClustering(dataSet);
	}

}
