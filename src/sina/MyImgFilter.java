package sina;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class MyImgFilter {
	private String FileName;
	BufferedImage image;
	private int iw, ih;
	private int[] pixels;
	public static List<String> FileList = new ArrayList();
	public static String OutPath;

	public String[] fileabsolutePath = new String[5];

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

	public MyImgFilter(BufferedImage image) {
		this.image = image;
		iw = image.getWidth();
		ih = image.getHeight();
		pixels = new int[iw * ih];
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
		int alpha = cm.getAlpha(PixelsValue);
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

	public void SeparateImage() {
		// 统计直方图
		List<Integer> His = new ArrayList();
		int i, j;
		for (i = 0; i < image.getWidth(); i++)
			His.add(0);
		PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,
				pixels, 0, iw);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 对图像进行二值化处理
		for (i = 0; i < iw; i++) {
			for (j = 0; j < ih; j++) {
				int Value = this.GetGreyValue(pixels[j * iw + i]);
				if (Value == 0)
					His.set(i, His.get(i) + 1);
			}
			// System.out.println(His.get(i));
		}
		// 字符的x坐标范围
		List<Integer> StartXList = new ArrayList();
		List<Integer> EndXList = new ArrayList();
		i = 0;
		while (i < iw) {
			int Start, End;
			while (i < iw && (His.get(i) == 0 || His.get(i) == 1)) {
				i++;
				continue;
			}
			if (i == iw)
				break;
			// 向前找到第一个0的边界
			while (i >= 0 && i < iw && His.get(i) != 0) {
				i--;
			}
			Start = i;
			StartXList.add(i);
			i++;
			// 向后找到第一个0的边界
			while (i < iw && His.get(i) != 0) {
				i++;
			}
			End = i;
			EndXList.add(i);
			if (End - Start <= 6)// 噪声点
			{
				StartXList.remove(StartXList.size() - 1);
				EndXList.remove(EndXList.size() - 1);
			} else if (End - Start > iw / 3) {
				// 中间找到一个最少的点做切分
				boolean bFirst = true;
				int Min = 0;
				int Pos = 0;
				for (int k = Start + (End - Start) / 4; k < End - (End - Start)
						/ 4; k++) {
					if (bFirst) {
						Min = His.get(k);
						Pos = k;
						bFirst = false;
					} else {
						if (His.get(k) < Min) {
							Min = His.get(k);
							Pos = k;
						}
					}
				}
				EndXList.remove(EndXList.size() - 1);
				EndXList.add(Pos);
				StartXList.add(Pos);
				EndXList.add(End);

			}

		}
		/*
		 * System.out.println("x axis"); for (i = 0; i < StartXList.size(); i++)
		 * System.out.println(StartXList.get(i) + ":" + EndXList.get(i));
		 */

		// 字符的y坐标范围
		List<Integer> StartYList = new ArrayList();
		List<Integer> EndYList = new ArrayList();
		// 搜索每个y的范围
		int k;
		for (k = 0; k < StartXList.size(); k++) {
			// int LastCount=0;
			for (i = 0; i < ih; i++) {
				int Count = 0;
				for (j = StartXList.get(k); j < EndXList.get(k); j++) {
					int Value = this.GetGreyValue(pixels[i * iw + j]);
					if (Value == 0)
						Count++;
				}

				if (Count > 1)// && LastCount!=0)
				{
					StartYList.add(i - 2);
					break;
				}
				// LastCount=Count;
			}

			// LastCount=0;
			for (i = ih - 1; i >= 0; i--) {
				int Count = 0;
				for (j = StartXList.get(k); j < EndXList.get(k); j++) {
					int Value = this.GetGreyValue(pixels[i * iw + j]);
					if (Value == 0)
						Count++;
				}

				if (Count > 1)// !=0 && LastCount!=0)
				{
					EndYList.add(i + 2);
					break;
				}
				// LastCount=Count;
			}
		}
		/*
		 * System.out.println("y axis"); for (i = 0; i < StartYList.size(); i++)
		 * System.out.println(StartYList.get(i) + ":" + EndYList.get(i));
		 */

		// 保存切割的字符
		for (i = 0; i < StartXList.size(); i++) {
			if (i == StartYList.size())
				break;
			BufferedImage SubImg = getSubImage(image, StartXList.get(i),
					StartYList.get(i), EndXList.get(i) - StartXList.get(i),
					EndYList.get(i) - StartYList.get(i));
			//windows
			//String pname = OutPath + "/" + FileName + "_" + i;
			//linux
			String pname = FileName + "_" + i;
			
			fileabsolutePath[i] = pname + ".jpg";
			File file = new File(pname + ".jpg");
			try {
				ImageIO.write(SubImg, "jpg", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static BufferedImage getSubImage(BufferedImage src, int x, int y,
			int width, int height) {
		BufferedImage dest;
		int imageType = src.getType();
		// if (imageType == BufferedImage.TYPE_CUSTOM)
		// dest = ImageUtils.newBufferedImage(src.getColorModel(), width,
		// height);
		// else
		dest = new BufferedImage(width, height, imageType);

		AffineTransform xform = AffineTransform.getTranslateInstance(-x, -y);
		BufferedImageOp op = new AffineTransformOp(xform, null);
		dest = op.filter(src, dest);
		return dest;
	}

	public static void Usage() {
		System.out.println("Filter [图像路径] [目标存放路径]");
	}

	private static void refreshFileList(String strPath) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();

		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				refreshFileList(files[i].getAbsolutePath());
			} else {
				String strFileName = files[i].getAbsolutePath().toLowerCase();
				// System.out.println("---"+strFileName);
				FileList.add(files[i].getAbsolutePath());
			}
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			Usage();
			System.exit(1);
		} else {
			refreshFileList(args[0]);
			OutPath = args[1];
			for (int i = 0; i < FileList.size(); i++) {
				System.out.println("processing " + FileList.get(i));
				FileInputStream fin = new FileInputStream(FileList.get(i));
				BufferedImage bi = ImageIO.read(fin);
				MyImgFilter flt = new MyImgFilter(bi);
				flt.SetFileName(FileList.get(i));
				flt.changeGrey();
				flt.saveImage("changeGrey" + i);

				flt.getGrey();

				flt.getBrighten();

				flt.RemoveNoise();
				flt.saveImage("RemoveNoise" + i);

				// bi=flt.getProcessedImg();
				// String pname=args[0].substring(0,args[0].lastIndexOf("."));
				// File file = new File(pname+".jpg");
				// ImageIO.write(bi, "jpg", file);
				flt.SeparateImage();
			}
		}
	}

	void saveImage(String name) {
		String pname = OutPath + "/" + name;
		File file = new File(pname + ".jpg");
		try {
			ImageIO.write(image, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}