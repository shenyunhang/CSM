package baidu;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

public class MyData {
	public Instances dataset;

	private int dimension = 45;

	void CreateDatasets() {
		FastVector attributes = new FastVector();
		for (int i = 0; i < dimension * dimension; i++) {
			Attribute numeric = new Attribute("" + i);
			attributes.addElement(numeric);
		}

		FastVector labels = new FastVector();
		for (int i = 0; i < 9; i++)
			labels.addElement(i + "");

		Attribute cls = new Attribute("class", labels);
		attributes.addElement(cls);

		dataset = new Instances("Train-dataset", attributes, 20000);
		dataset.setClassIndex(dataset.numAttributes() - 1);

	}

	void FillDatasets(Mat img) {
		// fileAbsolutePath="I:\\target"

		for (int k = 0; k < 9; k++) {
			int rowStart = k / 3 * img.rows() / 3;
			int rowEnd = (k / 3 + 1) * img.rows() / 3;
			int colStart = k % 3 * img.cols() / 3;
			int colEnd = (k % 3 + 1) * img.cols() / 3;

			//System.out.println(k + " " + (rowEnd - rowStart) + " "
			//		+ (colEnd - colStart));

			Mat part = new Mat(rowEnd - rowStart, colEnd - colStart, img.type());
			part = img.submat(rowStart, rowEnd, colStart, colEnd);
			// Highgui.imwrite("D:/Desktop/baidutest/_mkp" + k + ".png", part);

			int[][] mat = ReadFile(part);

			AddData(mat, k + "");
		}

		// ===================================================
		// ==============================================
		try {
			DataSink.write("data.arff", dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int[][] ReadFile(Mat image) {
		int left = 30, right = 0, top = 30, bottom = 0;
		// System.out.println(files[k].getName());
		// System.out.println(file.getAbsolutePath());

		// Mat image = Highgui.imread(files[k].getAbsolutePath());
		// Mat image = Highgui.imread(file.getAbsolutePath());

		for (int i = 0; i < image.rows(); i++) {
			for (int j = 0; j < image.cols(); j++) {
				if (image.get(i, j)[0] > 255 / 2) {
			//		System.out.print(". ");
				} else {
			//		System.out.print("O ");
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
			//System.out.println("");
		}

		// System.out.println("top=" + top + " bottom" + bottom + " right="
		// + right + " left=" + left + " ¿í=" + (right - left + 1) + " ¸ß"
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
					if (x_mat >= 0 && x_mat < dimension && y_mat >= 0
							&& y_mat < dimension)
						mat[x_mat][y_mat] = 1;
				}

			}
		}
		/*
		 * for (int i = 0; i < dimension; i++) { for (int j = 0; j < dimension;
		 * j++) { if (mat[i][j] == 1) { System.out.print("0 "); } else
		 * System.out.print(". "); } System.out.println(""); }
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
		// System.out.println("cout=" + cout + " lable=" + lable);
		if (lable != null)
			values[cout] = dataset.attribute(dimension * dimension)
					.indexOfValue(lable);
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

	void FillTestDatasets(Mat img) {
		// String absolutePath = "I:\\target\\test2";

		for (int k = 0; k < 4; k++) {

			int colStart = k % 4 * img.cols() / 4;
			int colEnd = (k % 4 + 1) * img.cols() / 4;

			Mat part = new Mat(img.rows(), colEnd - colStart, img.type());
			part = img.submat(0, img.rows(), colStart, colEnd);
			int[][] mat = ReadFile(part);

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

		dataset.setClassIndex(dataset.numAttributes() - 1);

		try {
			DataSink.write("data2.arff", dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
