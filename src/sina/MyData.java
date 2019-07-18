package sina;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

public class MyData {
	public Instances dataset;

	private int dimension = 30;

	void CreateDatasets() {
		FastVector attributes = new FastVector();
		for (int i = 0; i < dimension * dimension; i++) {
			Attribute numeric = new Attribute("" + i);
			attributes.addElement(numeric);
		}

		FastVector labels = new FastVector();
		for (int i = 2; i < 9; i++)
			labels.addElement(i + "");

		labels.addElement("a");
		labels.addElement("A");
		labels.addElement("b");
		labels.addElement("B");
		labels.addElement("c");
		labels.addElement("d");
		labels.addElement("e");
		labels.addElement("E");
		labels.addElement("f");
		labels.addElement("F");
		labels.addElement("G");
		labels.addElement("h");
		labels.addElement("H");
		labels.addElement("k");
		labels.addElement("l");
		labels.addElement("L");
		labels.addElement("m");
		labels.addElement("M");
		labels.addElement("n");
		labels.addElement("N");
		labels.addElement("p");
		labels.addElement("q");
		labels.addElement("Q");
		labels.addElement("R");
		labels.addElement("s");
		labels.addElement("u");
		labels.addElement("v");
		labels.addElement("w");
		labels.addElement("x");
		labels.addElement("y");
		labels.addElement("Y");
		labels.addElement("z");

		Attribute cls = new Attribute("class", labels);
		attributes.addElement(cls);

		dataset = new Instances("Train-dataset", attributes, 20000);
		dataset.setClassIndex(dataset.numAttributes() - 1);

	}

	void FillDatasets(String fileAbsolutePath) {
		// fileAbsolutePath="I:\\target"
		for (int i = 2; i < 9; i++) {
			String absolutePath = fileAbsolutePath + "/" + i;
			ReadDir(absolutePath, i + "");
		}

		ReadDir(fileAbsolutePath + "/a", "a");
		ReadDir(fileAbsolutePath + "/AA", "A");
		ReadDir(fileAbsolutePath + "/b", "b");
		ReadDir(fileAbsolutePath + "/BB", "B");
		ReadDir(fileAbsolutePath + "/c", "c");
		ReadDir(fileAbsolutePath + "/d", "d");
		ReadDir(fileAbsolutePath + "/e", "e");
		ReadDir(fileAbsolutePath + "/EE", "E");
		ReadDir(fileAbsolutePath + "/f", "f");
		ReadDir(fileAbsolutePath + "/FF", "F");
		ReadDir(fileAbsolutePath + "/GG", "G");
		ReadDir(fileAbsolutePath + "/h", "h");
		ReadDir(fileAbsolutePath + "/HH", "H");
		ReadDir(fileAbsolutePath + "/k", "k");
		ReadDir(fileAbsolutePath + "/l", "l");
		ReadDir(fileAbsolutePath + "/LL", "L");
		ReadDir(fileAbsolutePath + "/m", "m");
		ReadDir(fileAbsolutePath + "/MM", "M");
		ReadDir(fileAbsolutePath + "/n", "n");
		ReadDir(fileAbsolutePath + "/NN", "N");
		ReadDir(fileAbsolutePath + "/p", "p");
		ReadDir(fileAbsolutePath + "/q", "q");
		ReadDir(fileAbsolutePath + "/QQ", "Q");
		ReadDir(fileAbsolutePath + "/RR", "R");
		ReadDir(fileAbsolutePath + "/s", "s");
		ReadDir(fileAbsolutePath + "/u", "u");
		ReadDir(fileAbsolutePath + "/v", "v");
		ReadDir(fileAbsolutePath + "/w", "w");
		ReadDir(fileAbsolutePath + "/x", "x");
		ReadDir(fileAbsolutePath + "/y", "y");
		ReadDir(fileAbsolutePath + "/YY", "Y");
		ReadDir(fileAbsolutePath + "/z", "z");

		// ===================================================
		// ==============================================
		try {
			DataSink.write(fileAbsolutePath + "\\data.arff", dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void ReadDir(String absolutePath, String lable) {
		int[] withCout = new int[40];
		int[] heightCout = new int[40];
		for (int i = 0; i < 40; i++) {
			withCout[i] = 0;
			heightCout[i] = 0;
		}

		File file = new File(absolutePath);
		File[] files = file.listFiles();
		// System.out.println("文件数：" + files.length);

		for (int k = 0; k < files.length; k++) {
			if (!files[k].isDirectory()) {
				int[][] mat = ReadFile(files[k]);
				if (mat != null)
					AddData(mat, lable);
			}
		}

		for (int i : withCout) {
			System.out.print(i + " ");
		}
		System.out.println("");
		for (int i : heightCout) {
			System.out.print(i + " ");
		}
	}

	int[][] ReadFile(File file) {
		int left = 20, right = 0, top = 20, bottom = 0;
		// System.out.println(files[k].getName());
		// System.out.println(file.getAbsolutePath());

		// Mat image = Highgui.imread(files[k].getAbsolutePath());
		Mat image = Highgui.imread(file.getAbsolutePath());
		for (int i = 0; i < image.rows(); i++) {
			for (int j = 0; j < image.cols(); j++) {
				if (image.get(i, j)[0] > 255 / 2) {
					// System.out.print(". ");
				} else {
					// System.out.print("O ");
					if (i < top)
						top = i;
					if (i > bottom)
						bottom = i;
					if (j <= left)
						left = j;
					if (j >= right)
						right = j;

				}

			}
			// System.out.println("");
		}

		// System.out.println("top=" + top + " bottom" + bottom + " right="
		// + right + " left=" + left + " 宽=" + (right - left + 1) + " 高"
		// + (bottom - top + 1));
		// ===============================================================
		int width, height;
		width = right - left + 1;
		height = bottom - top + 1;

		/*
		 * if (width > 30) { // makeDir("F:\\target\\test2\\hard"); //
		 * copyFile(file.getAbsolutePath(),"F:\\target\\test2\\hard\\" + //
		 * file.getName()); width = 30; } if (height > 30) { height = 30; }
		 */

		// ==============================================================
		// withCout[right - left + 1]++;
		// heightCout[bottom - top + 1]++;

		int[][] mat = new int[dimension][dimension];
		for (int i = 0; i < dimension; i++)
			for (int j = 0; j < dimension; j++)
				mat[i][j] = 0;

		int margin_top = (dimension - height) / 2;
		int margin_left = (dimension - width) / 2;
		for (int i = 0; i < image.rows(); i++) {
			for (int j = 0; j < image.cols(); j++) {
				if (image.get(i, j)[0] < 255 / 2) {
					int x_mat = i - top + margin_top;
					int y_mat = j - left + margin_left;
					if (x_mat >= 0 && x_mat < 30 && y_mat >= 0 && y_mat < 30)
						mat[x_mat][y_mat] = 1;
				}

			}
		}

		/*
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (mat[i][j] == 1) {
					System.out.print("0 ");
				} else
					System.out.print(". ");
			}
			System.out.println("");
		}
	*/
		return mat;
	}

	void AddData(int mat[][], String lable) {
		double[] values = new double[dataset.numAttributes()];
		int cout = 0;
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				values[cout] = mat[i][j];
				cout++;
			}
		}
		if (lable != null)
			values[cout] = dataset.attribute(900).indexOfValue(lable);
		Instance inst = new Instance(1.0, values);
		dataset.add(inst);
	}

	/*
	 * void AddData2(Instances dataset, int mat[][]) { double[] values = new
	 * double[dataset.numAttributes()]; int cout = 0; for (int i = 0; i <
	 * dimension; i++) { for (int j = 0; j < dimension; j++) { values[cout] =
	 * mat[i][j]; cout++; } } values[cout] =
	 * dataset.attribute(900).indexOfValue("no"); Instance inst = new
	 * Instance(1.0, values); dataset.add(inst); }
	 */

	void FillTestDatasets(String absolutePath) {
		// String absolutePath = "I:\\target\\test2";
		File file = new File(absolutePath);
		File[] files = file.listFiles();
		System.out.println("文件数：" + files.length);

		for (int k = 0; k < files.length; k++) {
			if (!files[k].isDirectory()) {
				int[][] mat = ReadFile(files[k]);
				if (mat == null) {
					continue;
				}
				double[] values = new double[dataset.numAttributes()];
				int cout = 0;
				for (int i = 0; i < dimension; i++) {
					for (int j = 0; j < dimension; j++) {
						values[cout] = mat[i][j];
						cout++;
					}
				}
				// values[cout] = dataset.attribute(900).indexOfValue("yes");
				Instance inst = new Instance(1.0, values);
				dataset.add(inst);
			}
		}

		dataset.setClassIndex(dataset.numAttributes() - 1);
	}

	void FillRecognizeData(String[] absolutePath) {
		// System.out.println("文件数：" + absolutePath.length);

		for (int k = 0; k < absolutePath.length; k++) {

			if (absolutePath[k] != null) {
				File file = new File(absolutePath[k]);
				int[][] mat = ReadFile(file);
				if (mat == null) {
					continue;
				}
				double[] values = new double[dataset.numAttributes()];
				int cout = 0;
				for (int i = 0; i < dimension; i++) {
					for (int j = 0; j < dimension; j++) {
						values[cout] = mat[i][j];
						cout++;
					}
				}
				// values[cout] = dataset.attribute(900).indexOfValue("yes");
				Instance inst = new Instance(1.0, values);
				dataset.add(inst);
			}
		}

		dataset.setClassIndex(dataset.numAttributes() - 1);
	}

}
