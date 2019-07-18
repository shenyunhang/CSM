package sina;
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
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CrawlWeibo {

	public WebClient CrawlWebClient = new WebClient();
	public String ResultPage;
	public boolean bCloseWindow = true;
	private CookieManager CookieManager = new CookieManager();
	private Captcha captcha;

	private int right = 0;
	private int all = 0;

	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		CrawlWeibo crawl = new CrawlWeibo();
		crawl.Login("http://weibo.com", "18046174402", "yunhang");

		/*
		 * HtmlPage page = crawl.CrawlWebClient
		 * .getPage("http://huati.weibo.com/?refer=index_hot");
		 * System.out.println(page.getTitleText());
		 * 
		 * crawl.CrawlWebClient
		 * .waitForBackgroundJavaScript(ConstVar.PageWaitSeconds); // String
		 * pageAsXml = page.asXml(); // System.out.println(pageAsXml);
		 * 
		 * String pageAsText = page.asText(); System.out.println(pageAsText);
		 */

		// int count = 1;
		while (true) {
			// crawl.DownloadPage("http://huati.weibo.com/?refer=index_hot");
			crawl.DownloadPage("http://s.weibo.com/weibo/%2523" + "嫦娥"
					+ "%2523&xsort=time&Refer=STopic_realtime");
			// System.out.println(count);
			// count++;
		}

	}

	CrawlWeibo() {
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
				System.out.println("请输入验证码:");
				VerifyStr = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			VerifyInput.removeAttribute("disabled");
			VerifyInput.setAttribute("value", VerifyStr);
		}
		// 点击并获得返回结果
		HtmlPage RPage = null;
		try {
			RPage = LoginBtn.click();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
		ResultPage = RPage.asXml();
		if (ResultPage.contains("<!-- 登录模块 -->")) {
			System.out.println("登陆失败");
			System.exit(0);
		} else
			System.out.println("登陆成功");
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
		Page = new HtmlPage[50];

		int count2=0;
		System.out.println();
		for (int i = 0; i < Page.length; i++)
			try {
				Page[i] = CrawlWebClient.getPage(Url);
				count2++;
				System.out.print(count2);
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
		/*
		 * Page.executeJavaScript("window.scrollTo(0, document.body.scrollHeight);"
		 * );
		 * CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
		 * Page
		 * .executeJavaScript("window.scrollTo(0, document.body.scrollHeight);"
		 * );
		 * CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
		 */
		for (int i = 0; i < Page.length; i++) {
			if (Page != null) {
				boolean isCaptcha = false;
				String VerifyStr = null;
				int count = 0;

				System.out.println("Page" + i);
				ResultPage = Page[i].asXml();// Response.getContentAsString();
				// System.out.println(Page.asText());
				// System.out.println(Page.asXml());
				while (ResultPage.contains("你的行为有些异常，请输入验证码："))// "你刷新太快啦，休息一下吧"))
				{
					isCaptcha = true;

					System.out.println("你的行为有些异常，请输入验证码");

					// 处理验证码
					HtmlImage VerifyImage = (HtmlImage) Page[i]
							.getByXPath(
									"//*[@id=\"pl_common_sassfilter\"]/div/div/div/div[1]/span[2]/img")
							.get(0);
					HtmlInput VerifyInput = (HtmlInput) Page[i]
							.getByXPath(
									"//*[@id=\"pl_common_sassfilter\"]/div/div/div/div[1]/span[1]/input")
							.get(0);
					HtmlAnchor LoginBtn = (HtmlAnchor) Page[i]
							.getByXPath(
									"//*[@id=\"pl_common_sassfilter\"]/div/div/div/div[3]/a")
							.get(0);// Form.getButtonByName("");
					String FileName = "CodeImage.png";
					try {
						// VerifyImage.click();
						// CrawlWebClient.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
						VerifyImage.saveAs(new File(FileName));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// synchronized (VerifyLock)
					// {
					try {
						VerifyStr = captcha.recognize(FileName);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}// 验证码图片的路径
						// }
					System.out.println(VerifyStr);
					VerifyInput.setAttribute("value", VerifyStr);
					try {
						Page[i] = LoginBtn.click();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					CrawlWebClient
							.waitForBackgroundJavaScript(ConstVar.PageWaitSeconds);
					ResultPage = Page[i].asXml();// Response.getContentAsString();
					count++;
					
					copyFile("CodeImage.png","data/wrong/"+ VerifyStr + ".png");
				}
				if (isCaptcha) {
					count--;
					right++;
					all = all + count;
					System.out.println("Pass , count=" + count);

					System.out.println("Right=" + right + " All=" + all);

					copyFile("CodeImage.png", "data/right/"+VerifyStr + ".png");
					deleteFile("data/wrong/"+ VerifyStr + ".png");
					
					return;
				}
			}
		}
	}

	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				fs.close();
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}
	
    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
			flag = true;
        }
        return flag;
    }
}
