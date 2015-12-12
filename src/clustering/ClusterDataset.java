package clustering;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import mlsp.cs.cmu.edu.dnn.training.DataInstance;

public class ClusterDataset {
	
	private List<DataInstance> data;

	public ClusterDataset(String fileName) {
		this.data = new ArrayList<>();
		try {
			System.out.println("Reading data values in " + fileName + "...");			
			Scanner scn = new Scanner(new File(fileName));
			while (scn.hasNextLine()) {
				String[] arr = scn.nextLine().split(Pattern.quote(","));
				double[] vals = new double[arr.length];
				for(int i =0;i < arr.length; i++) {
					vals[i] = Double.parseDouble(arr[i]);
				}
				DataInstance instance = new DataInstance(2, 1, vals);
				data.add(instance);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
		
	public List<DataInstance> getClusteringData() {
		return data;
	}

}
