package clustering;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

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
		algo.setConvergenceCriteria(minDiff, maxIterations);
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
		for (DataInstance instance : dataset.getClusteringData()) {
			clusterData.add(instance);
		}
		calculateAffinityMatrix();
		calculateDiagonalMatrix();
		calculateLaplacianMatrix();
	}

	@Override
	public void doClustering() {
		System.out.println("Performing Eigen Decomposition on Laplacian matrix...");
		/* Eigen decomposition */
		EigenDecomposition eig = new EigenDecomposition(laplacianMatrix);
		/* Now get the K largest column vectors */
		RealMatrix stackedEigenVectors = eig.getV();
		int rowDim = stackedEigenVectors.getRowDimension();
		/* eigven vecs matrix */
    RealMatrix X = new Array2DRowRealMatrix(rowDim, K);
		for(int i = 0; i < K; i++) {
			X.setColumn(i, stackedEigenVectors.getColumn(i));
		}
		/* Normalize the rows of X */
		RealMatrix Y = new Array2DRowRealMatrix(X.getRowDimension(), X.getColumnDimension());
		for(int i = 0; i < X.getRowDimension(); i++) {
		  double xDenom = 0;
		  for(int j = 0; j < X.getColumnDimension(); j++) {
		    xDenom += Math.pow(X.getEntry(i, j), 2);
		  }
		  xDenom = (xDenom > 0) ? 1.0 / Math.sqrt(xDenom) : 0;
		  for(int j = 0; j < X.getColumnDimension(); j++) {
        double val = X.getEntry(i, j) * xDenom;
		    Y.setEntry(i, j, val);
      }
		}
		/* Re-project the data into a copy using the rows of Y */
		List<DataInstance> clusterDataCopy = new ArrayList<>();
		for(int i = 0; i < Y.getRowDimension(); i++) {
		  DataInstance inst = clusterData.get(i);
		  DataInstance instCopy = new DataInstance(0, 0, new double[] {0});
		  instCopy.replaceDataVector(Y.getRow(i));
		  instCopy.replaceLabelValue(inst.getLabelValue());
		  clusterDataCopy.add(instCopy);
		}
		/* cluster the copied data */
		algo.initialize(new ClusterDataSet(clusterDataCopy));
		algo.doClustering();
	}

	/* ================================================================== */
	/* ================================================================== */
	/* ==================== HELPER FUNCTIONS ============================ */
	/* ================================================================== */
	/* ================================================================== */

	/**
	 * Calculates the Affinity matrix
	 */
	private void calculateAffinityMatrix() {
		System.out.println("Computing Affinity matrix...");
		int size = clusterData.size();
		/* initialize matrix */
		affinityMatrix = new Array2DRowRealMatrix(size, size);
		for (int i = 0; i < affinityMatrix.getRowDimension(); ++i) {
			DataInstance a1 = clusterData.get(i);
			for (int j = 0; j < affinityMatrix.getColumnDimension(); ++j) {
				DataInstance a2 = clusterData.get(j);
				double distance = distMetric.getDistance(a1, a2);
				affinityMatrix.setEntry(i, j, distance);
			}
		}
		if (outputOn)
			System.out.println(printMatrix(affinityMatrix));
	}

	/**
	 * Calculates Diagonal matrix from Affinity matrix
	 */
	private void calculateDiagonalMatrix() {
		System.out.println("Computing Diagonal values from Affinity matrix...");
		int size = clusterData.size();
		/* initialize matrix */
		diagonalMatrix = new DiagonalMatrix(size);
		for (int i = 0; i < affinityMatrix.getRowDimension(); i++) {
			double[] row = affinityMatrix.getRow(i);
			double sum = sum(row);
			diagonalMatrix.setEntry(i, i, sum);
		}
		if (outputOn)
			System.out.println(printMatrix(diagonalMatrix));
	}

	/**
	 * Calculates the Laplacian matrix
	 */
	private void calculateLaplacianMatrix() {
		System.out.println("Computing Laplacian matrix from Diagonal matrix...");
		int size = clusterData.size();
		/* initialize matrix */
		RealMatrix A = affinityMatrix;
		RealMatrix D = diagonalInverseSquareRoot(diagonalMatrix);
		laplacianMatrix = D.multiply(A).multiply(D);
		if (outputOn)
			System.out.println(printMatrix(laplacianMatrix));
	}

	/**
	 * Takes the D^-1/2 for a diagonal matrix... 
	 * 
	 * @param matrix
	 * @return
	 */
	private RealMatrix diagonalInverseSquareRoot(RealMatrix matrix) {
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			double d = matrix.getEntry(i, i);
			d = 1.0 / Math.sqrt(d);
			matrix.setEntry(i, i, d);
		}
		return matrix;
	}

	/**
	 * Sums a row or col... 
	 * 
	 * @param arr
	 * @return
	 */
	private double sum(double... arr) {
		double sum = 0;
		for (double d : arr)
			sum += d;
		return sum;
	}

	private String printMatrix(RealMatrix matrix) {
		StringBuilder sb = new StringBuilder();
		DecimalFormat f = new DecimalFormat("###.###");
		for (int i = 0; i < matrix.getRowDimension(); i++) {
			String[] arr = new String[matrix.getColumnDimension()];
			for (int j = 0; j < matrix.getColumnDimension(); j++) {
				arr[j] = f.format(matrix.getEntry(i, j));
			}
			sb.append(String.join("\t", arr) + "\n");
		}
		return sb.toString();
	}

}
