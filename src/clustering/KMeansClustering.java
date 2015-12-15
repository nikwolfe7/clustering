package clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class KMeansClustering implements ClusteringAlgorithm {

	private List<Cluster> clusters;
	
	private List<DataInstance> clusterData;

	private DistanceMetric distMetric;

	private boolean outputOn;

	private double targetCentroidDifference = 1.0e-10;

	private int maxIterations = 100;

	private int K;
	
	public KMeansClustering(int K, DistanceMetric metric) {
		this.K = K;
		this.distMetric = metric;
		this.clusters = new LinkedList<>();
		this.clusterData = new ArrayList<>();
		for (int i = 0; i < K; i++) {
			Cluster c = new Cluster(metric);
			c.setClusterId(i);
			clusters.add(c);
		}
		this.outputOn = false;
	}

	public DistanceMetric getDistanceMetric() {
		return distMetric;
	}

	@Override
	public void setConvergenceCriteria(double minDiff, int maxIterations) {
		this.targetCentroidDifference = minDiff;
		this.maxIterations = maxIterations;
	}

	@Override
	public void initialize(ClusterDataSet dataset) {
		System.out.println("Randomly initializing K-means with " + K + " clusters... ");
		List<DataInstance> data = dataset.getClusteringData();
		/* Add data to our set... */
		for(DataInstance instance : data) {
		  clusterData.add(instance);
		}
		/* Initialize clusters */
		spreadData();
	}

	@Override
	public void doClustering() {
		/* Calculate initial centroids... */
		calcCentroids();
		/* Begin iterating */
		int iteration = 1;
		double prevDiff = Double.POSITIVE_INFINITY;
		/* START */
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
					/*
					 * Calculate the distance to the current cluster centroid...
					 */
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
			/* Fix dead clusters... */
			for (int i = 0; i < getKClusters().size(); i++) {
				Cluster cluster = getKClusters().get(i);
				if (cluster.getClusterSize() == 0) {
					DataInstance newCentroid;
					newCentroid = getFarthestDataPoint();
					cluster.initializeCentroid(newCentroid);
				}
			}
			/* Update the world... */
			double diff = prevDiff - centroidChange;
			if (outputOn)
				System.out.println(
						"Iteration " + iteration + " | Total Centroid Change: " + centroidChange + " Diff: " + diff);
			/* Update Diff */
			prevDiff = centroidChange;
			/* Check stopping criteria... */
			if (maxIterations > 0 && iteration++ >= maxIterations)
				break;
			if (targetCentroidDifference > 0 && Math.abs(diff) <= targetCentroidDifference)
				break;
		}
		System.out.println("K-Means Clustering Complete! ");
		calculateAccuracy();
	}

	/*
	 * Returns the data point with the largest minimum distance from any
	 * centroid, i.e. the most "distant" datapoint
	 */
	private DataInstance getFarthestDataPoint() {
		Cluster farCluster = null;
		DataInstance farInstance = null;
		double farDist = 0;
		for (Cluster cluster : getKClusters()) {
			Iterator<DataInstance> iter = cluster.getDataInstances();
			while (iter.hasNext()) {
				DataInstance inst = iter.next();
				Cluster minDistCluster = cluster;
				double minDist = Double.POSITIVE_INFINITY;
				for (Cluster kCluster : getKClusters()) {
					double dist = kCluster.getCentroidDistance(inst);
					if (dist < minDist) {
						minDist = dist;
						minDistCluster = cluster;
					}
				}
				if (minDist > farDist) {
					farDist = minDist;
					farInstance = inst;
					farCluster = minDistCluster;
				}
			}
		}
		farCluster.removeDataInstance(farInstance);
		return farInstance;
	}

	private void calculateAccuracy() {
		int[][] cMat = new int[K][K];
		for (Cluster cluster : getKClusters()) {
			int clusterLabel = cluster.getClusterId();
			if (outputOn)
				System.out.println("Most common label: " + clusterLabel);
			Iterator<DataInstance> iter = cluster.getDataInstances();
			while (iter.hasNext()) {
				DataInstance inst = iter.next();
				int instLabel = (int) inst.getLabelValue()[0];
				cMat[clusterLabel][instLabel-1] += 1;
			}
		}
		StringBuilder sb = new StringBuilder("\nlabel\t");
		String s1, s2;
		s1 = "";
		s2 = "";
		for (int k = 0; k < K; k++) {
			s1 += "" + (k + 1) + "\t";
			s2 += "________";
		}
		sb.append(s1 + "\n" + s2 + "___\n");
		for (int i = 0; i < cMat.length; i++) {
			sb.append((i + 1) + " | \t");
			for (int j = 0; j < cMat[0].length; j++) {
				sb.append(cMat[i][j] + "\t");
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}

	private void spreadData() {
		Collections.shuffle(clusterData);
		int i = 0;
		for (DataInstance instance : clusterData) {
			Cluster cluster = getKClusters().get(i);
			if (cluster.getCentroid() == null)
				cluster.initializeCentroid(instance);
			else
				cluster.addDataInstance(instance);
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
