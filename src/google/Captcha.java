package google;

import org.opencv.core.Core;

public class Captcha {

	public Captcha() throws Exception {
	}

	String recognize(String filePath) throws Exception {
		String captcha_string = "";
		// String path = GetPath(filePath);
		// ÇÐ¸î
		MyImgFilter flt = new MyImgFilter(filePath);
		// MyImgFilter.OutPath = GetPath(filePath);
		flt.SetFileName(filePath);
		// flt.SeparateImage();
		// flt.Corner();
		// flt.myHist();
		// flt.Hist();
		flt.Sift();

		return captcha_string;
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
		Captcha captcha = new Captcha();
		captcha.recognize("/home/syh/workspace/75.jpg");
	}
}
