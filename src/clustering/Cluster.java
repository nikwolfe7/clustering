package clustering;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Cluster {

	private static int clusterIdGen = 1;
	private List<DataInstance> clusterData;
	private List<DataInstance> updateList;
	private DistanceMetric distanceMetric;
	private double[] centroid;
	private int clusterId;
	private double splitDeviation = 0.025;

	public Cluster(DistanceMetric distanceMetric) {
		this.clusterId = clusterIdGen++;
		this.distanceMetric = distanceMetric;
		this.clusterData = new LinkedList<>();
		setCentroid(null);
	}

	public Cluster(double[] centroid, DistanceMetric distanceMetric) {
		this.clusterId = clusterIdGen++;
		this.distanceMetric = distanceMetric;
		this.clusterData = new LinkedList<>();
		this.updateList = new LinkedList<>();
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

	public void initializeCentroid(DataInstance seed) {
		setCentroid(seed.getDataVector());
	}

	public void addToClusterUpdate(DataInstance instance) {
		updateList.add(instance);
	}

	public void update() {
		Iterator<DataInstance> iter = updateList.iterator();
		while (iter.hasNext()) {
			addDataInstance(iter.next());
			iter.remove();
		}
	}

	public void addDataInstance(DataInstance instance) {
		clusterData.add(instance);
	}

	public boolean removeDataInstance(DataInstance instance) {
		return clusterData.remove(instance);
	}

	public Iterator<DataInstance> getDataInstances() {
		return clusterData.iterator();
	}

	public int getClusterSize() {
		return clusterData.size();
	}

	public void drainCluster() {
		clusterData.clear();
		calculateCentroid();
	}

	public void combineCluster(Cluster cluster) {
		/* choose the lower valued id */
		int id1, id2;
		id1 = getClusterId();
		id2 = cluster.getClusterId();
		clusterId = id1 < id2 ? id1 : id2;
		Iterator<DataInstance> iter = cluster.getDataInstances();
		while (iter.hasNext()) {
			DataInstance instance = iter.next();
			addDataInstance(instance);
			iter.remove();
		}
		/* destroy other cluster */
		cluster = null;
		calculateCentroid();
	}

	public Cluster splitCluster() {
		double[] cent1, cent2, curr;
		curr = getCentroid();
		cent1 = new double[curr.length];
		cent2 = new double[curr.length];
		/* perturb centroid by 5% */
		for (int i = 0; i < curr.length; i++) {
			cent1[i] = curr[i] + (curr[i] * splitDeviation);
			cent2[i] = curr[i] - (curr[i] * splitDeviation);
		}
		setCentroid(cent1);
		return new Cluster(cent2, getDistanceMetric());
	}

	/* returns the amount the centroid changed */
	public double calculateCentroid() {
		update();
		double[] newCentroid = new double[getCentroid().length];
		Iterator<DataInstance> iter = getDataInstances();
		while (iter.hasNext()) {
			DataInstance instance = iter.next();
			for (int i = 0; i < instance.getDataDimension(); i++) {
				newCentroid[i] += instance.getDataVector()[i];
			}
		}
		double divisor = 1 / getClusterSize();
		for (int i = 0; i < newCentroid.length; i++) {
			newCentroid[i] *= divisor;
		}
		double diff = distanceMetric.getDistance(getCentroid(), newCentroid);
		setCentroid(newCentroid);
		return diff;
	}

	public double getCentroidDistance(DataInstance instance) {
		return distanceMetric.getDistance(getCentroid(), instance.getDataVector());
	}

}
