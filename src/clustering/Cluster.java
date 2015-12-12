package clustering;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Cluster {
	
	private List<DataInstance> clusterDatapoints;
	private DataInstance centroid;
	private final int clusterId;

	public Cluster(int id) {
		this.clusterId = id;
		this.clusterDatapoints = new LinkedList<>();
	}
	
	public void addDataInstance(DataInstance instance) {
		clusterDatapoints.add(instance);
	}
	
	public 
	
	
	
}
