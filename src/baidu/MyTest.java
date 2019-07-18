package baidu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

public class MyTest {
	void testKNN(IBk knn, Instances dataset) {

		try {
			for (int i = 0; i < dataset.numInstances(); i++) {

				double clsLabel = knn.classifyInstance(dataset.instance(i));
				dataset.instance(i).setClassValue(clsLabel);

				System.out.println(" result="
						+ dataset.attribute(dataset.numAttributes() - 1).value(
								(int) clsLabel));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void testSMO(SMO smo, Instances dataset) {

		try {
			for (int i = 0; i < dataset.numInstances(); i++) {

				double clsLabel = smo.classifyInstance(dataset.instance(i));
				dataset.instance(i).setClassValue(clsLabel);

				System.out.println(" result="
						+ dataset.attribute(dataset.numAttributes() - 1).value(
								(int) clsLabel));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void testAdaBoost(AdaBoostM1 adaboost, Instances dataset) {

		String absolutePath = "F:\\target\\test2";
		File file = new File(absolutePath);
		File[] files = file.listFiles();

		try {
			for (int i = 0; i < dataset.numInstances(); i++) {

				double clsLabel = adaboost
						.classifyInstance(dataset.instance(i));
				dataset.instance(i).setClassValue(clsLabel);

				System.out.println(files[i].getAbsolutePath()
						+ " result="
						+ dataset.attribute(dataset.numAttributes() - 1).value(
								(int) clsLabel));

				String newpath = absolutePath
						+ "\\"
						+ dataset.attribute(dataset.numAttributes() - 1).value(
								(int) clsLabel);
				makeDir(newpath);
				copyFile(files[i].getAbsolutePath(),
						newpath + "\\" + files[i].getName());
			}
			DataSink.write(absolutePath + "//result.arff", dataset);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void testTreeJ48(J48 j48, Instances dataset) {

		String absolutePath = "F:\\target\\test2";
		File file = new File(absolutePath);
		File[] files = file.listFiles();

		try {
			for (int i = 0; i < dataset.numInstances(); i++) {

				double clsLabel = j48.classifyInstance(dataset.instance(i));
				dataset.instance(i).setClassValue(clsLabel);

				System.out.println(files[i].getAbsolutePath()
						+ " result="
						+ dataset.attribute(dataset.numAttributes() - 1).value(
								(int) clsLabel));

				String newpath = absolutePath
						+ "\\"
						+ dataset.attribute(dataset.numAttributes() - 1).value(
								(int) clsLabel);
				makeDir(newpath);
				copyFile(files[i].getAbsolutePath(),
						newpath + "\\" + files[i].getName());
			}
			DataSink.write(absolutePath + "//result.arff", dataset);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void TestNaiveBayes(NaiveBayes naivebayes, Instances dataset) {
		String absolutePath = "F:\\target\\test2";
		File file = new File(absolutePath);
		File[] files = file.listFiles();

		try {
			for (int i = 0; i < dataset.numInstances(); i++) {

				double clsLabel = naivebayes.classifyInstance(dataset
						.instance(i));
				dataset.instance(i).setClassValue(clsLabel);

				System.out.println(files[i].getAbsolutePath()
						+ " result="
						+ dataset.attribute(dataset.numAttributes() - 1).value(
								(int) clsLabel));

				String newpath = absolutePath
						+ "\\"
						+ dataset.attribute(dataset.numAttributes() - 1).value(
								(int) clsLabel);
				makeDir(newpath);
				copyFile(files[i].getAbsolutePath(),
						newpath + "\\" + files[i].getName());
			}
			DataSink.write(absolutePath + "//result.arff", dataset);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	void makeDir(String absolutePath) {
		File file = new File(absolutePath);
		if (!file.exists())
			file.mkdirs();
	}

}
