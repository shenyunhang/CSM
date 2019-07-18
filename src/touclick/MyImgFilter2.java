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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
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

public class MyImgFilter2 {
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

	public MyImgFilter2(String fileabsolutePath, String word) {

		try {
			word2img(word);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		mat1 = Highgui.imread("word.jpg");
		mat2 = Highgui.imread(fileabsolutePath);

		radius1 = word.substring(0, word.indexOf(" ")).length() * 18;
		radius2 = word.substring(word.indexOf(" ") + 1).length() * 18;
		line = word.indexOf(" ") * spaceSize + spaceSize / 2;
		/*
		 * for (int i = 0; i < mat.rows(); i++) { for (int j = 0; j <
		 * mat.cols(); j++) { System.out.print(mat.get(i, j)[0] + " "); }
		 * System.out.println(); }
		 */

		System.out.println(word.indexOf(" ") + " " + radius1 + " " + radius2
				+ " " + line);
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

	public Mat RemoveSomething(Mat mat) {
		for (int i = 0; i < mat.rows(); i++) {
			for (int j = 0; j < mat.cols(); j++) {

				if (mat.get(i, j)[0] > 255 / 2) {
					double[] temp = { 255 };
					mat.put(i, j, temp);
				} else {
					double[] temp = { 0 };
					mat.put(i, j, temp);
				}

			}
		}
		Highgui.imwrite(OutPath + "/RemoveSomething.png", mat);
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
		for (int i = 0; i < word.length(); i++) {
			g.drawString(word.substring(i, i + 1), spaceSize * i + spaceSize
					/ 2 - g.getFont().getSize() / 2, spaceSize / 2
					+ g.getFont().getSize() / 2);
		}
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

		Imgproc.cvtColor(mat2, gray1, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(mat1, gray2, Imgproc.COLOR_BGR2GRAY);

		Highgui.imwrite(OutPath + "/" + FileName + "gray1.png", gray1);
		Highgui.imwrite(OutPath + "/" + FileName + "gray2.png", gray2);

		// gray2 = RemoveSomething(gray2);

		Mat out = new Mat();
		Mat out1 = new Mat(gray1.rows(), gray1.cols(), gray1.type());
		Mat out2 = new Mat(gray2.rows(), gray2.cols(), gray2.type());
		/*
		 * for (int i = 0; i < gray2.rows(); i++) { for (int j = 0; j <
		 * gray2.cols(); j++) { if (gray2.get(i, j)[0] > 255 / 2) { double[]
		 * temp = { 255 }; gray2.put(i, j, temp); } else { double[] temp = { 0
		 * }; gray2.put(i, j, temp); } } }
		 */

		/*
		 * Mat mask = new Mat(img[0].rows(), img[0].cols(), CvType.CV_8UC1); for
		 * (int i = 0; i < mask.rows(); i++) { for (int j = 0; j < mask.cols();
		 * j++) { if (j >= mask.cols() / 4 * n && j <= mask.cols() / 4 * (n +
		 * 1)) { double[] temp = { 1 }; mask.put(i, j, temp); } else { double[]
		 * temp = { 0 }; mask.put(i, j, temp); } } }
		 */

		// 获取特征点
		FeatureDetector fd = FeatureDetector.create(FeatureDetector.HARRIS );
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
				.create(DescriptorMatcher.BRUTEFORCE);
		// List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();

		MatOfDMatch matche = new MatOfDMatch();

		// matcher.knnMatch(desc1, desc2, matches, 2);
		matcher.match(desc1, desc2, matche);

		// Features2d.drawMatches2(gray1, mkp1, gray2, mkp2, matches, out);
		Features2d.drawMatches(gray1, mkp1, gray2, mkp2, matche, out);

		Highgui.imwrite(OutPath + "/" + FileName + "out.png", out);

		/*
		 * for (int i = 0; i < desc1.rows(); i++) { for (int j = 0; j <
		 * desc1.cols(); j++) { System.out.print(desc1.get(i, j)[0] + " "); }
		 * System.out.println(); } System.out.println(); for (int i = 0; i <
		 * desc2.rows(); i++) { for (int j = 0; j < desc2.cols(); j++) {
		 * System.out.print(desc2.get(i, j)[0] + " "); } System.out.println(); }
		 */

		// 选找区域
		System.out.println(mkp1.toList());

		System.out.println(desc1.rows() + " " + desc2.rows());

		float distance = 0;
		List<DMatch> matchetoList = matche.toList();
		List<KeyPoint> mkp2toList = mkp2.toList();
		List<KeyPoint> mkp1toList = mkp1.toList();

		int[] count = new int[matchetoList.size()];
		for (int i = 0; i < count.length; i++) {
			count[i] = 0;
		}

		for (int i = 0; i < matchetoList.size(); i++) {
			distance += matchetoList.get(i).distance;
			double x = mkp1toList.get(matchetoList.get(i).queryIdx).pt.x;
			double y = mkp1toList.get(matchetoList.get(i).queryIdx).pt.y;

			double xTrain = mkp2toList.get(matchetoList.get(i).trainIdx).pt.x;

			for (int j = 0; j < matche.toList().size(); j++) {

				double xxTrain = mkp2toList.get(matchetoList.get(j).trainIdx).pt.x;

				// 判断是否同一个字
				if ((xTrain - line) * (xxTrain - line) < 0 || i == j) {
					continue;
				}

				double xx = mkp1toList.get(matchetoList.get(j).queryIdx).pt.x;
				double yy = mkp1toList.get(matchetoList.get(j).queryIdx).pt.y;

				double keyDistance = Math.sqrt(Math.pow((x - xx), 2)
						+ Math.pow((y - yy), 2));

				if (x < line && keyDistance <= radius1) {
					count[i]++;

				} else if (x > line && keyDistance <= radius2) {
					count[i]++;
				}

			}

		}
		int temp1 = 0;
		int temp2 = 0;

		for (int i = 0; i < count.length; i++) {
			// System.out.println(count[i]);
			double xTrain = mkp2toList.get(matchetoList.get(i).trainIdx).pt.x;
			if (count[temp1] < count[i] && xTrain < line) {
				temp1 = i;
			} else if (count[temp2] < count[i] && xTrain > line) {
				temp2 = i;
			}
		}
		System.out.println("temp1=" + temp1 + " temp2=" + temp2);

		double x1 = mkp1toList.get(matchetoList.get(temp1).queryIdx).pt.x;
		double y1 = mkp1toList.get(matchetoList.get(temp1).queryIdx).pt.y;

		double x2 = mkp1toList.get(matchetoList.get(temp2).queryIdx).pt.x;
		double y2 = mkp1toList.get(matchetoList.get(temp2).queryIdx).pt.y;

		while (Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2)) <= radius2
				|| Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2)) <= radius1) {

			if (temp1 >= temp2) {
				count[temp2] = 0;
				count[temp1] = 0;

				for (int i = 0; i < count.length; i++) {
					// System.out.println(count[i]);
					double xTrain = mkp2toList
							.get(matchetoList.get(i).trainIdx).pt.x;
					if (count[temp2] < count[i] && xTrain > line) {
						temp2 = i;
					}
				}
				x2 = mkp1toList.get(matchetoList.get(temp2).queryIdx).pt.x;
				y2 = mkp1toList.get(matchetoList.get(temp2).queryIdx).pt.y;
			} else {
				count[temp2] = 0;
				count[temp1] = 0;

				for (int i = 0; i < count.length; i++) {
					// System.out.println(count[i]);
					double xTrain = mkp2toList
							.get(matchetoList.get(i).trainIdx).pt.x;
					if (count[temp1] < count[i] && xTrain < line) {
						temp1 = i;
					}
				}
				x1 = mkp1toList.get(matchetoList.get(temp1).queryIdx).pt.x;
				y1 = mkp1toList.get(matchetoList.get(temp1).queryIdx).pt.y;
			}
			System.out.println("temp1=" + temp1 + " temp2=" + temp2);

		}

		Point pt1 = new Point(x1, y1);
		Point pt2 = new Point(x2, y2);

		Scalar sc = new Scalar(0, 0, 255);

		Core.circle(mat2, pt1, radius1, sc);
		Core.circle(mat2, pt2, radius2, sc);

		Highgui.imwrite(OutPath + "/" + FileName + "result.png", mat2);

		System.out.println("結果:" + distance + " " + matche.toList());
	}

	/** 图像二值化 */
	public BufferedImage changeGrey() {

		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,
				pixels, 0, iw);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 设定二值化的域值，默认值为100
		int grey = 100;
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
	public BufferedImage getMedian() {
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

	public BufferedImage getGrey() {
		ColorConvertOp ccp = new ColorConvertOp(
				ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		return image = ccp.filter(image, null);
	}

	// Brighten using a linear formula that increases all color values
	public BufferedImage getBrighten() {
		RescaleOp rop = new RescaleOp(1.25f, 0, null);
		return image = rop.filter(image, null);
	}

	// Blur by "convolving" the image with a matrix
	public BufferedImage getBlur() {
		float[] data = { .1111f, .1111f, .1111f, .1111f, .1111f, .1111f,
				.1111f, .1111f, .1111f, };
		ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
		return image = cop.filter(image, null);
	}

	// Sharpen by using a different matrix
	public BufferedImage getSharpen() {
		float[] data = { 0.0f, -0.75f, 0.0f, -0.75f, 4.0f, -0.75f, 0.0f,
				-0.75f, 0.0f };
		ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));
		return image = cop.filter(image, null);
	}

	// 11) Rotate the image 180 degrees about its center point
	public BufferedImage getRotate() {
		AffineTransformOp atop = new AffineTransformOp(
				AffineTransform.getRotateInstance(Math.PI,
						image.getWidth() / 2, image.getHeight() / 2),
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return image = atop.filter(image, null);
	}

	public BufferedImage getProcessedImg() {
		return image;
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

	public void RemoveNoise() {
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