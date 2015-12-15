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
		
		runTest(0.44, 0.48, 0.01, metric, aggregation, false); /* good */
		
		runTest(0.42, 0.45, 0.001, metric, bridge, true); /* good */
		
		runTest(0.150, 0.155, 0.001, metric, compound, true); /* good */
		
		runTest(0.74, 0.75, 0.005, metric, flame, true); /* good */
		
		runTest(0.304, 0.309, 0.001, metric, jain, true); /* good */
		
		runTest(0.2, 0.4, 0.1, metric, spiral, true); /* good */
		
		runTest(0.08, 0.08, 0.01, metric, twoDiamonds, true); /* good */
		
	}
	
	private static void runTest(double start, double stop, double step, DistanceMetric metric, ClusterDataSet dataSet, boolean b) {
		double sigma = start;
		while (sigma <= stop) {
			System.out.println("\n========================== K MEANS ===========================");
			doKMeansClustering(metric, dataSet);
			System.out.println("\n===================== SPECTRAL CLUSTERING ====================");
			System.out.println("With sigma = " + sigma);
			doSpectralClustering(new GaussianKernel(sigma), dataSet, b);
			sigma += step;
		}
	}

	private static void doKMeansClustering(DistanceMetric metric, ClusterDataSet dataSet) {
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm kMeansClusterer = new KMeansClustering(K, metric);
		kMeansClusterer.setConvergenceCriteria(1.0e-10, 10000);
		kMeansClusterer.initialize(dataSet);
		kMeansClusterer.doClustering();
		
	}

	private static void doSpectralClustering(DistanceMetric metric, ClusterDataSet dataSet, boolean b) {
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm spectralClusterer = new SpectralClustering(K, metric, b);
		spectralClusterer.setConvergenceCriteria(1.0e-10, 10000);
		spectralClusterer.initialize(dataSet);
		spectralClusterer.doClustering();
	}

}
