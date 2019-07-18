package google;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class MyImgFilter {
	public String FileName;

	public Mat mat;
	public Mat mat2;

	public Mat img[];

	public Mat word[];
	public Mat[] hist;

	public Mat allCandidateWord;
	public Mat candidateWord[];

	private int[][] wordHistogram_h = new int[4][];
	private int[][] candidateWordHistogram_h = new int[9][30];
	private int[][] wordHistogram_v = new int[4][30];
	private int[][] candidateWordHistogram_v = new int[9][30];

	public String OutPath;

	private int threshold = 50;

	private int[] b;
	private boolean found = false;

	private int offset = 1;

	public void SetFileName(String Name) {
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
		mat = Highgui.imread(fileabsolutePath, -1);
		mat2 = Highgui.imread("/home/syh/workspace/11.jpg");
	}

	public void RemoveSomething() {
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {
				double[] temp = mat.get(i, j);
				if (temp[3] < threshold) {
					temp[3] = 0;
				} else {
					temp[3] = 255;
				}
				mat.put(i, j, temp);

			}
		}
		Highgui.imwrite(OutPath + "RemoveSomething.png", mat);
	}

	public void SeparateImage() {

		// RemoveSomething();

		// 垂直划分为四份
		for (int n = 0; n < 4; n++) {
			img[n] = new Mat(mat.rows() / 4, mat.cols(), mat.type());
			img[n] = mat.submat(img[n].rows() * (n), img[n].rows() * (n + 1),
					0, img[n].cols());
			Highgui.imwrite(OutPath + FileName + "_" + n + ".png", img[n]);
		}
		//
		allCandidateWord = new Mat(mat.rows() * 3 / 4, mat.cols(), mat.type());
		allCandidateWord = mat.submat(mat.rows() * 1 / 4, mat.rows(), 0,
				mat.cols());
		Highgui.imwrite(OutPath + FileName + "_allCandidateWord.png",
				allCandidateWord);

		// 追踪线条并去掉
		b = new int[img[0].cols()];
		genLine(0);
		AfterGetline();

		GetWordImage();
		GetCandidateWordImage();

	}

	public void myHist() {

		wordHistogram_h[0] = getHistogram(img[0]);

		for (int i = 0; i < wordHistogram_h[0].length; i++) {
			System.out.print(wordHistogram_h[0][i] + " ");
		}

	}

	public void Hist() {
		for (int n = 0; n < 13; n++) {
			List<Mat> list = new ArrayList<Mat>();
			if (n < 4)
				list.add(word[n]);
			else
				list.add(candidateWord[n - 4]); //
			list.add(word[1]); // list.add(word[2]); // list.add(word[3]);
			hist[n] = new Mat();
			Mat mask = new Mat();
			MatOfInt channels = new MatOfInt(3);
			MatOfInt histSize = new MatOfInt(30);
			MatOfFloat ranges = new MatOfFloat(0, 255);

			Imgproc.calcHist(list, channels, mask, hist[n], histSize, ranges,
					false);

			System.out.println("直方图:");
			for (int i = 0; i < hist[n].rows(); i++) {
				for (int j = 0; j < hist[n].cols(); j++) {
					System.out.print(hist[n].get(i, j)[0] + " ");
				} //
				System.out.println();
			}
			System.out.println();
		}

		int temp = 1;
		for (int n = 4; n < 13; n++) {
			double base_base = Imgproc.compareHist(hist[temp], hist[n],
					Imgproc.CV_COMP_CORREL);

			System.out.println("CV_COMP_CORREL  和" + n + " 结果:" + base_base);
		}
		for (int n = 4; n < 13; n++) {
			double base_base = Imgproc.compareHist(hist[temp], hist[n],
					Imgproc.CV_COMP_INTERSECT);

			System.out.println("CV_COMP_INTERSECT  和" + n + " 结果:" + base_base);
		}
		for (int n = 4; n < 13; n++) {
			double base_base = Imgproc.compareHist(hist[temp], hist[n],
					Imgproc.CV_COMP_CHISQR);

			System.out.println("CV_COMP_CHISQR  和" + n + " 结果:" + base_base);
		}
		for (int n = 4; n < 13; n++) {
			double base_base = Imgproc.compareHist(hist[temp], hist[n],
					Imgproc.CV_COMP_BHATTACHARYYA);

			System.out.println("CV_COMP_BHATTACHARYYA  和" + n + " 结果:"
					+ base_base);
		}
	}

	public void Sift() {

		Mat gray1 = new Mat();
		Mat gray2 = new Mat();
		Mat desc1 = new Mat();
		Mat desc2 = new Mat();

		// Mat in1 = Highgui.imread("/home/syh/workspace/download.jpg");
		// Mat in2 = Highgui.imread("/home/syh/workspace/snapshot3.png");

		Imgproc.cvtColor(mat, gray1, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(mat2, gray2, Imgproc.COLOR_BGR2GRAY);
		Highgui.imwrite("/home/syh/workspace/gray1.png", gray1);
		Highgui.imwrite("/home/syh/workspace/gray2.png", gray2);

		Mat out = new Mat();
		Mat out1 = new Mat(gray1.rows(), gray1.cols(), gray1.type());
		Mat out2 = new Mat(gray2.rows(), gray2.cols(), gray2.type());

		Mat mask = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
		for (int i = 0; i < mask.rows(); i++) {
			for (int j = 0; j < mask.cols(); j++) {
				if (j >= mask.cols() * 3 / 10) {
					double[] temp = { 0 };
					mask.put(i, j, temp);
				} else {
					double[] temp = { 1 };
					mask.put(i, j, temp);
				}
			}
		}

		FeatureDetector fd = FeatureDetector.create(FeatureDetector.SIFT);
		MatOfKeyPoint mkp1 = new MatOfKeyPoint();
		MatOfKeyPoint mkp2 = new MatOfKeyPoint();

		fd.detect(gray1, mkp1, mask);
		fd.detect(gray2, mkp2);

		Features2d.drawKeypoints(gray1, mkp1, out1);
		Highgui.imwrite("/home/syh/workspace/out1.png", out1);

		Features2d.drawKeypoints(gray2, mkp2, out2);
		Highgui.imwrite("/home/syh/workspace/out2.png", out2);

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

		Highgui.imwrite("/home/syh/workspace/out.png", out);

		/*
		 * for (int i = 0; i < desc1.rows(); i++) { for (int j = 0; j <
		 * desc1.cols(); j++) { System.out.print(desc1.get(i, j)[0] + " "); }
		 * System.out.println(); } System.out.println(); for (int i = 0; i <
		 * desc2.rows(); i++) { for (int j = 0; j < desc2.cols(); j++) {
		 * System.out.print(desc2.get(i, j)[0] + " "); } System.out.println(); }
		 */
		System.out.println(mkp1.toList());

		System.out.println(desc1.rows() + " " + desc2.rows());

		float distance = 0;
		int[] count = new int[9];
		for (int i = 0; i < count.length; i++) {
			count[i] = 0;
		}
		for (int i = 0; i < matche.toList().size(); i++) {
			distance += matche.toList().get(i).distance;
			int x = (int) (mkp2.toList().get(matche.toList().get(i).trainIdx).pt.x
					/ gray2.cols() * 3);
			int y = (int) (mkp2.toList().get(matche.toList().get(i).trainIdx).pt.y
					/ gray2.rows() * 3);
			count[x + y * 3]++;
			// System.out.println("x=" + x + " y=" + y);
		}
		for (int i = 0; i < count.length; i++) {
			// System.out.println(count[i]);
		}
		System.out.println("結果:" + distance + " " + matche.toList());

		// ================================================================
		/*
		 * int[][] point1 = new int[gray1.rows()][gray1.cols()]; double[] angle1
		 * = new double[mkp1.toList().size() - 1];
		 * 
		 * for (int i = 0; i < gray1.rows(); i++) { for (int j = 0; j <
		 * gray1.cols(); j++) { point1[i][j] = 0; } }
		 * 
		 * for (int i = 0; i < mkp1.toList().size(); i++) {
		 * 
		 * System.out.println(mkp1.toList().get(i).pt.x + " " +
		 * mkp1.toList().get(i).pt.y); point1[(int) mkp1.toList().get(i).pt.y -
		 * 1][(int) mkp1.toList() .get(i).pt.x - 1] = i + 1;
		 * 
		 * if (i > 0) { angle1[i - 1] = Math.atan2(mkp1.toList().get(i).pt.y -
		 * mkp1.toList().get(0).pt.y, mkp1.toList().get(i).pt.x -
		 * mkp1.toList().get(0).pt.x); } }
		 * 
		 * for (int i = 0; i < angle1.length; i++) {
		 * 
		 * System.out.print(angle1[i] + " "); } System.out.println();
		 * 
		 * for (int i = 0; i < gray1.rows(); i++) { for (int j = 0; j <
		 * gray1.cols(); j++) { if (i == gray1.rows() / 2 && j == gray1.cols() /
		 * 2) { System.out.print("X ");
		 * 
		 * } else { if (point1[i][j] > 9) { System.out.print(point1[i][j]); }
		 * else { System.out.print(point1[i][j] + " "); } }
		 * 
		 * } System.out.println(); }
		 * 
		 * int[][] point2 = new int[gray2.rows()][gray2.cols()]; double[] angle2
		 * = new double[mkp1.toList().size() - 1];
		 * 
		 * for (int i = 0; i < gray2.rows(); i++) { for (int j = 0; j <
		 * gray2.cols(); j++) { point2[i][j] = 0; } }
		 * 
		 * for (int i = 0; i < matche.toList().size(); i++) {
		 * 
		 * System.out .println(matche.toList().get(i).trainIdx + " " +
		 * (mkp2.toList().get( matche.toList().get(i).trainIdx).pt.x) + " " +
		 * (mkp2.toList().get( matche.toList().get(i).trainIdx).pt.y));
		 * point2[(int) mkp2.toList().get(matche.toList().get(i).trainIdx).pt.y
		 * - 1][(int) mkp2 .toList().get(matche.toList().get(i).trainIdx).pt.x -
		 * 1] = i + 1;
		 * 
		 * if (i > 0) { angle2[i - 1] = Math .atan2(mkp2.toList().get(
		 * matche.toList().get(i).trainIdx).pt.y - mkp2.toList().get(
		 * matche.toList().get(0).trainIdx).pt.y, mkp2.toList().get(
		 * matche.toList().get(i).trainIdx).pt.x - mkp2.toList()
		 * .get(matche.toList().get(0).trainIdx).pt.x); } } for (int i = 0; i <
		 * angle2.length; i++) {
		 * 
		 * System.out.print(angle2[i] + " "); } System.out.println(); for (int i
		 * = 0; i < gray2.rows(); i++) { for (int j = 0; j < gray2.cols(); j++)
		 * { if (i == gray2.rows() / 2 && j == gray2.cols() / 2) {
		 * System.out.print("X ");
		 * 
		 * } else { if (point2[i][j] > 9) { System.out.print(point2[i][j]); }
		 * else { System.out.print(point2[i][j] + " "); } } }
		 * System.out.println(); }
		 */
		// ================================================================

		System.out.println("===============================================");

	}

	public void Corner() {

		Mat gray1 = new Mat();
		Mat gray2 = new Mat();
		Mat desc1 = new Mat();
		Mat desc2 = new Mat();

		// Mat in1 = Highgui.imread("/home/syh/workspace/download.jpg");
		// Mat in2 = Highgui.imread("/home/syh/workspace/snapshot3.png");

		Imgproc.cvtColor(mat, gray1, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(mat2, gray2, Imgproc.COLOR_BGR2GRAY);
		Highgui.imwrite("/home/syh/workspace/gray1.png", gray1);
		Highgui.imwrite("/home/syh/workspace/gray2.png", gray2);

		Mat out = new Mat();
		Mat out1 = new Mat(gray1.rows(), gray1.cols(), gray1.type());
		Mat out2 = new Mat(gray2.rows(), gray2.cols(), gray2.type());

		Mat mask = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
		for (int i = 0; i < mask.rows(); i++) {
			for (int j = 0; j < mask.cols(); j++) {
				if (j >= mask.cols() * 3 / 10) {
					double[] temp = { 0 };
					mask.put(i, j, temp);
				} else {
					double[] temp = { 1 };
					mask.put(i, j, temp);
				}
			}
		}

		FeatureDetector fd = FeatureDetector.create(FeatureDetector.HARRIS);
		MatOfKeyPoint mkp1 = new MatOfKeyPoint();
		MatOfKeyPoint mkp2 = new MatOfKeyPoint();

		fd.detect(gray1, mkp1, mask);
		fd.detect(gray2, mkp2);

		Features2d.drawKeypoints(gray1, mkp1, out1);
		Highgui.imwrite("/home/syh/workspace/out1.png", out1);

		Features2d.drawKeypoints(gray2, mkp2, out2);
		Highgui.imwrite("/home/syh/workspace/out2.png", out2);

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

		Highgui.imwrite("/home/syh/workspace/out.png", out);

		/*
		 * for (int i = 0; i < desc1.rows(); i++) { for (int j = 0; j <
		 * desc1.cols(); j++) { System.out.print(desc1.get(i, j)[0] + " "); }
		 * System.out.println(); } System.out.println(); for (int i = 0; i <
		 * desc2.rows(); i++) { for (int j = 0; j < desc2.cols(); j++) {
		 * System.out.print(desc2.get(i, j)[0] + " "); } System.out.println(); }
		 */
		System.out.println(mkp1.toList());

		System.out.println(desc1.rows() + " " + desc2.rows());

		float distance = 0;
		int[] count = new int[9];
		for (int i = 0; i < count.length; i++) {
			count[i] = 0;
		}
		for (int i = 0; i < matche.toList().size(); i++) {
			distance += matche.toList().get(i).distance;
			int x = (int) (mkp2.toList().get(matche.toList().get(i).trainIdx).pt.x
					/ gray2.cols() * 3);
			int y = (int) (mkp2.toList().get(matche.toList().get(i).trainIdx).pt.y
					/ gray2.rows() * 3);
			count[x + y * 3]++;
			// System.out.println("x=" + x + " y=" + y);
		}
		for (int i = 0; i < count.length; i++) {
			// System.out.println(count[i]);
		}
		System.out.println("結果:" + distance + " " + matche.toList());

	}

	public void GetWordImage() {

		img[0] = MinImage(img[0], 5, 0);
		Highgui.imwrite(OutPath + FileName + "_00.png", img[0]);

		word[0] = img[0].submat(0, img[0].rows(), 0, img[0].cols() * 1 / 4
				+ offset);
		word[0] = MinImage(word[0], 0, 0);
		Highgui.imwrite(OutPath + FileName + "_000.png", word[0]);

		word[1] = img[0].submat(0, img[0].rows(), img[0].cols() * 1 / 4
				- offset, img[0].cols() * 2 / 4 + offset);
		word[1] = MinImage(word[1], 0, 0);
		Highgui.imwrite(OutPath + FileName + "_001.png", word[1]);

		word[2] = img[0].submat(0, img[0].rows(), img[0].cols() * 2 / 4
				- offset, img[0].cols() * 3 / 4 + offset);
		word[2] = MinImage(word[2], 0, 0);
		Highgui.imwrite(OutPath + FileName + "_002.png", word[2]);

		word[3] = img[0].submat(0, img[0].rows(), img[0].cols() * 3 / 4
				- offset, img[0].cols());
		word[3] = MinImage(word[3], 0, 0);
		Highgui.imwrite(OutPath + FileName + "_003.png", word[3]);

	}

	public Mat MinImage(Mat mymat, int threshold_h, int threshold_v) {
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
				if (mymat.get(i, j)[3] > threshold) {
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

		mymat = mymat.submat(top + 1, bottom + 1, left + 1, right + 1);
		return mymat;

	}

	public void GetCandidateWordImage() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				candidateWord[i * 3 + j] = new Mat(img[i + 1].rows(),
						img[i + 1].cols() / 3, img[i + 1].type());
				candidateWord[i * 3 + j] = img[i + 1].submat(0,
						img[i + 1].rows(), candidateWord[i * 3 + j].cols() * j,
						candidateWord[i * 3 + j].cols() * (j + 1));

				// ======================================================================
				candidateWord[i * 3 + j] = MinImage(candidateWord[i * 3 + j],
						0, 0);

				Highgui.imwrite(
						OutPath + FileName + "_" + (i + 1) + j + ".png",
						candidateWord[i * 3 + j]);

			}
		}
	}

	public void genLine(int n) {

		if (found == true)
			return;

		if (n == b.length) {
			for (int i = 0; i < b.length; i++) {
				System.out.print(b[i] + "  ");
			}
			System.out.println("");
		}
		if (n == 0) {
			for (int j = 0; j < img[0].rows(); j++) {
				if (img[0].get(j, 0)[3] > threshold) {
					b[0] = j;
					genLine(n + 1);
				}
			}
		}
		if (n > 0 && n < b.length) {
			int hasMore = 0;
			if (img[0].get(b[n - 1], n)[3] > threshold) {
				b[n] = b[n - 1];
				hasMore = 1;
				genLine(n + 1);
			}
			// else {
			if (b[n - 1] > 0 && img[0].get(b[n - 1] - 1, n)[3] > threshold) {
				b[n] = b[n - 1] - 1;
				hasMore = 1;
				genLine(n + 1);
			}
			if (b[n - 1] < img[0].rows() - 1
					&& img[0].get(b[n - 1] + 1, n)[3] > threshold) {
				b[n] = b[n - 1] + 1;
				hasMore = 1;
				genLine(n + 1);
			}
			// }
			if (n + 25 > img[0].cols() && hasMore == 0) {
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

	void AfterGetline() {
		for (int i = 0; i < b.length; i++) {
			if (img[0].get(b[i] + 1, i)[3] > threshold
					|| img[0].get(b[i] - 1, i)[3] > threshold) {
				continue;
			}

			double[] temp = img[0].get(b[i], i);
			temp[3] = 0;
			img[0].put(b[i], i, temp);
		}
		Highgui.imwrite(OutPath + "AfterGetLine.png", img[0]);
	}

	int[] getHistogram(Mat mymat) {
		int[] horizontal;
		horizontal = new int[mymat.cols()];

		for (int i = 0; i < horizontal.length; i++) {
			horizontal[i] = 0;
		}

		// 获取直方图
		for (int i = 0; i < mymat.rows(); i++) {
			for (int j = 0; j < mymat.cols(); j++) {

				if (mymat.get(i, j)[3] > threshold) {
					horizontal[j] += mymat.get(i, j)[3];

				}
			}
		}

		for (int i = 0; i < horizontal.length; i++) {
			System.out.print(horizontal[i] + " ");
		}
		System.out.println();
		return horizontal;

	}

	double getBDistance(int[] a, int[] b) {
		double result = 0;
		int aa = 0, bb = 0;
		for (int i = 0; i < a.length; i++) {
			aa += a[i];
			bb += b[i];
			// System.out.println(a[i] + " ");
		}
		for (int i = 0; i < a.length; i++) {
			result += Math.sqrt((double) a[i] * (double) b[i] / (double) aa
					/ (double) bb);
		}
		// System.out.println(result);
		result = -Math.log(result);

		return result;
	}
}