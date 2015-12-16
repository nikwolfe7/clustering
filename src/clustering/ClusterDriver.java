package clustering;

import java.text.DecimalFormat;

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

		runTest("aggregation", 0.47, 0.48, 0.01, metric, aggregation, false); /* good */

		runTest("bridge", 0.42, 0.45, 0.01, metric, bridge, true); /* good */

		runTest("compound", 0.150, 0.155, 0.001, metric, compound, true); /* good */

		runTest("flame", 0.735, 0.735, 0.001, metric, flame, true); /* good */

		runTest("jain", 0.301, 0.310, 0.001, metric, jain, true); /* good */

		runTest("spiral", 0.2, 0.4, 0.1, metric, spiral, true); /* good */

		runTest("two diamonds", 20, 25, 1.0, metric, twoDiamonds, false); /* good */

	}

	private static void runTest(String label, double start, double stop, double step, DistanceMetric metric, ClusterDataSet dataSet, boolean b) {
		System.out.println("\n=============== " + label.toUpperCase() + ": SPECTRAL CLUSTERING ===============");
		double sigma = start;
		DecimalFormat f = new DecimalFormat("#.###");
		while (sigma <= stop) {
			System.out.println("With sigma = " + sigma);
			doSpectralClustering(label + "-sig" + f.format(sigma), new GaussianKernel(sigma), dataSet, b);
			sigma += step;
		}
		System.out.println("\n===================== " + label.toUpperCase() + ": K MEANS =====================");
		doKMeansClustering(label, metric, dataSet);
		System.out.println("\n----------------------------------------------------------\n");
	}

	private static void doKMeansClustering(String label, DistanceMetric metric, ClusterDataSet dataSet) {
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm kMeansClusterer = new KMeansClustering(K, metric);
		kMeansClusterer.setConvergenceCriteria(1.0e-10, 10000);
		kMeansClusterer.initialize(dataSet);
		kMeansClusterer.doClustering();
		kMeansClusterer.printResultsToFile("results-" + label + ".csv"); 
	}

	private static void doSpectralClustering(String label, DistanceMetric metric, ClusterDataSet dataSet, boolean b) {
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm spectralClusterer = new SpectralClustering(K, metric, b);
		spectralClusterer.setConvergenceCriteria(1.0e-10, 10000);
		spectralClusterer.initialize(dataSet);
		spectralClusterer.doClustering();
		spectralClusterer.printResultsToFile("results-" + label + ".csv"); 
	}

}
