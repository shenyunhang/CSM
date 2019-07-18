package touclick;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

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

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class CopyOfMyImgFilter {
	BufferedImage image;
	private int iw, ih;
	private int[] pixels;
	public static String OutPath;

	private String FileName;

	// private String[] fileabsolutePath = new String[5];

	private Mat mat1;
	private Mat mat2;

	private int radius1;
	private int radius2;
	private int line;
	private int spaceSize = 60;
	private Point cpt_l;
	private Point cpt_r;

	public CopyOfMyImgFilter(String fileabsolutePath, String word) throws IOException {

		// 文字转换为图片
		try {
			word2img(word);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		SetFileName(fileabsolutePath);

		// 读取验证码图片并预处理
		FileInputStream fin = new FileInputStream(fileabsolutePath);
		image = ImageIO.read(fin);
		iw = image.getWidth();
		ih = image.getHeight();
		pixels = new int[iw * ih];

		changeGrey();

		// getGrey();

		// flt.getMedian();
		// getBrighten();

		// flt.RemoveNoise();
		saveImage(OutPath + "/" + FileName + "gray2.png");

		// 读取文字图片和原始验证码图片
		mat1 = Highgui.imread("word.jpg");
		mat2 = Highgui.imread(fileabsolutePath);

		// Highgui.imwrite(OutPath + "/" + FileName + "mat1.jpg", mat1);

		// 计算圆圈半径
		radius1 = word.substring(0, word.indexOf(" ")).length() * 18;
		radius2 = word.substring(word.indexOf(" ") + 1).length() * 18;
		line = word.length() * spaceSize / 2;
		cpt_l = new Point(word.length() * spaceSize / 4, spaceSize / 2);
		cpt_r = new Point(word.length() * spaceSize / 4 * 3, spaceSize / 2);

		if (word.contains("梦")) {
			// radius2 += 18;
		}

		System.out.println(word.indexOf(" ") + " " + radius1 + " " + radius2
				+ " " + line);
	}

	private void saveImage(String fileOutPath) {
		String pname = fileOutPath;
		File file = new File(pname);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
	}

	private Mat RemoveSomething(Mat mat) {
		int all = 0;
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {

				if (mat.get(i, j)[0] > 255 / 2) {
					// double[] temp = { 255 };
					// mat.put(i, j, temp);
					all--;
				} else {
					// double[] temp = { 0 };
					// mat.put(i, j, temp);
					all++;
				}

			}
		}

		if (all > 0) {
			for (int i = 0; i < mat.rows(); i++) {
				for (int j = 0; j < mat.cols(); j++) {

					double[] temp = { 255 - mat.get(i, j)[0] };
					mat.put(i, j, temp);

				}
			}
		}

		// Highgui.imwrite(OutPath + "/" + FileName + "RemoveSomething.png",
		// mat);
		return mat;
	}

	private void word2img(String word) throws FileNotFoundException,
			IOException, FontFormatException {

		// 载入字体
		File file = new java.io.File("STHeiti-Light.ttc");
		java.io.FileInputStream fi = new java.io.FileInputStream(file);
		java.io.BufferedInputStream fb = new java.io.BufferedInputStream(fi);
		Font nf;
		nf = Font.createFont(Font.TRUETYPE_FONT, fb);
		nf = nf.deriveFont(Font.BOLD, 30);
		// System.out.println(nf.getFontName());
		// System.out.println(nf.getSize());

		// 获取全部字体库
		// java.awt.GraphicsEnvironment eq = java.awt.GraphicsEnvironment
		// .getLocalGraphicsEnvironment();
		// String[] fontName = eq.getAvailableFontFamilyNames();
		// for (int i = 0; i < fontName.length; i++) {

		// System.out.println(fontName[i]);
		// }

		// 画布

		BufferedImage image = new BufferedImage(spaceSize * word.length(),
				spaceSize, BufferedImage.TYPE_INT_BGR);
		// ImageIO.read(new FileInputStream("nullRemoveSomething.png"));

		Graphics2D g = image.createGraphics();// 得到图形上下文
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(Color.BLACK); // 设置画笔颜色
		// 设置字体
		g.setFont(new Font("WenQuanYi Micro Hei", Font.LAYOUT_LEFT_TO_RIGHT, 30));
		// g.setFont(nf);
		System.out.println("字数" + word.length());

		int k = word.indexOf(" ");
		g.drawString(word.substring(0, k), spaceSize * word.length() / 4
				- g.getFont().getSize() / 2 * (k - 0), spaceSize / 2
				+ g.getFont().getSize() / 2);

		g.drawString(word.substring(k, word.length()),
				spaceSize * word.length() * 3 / 4 - g.getFont().getSize() / 2
						* (word.length() - k - 1), spaceSize / 2
						+ g.getFont().getSize() / 2);
		/*
		 * for (int i = 0; i < word.length(); i++) {
		 * g.drawString(word.substring(i, i + 1), spaceSize * i + spaceSize / 2
		 * - g.getFont().getSize() / 2, spaceSize / 2 + g.getFont().getSize() /
		 * 2); }
		 */

		g.dispose();
		FileOutputStream out = new FileOutputStream("word.jpg");
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(image);
		out.close();
	}

	void Sift() {
		Mat gray1 = new Mat();
		Mat gray2 = new Mat();
		Mat desc1 = new Mat();
		Mat desc2 = new Mat();

		Mat gray2_p = Highgui.imread(OutPath + "/" + FileName + "gray2.png");

		Imgproc.cvtColor(mat1, gray1, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(gray2_p, gray2, Imgproc.COLOR_BGR2GRAY);

		gray2 = RemoveSomething(gray2);

		Highgui.imwrite(OutPath + "/" + FileName + "gray1.png", gray1);
		Highgui.imwrite(OutPath + "/" + FileName + "gray2.png", gray2);

		Mat out = new Mat();
		Mat out1 = new Mat(gray1.rows(), gray1.cols(), gray1.type());
		Mat out2 = new Mat(gray2.rows(), gray2.cols(), gray2.type());

		// 获取特征点
		FeatureDetector fd = FeatureDetector
				.create(FeatureDetector.PYRAMID_HARRIS);
		MatOfKeyPoint mkp1 = new MatOfKeyPoint();
		MatOfKeyPoint mkp2 = new MatOfKeyPoint();

		fd.detect(gray1, mkp1);
		fd.detect(gray2, mkp2);

		Features2d.drawKeypoints(gray1, mkp1, out1);
		Highgui.imwrite(OutPath + "/" + FileName + "out1.png", out1);

		Features2d.drawKeypoints(gray2, mkp2, out2);
		Highgui.imwrite(OutPath + "/" + FileName + "out2.png", out2);

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

		/*
		List<DMatch> matchetoList_temp = matche.toList();
		List<DMatch> matchetoList_new = new ArrayList<DMatch>();
		// 筛选
		double max_dist = 0;
		double min_dist = 1000;

		// -- Quick calculation of max and min distances between keypoints
		for (int i = 0; i < matchetoList_temp.size(); i++) {
			double dist = matchetoList_temp.get(i).distance;
			if (dist < min_dist)
				min_dist = dist;
			if (dist > max_dist)
				max_dist = dist;
		}

		for (int i = 0; i < matchetoList_temp.size(); i++) {
			if (matchetoList_temp.get(i).distance < 5 * min_dist) {
				matchetoList_new.add(matchetoList_temp.get(i));
			}
		}
		matche.fromList(matchetoList_new);
		*/
		

		// Features2d.drawMatches2(gray1, mkp1, gray2, mkp2, matches, out);
		Features2d.drawMatches(gray1, mkp1, gray2, mkp2, matche, out);

		Highgui.imwrite(OutPath + "/" + FileName + "out.png", out);

		// 选找区域
		System.out.println(mkp1.toList());

		System.out.println(desc1.rows() + " " + desc2.rows());
		List<DMatch> matchetoList = matche.toList();
		List<KeyPoint> mkp2toList = mkp2.toList();
		List<KeyPoint> mkp1toList = mkp1.toList();

		int[] count1 = new int[matchetoList.size()];
		int[] count2 = new int[matchetoList.size()];
		for (int i = 0; i < count1.length; i++) {
			count1[i] = 0;
			count2[i] = 0;
		}

		for (int i = 0; i < matchetoList.size(); i++) {
			double x = mkp2toList.get(matchetoList.get(i).trainIdx).pt.x;
			double y = mkp2toList.get(matchetoList.get(i).trainIdx).pt.y;

			double xQuery = mkp1toList.get(matchetoList.get(i).queryIdx).pt.x;

			for (int j = 0; j < matche.toList().size(); j++) {

				double xxQuery = mkp1toList.get(matchetoList.get(j).queryIdx).pt.x;

				// 判断是否同一个字
				if ((xQuery - line) * (xxQuery - line) < 0 || i == j) {
					continue;
				}

				double xx = mkp2toList.get(matchetoList.get(j).trainIdx).pt.x;
				double yy = mkp2toList.get(matchetoList.get(j).trainIdx).pt.y;

				double keyDistance = Math.sqrt(Math.pow((x - xx), 2)
						+ Math.pow((y - yy), 2));

				if (xQuery < line && keyDistance <= radius1) {
					count1[i]++;

				} else if (xQuery > line && keyDistance <= radius2) {
					count2[i]++;
				}

			}

		}
		/*
		 * for (int i = 0; i < matchetoList.size(); i++) { double xCenterTrain =
		 * mkp2toList.get(matchetoList.get(i).trainIdx).pt.x; double
		 * yCenterTrain = mkp2toList.get(matchetoList.get(i).trainIdx).pt.y;
		 * 
		 * double xCenterQuery =
		 * mkp1toList.get(matchetoList.get(i).queryIdx).pt.x; double
		 * yCenterQuery = mkp1toList.get(matchetoList.get(i).queryIdx).pt.y;
		 * 
		 * for (int j = 0; j < matche.toList().size(); j++) {
		 * 
		 * double xQuery = mkp1toList.get(matchetoList.get(j).queryIdx).pt.x;
		 * double yQuery = mkp1toList.get(matchetoList.get(j).queryIdx).pt.y;
		 * 
		 * // 判断是否同一个字 if ((xCenterQuery - line) * (xQuery - line) < 0 || i ==
		 * j) { continue; }
		 * 
		 * double xTrain = mkp2toList.get(matchetoList.get(j).trainIdx).pt.x;
		 * double yTrain = mkp2toList.get(matchetoList.get(j).trainIdx).pt.y;
		 * 
		 * double keyDistance1 = Math.sqrt(Math.pow( (xCenterTrain - xTrain), 2)
		 * + Math.pow((yCenterTrain - yTrain), 2)); double keyDistance2 =
		 * Math.sqrt(Math.pow( (xCenterQuery - xQuery), 2) +
		 * Math.pow((yCenterQuery - yQuery), 2));
		 * 
		 * if (xCenterQuery < line && keyDistance1 <= radius1 && keyDistance2 <=
		 * radius1) { count1[i]++;
		 * 
		 * } else if (xCenterQuery > line && keyDistance1 <= radius2 &&
		 * keyDistance2 <= radius2) { count2[i]++; }
		 * 
		 * }
		 * 
		 * }
		 */

		int temp1 = 0;
		int temp2 = 0;

		for (int i = 0; i < count1.length; i++) {

			if (count1[temp1] < count1[i]) {
				temp1 = i;
			}
			if (count2[temp2] < count2[i]) {
				temp2 = i;
			}
		}
		System.out.println("temp1=" + temp1 + " temp2=" + temp2);

		double x1 = mkp2toList.get(matchetoList.get(temp1).trainIdx).pt.x;
		double y1 = mkp2toList.get(matchetoList.get(temp1).trainIdx).pt.y;

		double x2 = mkp2toList.get(matchetoList.get(temp2).trainIdx).pt.x;
		double y2 = mkp2toList.get(matchetoList.get(temp2).trainIdx).pt.y;

		double centerDistance = Math.sqrt(Math.pow((x1 - x2), 2)
				+ Math.pow((y1 - y2), 2));
		while (centerDistance < radius2 || centerDistance < radius1) {
			if (temp1 >= temp2) {
				count2[temp2] = 0;
				// count[temp1] = 0;

				for (int i = 0; i < count2.length; i++) {
					if (count2[temp2] < count2[i]) {
						temp2 = i;
					}
				}
				if (count2[temp2] == 0)
					break;
				x2 = mkp2toList.get(matchetoList.get(temp2).trainIdx).pt.x;
				y2 = mkp2toList.get(matchetoList.get(temp2).trainIdx).pt.y;
			} else {
				// count[temp2] = 0;
				count1[temp1] = 0;

				for (int i = 0; i < count1.length; i++) {
					if (count1[temp1] < count1[i]) {
						temp1 = i;
					}
				}
				if (count1[temp1] == 0)
					break;
				x1 = mkp2toList.get(matchetoList.get(temp1).trainIdx).pt.x;
				y1 = mkp2toList.get(matchetoList.get(temp1).trainIdx).pt.y;
			}
			System.out.println("temp1=" + temp1 + " temp2=" + temp2);
			centerDistance = Math.sqrt(Math.pow((x1 - x2), 2)
					+ Math.pow((y1 - y2), 2));
		}

		//Verification(matchetoList, mkp1toList, mkp2toList, temp1, radius1);
		//Verification(matchetoList, mkp1toList, mkp2toList, temp2, radius2);

		ransac(matchetoList, mkp1toList, mkp2toList);

		Point pt1 = new Point(x1, y1);
		Point pt2 = new Point(x2, y2);

		Scalar sc = new Scalar(0, 0, 255);

		//Core.circle(mat2, pt1, radius1, sc);
		//Core.circle(mat2, pt2, radius2, sc);

		Highgui.imwrite(OutPath + "/" + FileName + "result.png", mat2);

		System.out.println("結果:" + matchetoList);
	}

	private void ransac(List<DMatch> matchetoList, List<KeyPoint> mkp1toList,
			List<KeyPoint> mkp2toList) {

		// -- Localize the object
		MatOfPoint2f obj = new MatOfPoint2f();
		MatOfPoint2f scene = new MatOfPoint2f();
		List<Point> lp1 = new ArrayList<Point>();
		List<Point> lp2 = new ArrayList<Point>();
		for (int i = 0; i < matchetoList.size(); i++) {
			// -- Get the keypoints from the good matches
			if (mkp1toList.get(matchetoList.get(i).queryIdx).pt.x < line) {
				lp1.add(mkp1toList.get(matchetoList.get(i).queryIdx).pt);
				lp2.add(mkp2toList.get(matchetoList.get(i).trainIdx).pt);
			}

		}
		obj.fromList(lp1);
		scene.fromList(lp2);

		Mat H = Calib3d.findHomography(obj, scene, Calib3d.FM_RANSAC, 5);

		// -- Get the corners from the image_1 ( the object to be "detected" )

		MatOfPoint2f obj_center = new MatOfPoint2f(cpt_l);
		MatOfPoint2f scene_center = new MatOfPoint2f();

		Core.perspectiveTransform(obj_center, scene_center, H);

		Scalar sc = new Scalar(0, 0, 255);
		Core.circle(mat2, scene_center.toList().get(0), radius1, sc);

		// ======================================================================
		obj = new MatOfPoint2f();
		scene = new MatOfPoint2f();
		lp1 = new ArrayList<Point>();
		lp2 = new ArrayList<Point>();
		for (int i = 0; i < matchetoList.size(); i++) {
			// -- Get the keypoints from the good matches
			if (mkp1toList.get(matchetoList.get(i).queryIdx).pt.x > line) {
				lp1.add(mkp1toList.get(matchetoList.get(i).queryIdx).pt);
				lp2.add(mkp2toList.get(matchetoList.get(i).trainIdx).pt);
			}

		}
		obj.fromList(lp1);
		scene.fromList(lp2);

		H = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 5);

		// -- Get the corners from the image_1 ( the object to be "detected" )

		obj_center = new MatOfPoint2f(cpt_r);
		scene_center = new MatOfPoint2f();

		Core.perspectiveTransform(obj_center, scene_center, H);

		Core.circle(mat2, scene_center.toList().get(0), radius2, sc);

	}

	private boolean Verification(List<DMatch> matchetoList,
			List<KeyPoint> mkp1toList, List<KeyPoint> mkp2toList, int temp,
			int radius) {
		double xTrain = mkp2toList.get(matchetoList.get(temp).trainIdx).pt.x;
		double yTrain = mkp2toList.get(matchetoList.get(temp).trainIdx).pt.y;

		double xQuery = mkp1toList.get(matchetoList.get(temp).queryIdx).pt.x;
		double yQuery = mkp1toList.get(matchetoList.get(temp).queryIdx).pt.y;

		float countTrain = 0;
		float countQuery = 0;

		for (int j = 0; j < matchetoList.size(); j++) {

			double xxQuery = mkp1toList.get(matchetoList.get(j).queryIdx).pt.x;

			// 判断是否同一个字
			if ((xQuery - line) * (xxQuery - line) < 0 || temp == j) {
				continue;
			}

			double xxTrain = mkp2toList.get(matchetoList.get(j).trainIdx).pt.x;
			double yyTrain = mkp2toList.get(matchetoList.get(j).trainIdx).pt.y;

			double keyDistance = Math.sqrt(Math.pow((xTrain - xxTrain), 2)
					+ Math.pow((yTrain - yyTrain), 2));

			if (keyDistance <= radius) {
				countTrain++;
			} else {
				continue;
			}

			xxQuery = mkp1toList.get(matchetoList.get(j).queryIdx).pt.x;
			double yyQuery = mkp1toList.get(matchetoList.get(j).queryIdx).pt.y;

			keyDistance = Math.sqrt(Math.pow((xQuery - xxQuery), 2)
					+ Math.pow((yQuery - yyQuery), 2));

			if (keyDistance <= radius) {
				countQuery++;
			}

		}
		System.out.println(countQuery + " " + countTrain);
		System.out.println("countQuery/countTrain=" + 1.0 * countQuery
				/ countTrain);

		return false;
	}

	/** 图像二值化 */
	private BufferedImage changeGrey() {

		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,
				pixels, 0, iw);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 设定二值化的域值，默认值为85
		int grey = 85;
		// 对图像进行二值化处理，Alpha值保持不变
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 0; i < iw * ih; i++) {
			int red, green, blue;
			int alpha = cm.getAlpha(pixels[i]);
			if (cm.getRed(pixels[i]) > grey) {
				red = 255;
			} else {
				red = 0;
			}
			if (cm.getGreen(pixels[i]) > grey) {
				green = 255;
			} else {
				green = 0;
			}
			if (cm.getBlue(pixels[i]) > grey) {
				blue = 255;
			} else {
				blue = 0;
			}
			pixels[i] = alpha << 24 | red << 16 | green << 8 | blue; // 通过移位重新构成某一点像素的RGB值
		}

		// 将数组中的象素产生一个图像
		Image tempImg = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(iw, ih, pixels, 0, iw));
		image = new BufferedImage(tempImg.getWidth(null),
				tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
		image.createGraphics().drawImage(tempImg, 0, 0, null);
		return image;
	}

	/** 中值滤波 */
	private BufferedImage getMedian() {
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,
				pixels, 0, iw);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 对图像进行中值滤波，Alpha值保持不变
		ColorModel cm = ColorModel.getRGBdefault();
		for (int i = 1; i < ih - 1; i++) {
			for (int j = 1; j < iw - 1; j++) {
				int red, green, blue;
				int alpha = cm.getAlpha(pixels[i * iw + j]);
				// int red2 = cm.getRed(pixels[(i - 1) * iw + j]);
				int red4 = cm.getRed(pixels[i * iw + j - 1]);
				int red5 = cm.getRed(pixels[i * iw + j]);
				int red6 = cm.getRed(pixels[i * iw + j + 1]);
				// int red8 = cm.getRed(pixels[(i + 1) * iw + j]);
				// 水平方向进行中值滤波
				if (red4 >= red5) {
					if (red5 >= red6) {
						red = red5;
					} else {
						if (red4 >= red6) {
							red = red6;
						} else {
							red = red4;
						}
					}
				} else {
					if (red4 > red6) {
						red = red4;
					} else {
						if (red5 > red6) {
							red = red6;
						} else {
							red = red5;
						}
					}
				}
				int green4 = cm.getGreen(pixels[i * iw + j - 1]);
				int green5 = cm.getGreen(pixels[i * iw + j]);
				int green6 = cm.getGreen(pixels[i * iw + j + 1]);
				// 水平方向进行中值滤波
				if (green4 >= green5) {
					if (green5 >= green6) {
						green = green5;
					} else {
						if (green4 >= green6) {
							green = green6;
						} else {
							green = green4;
						}
					}
				} else {
					if (green4 > green6) {
						green = green4;
					} else {
						if (green5 > green6) {
							green = green6;
						} else {
							green = green5;
						}
					}
				}
				// int blue2 = cm.getBlue(pixels[(i - 1) * iw + j]);
				int blue4 = cm.getBlue(pixels[i * iw + j - 1]);
				int blue5 = cm.getBlue(pixels[i * iw + j]);
				int blue6 = cm.getBlue(pixels[i * iw + j + 1]);
				// int blue8 = cm.getBlue(pixels[(i + 1) * iw + j]);
				// 水平方向进行中值滤波
				if (blue4 >= blue5) {
					if (blue5 >= blue6) {
						blue = blue5;
					} else {
						if (blue4 >= blue6) {
							blue = blue6;
						} else {
							blue = blue4;
						}
					}
				} else {
					if (blue4 > blue6) {
						blue = blue4;
					} else {
						if (blue5 > blue6) {
							blue = blue6;
						} else {
							blue = blue5;
						}
					}
				}
				pixels[i * iw + j] = alpha << 24 | red << 16 | green << 8
						| blue;
			}
		}
		// 将数组中的象素产生一个图像
		Image tempImg = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(iw, ih, pixels, 0, iw));
		image = new BufferedImage(tempImg.getWidth(null),
				tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
		image.createGraphics().drawImage(tempImg, 0, 0, null);
		return image;
	}

	private BufferedImage getGrey() {
		ColorConvertOp ccp = new ColorConvertOp(
				ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		return image = ccp.filter(image, null);
	}

	// Brighten using a linear formula that increases all color values
	private BufferedImage getBrighten() {
		RescaleOp rop = new RescaleOp(1.25f, 0, null);
		return image = rop.filter(image, null);
	}

	// Blur by "convolving" the image with a matrix
	private BufferedImage getBlur() {
		float[] data = { .1111f, .1111f, .1111f, .1111f, .1111f, .1111f,
				.1111f, .1111f, .1111f, };
		ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
		return image = cop.filter(image, null);
	}

	// Sharpen by using a different matrix
	private BufferedImage getSharpen() {
		float[] data = { 0.0f, -0.75f, 0.0f, -0.75f, 4.0f, -0.75f, 0.0f,
				-0.75f, 0.0f };
		ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
		return image = cop.filter(image, null);
	}

	// 11) Rotate the image 180 degrees about its center point
	private BufferedImage getRotate() {
		AffineTransformOp atop = new AffineTransformOp(
				AffineTransform.getRotateInstance(Math.PI,
						image.getWidth() / 2, image.getHeight() / 2),
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return image = atop.filter(image, null);
	}

	private int GetGreyValue(int PixelsValue) {
		int Value;
		ColorModel cm = ColorModel.getRGBdefault();
		// int alpha = cm.getAlpha(PixelsValue);
		int r = cm.getRed(PixelsValue);
		int g = cm.getGreen(PixelsValue);
		int b = cm.getBlue(PixelsValue);
		Value = (int) (r * 0.3 + g * 0.59 + b * 0.11);
		return Value;
	}

	private void RemoveNoise() {
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,
				pixels, 0, iw);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 对图像进行二值化处理
		for (int i = 0; i < ih; i++) {
			for (int j = 0; j < iw; j++) {
				int Value = (int) GetGreyValue(pixels[i * iw + j]);
				if (Value != 0) {
					pixels[i * iw + j] = 255 << 24 | 255 << 16 | 255 << 8 | 255; // 通过移位重新构成某一点像素的RGB值
				} else {
					pixels[i * iw + j] = 0 << 24 | 0 << 16 | 0 << 8 | 0; // 通过移位重新构成某一点像素的RGB值
					// ColorModel cm = ColorModel.getRGBdefault();
					// int alpha = cm.getAlpha(pixels[i*iw+j]);
					// int r = cm.getRed(pixels[i*iw+j]);
					// int g = cm.getGreen(pixels[i*iw+j]);
					// int b = cm.getBlue(pixels[i*iw+j]);
					// System.out.println(alpha+":"+r+":"+g+":"+b);
				}
			}
		}

		for (int i = 1; i < ih - 1; i++) {
			for (int j = 1; j < iw - 1; j++) {
				int Value = (int) GetGreyValue(pixels[i * iw + j]);// (r*0.3+g*0.59+b*0.11);
				int CutValue = 1;

				// System.out.println(i+":"+j+"="+Value);

				if (Value == 0) {
					int Count = 0;
					if (GetGreyValue(pixels[(i - 1) * iw + (j - 1)]) >= CutValue)
						Count++;
					if (GetGreyValue(pixels[(i - 1) * iw + (j)]) >= CutValue)
						Count++;
					if (GetGreyValue(pixels[(i - 1) * iw + (j + 1)]) >= CutValue)
						Count++;
					if (GetGreyValue(pixels[(i) * iw + (j - 1)]) >= CutValue)
						Count++;
					if (GetGreyValue(pixels[(i) * iw + (j + 1)]) >= CutValue)
						Count++;
					if (GetGreyValue(pixels[(i + 1) * iw + (j - 1)]) >= CutValue)
						Count++;
					if (GetGreyValue(pixels[(i + 1) * iw + (j)]) >= CutValue)
						Count++;
					if (GetGreyValue(pixels[(i + 1) * iw + (j + 1)]) >= CutValue)
						Count++;
					if (Count >= 8)
						pixels[i * iw + j] = 255 << 24 | 255 << 16 | 255 << 8
								| 255; // 通过移位重新构成某一点像素的RGB值
					// if((GetGreyValue(pixels[(i-1)*iw+(j-1)])>=CutValue &&
					// GetGreyValue(pixels[(i-1)*iw+(j)])>=CutValue &&
					// GetGreyValue(pixels[(i-1)*iw+(j+1)])>=CutValue &&
					// GetGreyValue(pixels[(i)*iw+(j-1)])>=CutValue &&
					// GetGreyValue(pixels[(i)*iw+(j+1)])>=CutValue &&
					// GetGreyValue(pixels[(i+1)*iw+(j-1)])>=CutValue &&
					// GetGreyValue(pixels[(i+1)*iw+(j)])>=CutValue &&
					// GetGreyValue(pixels[(i+1)*iw+(j+1)])>=CutValue))
					// pixels[i*iw+j] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
					// //通过移位重新构成某一点像素的RGB值
					// pixels[i*ih+j]=(new Color(255,255,255)).getRGB();
				} else {
					// pixels[i*iw+j] = 0 << 24 | 0 << 16 | 0 << 8 | 0;
					// //通过移位重新构成某一点像素的RGB值
					// pixels[i*ih+j]=(new Color(255,0,0)).getRGB();
				}
			}
		}

		// 将数组中的象素产生一个图像
		Image tempImg = Toolkit.getDefaultToolkit().createImage(
				new MemoryImageSource(iw, ih, pixels, 0, iw));
		image = new BufferedImage(tempImg.getWidth(null),
				tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR);
		image.createGraphics().drawImage(tempImg, 0, 0, null);
	}

}