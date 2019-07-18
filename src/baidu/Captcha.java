package baidu;

import java.io.File;

import org.opencv.core.Core;

import weka.classifiers.Classifier;

public class Captcha {
	Classifier cls;

	public Captcha() throws Exception {
		// filePath="I:\\target\\smo.model";
		// cls = (Classifier) SerializationHelper.read(filePath);
	}

	public String recognize(String filePath) throws Exception {

		// String path = GetPath(filePath);
		// 切割
		// FileInputStream fin = new FileInputStream(filePath);
		// BufferedImage bi = ImageIO.read(fin);
		MyImgFilter flt = new MyImgFilter(filePath);
		// MyImgFilter.OutPath = GetPath(filePath);
		// flt.SetFileName(filePath);
		flt.SeparateImage();
		flt.Corner2();
		// flt.myHist();
		// flt.Hist();
		// flt.Sift();
		// fin.close();

		return (flt.result[0] + 1) + "" + (flt.result[1] + 1) + ""
				+ (flt.result[2] + 1) + "" + (flt.result[3] + 1);

	}

	public String GetPath(String filePath) {
		int SlashPos = filePath.lastIndexOf('/');
		if (SlashPos == -1)
			SlashPos = filePath.lastIndexOf('\\');

		if (SlashPos == -1)
			return "";
		else
			return filePath.substring(0, SlashPos);
	}

	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File dir = new File("D:/Desktop/baidutest/");

		File[] files = dir.listFiles();
		int right = 0;
		int rightWord = 0;
		for (int j = 0; j < files.length; j++) {
			Captcha captcha = new Captcha();

			String result = captcha.recognize(files[j].getAbsolutePath());

			String Name = files[j].getAbsolutePath();
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
			String FileName = Name.substring(Start, End);

			if (result.compareTo(FileName) == 0) {
				right++;
				System.out
						.println("==========================================\n================================\n=================================");
			}

			for (int i = 0; i < 4; i++) {
				if (result.charAt(i) == FileName.charAt(i)) {
					rightWord++;
				}
			}

			System.out.println(FileName + " " + result);

		}

		System.out.println("正确=" + right + " " + (float) right / files.length);
		System.out.println("正确字符=" + rightWord + " " + (float) rightWord
				/ files.length / 4);

	}
}
