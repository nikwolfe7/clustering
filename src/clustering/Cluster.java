package clustering;

import java.util.LinkedList;
import java.util.List;

public class Cluster {

	private static int clusterIdGen = 1;
	private List<DataInstance> clusterDatapoints;
	private DistanceMetric distanceMetric;
	private double[] centroid;
	private int clusterId;
	private double splitDeviation = 0.025;

	public Cluster(DistanceMetric distanceMetric) {
		this.clusterId = clusterIdGen++;
		this.distanceMetric = distanceMetric;
		this.clusterDatapoints = new LinkedList<>();
		setCentroid(new double[] { 0 });
	}

	public Cluster(double[] centroid, DistanceMetric distanceMetric) {
		this.clusterId = clusterIdGen++;
		this.distanceMetric = distanceMetric;
		this.clusterDatapoints = new LinkedList<>();
		setCentroid(centroid);
	}

	public int getClusterId() {
		return clusterId;
	}

	public double[] getCentroid() {
		return centroid;
	}
	
	public DistanceMetric getDistanceMetric() {
		return distanceMetric;
	}

	public void setCentroid(double[] centroid) {
		this.centroid = centroid;
	}

	public void addDataInstance(DataInstance instance) {
		clusterDatapoints.add(instance);
	}

	public boolean removeDataInstance(DataInstance instance) {
		return clusterDatapoints.remove(instance);
	}

	public List<DataInstance> getClusterDataInstances() {
		return clusterDatapoints;
	}

	public void drainCluster() {
		clusterDatapoints.clear();
		recalculateCentroid();
	}

	public void combineCluster(Cluster cluster) {
		/* choose the lower valued id */
		int id1, id2;
		id1 = getClusterId();
		id2 = cluster.getClusterId();
		clusterId = id1 < id2 ? id1 : id2;
		for (DataInstance instance : cluster.getClusterDataInstances()) {
			addDataInstance(instance);
		}
		cluster.setCentroid(new double[] { 0 });
		cluster.drainCluster();
		recalculateCentroid();
	}
	
	public Cluster getSplitCluster() {
		double[] cent1, cent2, curr;
		curr = getCentroid();
		cent1 = new double[curr.length];
		cent2 = new double[curr.length];
		/* perturb centroid by 5% */
		for(int i = 0; i < curr.length; i++) {
			cent1[i] = curr[i] + (curr[i] * splitDeviation);
			cent2[i] = curr[i] - (curr[i] * splitDeviation);
		}
		setCentroid(cent1);
		return new Cluster(cent2, getDistanceMetric());
	}

	public void recalculateCentroid() {
		double[] newCentroid = new double[getCentroid().length];
		for (DataInstance instance : getClusterDataInstances()) {

		}
		setCentroid(newCentroid);
	}

	public double getCentroidDistance(DataInstance instance) {
		return distanceMetric.getDistance(getCentroid(), instance.getDataVector());
	}

}
