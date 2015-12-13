package clustering;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class KMeansClustering {

	private List<Cluster> clusters;
	private DistanceMetric distMetric;
	private double targetCentroidDifference = 1.0e-2;
	private int maxIterations = 1000;
	private int K;

	public KMeansClustering(int K, DistanceMetric metric) {
		this.K = K;
		this.distMetric = metric;
		this.clusters = new LinkedList<>();
		for (int i = 0; i < K; i++) {
			clusters.add(new Cluster(metric));
		}
	}

	public DistanceMetric getDistanceMetric() {
		return distMetric;
	}

	public void setConvergenceCriteria(double minDiff, int maxIterations) {
		this.targetCentroidDifference = minDiff;
		this.maxIterations = maxIterations;
	}

	public void doKMeansClustering(ClusterDataset dataset) {
		System.out.println("Randomly initializing K-means with " + K + " clusters... ");
		List<DataInstance> data = dataset.getClusteringData();
		/* Initialize clusters */
		spreadData(data);
		/* Calculate initial centroids... */
		calcCentroids();
		/* Begin iterating */
		int iteration = 1;
		while (true) {
			/* Reassign Clusters */
			for (int i = 0; i < getKClusters().size(); i++) {
				/* For each cluster... */
				Cluster cluster = getKClusters().get(i);
				/* Best cluster assignment pointer */
				Cluster bestCluster = cluster;
				Iterator<DataInstance> iter = cluster.getDataInstances();
				/* For each data point in the cluster... */
				while (iter.hasNext()) {
					DataInstance instance = iter.next();
					/* Calculate the distance to the current cluster centroid... */
					double minDistance = cluster.getCentroidDistance(instance);
					for (int j = 0; j < getKClusters().size(); j++) {
						/* Now check the other centroids */
						if (i != j) {
							Cluster kCluster = getKClusters().get(j);
							/* Check distance to other centroid... */
							double dist = kCluster.getCentroidDistance(instance);
							/* Check if better... */
							if (dist < minDistance) {
								minDistance = dist;
								bestCluster = kCluster;
							}
						}
					}
					/* Reassign cluster if necessary */
					if (bestCluster != cluster) {
						iter.remove();
						bestCluster.addToClusterUpdate(instance);
					}
				}
			}
			/* Recalculate Centroids */
			double centroidChange = calcCentroids();
			System.out.println("Iteration " + iteration + " | Total Centroid Change: " + centroidChange);
			/* Check stopping criteria... */
			if (iteration++ > maxIterations)
				break;
			if (centroidChange < targetCentroidDifference) 
				break;
		}
		System.out.println("K-Means Clustering Complete! ");
	}

	private void spreadData(List<DataInstance> data) {
		Collections.shuffle(data);
		int i = 0;
		for (DataInstance instance : data) {
			Cluster cluster = getKClusters().get(i);
			cluster.addDataInstance(instance);
			if (cluster.getCentroid() == null)
				cluster.initializeCentroid(instance);
			i = (i + 1) % K;
		}
	}

	private double calcCentroids() {
		double centroidChange = 0;
		for (Cluster cluster : getKClusters()) {
			centroidChange += cluster.calculateCentroid();
		}
		return centroidChange;
	}

	public List<Cluster> getKClusters() {
		return clusters;
	}

}
