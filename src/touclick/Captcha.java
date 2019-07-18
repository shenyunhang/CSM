package touclick;

import java.io.File;

import org.opencv.core.Core;

public class Captcha {

	Captcha() {
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
		String absolutePath = "D:/Desktop/touclickData";

		File dir = new File(absolutePath);
		File[] dirs = dir.listFiles();

		long totalTime = 0;
		long maxTime = 0;
		long minTime = Long.MAX_VALUE;
		int worldNumber = 0;
		int fileNumber = 0;
		int featureNumber = 0;
		int maxfeatureNumber = 0;
		int minfeatureNumber = Integer.MAX_VALUE;

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
				} else if ("guomeng".compareTo(dirs[i].getName()) == 0) {
					key = "国 梦";
				} else if ("bihai".compareTo(dirs[i].getName()) == 0) {
					key = "碧 海";
				} else if ("weilan".compareTo(dirs[i].getName()) == 0) {
					key = "蔚 蓝";
				} else if ("dujia".compareTo(dirs[i].getName()) == 0) {
					key = "度 假";
				}

				File[] files = dirs[i].listFiles();
				for (int j = 0; j < files.length; j++) {

					// if (files[j].getName().compareTo("15") != 0) {
					// continue;
					// }

					System.out.println("\t\t文件名:" + files[j].getName());
					long startTime = System.currentTimeMillis();

					new Captcha().recognize(files[j].getAbsolutePath(), key);

					long timeCost = System.currentTimeMillis() - startTime;
					if (maxTime < timeCost) {
						maxTime = timeCost;
					}
					if (minTime > timeCost) {
						minTime = timeCost;
					}
					totalTime += timeCost;
					System.out.println("time cost:" + timeCost + "ms");

					if (maxfeatureNumber < MyImgFilter.featureNumber1) {
						maxfeatureNumber = MyImgFilter.featureNumber1;
					}
					if (minfeatureNumber > MyImgFilter.featureNumber1) {
						minfeatureNumber = MyImgFilter.featureNumber1;
					}
					if (maxfeatureNumber < MyImgFilter.featureNumber2) {
						maxfeatureNumber = MyImgFilter.featureNumber2;
					}
					if (minfeatureNumber > MyImgFilter.featureNumber2) {
						minfeatureNumber = MyImgFilter.featureNumber2;
					}

					featureNumber += MyImgFilter.featureNumber1
							+ MyImgFilter.featureNumber2;
					worldNumber += key.length() - 1;
					fileNumber++;
				}
			}

		}

		System.out.println("worldNumber=" + worldNumber);
		System.out.println("fileNumber=" + fileNumber);
		System.out.println("featureNumber=" + featureNumber / worldNumber);
		System.out.println("minfeatureNumber:" + minfeatureNumber);
		System.out.println("maxfeatureNumber:" + maxfeatureNumber);

		System.out.println("time cost:" + totalTime / fileNumber + "ms");
		System.out.println("minTime:" + minTime + "ms");
		System.out.println("maxTime:" + maxTime + "ms");

	}
}
