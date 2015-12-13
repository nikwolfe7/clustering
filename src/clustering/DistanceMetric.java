package clustering;

public interface DistanceMetric {

  public double getDistance(double[] inst1, double[] inst2);

  double getDistance(DataInstance inst1, DataInstance inst2); 
  
}
