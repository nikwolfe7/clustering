package clustering;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		
		ExecutorService exec = Executors.newCachedThreadPool();

		double val = 0.1;
		while(val < 10) {
//			doKMeansClustering(metric, aggregation);
			System.out.println("With val = " + val);
			doSpectralClustering(new GaussianKernel(val), aggregation);
			val += 0.1;
		}
	
//		doKMeansClustering(metric, aggregation);
////		doSpectralClustering(new GaussianKernel(val), aggregation);
//		
//		doKMeansClustering(metric, bridge);
////		doSpectralClustering(new GaussianKernel(1), bridge);
//
//		doKMeansClustering(metric, compound);
////		doSpectralClustering(new GaussianKernel(5), compound);
//
//		doKMeansClustering(metric, flame);
////		doSpectralClustering(new GaussianKernel(5), flame);
//
//		doKMeansClustering(metric, jain);
////		doSpectralClustering(new GaussianKernel(5), jain);
//
//		doKMeansClustering(metric, spiral);
////		doSpectralClustering(new GaussianKernel(5), spiral);
//
//		doKMeansClustering(metric, twoDiamonds);
////		doSpectralClustering(new GaussianKernel(2), twoDiamonds);
	}

	private static void doKMeansClustering(DistanceMetric metric, ClusterDataSet dataSet) {
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm kMeansClusterer = new KMeansClustering(K, metric);
		kMeansClusterer.setConvergenceCriteria(1.0e-10, 10000);
		kMeansClusterer.initialize(dataSet);
		kMeansClusterer.doClustering();
	}

	private static void doSpectralClustering(DistanceMetric metric, ClusterDataSet dataSet) {
		int K = dataSet.getIdealNumClusters();
		ClusteringAlgorithm spectralClusterer = new SpectralClustering(K, metric);
		spectralClusterer.setConvergenceCriteria(1.0e-10, 10000);
		spectralClusterer.initialize(dataSet);
		spectralClusterer.doClustering();
	}

}
