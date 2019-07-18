package touclick;

import java.io.File;

import org.opencv.core.Core;

public class CopyOfCaptcha {

	CopyOfCaptcha() {
	}

	void recognize(String filePath, String word) throws Exception {

		MyImgFilter flt = new MyImgFilter(filePath, word);
		// flt.SetFileName(filePath);
		// flt.RemoveSomething();
		flt.detect();

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

		/*
		 * MyImgFilter.OutPath = "/home/syh/Downloads/touclick/";
		 * MyImgFilter2.OutPath = "/home/syh/Downloads/touclick/"; String
		 * absolutePath = "/home/syh/Downloads/touclick/";
		 */
		MyImgFilter.OutPath = "D:/Desktop/touclick";
		MyImgFilter2.OutPath = "D:/Desktop/touclick";
		String absolutePath = "D:/Desktop/touclick";

		File dir = new File(absolutePath);
		File[] dirs = dir.listFiles();

		for (int i = 0; i < dirs.length; i++) {
			if (dirs[i].isDirectory()) {

				String key = "";
				if ("dongmei".compareTo(dirs[i].getName()) == 0) {
					key = "冬 梅";
				} else if ("jieti".compareTo(dirs[i].getName()) == 0) {
					key = "解 体";
				} else if ("jiqiren".compareTo(dirs[i].getName()) == 0) {
					key = "机 器人";
				} else if ("qiuju".compareTo(dirs[i].getName()) == 0) {
					key = "秋 菊";
				} else if ("taochun".compareTo(dirs[i].getName()) == 0) {
					key = "春 桃";
				} else if ("xiahe".compareTo(dirs[i].getName()) == 0) {
					key = "夏 荷";
				} else if ("yonghu".compareTo(dirs[i].getName()) == 0) {
					key = "用 户";
				} else if ("zhongguomeng".compareTo(dirs[i].getName()) == 0) {
					key = "中国 梦";
				} else if ("bajin".compareTo(dirs[i].getName()) == 0) {
					key = "巴 金";
				} else if ("qujing".compareTo(dirs[i].getName()) == 0) {
					key = "取 经";
				} else if ("tongxing".compareTo(dirs[i].getName()) == 0) {
					key = "通 行";
				} else if ("daojishi".compareTo(dirs[i].getName()) == 0) {
					key = "倒 计时";
				}

				File[] files = dirs[i].listFiles();
				for (int j = 0; j < files.length; j++) {

					if (files[j].getName().compareTo(".directory") == 0) {
						continue;
					}
					System.out.println("\t\t文件名" + files[j].getName());

					CopyOfCaptcha captcha = new CopyOfCaptcha();
					captcha.recognize(files[j].getAbsolutePath(), key);

				}
			}

		}

	}
}
