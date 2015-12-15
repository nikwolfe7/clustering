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
		
//		runTest(0.45, 0.50, 0.01, metric, aggregation);
		
//		runTest(0.000001, 5, 0.00001, metric, bridge);
//		
//		runTest(0.1, 10, 0.1, metric, compound);
//		
//		runTest(0.1, 10, 0.1, metric, flame);
//		
//		runTest(0.1, 10, 0.1, metric, jain);
//		
		runTest(0.2, 0.4, 0.1, metric, spiral);
//		
//		runTest(0.1, 10, 0.1, metric, twoDiamonds);
		
	}
	
	private static void runTest(double start, double stop, double step, DistanceMetric metric, ClusterDataSet dataSet) {
		double val = start;
		while (val <= stop) {
//			doKMeansClustering(metric, dataSet);
			System.out.println("With val = " + val);
			doSpectralClustering(new GaussianKernel(val), dataSet);
			val += step;
		}
	}

	private static void doKMeansClustering(DistanceMetric metric, ClusterDataSet dataSet) {
		System.out.println("\n=========================== K MEANS ===========================\n");
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm kMeansClusterer = new KMeansClustering(K, metric);
		kMeansClusterer.setConvergenceCriteria(1.0e-10, 10000);
		kMeansClusterer.initialize(dataSet);
		kMeansClusterer.doClustering();
		System.out.println("\n======================= END K MEANS ===========================\n");
	}

	private static void doSpectralClustering(DistanceMetric metric, ClusterDataSet dataSet) {
		System.out.println("\n===================== SPECTRAL CLUSTERING ====================\n");
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm spectralClusterer = new SpectralClustering(K, metric);
		spectralClusterer.setConvergenceCriteria(1.0e-10, 10000);
		spectralClusterer.initialize(dataSet);
		spectralClusterer.doClustering();
		System.out.println("\n================== END SPECTRAL CLUSTERING ====================\n");
	}

}
