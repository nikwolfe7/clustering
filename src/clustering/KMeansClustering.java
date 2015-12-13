package clustering;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class KMeansClustering {

	private List<Cluster> clusters;
	private DistanceMetric distMetric;
	private double targetCentroidDifference = 1.0e-10;
	private int maxIterations = 100000;
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

	public void doKMeansClustering(ClusterDataSet dataset) {
		System.out.println("Randomly initializing K-means with " + K + " clusters... ");
		List<DataInstance> data = dataset.getClusteringData();
		/* Initialize clusters */
		spreadData(data);
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
			/* Fix dead clusters... */
			for(int i = 0; i < getKClusters().size(); i++) {
				Cluster cluster = getKClusters().get(i);
				if(cluster.getClusterSize() == 0) {
					DataInstance newCentroid;
					newCentroid = getFarthestDataPoint();
					cluster.initializeCentroid(newCentroid);
				}
			}
			/* Update the world... */
			double diff = prevDiff - centroidChange;
			System.out.println("Iteration " + iteration + " | Total Centroid Change: " + centroidChange + " Diff: " + diff);
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
	 * Returns the data point with the largest
	 * minimum distance from any centroid, i.e.
	 * the most "distant" datapoint
	 * 
	 * */
	private DataInstance getFarthestDataPoint() {
		Cluster farCluster = null;
		DataInstance farInstance = null;
		double farDist = 0;
		for(Cluster cluster : getKClusters()) {
		  Iterator<DataInstance> iter = cluster.getDataInstances();
		  while(iter.hasNext()) {
		    DataInstance inst = iter.next();
		    Cluster minDistCluster = cluster;
		    double minDist = Double.POSITIVE_INFINITY;
		    for(Cluster kCluster : getKClusters()) {
		      double dist = kCluster.getCentroidDistance(inst);
		      if(dist < minDist) {
		        minDist = dist;
		        minDistCluster = cluster;
		      }
		    }
		    if(minDist > farDist) {
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
		for(Cluster cluster : getKClusters()) {
			int clusterLabel = cluster.pickClusterLabelFromData();
			System.out.println("Most common label: " + clusterLabel);
			Iterator<DataInstance> iter = cluster.getDataInstances();
			while(iter.hasNext()) {
				DataInstance inst = iter.next();
				int instLabel = (int) inst.getLabelValue()[0];
				cMat[clusterLabel-1][instLabel-1] += 1;
			}
		}
		StringBuilder sb = new StringBuilder("\n");
		for(int i = 0; i < cMat.length; i++) {
			for(int j = 0; j < cMat[0].length; j++) {
				sb.append(cMat[i][j] + "\t");
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
	
	private void spreadData(List<DataInstance> data) {
		//Collections.shuffle(data);
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
