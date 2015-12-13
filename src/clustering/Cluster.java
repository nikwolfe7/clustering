package clustering;

import java.util.LinkedList;
import java.util.List;

public class Cluster {
	
	private List<DataInstance> clusterDatapoints;
	private DistanceMetric distanceMetric;
	private double[] centroid;
	private int clusterId;

	public Cluster(int id, DistanceMetric distanceMetric) {
		this.clusterId = id;
		this.distanceMetric = distanceMetric;
		this.clusterDatapoints = new LinkedList<>();
	}
	
	public int getClusterId() {
	  return clusterId;
	}
	
	public double[] getCentroid() {
	  return centroid;
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
	
	public List<DataInstance> getClusterDatapoints() {
	  return clusterDatapoints;
	}
	
	public void drainCluster() {
	  clusterDatapoints.clear();
	}
	
	public void combineCluster(Cluster cluster) {
	  /* choose the lower valued id */
	  int id1, id2;
	  id1 = getClusterId();
	  id2 = cluster.getClusterId();
	  clusterId = id1 < id2 ? id1 : id2;
	  for(DataInstance instance : cluster.getClusterDatapoints()) {
	    addDataInstance(instance);
	  }
	  cluster.drainCluster();
	  recalculateCentroid();
	}

  public void recalculateCentroid() {
    double[] newCentroid = new double[]{};
    double minDist = Double.POSITIVE_INFINITY;
    for(int i = 0; i < clusterDatapoints.size(); i++) {
      double ijDistance = 0;
      DataInstance inst_i = clusterDatapoints.get(i);
      for(int j = 0; j < clusterDatapoints.size(); j++) {
        if(i != j) {
          DataInstance inst_j = clusterDatapoints.get(j);
          ijDistance += getDistance(inst_i, inst_j);
        }
      }
      if(ijDistance <= minDist) {
        minDist = ijDistance;
        newCentroid = inst_i.getDataVector();
      }
    }
    setCentroid(newCentroid);
  }
  
  private double getDistance(DataInstance inst1, DataInstance inst2) {
    return distanceMetric.getDistance(inst1, inst2);
  }
	
  public double getCentroidDistance(DataInstance instance) {
    return distanceMetric.getDistance(centroid, instance.getDataVector());
  }
 	
	
	
}
