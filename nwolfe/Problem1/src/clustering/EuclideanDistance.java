package clustering;

public class EuclideanDistance implements DistanceMetric {

  @Override
  public double getDistance(DataInstance inst1, DataInstance inst2) {
    return getDistance(inst1.getDataVector(), inst2.getDataVector());
  }

  @Override
  public double getDistance(double[] vec1, double[] vec2) {
    double dist = 0;
    /* shame on you if dimensions aren't the same */
    for (int i = 0; i < vec1.length; i++) {
      double pq = Math.pow((vec1[i] - vec2[i]), 2);
      dist += pq;
    }
    return Math.sqrt(dist);
  }

}
