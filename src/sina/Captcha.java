package sina;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import weka.classifiers.Classifier;
import weka.core.SerializationHelper;

public class Captcha {
	Classifier cls;

	public Captcha(String filePath) throws Exception {
		// filePath="I:\\target\\smo.model";
		cls = (Classifier) SerializationHelper.read(filePath);
	}

	String recognize(String filePath) throws Exception {
		String captcha_string = "";
		// String path = GetPath(filePath);
		// 切割
		FileInputStream fin = new FileInputStream(filePath);
		BufferedImage bi = ImageIO.read(fin);
		MyImgFilter flt = new MyImgFilter(bi);
		MyImgFilter.OutPath = GetPath(filePath);
		flt.SetFileName(filePath);
		flt.changeGrey();
		flt.getGrey();
		flt.getBrighten();
		flt.RemoveNoise();
		flt.SeparateImage();
		fin.close();

		// 获取像素数据
		MyData mydata = new MyData();
		mydata.CreateDatasets();
		mydata.FillRecognizeData(flt.fileabsolutePath);

		// 分类
		for (int i = 0; i < mydata.dataset.numInstances(); i++) {
			double clsLabel = cls.classifyInstance(mydata.dataset.instance(i));
			mydata.dataset.instance(i).setClassValue(clsLabel);
			/*
			 * System.out.println(flt.fileabsolutePath[i] + " result=" +
			 * mydata.dataset.attribute( mydata.dataset.numAttributes() -
			 * 1).value( (int) clsLabel));
			 */
			captcha_string = captcha_string
					+ mydata.dataset.attribute(
							mydata.dataset.numAttributes() - 1).value(
							(int) clsLabel);
		}
		System.out.println("Captcha=" + captcha_string);
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

		// 准备测试数据
		// MyData mytestdata = new MyData();
		// mytestdata.CreateDatasets();
		// mytestdata.FillTestDatasets("I:\\target\\test2");

		// 准备训练数据
		// MyData mydata = new MyData();
		// mydata.CreateDatasets();
		// mydata.FillDatasets("\\mnt\\wing\\target");
		// mydata.FillDatasets("/mnt/wing/target");

		// 初始化机器学习类
		// MyLearn mylearn = new MyLearn();
		// 初始化测试类
		// MyTest mytest = new MyTest();

		// KNN算法
		// mylearn.KNN(mydata);
		// mytest.testKNN(mylearn.knn, mytestdata.dataset);

		// SMO算法
		// System.out.println("训练SMO分类器....");
		// mylearn.SMO(mydata);
		// mytest.testSMO(mylearn.smo, mytestdata.dataset);

		// SMO序列化存储
		// SerializationHelper.write("I:\\target\\smo.model", mylearn.smo);

		// AdaBoost算法
		// mylearn.AdaBoost(mydata);
		// mytest.testAdaBoost(mylearn.adaboost, mytestdata.dataset);

		// 决策树算法
		// mylearn.TreeJ48(mydata);
		// mytest.testTreeJ48(mylearn.j48, mytestdata.dataset);

		// 接口测试

		// String[] result = new String[566];
		// Captcha captcha = new Captcha("I:\\target\\smo.model");
		// for (int i = 0; i < 566; i++) {
		// result[i] = captcha.recognize("I:\\aa\\" + (i + 5872) + ".png");

		// File f = new File("I:\\aa\\" + (i + 5872) + ".png");
		// f.renameTo(new File("I:\\aa\\" + result[i] + ".png"));
		// }
		// for (int i = 0; i < 566; i++) {
		// System.out.println("Captcha=" + result[i]);
		// }

		WebClient webClient = new WebClient();
		HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
		System.out.println(page.getTitleText());

		String pageAsXml = page.asXml();
		System.out.println(pageAsXml);

		String pageAsText = page.asText();
		System.out.println(pageAsText);

		webClient.closeAllWindows();

	}
}
