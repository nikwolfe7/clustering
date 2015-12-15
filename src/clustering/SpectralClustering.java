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
		List<RealVector> eigenVectors = new ArrayList<>();
		for(int i = 0; i < K; i++) {
			eigenVectors.add(stackedEigenVectors.getColumnVector(i));
		}
		
		RealMatrix X = new Array2DRowRealMatrix(eigenVectors.size());
		
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
