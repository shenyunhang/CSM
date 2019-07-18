import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.logging.Level;

import org.opencv.core.Core;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CrawlGoogle {
	public WebClient CrawlWebClient = new WebClient();
	public String ResultPage;
	public boolean bCloseWindow = true;
	private CookieManager CookieManager = new CookieManager();
	private Captcha captcha;

	private int right = 0;
	private int all = 0;

	private int count = 0;

	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		CrawlGoogle crawl = new CrawlGoogle();

		while (true) {
			crawl.DownloadCaptcha("https://google.com/sorry/IndexRedirect?continue=https://www.google.com.hk/search?newwindow=1&safe=strict&client=aff-9991&hs=bEk&affdom=9991.com&hl=zh-CN&channel=link&site=webhp&source=hp&q=a&btnK=Google+%E6%90%9C%E7%B4%A2&oq=a&gs_l=hp.12..0l10.2470.2470.0.4305.1.1.0.0.0.0.213.213.2-1.1.0....0...1c.1.32.hp..0.1.212.PalEww9oDEM&bav=on.2,or.&bvm=bv.57155469%2Cd.aGc%2Cpv.xjs.s.en_US.v-r5CthikH8.O&biw=1366&bih=568&dpr=1&ech=1&psi=gW2dUtSXAeuQiAfn_IDADA.1386048897653.3&emsg=NCSR&noj=1&ei=gW2dUtSXAeuQiAfn_IDADA");
		}
		/*
		while (true) {
			// crawl.DownloadPage("http://huati.weibo.com/?refer=index_hot");
			crawl.DownloadPage("https://www.google.com.hk/webhp?hl=zh-CN&client=aff-9991&channel=link#hl=zh-CN&newwindow=1&q=%E5%AB%A6%E5%A8%A5&safe=strict");
			// System.out.println(count);
			// count++;
		}*/
		

	}

	CrawlGoogle() {
		// DownThread DThread = new DownThread();
		// if (this.ThreadNum == 1)
		// DThread.bCloseWindow = false;
		// DThread.GlobalFileIndex = FileIndex;
		// DThread.GlobalOutStream = OutStream;
		// DThread.GlobalWeiboStream = WeiboStream;
		// DThread.GlobalLink = HtmlLink;
		CrawlWebClient = new WebClient();
		CrawlWebClient.setCookieManager(CookieManager);
		CrawlWebClient.getOptions().setJavaScriptEnabled(true);
		CrawlWebClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		// DThread.CrawlWebClient.setJavaScriptTimeout(ConstVar.JAVASCRIPT_TIMOUT);
		// DThread.CrawlWebClient.getOptions().setTimeout(ConstVar.WEB_TIMEOUT);
		CrawlWebClient.getOptions().setCssEnabled(true);
		CrawlWebClient.getOptions().setThrowExceptionOnScriptError(false);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				Level.OFF);
		java.util.logging.Logger.getLogger("org.apache").setLevel(Level.OFF);
		CrawlWebClient.setIncorrectnessListener(new IncorrectnessListener() {

			@Override
			public void notify(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}
		});
		CrawlWebClient.setCssErrorHandler(new ErrorHandler() {

			@Override
			public void warning(CSSParseException exception)
					throws CSSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void fatalError(CSSParseException exception)
					throws CSSException {
				// TODO Auto-generated method stub

			}

			@Override
			public void error(CSSParseException exception) throws CSSException {
				// TODO Auto-generated method stub

			}
		});

		try {
			captcha = new Captcha("smo.model");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String Login(String Url, String UserName, String Password) {
		HtmlPage Page = null;
		try {
			Page = CrawlWebClient.getPage(Url);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
		// DomElement Form=Page.getElementById("pl_login_form");
		HtmlInput NameInput = Page.getElementByName("username");
		HtmlInput PasswordInput = Page.getElementByName("password");
		NameInput.setAttribute("value", UserName);// .setValueAttribute(UserName);
		PasswordInput.setAttribute("value", Password);// .setValueAttribute(Password);
		CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
		HtmlInput VerifyInput = Page.getElementByName("verifycode");
		HtmlImage VerifyImage = (HtmlImage) Page.getByXPath(
				"//*[@id=\"pl_login_form\"]/div[1]/div[2]/div[3]/div/a[1]/img")
				.get(0);
		// *[@id="pl_login_form"]/div[3]/div/a[1]/img
		HtmlAnchor LoginBtn = (HtmlAnchor) Page.getByXPath(
				"//*[@id=\"pl_login_form\"]/div[1]/div[2]/div[6]/div[1]/a")
				.get(0);// Form.getButtonByName("");

		if (VerifyImage != null) {
			try {
				VerifyImage.click();
				CrawlWebClient
						.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
				VerifyImage.saveAs(new File("./VImage.jpg"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String VerifyStr = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			try {
				System.out.println("��������֤��:");
				VerifyStr = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			VerifyInput.removeAttribute("disabled");
			VerifyInput.setAttribute("value", VerifyStr);
		}
		// �������÷��ؽ��
		HtmlPage RPage = null;
		try {
			RPage = LoginBtn.click();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
		ResultPage = RPage.asXml();
		if (ResultPage.contains("<!-- ��¼ģ�� -->")) {
			System.out.println("��½ʧ��");
			System.exit(0);
		} else
			System.out.println("��½�ɹ�");
		/*
		 * if(RPage!=null) { ObtainAnchorAndImage(RPage); }
		 */
		/*
		 * try { Page = GlobalCrawlWebClientV.get(1)
		 * .getPage("http://weibo.com/u/2280213565?wvr=5&wvr=5&lf=reg"); } catch
		 * (FailingHttpStatusCodeException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (MalformedURLException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * GlobalCrawlWebClientV.get(1).waitForBackgroundJavaScript
		 * (ConstVar.PageWaitSeconds); ResultPage=Page.asText();
		 */

		/*
		 * String FileName=null; synchronized (IndexLock) {
		 * FileName=String.format("web/%d.html", GlobalFileIndex.Value);
		 * GlobalFileIndex.Value++; } try { synchronized (StreamLock) {
		 * GlobalOutStream.write((Url+"\t\t"+FileName+"\n").getBytes());
		 * GlobalOutStream.flush(); } } catch (IOException e) {
		 * e.printStackTrace(); }
		 * 
		 * Save.SaveHtml(FileName, ResultPage);
		 */
		if (bCloseWindow)
			CrawlWebClient.closeAllWindows();
		return ResultPage;
	}

	public void DownloadPage(String Url) {
		// CrawlWebClient.setThrowExceptionOnScriptError(false);
		HtmlPage[] Page;
		Page = new HtmlPage[100];

		for (int i = 0; i < Page.length; i++)
			try {
				Page[i] = CrawlWebClient.getPage(Url);
				count++;
				System.out.println(count);
			} catch (FailingHttpStatusCodeException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (MalformedURLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);

		return;

		/*
		 * for (int i = 0; i < Page.length; i++) { if (Page != null) { boolean
		 * isCaptcha = false; String VerifyStr = null; int count = 0;
		 * 
		 * System.out.println("Page" + i); ResultPage = Page[i].asXml();//
		 * Response.getContentAsString(); // System.out.println(Page.asText());
		 * // System.out.println(Page.asXml()); while
		 * (ResultPage.contains("�����Ϊ��Щ�쳣����������֤�룺"))// "��ˢ��̫��������Ϣһ�°�")) {
		 * isCaptcha = true;
		 * 
		 * System.out.println("�����Ϊ��Щ�쳣����������֤��");
		 * 
		 * // ������֤�� HtmlImage VerifyImage = (HtmlImage) Page[i] .getByXPath(
		 * "//*[@id=\"pl_common_sassfilter\"]/div/div/div/div[1]/span[2]/img")
		 * .get(0); HtmlInput VerifyInput = (HtmlInput) Page[i] .getByXPath(
		 * "//*[@id=\"pl_common_sassfilter\"]/div/div/div/div[1]/span[1]/input")
		 * .get(0); HtmlAnchor LoginBtn = (HtmlAnchor) Page[i] .getByXPath(
		 * "//*[@id=\"pl_common_sassfilter\"]/div/div/div/div[3]/a") .get(0);//
		 * Form.getButtonByName(""); String FileName = "CodeImage.png"; try { //
		 * VerifyImage.click(); //
		 * CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
		 * VerifyImage.saveAs(new File(FileName)); } catch (IOException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 * 
		 * // synchronized (VerifyLock) // { try { VerifyStr =
		 * captcha.recognize(FileName); } catch (Exception e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }// ��֤��ͼƬ��·�� // }
		 * System.out.println(VerifyStr); VerifyInput.setAttribute("value",
		 * VerifyStr); try { Page[i] = LoginBtn.click(); } catch (IOException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); }
		 * CrawlWebClient
		 * .waitForBackgroundJavaScript(ConstVar.PageWaitSeconds); ResultPage =
		 * Page[i].asXml();// Response.getContentAsString(); count++;
		 * 
		 * copyFile("CodeImage.png", "data/wrong/" + VerifyStr + ".png"); } if
		 * (isCaptcha) { count--; right++; all = all + count;
		 * System.out.println("Pass , count=" + count);
		 * 
		 * System.out.println("Right=" + right + " All=" + all);
		 * 
		 * copyFile("CodeImage.png", "data/right/" + VerifyStr + ".png");
		 * deleteFile("data/wrong/" + VerifyStr + ".png");
		 * 
		 * return; } } }
		 */
	}

	public void DownloadCaptcha(String Url) {
		// CrawlWebClient.setThrowExceptionOnScriptError(false);
		HtmlPage[] Page;
		Page = new HtmlPage[100];

		for (int i = 0; i < Page.length; i++)
			try {
				Page[i] = CrawlWebClient.getPage(Url);
			} catch (FailingHttpStatusCodeException e2) {
				e2.printStackTrace();
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

		CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);

		for (int i = 0; i < Page.length; i++) {
			if (Page != null) {

				ResultPage = Page[i].asXml();// Response.getContentAsString();
				// System.out.println(Page.asText());
				// System.out.println(Page.asXml());

				HtmlImage VerifyImage = (HtmlImage) Page[i].getByXPath(
						"/html/body/div/img").get(0);
				String FileName = "googledata/"+count + ".jpg";
				count++;
				System.out.println("count=" + count);
				try {

					VerifyImage.saveAs(new File(FileName));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		}

	}

	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // �ļ�����ʱ
				InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // �ֽ��� �ļ���С
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("���Ƶ����ļ���������");
			e.printStackTrace();

		}

	}

	/**
	 * ɾ�������ļ�
	 * 
	 * @param sPath
	 *            ��ɾ���ļ����ļ���
	 * @return �����ļ�ɾ���ɹ�����true�����򷵻�false
	 */
	public boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

}