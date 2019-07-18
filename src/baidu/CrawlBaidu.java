package baidu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

public class CrawlBaidu {
	public WebClient CrawlWebClient = new WebClient();
	public String ResultPage;
	public boolean bCloseWindow = true;
	private CookieManager CookieManager = new CookieManager();

	private int count = 0;

	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		/*
		 * CrawlBaidu crawl = new CrawlBaidu(); //
		 * crawl.Login("http://weibo.com", "18046174402", "yunhang");
		 * 
		 * // int count = 1;
		 * 
		 * while (true) { //
		 * crawl.DownloadPage("http://huati.weibo.com/?refer=index_hot");
		 * crawl.DownloadCaptcha(
		 * "http://tieba.baidu.com/cgi-bin/genimg?captchaservice62613436384258704c5a7a672f68596433314c703473613442586d6c49306c5767515553416631646a434b4e347847446f66546c63624749615550547372365137372b55506b6745312f34735a47414664343963555441424f727430566338726c496d6f766b7246777732744c4d3776357338313556717a6c72745177314f6d33637269642f427475774b7979546d7a756377345a4747475a52715a756f796641506f393930396f464949475a4245487a74495a5862644d7056713152787a336c3845466c396f6b4f5a765a2b59746568413457582b70584238415274753368417072372f416e656476636f2f32527667387a64644c694c4b6c563865792f507a4d702f2f437658724f642f4c754a7048304a4d4f556561627568356a2b6737&tag=pc&t=0.39564807526767254"
		 * ); crawl.DownloadCaptcha(
		 * "http://tieba.baidu.com/cgi-bin/genimg?captchaservice623831634b6b344c4b7771496c337277614b713343427272776a496f556a62424a324d4b7673484f67626e72506768414976534c6f72667077693552733931386234657242667a7455465750386f6b365a3044777142686b496b507a716a747837396f7a49785669385059355a304c392f6730674248426f644b646e4671596c5570505a5372434b517956626f4f6750536135793347457056784b684d5554707a345647755a517033324f5065795139756c415164484e656c78654846724c6e556e765774305933614a354b38426f785a4c69756337666e436e325066674f4f75534747414568572f49742b4741524a644c78584f50352b34516a533863434a4a2f645061584a6265466d6167566938734c4e63347a79596d764a33784d77764941&tag=pc&t=0.3196446595247835"
		 * ); // http://tieba.baidu.com/cgi-bin/genimg?
		 * captchaservice656565F64314d4d6a6439764b52616733563263444775416b464c496b52713355365367484a2f6b4a764b324d4e724e59737443745a2b6b3650474f75735358427a4f4e707571696c4976756d71534f455a464d366156724c6978386748426f4b636a5a335a5a41667248586c30582f6a72314b764e4975784b68726c5a31365336686e5a475675584b38552b554d37415764394f624539615a444c4a507551334e714d35544a396b36456e3768306a652f546465554f73487959584738734156742b4a4d77774f4f437a3130424a364c495737796c3441626e4d4867534a63726a464f354f625053356355345145344c612b776a5772425738746e466557713247416b31727433422b48674d3866454b774956626d5a7235577443302b656570525951
		 * &tag=pc&t=0.09865409531630576 // count++; }
		 */

		int count = 0;
		while (true) {
			// URL url = new
			// URL("http://tieba.baidu.com/cgi-bin/genimg?captchaservice62613436384258704c5a7a672f68596433314c703473613442586d6c49306c5767515553416631646a434b4e347847446f66546c63624749615550547372365137372b55506b6745312f34735a47414664343963555441424f727430566338726c496d6f766b7246777732744c4d3776357338313556717a6c72745177314f6d33637269642f427475774b7979546d7a756377345a4747475a52715a756f796641506f393930396f464949475a4245487a74495a5862644d7056713152787a336c3845466c396f6b4f5a765a2b59746568413457582b70584238415274753368417072372f416e656476636f2f32527667387a64644c694c4b6c563865792f507a4d702f2f437658724f642f4c754a7048304a4d4f556561627568356a2b6737&tag=pc&t=0.39564807526767254");
			URL url = new URL(
					"http://tieba.baidu.com/cgi-bin/genimg?captchaservice623831634b6b344c4b7771496c337277614b713343427272776a496f556a62424a324d4b7673484f67626e72506768414976534c6f72667077693552733931386234657242667a7455465750386f6b365a3044777142686b496b507a716a747837396f7a49785669385059355a304c392f6730674248426f644b646e4671596c5570505a5372434b517956626f4f6750536135793347457056784b684d5554707a345647755a517033324f5065795139756c415164484e656c78654846724c6e556e765774305933614a354b38426f785a4c69756337666e436e325066674f4f75534747414568572f49742b4741524a644c78584f50352b34516a533863434a4a2f645061584a6265466d6167566938734c4e63347a79596d764a33784d77764941&tag=pc&t=0.3196446595247835");
			// File outFile = new File("baidudata/1/" + count + ".png");
			File outFile = new File("baidudata/2/" + count + ".png");
			System.out.println("count=" + count);
			count++;
			OutputStream os = new FileOutputStream(outFile);
			InputStream is = url.openStream();
			byte[] buff = new byte[1024];
			while (true) {
				int readed = is.read(buff);
				if (readed == -1) {
					break;
				}
				byte[] temp = new byte[readed];
				System.arraycopy(buff, 0, temp, 0, readed);
				os.write(temp);
			}
			is.close();
			os.close();
		}

	}

	CrawlBaidu() {
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
		 * (ResultPage.contains("你的行为有些异常，请输入验证码："))// "你刷新太快啦，休息一下吧")) {
		 * isCaptcha = true;
		 * 
		 * System.out.println("你的行为有些异常，请输入验证码");
		 * 
		 * // 处理验证码 HtmlImage VerifyImage = (HtmlImage) Page[i] .getByXPath(
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
		 * Auto-generated catch block e1.printStackTrace(); }// 验证码图片的路径 // }
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
		Page page;

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

				// 处理验证码
				HtmlImage VerifyImage = (HtmlImage) Page[i].getByXPath(
						"/html/body/img").get(0);
				String FileName = "baidudata/" + count + ".png";
				System.out.println("count=" + count);
				count++;
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
	 * 
	 * @param sPath
	 *            被删除文件的文件名
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
