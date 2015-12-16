package clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

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

	private boolean SVD = false;

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
	
	public SpectralClustering(int k, DistanceMetric metric, boolean SVD) {
		this(k, metric);
		this.SVD = SVD;
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
		print("Initializing spectral clustering with " + K + " clusters...");
		for (DataInstance instance : dataset.getClusteringData()) {
			clusterData.add(instance);
		}
		calculateAffinityMatrix();
		calculateDiagonalMatrix();
		calculateLaplacianMatrix();
	}

	@Override
	public void doClustering() {
		int rowDim = laplacianMatrix.numRows();
		/* eigven vecs matrix */
		SimpleMatrix X = new SimpleMatrix(rowDim, K);
		if (!SVD) {
			/* Eigen decomposition */
			print("Performing Eigen Decomposition on Laplacian matrix...");
			SimpleEVD<?> eig = laplacianMatrix.eig();
			/* Now get the K largest column vectors */
			for (int i = 0; i < K; i++) {
				SimpleMatrix vec = eig.getEigenVector(i);
				for (int j = 0; j < rowDim; j++) {
					X.set(j, i, vec.get(j));
				}
			}
		} else {
			/* SVD decomposition */
			print("Performing Singular Value Decomposition on Laplacian matrix...");
			SimpleMatrix svd = laplacianMatrix.svd().getV();
			/* Now get the K largest column vectors */
			for (int i = 0; i < X.numRows(); i++) {
				for (int j = 0; j < X.numCols(); j++) {
					X.set(i, j, svd.get(i, j));
				}
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
			AugmentedDataInstance instCopy = new AugmentedDataInstance(0, 0, new double[] { 0 });
			instCopy.replaceDataVector(mat2Dbl(Y.extractVector(true, i)));
			instCopy.replaceLabelValue(inst.getLabelValue());
			instCopy.setOriginalDataVector(inst.getDataVector());
			clusterDataCopy.add(instCopy);
		}
		this.clusterData = clusterDataCopy;
		/* cluster the copied data */
		algo.initialize(new ClusterDataSet(clusterDataCopy));
		algo.doClustering();
	}

	/* ================================================================== */
	/* ================================================================== */
	/* ==================== HELPER FUNCTIONS ============================ */
	/* ================================================================== */
	/* ================================================================== */
	
	private class AugmentedDataInstance extends DataInstance {

		private double[] originalVector;

		public AugmentedDataInstance(int dataDimension, int labelDimension, double[] vector) {
			super(dataDimension, labelDimension, vector);
			this.originalVector = new double[] { 0 };
		}
		
		public void setOriginalDataVector(double[] vector) {
			this.originalVector = vector;
		}
		
		public double[] getOriginalDataVector() {
			return originalVector;
		}
	}

	private double[] mat2Dbl(SimpleMatrix extractVector) {
		double[] d = new double[extractVector.getNumElements()];
		for (int i = 0; i < d.length; i++) {
			d[i] = extractVector.get(i);
		}
		return d;
	}

	/**
	 * Calculates the Affinity matrix
	 */
	private void calculateAffinityMatrix() {
		print("Computing Affinity matrix...");
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
		print(printMatrix(affinityMatrix));
	}

	/**
	 * Calculates Diagonal matrix from Affinity matrix
	 */
	private void calculateDiagonalMatrix() {
		print("Computing Diagonal values from Affinity matrix...");
		int size = clusterData.size();
		/* initialize matrix */
		diagonalMatrix = new SimpleMatrix(size, size);
		for (int i = 0; i < affinityMatrix.numRows(); i++) {
			SimpleMatrix row = affinityMatrix.extractVector(true, i);
			double sum = 0;
			for (int j = 0; j < row.numCols(); j++)
				sum += row.get(j);
			diagonalMatrix.set(i, i, sum);
		}
		print(printMatrix(diagonalMatrix));
	}

	/**
	 * Calculates the Laplacian matrix
	 */
	private void calculateLaplacianMatrix() {
		print("Computing Laplacian matrix from Diagonal matrix...");
		/* initialize matrix */
		SimpleMatrix A = affinityMatrix;
		SimpleMatrix D = diagonalInverseSquareRoot(diagonalMatrix);
		laplacianMatrix = D.mult(A).mult(D);
		print(printMatrix(laplacianMatrix));
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

	private void print(String s) {
		if(outputOn)
			System.out.println(s);
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

	private String getCSVString(double[] arr) {
		String[] strArr = new String[arr.length];
		for(int i = 0; i < arr.length; i++) {
			strArr[i] = "" + arr[i];
		}
		return String.join(",", strArr);
	}
	
	@Override
	public void printResultsToFile(String filename) {
		filename = "kmeans-" + filename;
		try {
			FileWriter writer = new FileWriter(new File(filename));
			FileWriter lblWriter = new FileWriter(new File("labels-" + filename));
			for(Cluster cluster : getKClusters()) {
				String label = "" + cluster.pickMostCommonClusterLabelFromData();
				Iterator<DataInstance> iter = cluster.getDataInstances();
				while(iter.hasNext()) {
					DataInstance instance = iter.next();
					/* get original data vector with the label... we clustered on the other stuff */
					writer.write(getCSVString(((AugmentedDataInstance) instance).getOriginalDataVector()) + "\n");
					lblWriter.write(label + "\n");
				}
			}
			writer.close();
			lblWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Cluster> getKClusters() {
		return algo.getKClusters();
	}
}
