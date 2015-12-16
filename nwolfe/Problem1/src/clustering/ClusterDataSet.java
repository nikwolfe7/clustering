package clustering;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ClusterDataSet {

  private List<DataInstance> data;

  private HashSet<Integer> labels;

  public ClusterDataSet(String fileName) {
    this.data = new LinkedList<>();
    this.labels = new HashSet<Integer>();
    try {
      System.out.println("Reading data values in " + fileName + "...");
      Scanner scn = new Scanner(new File(fileName));
      while (scn.hasNextLine()) {
        String[] arr = scn.nextLine().split(Pattern.quote(","));
        double[] vals = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
          vals[i] = Double.parseDouble(arr[i]);
        }
        DataInstance instance = new DataInstance(2, 1, vals);
        labels.add((int) instance.getLabelValue()[0]);
        data.add(instance);
      }
      scn.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public ClusterDataSet(List<DataInstance> items) {
    this.data = new LinkedList<>();
    this.labels = new HashSet<>();
    for (DataInstance instance : items) {
      labels.add((int) instance.getLabelValue()[0]);
      data.add(instance);
    }
  }

  public int getIdealNumClusters() {
    return labels.size();
  }

  public HashSet<Integer> getLabels() {
    return labels;
  }

  public List<DataInstance> getClusteringData() {
    return data;
  }

}
