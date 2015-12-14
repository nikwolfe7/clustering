package clustering;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class SpectralClustering implements ClusteringAlgorithm {

  private int K;

  private DistanceMetric distMetric;

  private List<DataInstance> clusterData;

  private RealMatrix affinityMatrix;
  
  private RealMatrix diagonalMatrix;
  
  private RealMatrix laplacianMatrix;
  
  private ClusteringAlgorithm algo;
  
  private double minDiff = 1.0e-10;
  
  private int maxIterations = 100;

  private boolean outputOn;

  public SpectralClustering(int k, DistanceMetric metric) {
    this.K = k;
    this.distMetric = metric;
    this.clusterData = new ArrayList<>();
    this.affinityMatrix = new Array2DRowRealMatrix();
    this.diagonalMatrix = new Array2DRowRealMatrix();
    this.laplacianMatrix = new Array2DRowRealMatrix();
    /* Safe default... */
    this.algo = new KMeansClustering(k, new EuclideanDistance());
    this.outputOn = false;
  }
  
  public void setClusteringAlgorithm(ClusteringAlgorithm algo) {
    this.algo = algo;
  }

  @Override
  public void setConvergenceCriteria(double minDiff, int maxIterations) {
    this.minDiff = minDiff;
    this.maxIterations = maxIterations;
    algo.setConvergenceCriteria(minDiff, maxIterations);
  }

  @Override
  public void initialize(ClusterDataSet dataset) {
    System.out.println("Initializing spectral clustering with " + K + " clusters...");
    for(DataInstance instance : dataset.getClusteringData()) {
      clusterData.add(instance);
    }
    calculateAffinityMatrix();
    calculateDiagonalMatrix();
    calculateLaplacianMatrix();
  }
  
  private void calculateAffinityMatrix() {
    System.out.println("Computing Affinity matrix...");
    int size = clusterData.size();
    /* initialize matrix */
    affinityMatrix = new Array2DRowRealMatrix(size, size);
    for(int i = 0; i < affinityMatrix.getRowDimension(); ++i) {
      DataInstance a1 = clusterData.get(i);
      for(int j = 0; j < affinityMatrix.getColumnDimension(); ++j) {
        DataInstance a2 = clusterData.get(j);
        double distance = distMetric.getDistance(a1, a2);
        affinityMatrix.setEntry(i, j, distance);
      }
    }
    if(outputOn)
      System.out.println(printMatrix(affinityMatrix));
  }

  private void calculateDiagonalMatrix() {
    System.out.println("Computing Diagonal values from Affinity matrix...");
    int size = clusterData.size();
    /* initialize matrix */
    diagonalMatrix = new DiagonalMatrix(size);
    for(int i = 0; i < affinityMatrix.getRowDimension(); i++) {
      double[] row = affinityMatrix.getRow(i);
      double sum = sum(row);
      diagonalMatrix.setEntry(i, i, sum);
    }
    if(outputOn)
      System.out.println(printMatrix(diagonalMatrix));
  }
  
  private void calculateLaplacianMatrix() {
    System.out.println("Computing Laplacian matrix from Diagonal matrix...");
    int size = clusterData.size();
    /* initialize matrix */
    RealMatrix A = affinityMatrix;
    RealMatrix D = diagonalInverseSquareRoot(diagonalMatrix);
    laplacianMatrix = diagonalMultiply(D, A, D);
    if(outputOn)
      System.out.println(printMatrix(laplacianMatrix));
  }
  
  private RealMatrix diagonalMultiply(RealMatrix... matrices) {
    RealMatrix result = (matrices.length > 0) ? matrices[0].copy() : null;
    for(int i = 1; i < matrices.length; i++) {
      RealMatrix M = matrices[i];
      for(int j = 0; j < result.getRowDimension(); j++) {
        double product = result.getEntry(j, j) * M.getEntry(j, j);
        result.setEntry(j, j, product);
      }
    }
    return result;
  }
  
  
  private RealMatrix diagonalInverseSquareRoot(RealMatrix matrix) {
    for(int i = 0; i < matrix.getRowDimension(); i++) {
        double d = matrix.getEntry(i, i);
        d = 1.0 / Math.sqrt(d);
        matrix.setEntry(i, i, d);
    }
    return matrix;
  }

  private double sum(double... arr) {
    double sum = 0;
    for(double d : arr)
      sum += d;
    return sum;
  }
  
  private String printMatrix(RealMatrix matrix) {
    StringBuilder sb = new StringBuilder();
    DecimalFormat f = new DecimalFormat("#.###");
    for(int i = 0; i < matrix.getRowDimension(); i++) {
      String[] arr = new String[matrix.getColumnDimension()];
      for(int j = 0; j < matrix.getColumnDimension(); j++) {
        arr[j] = f.format(matrix.getEntry(i, j));
      }
      sb.append(String.join("\t", arr) + "\n");
    }
    return sb.toString();
  }

  @Override
  public void doClustering() {
    // TODO Auto-generated method stub

  }

}
