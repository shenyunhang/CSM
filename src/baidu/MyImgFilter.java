package baidu;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class MyImgFilter {
	public String OutPath;
	private String FileName;

	private Mat mat;
	private Mat gray;

	private Mat imgRow[];

	private Mat keyWord[];

	private Mat allCandidateWord;
	private Mat candidateWord[];

	private int threshold = 255 / 2;

	private int[] b;
	private boolean found = false;

	private int offset = 0;

	private int more = 2;

	private int myShort = 0;

	public int[] result = { -1, -1, -1, -1 };

	private int[] separatePosition = { 0, 0, 0 };

	private void SetFileName(String Name) {
		int DotPos = Name.lastIndexOf('.');
		int SlashPos = Name.lastIndexOf('/');
		if (SlashPos == -1)
			SlashPos = Name.lastIndexOf('\\');
		int Start, End;
		if (SlashPos == -1)
			Start = 0;
		else
			Start = SlashPos + 1;
		if (DotPos == -1)
			End = Name.length();
		else
			End = DotPos;
		FileName = Name.substring(Start, End);
		OutPath = Name.substring(0, Start);
		System.out.println("FileName=" + FileName);
		System.out.println("OutPath=" + OutPath);
	}

	public MyImgFilter(String fileabsolutePath) {
		SetFileName(fileabsolutePath);

		// 读取并预处理
		mat = Highgui.imread(fileabsolutePath, -1);
		System.out.println(mat.channels());
		mat = mat.submat(0, mat.rows(), 5, mat.cols());
		gray = mat.clone();
		gray = toGray(gray);
		// gray = RemoveSomething(gray);
		Highgui.imwrite(OutPath + FileName + "_gray" + ".png", gray);

		imgRow = new Mat[4];
		keyWord = new Mat[4];
		candidateWord = new Mat[9];

	}

	/**
	 * 
	 * @param inputMat
	 * @return Mat
	 */
	private Mat toGray(Mat inputMat) {
		for (int i = 0; i < inputMat.rows(); i++) {
			for (int j = 0; j < inputMat.cols(); j++) {
				double[] temp = inputMat.get(i, j);
				/*
				 * if (temp[3] > 255 / 2) { temp[3] = 255; } else { temp[3] = 0;
				 * }
				 */

				temp[0] = 255 - temp[3];
				temp[1] = 255 - temp[3];
				temp[2] = 255 - temp[3];
				temp[3] = 0;

				inputMat.put(i, j, temp);

			}
		}
		Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2GRAY);

		// Mat gray=new Mat(inputMat.rows(),inputMat.cols(),Highgui.);
		Highgui.imwrite(OutPath + "toGray.png", inputMat);

		return inputMat;
	}

	private Mat RemoveSomething(Mat inputMat) {

		for (int i = 0; i < inputMat.rows(); i++) {
			for (int j = 0; j < inputMat.cols(); j++) {
				double[] temp = inputMat.get(i, j);

				if (temp[0] < threshold) {
					temp[0] = 0;
				} else {
					temp[0] = 255;
				}

				inputMat.put(i, j, temp);

			}
		}
		Highgui.imwrite(OutPath + "RemoveSomething.png", inputMat);

		return inputMat;
	}

	/**
	 * 切割图片
	 */
	public void SeparateImage() {

		// RemoveSomething();

		// 水平划分为四份
		for (int n = 0; n < 4; n++) {
			imgRow[n] = new Mat(gray.rows() / 4, gray.cols(), gray.type());
			imgRow[n] = gray.submat(imgRow[n].rows() * (n), imgRow[n].rows()
					* (n + 1), 0, imgRow[n].cols());
			// Highgui.imwrite(OutPath + FileName + "_" + n + ".png",
			// imgRow[n]);
		}

		// 完整的候选词候选图片
		allCandidateWord = new Mat(gray.rows() * 3 / 4, gray.cols(),
				gray.type());
		allCandidateWord = gray.submat(gray.rows() * 1 / 4, gray.rows(), 0,
				gray.cols());
		// Highgui.imwrite(OutPath + FileName + "_allCandidateWord.png",
		// allCandidateWord);

		// 追踪线条并去掉
		b = new int[imgRow[0].cols()];
		genLine(0);
		AfterGetline();

		// 获取单个关键词
		GetKeyWordImage();
		// 获取单个候选词
		// GetCandidateWordImage();

	}

	public void Sift() {

		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				double[] temp = mat.get(i, j);
				temp[1] = temp[3];
				temp[3] = 255;
				mat.put(i, j, temp);
			}
		}

		for (int n = 0; n < 9; n++) {

			Mat gray1 = new Mat();
			Mat gray2 = new Mat();
			Mat desc1 = new Mat();
			Mat desc2 = new Mat();

			// Mat in1 = Highgui.imread("/home/syh/workspace/download.jpg");
			// Mat in2 = Highgui.imread("/home/syh/workspace/snapshot3.png");

			Imgproc.cvtColor(keyWord[2], gray1, Imgproc.COLOR_BGR2GRAY);
			Imgproc.cvtColor(candidateWord[n], gray2, Imgproc.COLOR_BGR2GRAY);
			Highgui.imwrite("/home/syh/workspace/gray1.png", gray1);
			Highgui.imwrite("/home/syh/workspace/gray2_" + n + ".png", gray2);

			Mat out = new Mat();
			Mat out1 = new Mat(gray1.rows(), gray1.cols(), gray1.type());
			Mat out2 = new Mat(gray2.rows(), gray2.cols(), gray2.type());

			FeatureDetector fd = FeatureDetector.create(FeatureDetector.SIFT);
			MatOfKeyPoint mkp1 = new MatOfKeyPoint();
			MatOfKeyPoint mkp2 = new MatOfKeyPoint();

			fd.detect(gray1, mkp1);
			fd.detect(gray2, mkp2);

			Features2d.drawKeypoints(gray1, mkp1, out1);
			Highgui.imwrite("/home/syh/workspace/out1.png", out1);

			Features2d.drawKeypoints(gray2, mkp2, out2);
			Highgui.imwrite("/home/syh/workspace/out2_" + n + ".png", out2);

			DescriptorExtractor de = DescriptorExtractor
					.create(DescriptorExtractor.SIFT);

			de.compute(gray1, mkp1, desc1);
			de.compute(gray2, mkp2, desc2);

			DescriptorMatcher matcher = DescriptorMatcher
					.create(DescriptorMatcher.BRUTEFORCE);
			// List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();

			MatOfDMatch matche = new MatOfDMatch();

			// matcher.knnMatch(desc1, desc2, matches, 2);
			matcher.match(desc1, desc2, matche);

			// Features2d.drawMatches2(gray1, mkp1, gray2, mkp2, matches, out);
			Features2d.drawMatches(gray1, mkp1, gray2, mkp2, matche, out);

			Highgui.imwrite("/home/syh/workspace/out_" + n + ".png", out);

			System.out.println(mkp1.toList());

			System.out.println(desc1.rows() + " " + desc2.rows());

			float distance = 0;
			int[] count = new int[9];
			for (int i = 0; i < count.length; i++) {
				count[i] = 0;
			}
			for (int i = 0; i < matche.toList().size(); i++) {
				distance += matche.toList().get(i).distance;
				int x = (int) (mkp2.toList().get(
						matche.toList().get(i).trainIdx).pt.x
						/ gray2.cols() * 3);
				int y = (int) (mkp2.toList().get(
						matche.toList().get(i).trainIdx).pt.y
						/ gray2.rows() * 3);
				count[x + y * 3]++;
				// System.out.println("x=" + x + " y=" + y);
			}
			for (int i = 0; i < count.length; i++) {
				// System.out.println(count[i]);
			}
			System.out.println("n=" + n + " 結果:" + distance + " "
					+ matche.toList());

			// ================================================================
			/*
			 * int[][] point1 = new int[gray1.rows()][gray1.cols()]; double[]
			 * angle1 = new double[mkp1.toList().size() - 1];
			 * 
			 * for (int i = 0; i < gray1.rows(); i++) { for (int j = 0; j <
			 * gray1.cols(); j++) { point1[i][j] = 0; } }
			 * 
			 * for (int i = 0; i < mkp1.toList().size(); i++) {
			 * 
			 * System.out.println(mkp1.toList().get(i).pt.x + " " +
			 * mkp1.toList().get(i).pt.y); point1[(int)
			 * mkp1.toList().get(i).pt.y - 1][(int) mkp1.toList() .get(i).pt.x -
			 * 1] = i + 1;
			 * 
			 * if (i > 0) { angle1[i - 1] = Math.atan2(mkp1.toList().get(i).pt.y
			 * - mkp1.toList().get(0).pt.y, mkp1.toList().get(i).pt.x -
			 * mkp1.toList().get(0).pt.x); } }
			 * 
			 * for (int i = 0; i < angle1.length; i++) {
			 * 
			 * System.out.print(angle1[i] + " "); } System.out.println();
			 * 
			 * for (int i = 0; i < gray1.rows(); i++) { for (int j = 0; j <
			 * gray1.cols(); j++) { if (i == gray1.rows() / 2 && j ==
			 * gray1.cols() / 2) { System.out.print("X ");
			 * 
			 * } else { if (point1[i][j] > 9) { System.out.print(point1[i][j]);
			 * } else { System.out.print(point1[i][j] + " "); } }
			 * 
			 * } System.out.println(); }
			 * 
			 * int[][] point2 = new int[gray2.rows()][gray2.cols()]; double[]
			 * angle2 = new double[mkp1.toList().size() - 1];
			 * 
			 * for (int i = 0; i < gray2.rows(); i++) { for (int j = 0; j <
			 * gray2.cols(); j++) { point2[i][j] = 0; } }
			 * 
			 * for (int i = 0; i < matche.toList().size(); i++) {
			 * 
			 * System.out .println(matche.toList().get(i).trainIdx + " " +
			 * (mkp2.toList().get( matche.toList().get(i).trainIdx).pt.x) + " "
			 * + (mkp2.toList().get( matche.toList().get(i).trainIdx).pt.y));
			 * point2[(int)
			 * mkp2.toList().get(matche.toList().get(i).trainIdx).pt.y -
			 * 1][(int) mkp2 .toList().get(matche.toList().get(i).trainIdx).pt.x
			 * - 1] = i + 1;
			 * 
			 * if (i > 0) { angle2[i - 1] = Math .atan2(mkp2.toList().get(
			 * matche.toList().get(i).trainIdx).pt.y - mkp2.toList().get(
			 * matche.toList().get(0).trainIdx).pt.y, mkp2.toList().get(
			 * matche.toList().get(i).trainIdx).pt.x - mkp2.toList()
			 * .get(matche.toList().get(0).trainIdx).pt.x); } } for (int i = 0;
			 * i < angle2.length; i++) {
			 * 
			 * System.out.print(angle2[i] + " "); } System.out.println(); for
			 * (int i = 0; i < gray2.rows(); i++) { for (int j = 0; j <
			 * gray2.cols(); j++) { if (i == gray2.rows() / 2 && j ==
			 * gray2.cols() / 2) { System.out.print("X ");
			 * 
			 * } else { if (point2[i][j] > 9) { System.out.print(point2[i][j]);
			 * } else { System.out.print(point2[i][j] + " "); } } }
			 * System.out.println(); }
			 */
			// ================================================================

			System.out
					.println("===============================================");
		}

	}

	/**
	 * Harris 角点检测
	 */
	public void Corner() {

		Mat gray1 = new Mat();
		Mat gray2 = new Mat();
		Mat desc1 = new Mat();
		Mat desc2 = new Mat();

		// Mat in1 = Highgui.imread("/home/syh/workspace/download.jpg");
		// Mat in2 = Highgui.imread("/home/syh/workspace/snapshot3.png");

		// Imgproc.cvtColor(imgRow[0], gray1, Imgproc.COLOR_BGR2GRAY);
		gray1 = imgRow[0];
		// gray1 = RemoveSomething(gray1);

		// Imgproc.cvtColor(allCandidateWord, gray2, Imgproc.COLOR_BGR2GRAY);
		// gray2 = RemoveSomething(gray2);
		gray2 = allCandidateWord;

		// Highgui.imwrite("/home/syh/workspace/gray1.png", gray1);
		// Highgui.imwrite("/home/syh/workspace/gray2.png", gray2);

		Mat out = new Mat();
		Mat out1 = new Mat(gray1.rows(), gray1.cols(), gray1.type());
		Mat out2 = new Mat(gray2.rows(), gray2.cols(), gray2.type());

		// 获取特征点
		FeatureDetector fd = FeatureDetector.create(FeatureDetector.HARRIS);
		MatOfKeyPoint mkp1 = new MatOfKeyPoint();
		MatOfKeyPoint mkp2 = new MatOfKeyPoint();

		fd.detect(gray1, mkp1);
		fd.detect(gray2, mkp2);

		Features2d.drawKeypoints(gray1, mkp1, out1);
		Highgui.imwrite(OutPath + FileName + "_out1.png", out1);

		Features2d.drawKeypoints(gray2, mkp2, out2);
		Highgui.imwrite(OutPath + FileName + "_out2.png", out2);

		// 特征点匹配
		DescriptorExtractor de = DescriptorExtractor
				.create(DescriptorExtractor.SIFT);
		de.compute(gray1, mkp1, desc1);
		de.compute(gray2, mkp2, desc2);

		DescriptorMatcher matcher = DescriptorMatcher
				.create(DescriptorMatcher.BRUTEFORCE_SL2);
		// List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();

		MatOfDMatch matche = new MatOfDMatch();

		// matcher.knnMatch(desc1, desc2, matches, 2);
		matcher.match(desc1, desc2, matche);

		// ==============================================================

		List<DMatch> matchetoList_temp = matche.toList();
		List<DMatch> matchetoList_new = new ArrayList<DMatch>(); // 筛选
		double max_dist = 0;
		double min_dist = 1000;

		// -- Quick calculation of max and min distances between keypoints
		System.out.println("matchetoList_temp.size()="
				+ matchetoList_temp.size());
		for (int i = 0; i < matchetoList_temp.size(); i++) {
			double dist = matchetoList_temp.get(i).distance;
			if (dist < min_dist)
				min_dist = dist;
			if (dist > max_dist)
				max_dist = dist;
		}

		for (int i = 0; i < matchetoList_temp.size(); i++) {
			if (matchetoList_temp.get(i).distance < 2 * min_dist) {
				matchetoList_new.add(matchetoList_temp.get(i));
			}
		}
		matche.fromList(matchetoList_new);
		System.out
				.println("matchetoList_new.size()=" + matchetoList_new.size());
		// ========================================

		// Features2d.drawMatches2(gray1, mkp1, gray2, mkp2, matches, out);
		Features2d.drawMatches(gray1, mkp1, gray2, mkp2, matche, out);

		Highgui.imwrite(OutPath + FileName + "_out.png", out);

		System.out.println(mkp1.toList());

		System.out.println(desc1.rows() + " " + desc2.rows());

		// 寻找区域
		float[][] count = new float[4][9];
		int[][] countDistance = new int[4][9];
		int[] featureNumber = new int[4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 9; j++) {
				count[i][j] = 0;
				countDistance[i][j] = 0;
			}
		}
		for (int i = 0; i < 4; i++) {
			featureNumber[i] = 0;
		}

		List<DMatch> matchetoList = matche.toList();
		List<KeyPoint> mkp2toList = mkp2.toList();
		List<KeyPoint> mkp1toList = mkp1.toList();

		Mat mmkp2 = drawMKP(mkp2toList, gray2, 2);
		Mat mmkp1 = drawMKP(mkp1toList, gray1, 1);
		// KNN(mmkp1, mmkp2);

		for (int i = 0; i < matchetoList.size(); i++) {

			int x = (int) (mkp2toList.get(matchetoList.get(i).trainIdx).pt.x * 3 / gray2
					.cols());
			int y = (int) (mkp2toList.get(matchetoList.get(i).trainIdx).pt.y * 3 / gray2
					.rows());

			double keyImgPosition = mkp1toList
					.get(matchetoList.get(i).queryIdx).pt.x;

			if (keyImgPosition >= 0
					&& keyImgPosition <= separatePosition[0] + offset) {
				count[0][x + y * 3]++;
				countDistance[0][x + y * 3] += matchetoList.get(i).distance;
				featureNumber[0]++;

			}
			if (keyImgPosition >= separatePosition[0] - offset
					&& keyImgPosition <= separatePosition[1] + offset) {
				count[1][x + y * 3]++;
				countDistance[1][x + y * 3] += matchetoList.get(i).distance;
				featureNumber[1]++;
			}
			if (keyImgPosition >= separatePosition[1] - offset
					&& keyImgPosition <= separatePosition[2] + offset) {
				count[2][x + y * 3]++;
				countDistance[2][x + y * 3] += matchetoList.get(i).distance;
				featureNumber[2]++;
			}

			if (keyImgPosition >= separatePosition[2] - offset
					&& keyImgPosition <= imgRow[0].cols()) {
				count[3][x + y * 3]++;
				countDistance[3][x + y * 3] += matchetoList.get(i).distance;
				featureNumber[3]++;
			}

		}

		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 9; j++) {

				if (j % 3 == 0) {
					System.out.println();
				}
				count[i][j] = count[i][j] / (float) featureNumber[i];
				System.out.print(count[i][j] + " ");
			}
			System.out.println();

			for (int j = 0; j < 9; j++) {
				if (j % 3 == 0) {
					System.out.println();
				}
				System.out.print(countDistance[i][j] + " ");
			}
			System.out.println();
			System.out.println();
		}

		// 获取结果

		int ii = 0;
		int jj = 0;
		int k = 0;
		while (k < 4) {

			// 获取特征点最多的位置
			for (int i = 0; i < 4; i++) {

				for (int j = 0; j < 9; j++) {
					if (count[ii][jj] < count[i][j]) {
						ii = i;
						jj = j;
					} else if (count[ii][jj] == count[i][j]
							&& count[ii][jj] != 0) {
						if (countDistance[ii][jj] > countDistance[i][j]) {
							ii = i;
							jj = j;
						}
					}
				}
			}

			count[ii][jj] = 0;

			// 判断位置是否已经被占用
			boolean isLegal = true;
			if (result[ii] != -1) {
				isLegal = false;
			}
			for (int i = 0; i < result.length; i++) {
				if (jj == result[i]) {
					isLegal = false;
				}
			}
			if (isLegal) {
				result[ii] = jj;
				k++;
			}
		}

		System.out.println("result=" + (result[0] + 1) + (result[1] + 1)
				+ (result[2] + 1) + (result[3] + 1)
				+ "\n============================================");

		DrawResult();
	}

	public void KNN(Mat mkp1, Mat mkp2) {
		// 准备测试数据
		MyData mytestdata = new MyData();
		mytestdata.CreateDatasets();
		mytestdata.FillTestDatasets(mkp1);

		// 准备训练数据
		MyData mydata = new MyData();
		mydata.CreateDatasets();
		mydata.FillDatasets(mkp2);

		// 初始化机器学习类
		MyLearn mylearn = new MyLearn();
		// 初始化测试类
		MyTest mytest = new MyTest();

		// KNN算法
		// mylearn.KNN(mydata);
		// mytest.testKNN(mylearn.knn, mytestdata.dataset);

		mylearn.SMO(mydata);
		mytest.testSMO(mylearn.smo, mytestdata.dataset);
	}

	public Mat drawMKP(List<KeyPoint> mkptoList, Mat mat, int flag) {
		Mat mkp = new Mat(mat.rows(), mat.cols(), mat.type());
		double[] temp = { 255 };

		for (int i = 0; i < mkp.rows(); i++) {
			for (int j = 0; j < mkp.cols(); j++) {
				mkp.put(i, j, temp);
			}
		}
		temp[0] = 0;
		for (int i = 0; i < mkptoList.size(); i++) {
			mkp.put((int) mkptoList.get(i).pt.y, (int) mkptoList.get(i).pt.x,
					temp);

		}
		Highgui.imwrite(OutPath + FileName + "_mkp" + flag + ".png", mkp);
		return mkp;
	}

	/**
	 * Harris 角点检测
	 */
	public void Corner2() {

		Mat gray1 = new Mat();
		Mat gray2 = new Mat();
		Mat desc1 = new Mat();
		Mat desc2 = new Mat();

		// Mat in1 = Highgui.imread("/home/syh/workspace/download.jpg");
		// Mat in2 = Highgui.imread("/home/syh/workspace/snapshot3.png");

		// Imgproc.cvtColor(imgRow[0], gray1, Imgproc.COLOR_BGR2GRAY);
		gray2 = imgRow[0];
		// gray1 = RemoveSomething(gray1);

		// Imgproc.cvtColor(allCandidateWord, gray2, Imgproc.COLOR_BGR2GRAY);
		// gray2 = RemoveSomething(gray2);
		gray1 = allCandidateWord;

		// Highgui.imwrite("/home/syh/workspace/gray1.png", gray1);
		// Highgui.imwrite("/home/syh/workspace/gray2.png", gray2);

		Mat out = new Mat();
		Mat out1 = new Mat(gray1.rows(), gray1.cols(), gray1.type());
		Mat out2 = new Mat(gray2.rows(), gray2.cols(), gray2.type());

		// 获取特征点
		FeatureDetector fd = FeatureDetector.create(FeatureDetector.HARRIS);
		MatOfKeyPoint mkp1 = new MatOfKeyPoint();
		MatOfKeyPoint mkp2 = new MatOfKeyPoint();

		fd.detect(gray1, mkp1);
		fd.detect(gray2, mkp2);

		Features2d.drawKeypoints(gray1, mkp1, out1);
		// Highgui.imwrite("/home/syh/workspace/out1.png", out1);

		Features2d.drawKeypoints(gray2, mkp2, out2);
		// Highgui.imwrite("/home/syh/workspace/out2.png", out2);

		FeatureDetector fd2 = FeatureDetector.create(FeatureDetector.SIFT);
		MatOfKeyPoint mkp3 = new MatOfKeyPoint();
		MatOfKeyPoint mkp4 = new MatOfKeyPoint();
		fd2.detect(gray1, mkp3);
		fd2.detect(gray2, mkp4);

		List<KeyPoint> mkp2toList = mkp2.toList();
		List<KeyPoint> mkp1toList = mkp1.toList();
		List<KeyPoint> mkp3toList = mkp3.toList();
		List<KeyPoint> mkp4toList = mkp4.toList();
		mkp2toList = getSim(mkp4toList, mkp2toList);
		mkp1toList = getSim(mkp3toList, mkp1toList);
		mkp1.fromList(mkp1toList);
		mkp2.fromList(mkp2toList);

		// 特征点匹配
		DescriptorExtractor de = DescriptorExtractor
				.create(DescriptorExtractor.SIFT);
		de.compute(gray1, mkp1, desc1);
		de.compute(gray2, mkp2, desc2);

		DescriptorMatcher matcher = DescriptorMatcher
				.create(DescriptorMatcher.BRUTEFORCE_L1);
		// List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();

		MatOfDMatch matche = new MatOfDMatch();

		// matcher.knnMatch(desc1, desc2, matches, 2);
		matcher.match(desc1, desc2, matche);

		// Features2d.drawMatches2(gray1, mkp1, gray2, mkp2, matches, out);
		// System.out.println(mkp1.toList());

		System.out.println(desc1.rows() + " " + desc2.rows());

		// 寻找区域
		float[][] count = new float[4][9];
		int[][] countDistance = new int[4][9];
		int[] featureNumber = new int[9];
		int[] featureNumber_o = new int[9];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 9; j++) {
				count[i][j] = 0;
				countDistance[i][j] = 0;
			}
		}
		for (int i = 0; i < 9; i++) {
			featureNumber[i] = 0;
			featureNumber_o[i] = 0;
		}

		List<DMatch> matchetoList2 = new ArrayList<DMatch>();
		List<DMatch> matchetoList = matche.toList();

		// ransac(matchetoList, mkp1toList, mkp2toList, gray1, gray2);

		for (int i = 0; i < matchetoList.size(); i++) {

			int x = (int) (mkp1toList.get(matchetoList.get(i).queryIdx).pt.x * 3 / gray1
					.cols());
			int y = (int) (mkp1toList.get(matchetoList.get(i).queryIdx).pt.y * 3 / gray1
					.rows());

			featureNumber_o[x + y * 3]++;

		}

		// 筛选匹配点

		for (int i = 0; i < mkp2toList.size(); i++) {
			double distance = Integer.MAX_VALUE;
			int which = -1;
			for (int j = 0; j < matchetoList.size(); j++) {
				if (matchetoList.get(j).trainIdx == i
						&& distance > matchetoList.get(j).distance) {
					which = j;
					distance = matchetoList.get(j).distance;
				}

			}
			if (which != -1) {
				matchetoList2.add(matchetoList.get(which));

			}

		}
		matchetoList = matchetoList2;
		matche.fromList(matchetoList);

		Features2d.drawMatches(gray1, mkp1, gray2, mkp2, matche, out);

		Highgui.imwrite(OutPath + FileName + "_out_.png", out);

		System.out.println(matchetoList);
		// =========================================================

		for (int i = 0; i < matchetoList.size(); i++) {

			int x = (int) (mkp1toList.get(matchetoList.get(i).queryIdx).pt.x * 3 / gray1
					.cols());
			int y = (int) (mkp1toList.get(matchetoList.get(i).queryIdx).pt.y * 3 / gray1
					.rows());

			double keyImgPosition = mkp2toList
					.get(matchetoList.get(i).trainIdx).pt.x;

			featureNumber[x + y * 3]++;

			if (keyImgPosition >= 0
					&& keyImgPosition <= separatePosition[0] + offset) {
				count[0][x + y * 3]++;
				countDistance[0][x + y * 3] += matchetoList.get(i).distance;

			}
			if (keyImgPosition >= separatePosition[0] - offset
					&& keyImgPosition <= separatePosition[1] + offset) {
				count[1][x + y * 3]++;
				countDistance[1][x + y * 3] += matchetoList.get(i).distance;

			}
			if (keyImgPosition >= separatePosition[1] - offset
					&& keyImgPosition <= separatePosition[2] + offset) {
				count[2][x + y * 3]++;
				countDistance[2][x + y * 3] += matchetoList.get(i).distance;

			}

			if (keyImgPosition >= separatePosition[2] - offset
					&& keyImgPosition <= imgRow[0].cols()) {
				count[3][x + y * 3]++;
				countDistance[3][x + y * 3] += matchetoList.get(i).distance;

			}

		}
		// ===================================================================

		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 9; j++) {

				count[i][j] = count[i][j] / featureNumber[j];

				count[i][j] = count[i][j] * featureNumber[j]
						/ featureNumber_o[j];
			}
		}

		// 输出信息

		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 9; j++) {

				if (j % 3 == 0) {
					System.out.println();
				}

				System.out.print(count[i][j] + " ");

			}
			System.out.println();

			for (int j = 0; j < 9; j++) {
				if (j % 3 == 0) {
					System.out.println();
				}
				System.out.print(countDistance[i][j] + " ");
			}
			System.out.println();
			System.out.println();
		}

		for (int j = 0; j < 9; j++) {

			if (j % 3 == 0) {
				System.out.println();
			}
			System.out.print(featureNumber[j] + " ");
		}
		System.out.println();

		for (int j = 0; j < 9; j++) {

			if (j % 3 == 0) {
				System.out.println();
			}
			System.out.print((float) (featureNumber_o[j] - featureNumber[j])
					/ featureNumber_o[j] + " ");
		}
		System.out.println();

		// ===================================================================

		// 获取结果

		int ii = 0;
		int jj = 0;
		int k = 0;
		while (k < 4) {

			// 获取特征点最多的位置
			for (int i = 0; i < 4; i++) {

				for (int j = 0; j < 9; j++) {
					if (count[ii][jj] < count[i][j]) {
						ii = i;
						jj = j;
					} else if (count[ii][jj] == count[i][j]
							&& count[ii][jj] != 0) {
						if (countDistance[ii][jj] > countDistance[i][j]) {
							ii = i;
							jj = j;
						}
					}
				}
			}

			count[ii][jj] = -1;

			// 判断位置是否已经被占用
			boolean isLegal = true;
			if (result[ii] != -1) {
				isLegal = false;
			}
			for (int i = 0; i < result.length; i++) {
				if (jj == result[i]) {
					isLegal = false;
				}
			}
			if (isLegal) {
				result[ii] = jj;
				k++;
			}
		}

		System.out.println("result=" + (result[0] + 1) + (result[1] + 1)
				+ (result[2] + 1) + (result[3] + 1)
				+ "\n============================================");

		DrawResult();
	}

	private List<KeyPoint> getSim(List<KeyPoint> mkptoListS,
			List<KeyPoint> mkptoListH) {
		List<KeyPoint> mkptoListSH = new ArrayList<KeyPoint>();
		for (int i = 0; i < mkptoListH.size(); i++) {

			int x = (int) (mkptoListH.get(i).pt.x);
			int y = (int) (mkptoListH.get(i).pt.y);

			for (int j = 0; j < mkptoListS.size(); j++) {

				int xx = (int) (mkptoListS.get(j).pt.x);
				int yy = (int) (mkptoListS.get(j).pt.y);
				if (Math.sqrt((x - xx) * (x - xx) + (y - yy) * (y - yy)) < 5) {
					// System.out.println("哈哈哈哈哈哈哈哈哈哈哈");
					mkptoListSH.add(mkptoListH.get(i));
					break;
				}

			}

		}
		return mkptoListSH;
	}

	private void ransac(List<DMatch> matchetoList, List<KeyPoint> mkp1toList,
			List<KeyPoint> mkp2toList, Mat gray1, Mat gray2) {

		// -- Localize the object

		MatOfPoint2f[] obj = new MatOfPoint2f[9];
		MatOfPoint2f[] scene = new MatOfPoint2f[9];
		List<Point>[] lp1 = new ArrayList[9];
		List<Point>[] lp2 = new ArrayList[9];
		for (int i = 0; i < 9; i++) {
			lp1[i] = new ArrayList<Point>();
			lp2[i] = new ArrayList<Point>();

			obj[i] = new MatOfPoint2f();
			scene[i] = new MatOfPoint2f();
		}

		for (int i = 0; i < matchetoList.size(); i++) {
			// -- Get the keypoints from the good matches
			int x = (int) (mkp1toList.get(matchetoList.get(i).queryIdx).pt.x * 3 / gray1
					.cols());
			int y = (int) (mkp1toList.get(matchetoList.get(i).queryIdx).pt.y * 3 / gray1
					.rows());

			double keyImgPosition = mkp2toList
					.get(matchetoList.get(i).trainIdx).pt.x;

			lp1[x + y * 3].add(mkp1toList.get(matchetoList.get(i).queryIdx).pt);
			lp2[x + y * 3].add(mkp2toList.get(matchetoList.get(i).trainIdx).pt);

		}

		for (int i = 0; i < 9; i++) {
			obj[i].fromList(lp1[i]);
			scene[i].fromList(lp2[i]);

			Mat H = Calib3d.findHomography(obj[i], scene[i], Calib3d.FM_RANSAC,
					10);

			// -- Get the corners from the image_1 ( the object to be "detected"
			// )

			Point[] pt = new Point[4];

			int left = (i % 3 + 1) * gray1.cols() / 3;
			int right = i % 3 * gray1.cols() / 3;
			int top = (i / 3 + 1) * gray1.rows() / 3;
			int buttom = i / 3 * gray1.rows() / 3;

			for (int m = i / 3 * gray1.rows() / 3; m < (i / 3 + 1)
					* gray1.rows() / 3; m++) {
				for (int n = i % 3 * gray1.cols() / 3; n < (i % 3 + 1)
						* gray1.cols() / 3; n++) {

					if (gray1.get(m, n)[0] < 255 / 2) {
						if (m < top)
							top = m;
						if (m > buttom)
							buttom = m;
						if (n > right)
							right = n;
						if (n < left)
							left = n;
					}

				}
			}
			top--;
			buttom++;
			left--;
			right++;

			pt[0] = new Point(left, top);
			pt[1] = new Point(right, top);
			pt[2] = new Point(right, buttom);
			pt[3] = new Point(left, buttom);

			MatOfPoint2f obj_corners = new MatOfPoint2f(pt);
			MatOfPoint2f scene_corners = new MatOfPoint2f();

			Core.perspectiveTransform(obj_corners, scene_corners, H);

			List<Point> scene_corners2 = scene_corners.toList();

			Scalar sc = new Scalar(0, 255, 255);
			Mat img = gray2.clone();
			Mat img2 = gray1.clone();

			Core.line(img, scene_corners2.get(0), scene_corners2.get(1), sc);
			Core.line(img, scene_corners2.get(1), scene_corners2.get(2), sc);
			Core.line(img, scene_corners2.get(2), scene_corners2.get(3), sc);
			Core.line(img, scene_corners2.get(3), scene_corners2.get(0), sc);

			Core.line(img2, pt[0], pt[1], sc);
			Core.line(img2, pt[1], pt[2], sc);
			Core.line(img2, pt[2], pt[3], sc);
			Core.line(img2, pt[3], pt[0], sc);

			Highgui.imwrite(OutPath + FileName + "_" + i + "ransac.png", img);

		}
	}

	public void Corner3() {

		// 寻找区域
		float[][] count = new float[4][9];
		int[][] countDistance = new int[4][9];
		int[] featureNumber = new int[9];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 9; j++) {
				count[i][j] = 0;
				countDistance[i][j] = 0;
			}
		}

		for (int n = 0; n < 4; n++) {

			for (int i = 0; i < 9; i++) {
				featureNumber[i] = 0;
			}

			Mat gray1 = new Mat();
			Mat gray2 = new Mat();
			Mat desc1 = new Mat();
			Mat desc2 = new Mat();

			// Mat in1 = Highgui.imread("/home/syh/workspace/download.jpg");
			// Mat in2 = Highgui.imread("/home/syh/workspace/snapshot3.png");

			// Imgproc.cvtColor(imgRow[0], gray1, Imgproc.COLOR_BGR2GRAY);
			gray2 = keyWord[n];
			// gray1 = RemoveSomething(gray1);

			// Imgproc.cvtColor(allCandidateWord, gray2,
			// Imgproc.COLOR_BGR2GRAY);
			// gray2 = RemoveSomething(gray2);
			gray1 = allCandidateWord;

			// Highgui.imwrite("/home/syh/workspace/gray1.png", gray1);
			// Highgui.imwrite("/home/syh/workspace/gray2.png", gray2);

			Mat out = new Mat();
			Mat out1 = new Mat(gray1.rows(), gray1.cols(), gray1.type());
			Mat out2 = new Mat(gray2.rows(), gray2.cols(), gray2.type());

			// 获取特征点
			FeatureDetector fd = FeatureDetector.create(FeatureDetector.SIFT);
			MatOfKeyPoint mkp1 = new MatOfKeyPoint();
			MatOfKeyPoint mkp2 = new MatOfKeyPoint();

			fd.detect(gray1, mkp1);
			fd.detect(gray2, mkp2);

			Features2d.drawKeypoints(gray1, mkp1, out1);
			// Highgui.imwrite("/home/syh/workspace/out1.png", out1);

			Features2d.drawKeypoints(gray2, mkp2, out2);
			// Highgui.imwrite("/home/syh/workspace/out2.png", out2);

			// 特征点匹配
			DescriptorExtractor de = DescriptorExtractor
					.create(DescriptorExtractor.SIFT);
			de.compute(gray1, mkp1, desc1);
			de.compute(gray2, mkp2, desc2);

			DescriptorMatcher matcher = DescriptorMatcher
					.create(DescriptorMatcher.BRUTEFORCE_L1);
			// List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();

			MatOfDMatch matche = new MatOfDMatch();

			// matcher.knnMatch(desc1, desc2, matches, 2);
			matcher.match(desc1, desc2, matche);

			// Features2d.drawMatches2(gray1, mkp1, gray2, mkp2, matches, out);
			Features2d.drawMatches(gray1, mkp1, gray2, mkp2, matche, out);

			Highgui.imwrite(OutPath + FileName + "_out.png", out);

			System.out.println(mkp1.toList());

			System.out.println(desc1.rows() + " " + desc2.rows());

			List<DMatch> matchetoList = matche.toList();
			List<KeyPoint> mkp2toList = mkp2.toList();
			List<KeyPoint> mkp1toList = mkp1.toList();

			for (int i = 0; i < matchetoList.size(); i++) {

				int x = (int) (mkp1toList.get(matchetoList.get(i).queryIdx).pt.x * 3 / gray1
						.cols());
				int y = (int) (mkp1toList.get(matchetoList.get(i).queryIdx).pt.y * 3 / gray1
						.rows());

				featureNumber[x + y * 3]++;

				count[n][x + y * 3] += matchetoList.get(i).distance;

				countDistance[n][x + y * 3] += matchetoList.get(i).distance;

			}
		}

		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 9; j++) {

				if (j % 3 == 0) {
					System.out.println();
				}
				count[i][j] = count[i][j] / (float) featureNumber[j];
				System.out.print(count[i][j] + " ");

			}
			System.out.println();

			for (int j = 0; j < 9; j++) {
				if (j % 3 == 0) {
					System.out.println();
				}
				System.out.print(countDistance[i][j] + " ");
			}
			System.out.println();
			System.out.println();
		}

		for (int j = 0; j < 9; j++) {

			if (j % 3 == 0) {
				System.out.println();
			}
			System.out.print(featureNumber[j] + " ");
		}
		System.out.println();

		// 获取结果

		int ii = 0;
		int jj = 0;
		int k = 0;
		while (k < 4) {

			// 获取特征点最多的位置
			for (int i = 0; i < 4; i++) {

				for (int j = 0; j < 9; j++) {
					if (count[ii][jj] > count[i][j]) {
						ii = i;
						jj = j;
					} else if (count[ii][jj] == count[i][j]
							&& count[ii][jj] != 0) {
						if (countDistance[ii][jj] > countDistance[i][j]) {
							ii = i;
							jj = j;
						}
					}
				}
			}

			count[ii][jj] = Integer.MAX_VALUE;

			// 判断位置是否已经被占用
			boolean isLegal = true;
			if (result[ii] != -1) {
				isLegal = false;
			}
			for (int i = 0; i < result.length; i++) {
				if (jj == result[i]) {
					isLegal = false;
				}
			}
			if (isLegal) {
				result[ii] = jj;
				k++;
			}
		}

		System.out.println("result=" + (result[0] + 1) + (result[1] + 1)
				+ (result[2] + 1) + (result[3] + 1)
				+ "\n============================================");

		DrawResult();
	}

	private void DrawResult() {

		Scalar[] sc = new Scalar[4];
		sc[0] = new Scalar(0, 0, 255);
		sc[1] = new Scalar(0, 255, 0);
		sc[2] = new Scalar(255, 0, 0);
		sc[3] = new Scalar(0, 0, 255);

		for (int n = 0; n < result.length; n++) {
			Point pt1 = new Point(imgRow[0].cols() / 4 * n + imgRow[0].cols()
					/ 8 + myShort, mat.rows() / 4 / 2);
			Point pt2 = new Point(mat.cols() / 3 * (result[n] % 3 + 0.5),
					mat.rows() / 4 * (result[n] / 3 + 1 + 0.5));

			Core.line(mat, pt1, pt2, sc[n]);

		}
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				double[] temp = mat.get(i, j);

				if (temp[0] != 0 || temp[1] != 0 || temp[2] != 0) {
					temp[3] = 255;
				}

				mat.put(i, j, temp);

			}
		}
		Highgui.imwrite(OutPath + "/" + FileName + "result.png", mat);

	}

	private void GetKeyWordImage() {

		imgRow[0] = MinImage(imgRow[0], 3, -1);
		Highgui.imwrite(OutPath + FileName + "_00.png", imgRow[0]);

		separatePosition[0] = (imgRow[0].cols() - 2 * more) / 4 * 1 + more;
		separatePosition[1] = (imgRow[0].cols() - 2 * more) / 4 * 2 + more;
		separatePosition[2] = (imgRow[0].cols() - 2 * more) / 4 * 3 + more;

		keyWord[0] = imgRow[0].submat(0, imgRow[0].rows(), 0,
				separatePosition[0] + offset);
		// keyWord[0] = MinImage(keyWord[0], 0, 0);
		Highgui.imwrite(OutPath + FileName + "_000.png", keyWord[0]);

		keyWord[1] = imgRow[0].submat(0, imgRow[0].rows(), separatePosition[0]
				- offset, separatePosition[1] + offset);
		// keyWord[1] = MinImage(keyWord[1], 0, 0);
		Highgui.imwrite(OutPath + FileName + "_001.png", keyWord[1]);

		keyWord[2] = imgRow[0].submat(0, imgRow[0].rows(), separatePosition[1]
				- offset, separatePosition[2] + offset);
		// keyWord[2] = MinImage(keyWord[2], 0, 0);
		Highgui.imwrite(OutPath + FileName + "_002.png", keyWord[2]);

		keyWord[3] = imgRow[0].submat(0, imgRow[0].rows(), separatePosition[2]
				- offset, imgRow[0].cols());
		// keyWord[3] = MinImage(keyWord[3], 0, 0);
		Highgui.imwrite(OutPath + FileName + "_003.png", keyWord[3]);

	}

	/**
	 * 
	 * @param mymat
	 * @param threshold_h
	 *            水平阈值
	 * @param threshold_v
	 *            垂直阈值
	 * @return Mat
	 */
	private Mat MinImage(Mat mymat, int threshold_h, int threshold_v) {
		int left = mymat.cols(), right = 0, top = mymat.rows(), bottom = 0;
		int[] horizontal, vertical;
		horizontal = new int[mymat.cols()];
		vertical = new int[mymat.rows()];
		for (int i = 0; i < horizontal.length; i++) {
			horizontal[i] = 0;
		}
		for (int i = 0; i < vertical.length; i++) {
			vertical[i] = 0;
		}

		// 获取直方图
		for (int i = 0; i < mymat.rows(); i++) {
			for (int j = 0; j < mymat.cols(); j++) {

				/*
				 * if (mymat.get(i, j)[3] > threshold) System.out.print((int)
				 * (mymat.get(i, j)[3])); else System.out.print(".  ");
				 */
				if (mymat.get(i, j)[0] < threshold) {
					horizontal[j]++;
					vertical[i]++;
				}
			}
			// System.out.println("");
		}

		for (int i = 0; i < horizontal.length; i++) {
			if (horizontal[i] > threshold_h) {
				if (left > i)
					left = i;
				if (right < i)
					right = i;

			}
		}

		for (int i = 0; i < vertical.length; i++) {
			if (vertical[i] > threshold_v) {
				if (top > i)
					top = i;
				if (bottom < i)
					bottom = i;

			}
		}
		myShort = left + 1;

		mymat = mymat
				.submat(0, mymat.rows(), left + 1 - more, right + 1 + more);
		return mymat;

	}

	private void GetCandidateWordImage() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				candidateWord[i * 3 + j] = new Mat(imgRow[i + 1].rows(),
						imgRow[i + 1].cols() / 3, imgRow[i + 1].type());
				candidateWord[i * 3 + j] = imgRow[i + 1].submat(0,
						imgRow[i + 1].rows(), candidateWord[i * 3 + j].cols()
								* j, candidateWord[i * 3 + j].cols() * (j + 1));

				// ======================================================================
				candidateWord[i * 3 + j] = MinImage(candidateWord[i * 3 + j],
						0, 0);

				Highgui.imwrite(
						OutPath + FileName + "_" + (i + 1) + j + ".png",
						candidateWord[i * 3 + j]);

			}
		}
	}

	private void genLine(int n) {

		if (found == true)
			return;

		if (n == b.length) {
			for (int i = 0; i < b.length; i++) {
				System.out.print(b[i] + "  ");
			}
			System.out.println("");
		}
		if (n == 0) {
			for (int j = 0; j < imgRow[0].rows(); j++) {
				if (imgRow[0].get(j, 0)[0] < threshold) {
					b[0] = j;
					genLine(n + 1);
				}
			}
		}
		if (n > 0 && n < b.length) {
			int hasMore = 0;
			if (imgRow[0].get(b[n - 1], n)[0] < threshold) {
				b[n] = b[n - 1];
				hasMore = 1;
				genLine(n + 1);
			}
			// else {
			if (b[n - 1] > 0 && imgRow[0].get(b[n - 1] - 1, n)[0] < threshold) {
				b[n] = b[n - 1] - 1;
				hasMore = 1;
				genLine(n + 1);
			}
			if (b[n - 1] < imgRow[0].rows() - 1
					&& imgRow[0].get(b[n - 1] + 1, n)[0] < threshold) {
				b[n] = b[n - 1] + 1;
				hasMore = 1;
				genLine(n + 1);
			}
			// }
			if (n + 25 > imgRow[0].cols() && hasMore == 0) {
				for (int i = 0; i < n; i++) {
					if (b[i] > 9)
						System.out.print(b[i] + " ");
					else
						System.out.print(b[i] + "  ");
				}
				System.out.println("");
				found = true;
			}
		}
	}

	private void AfterGetline() {
		double[] temp = { 255 };
		for (int i = 0; i < b.length; i++) {
			if (imgRow[0].get(b[i] - 1, i)[0] > threshold
					|| imgRow[0].get(b[i] + 1, i)[0] > threshold) {
				imgRow[0].put(b[i], i, temp);
			}

		}
		Highgui.imwrite(OutPath + "AfterGetLine.png", imgRow[0]);
	}

}