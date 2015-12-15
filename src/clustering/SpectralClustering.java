package clustering;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.Matrix;
import org.ejml.interfaces.decomposition.EigenDecomposition;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class SpectralClustering implements ClusteringAlgorithm {

	private int K;

	private DistanceMetric distMetric;

	private List<DataInstance> clusterData;

	private SimpleMatrix affinityMatrix;

	private SimpleMatrix diagonalMatrix;

	private SimpleMatrix laplacianMatrix;

	private ClusteringAlgorithm algo;

	private double minDiff = 1.0e-10;

	private int maxIterations = 10000;

	private boolean outputOn;

	public SpectralClustering(int k, DistanceMetric metric) {
		this.K = k;
		this.distMetric = metric;
		this.clusterData = new ArrayList<>();
		this.affinityMatrix = new SimpleMatrix();
		this.diagonalMatrix = new SimpleMatrix();
		this.laplacianMatrix = new SimpleMatrix();
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
		/* Eigen decomposition */
		System.out.println("Performing Eigen Decomposition on Laplacian matrix...");
		SimpleEVD<?> eig = laplacianMatrix.eig();
		/* Now get the K largest column vectors */
		int rowDim = laplacianMatrix.numRows();
		/* eigven vecs matrix */
		SimpleMatrix X = new SimpleMatrix(rowDim, K);
		for (int i = 0; i < K; i++) {
			SimpleMatrix vec = eig.getEigenVector(i);
			for(int j = 0; j < rowDim; j++) {
				X.set(j, i, vec.get(j));
			}
		}
		/* Normalize the rows of X */
		SimpleMatrix Y = new SimpleMatrix(X.numRows(), X.numCols());
		for (int i = 0; i < X.numRows(); i++) {
			double xDenom = 0;
			for (int j = 0; j < X.numCols(); j++) {
				xDenom += Math.pow(X.get(i, j), 2);
			}
			xDenom = (xDenom > 0) ? 1.0 / Math.sqrt(xDenom) : 0;
			for (int j = 0; j < X.numCols(); j++) {
				double val = X.get(i, j) * xDenom;
				Y.set(i, j, val);
			}
		}
		/* Re-project the data into a copy using the rows of Y */
		List<DataInstance> clusterDataCopy = new ArrayList<>();
		for (int i = 0; i < Y.numRows(); i++) {
			DataInstance inst = clusterData.get(i);
			DataInstance instCopy = new DataInstance(0, 0, new double[] { 0 });
			instCopy.replaceDataVector(mat2Dbl(Y.extractVector(true, i)));
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

	private double[] mat2Dbl(SimpleMatrix extractVector) {
		double[] d = new double[extractVector.getNumElements()];
		for(int i = 0; i < d.length; i++) {
			d[i] = extractVector.get(i);
		}
		return d;
	}

	/**
	 * Calculates the Affinity matrix
	 */
	private void calculateAffinityMatrix() {
		System.out.println("Computing Affinity matrix...");
		int size = clusterData.size();
		/* initialize matrix */
		affinityMatrix = new SimpleMatrix(size, size);
		for (int i = 0; i < affinityMatrix.numRows(); ++i) {
			DataInstance a1 = clusterData.get(i);
			for (int j = 0; j < affinityMatrix.numCols(); ++j) {
				DataInstance a2 = clusterData.get(j);
				double distance = distMetric.getDistance(a1, a2);
				affinityMatrix.set(i, j, distance);
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
		diagonalMatrix = new SimpleMatrix(size, size);
		for (int i = 0; i < affinityMatrix.numRows(); i++) {
			SimpleMatrix row = affinityMatrix.extractVector(true, i);
			double sum = 0;
			for(int j = 0; j < row.numCols(); j++)
				sum += row.get(j);
			diagonalMatrix.set(i, i, sum);
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
		SimpleMatrix A = affinityMatrix;
		SimpleMatrix D = diagonalInverseSquareRoot(diagonalMatrix);
		laplacianMatrix = D.mult(A).mult(D);
		if (outputOn)
			System.out.println(printMatrix(laplacianMatrix));
	}

	/**
	 * Takes the D^-1/2 for a diagonal matrix...
	 * 
	 * @param matrix
	 * @return
	 */
	private SimpleMatrix diagonalInverseSquareRoot(SimpleMatrix matrix) {
		for (int i = 0; i < matrix.numRows(); i++) {
			double d = matrix.get(i, i);
			d = 1.0 / Math.sqrt(d);
			matrix.set(i, i, d);
		}
		return matrix;
	}

	private double distance(double... v) {
		double dist = 0;
		for(double d : v)
			dist += d * d;
		return Math.sqrt(dist);
	}
	
	private String printMatrix(SimpleMatrix matrix) {
		StringBuilder sb = new StringBuilder();
		DecimalFormat f = new DecimalFormat("###.###");
		for (int i = 0; i < matrix.numRows(); i++) {
			String[] arr = new String[matrix.numCols()];
			for (int j = 0; j < matrix.numCols(); j++) {
				arr[j] = f.format(matrix.get(i, j));
			}
			sb.append(String.join("\t", arr) + "\n");
		}
		return sb.toString();
	}

}
