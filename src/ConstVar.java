import java.util.ArrayList;
import java.util.List;

public class ConstVar {
	public static String HomePage="http://weibo.com";
	public static final int PageWaitSeconds=10*1000;//10*1000;
	public static final int PageWaitOneSeconds=1000;
	public static final String ImagePattern="//*[name() = 'img' or name() = 'link' and @type = 'text/css']";
	public static final int SaveOp=1;
	
	public static final int StateBusy=1;
	public static final int StateFree=2;
	
	public static final int JAVASCRIPT_TIMOUT=20*1000;
	public static final int WEB_TIMEOUT=20*1000;
	
	public static final int TYPE_LINK=1;
	public static final int TYPE_ANCHOR=2;
	public static Captcha captcha=null;
	
	public static List<UrlPattern> PatternList;
	public static List<UrlPattern> ImagePatternList;
	public static void Init(){
		
		
		try {
			captcha = new Captcha("smo.model");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//smo.model的路径
		
		PatternList=new ArrayList<UrlPattern>();
		ImagePatternList=new ArrayList<UrlPattern>();
		
		//规则1，寻找微博用户
		UrlPattern NewPattern=new UrlPattern();
		/*NewPattern.SourceStr=new ArrayList<String>();
		NewPattern.TargetStr=new ArrayList<String>();
		NewPattern.PatternStr="usercard";
		NewPattern.Type=TYPE_LINK;
		PatternList.add(NewPattern);

		//规则3，下一页
		NewPattern=new UrlPattern();
		NewPattern.SourceStr=new ArrayList<String>();
		NewPattern.TargetStr=new ArrayList<String>();
		NewPattern.PatternStr="W_btn_c";
		NewPattern.Type=TYPE_LINK;
		PatternList.add(NewPattern);
		//规则4，关注
		NewPattern=new UrlPattern();
		NewPattern.SourceStr=new ArrayList<String>();
		NewPattern.TargetStr=new ArrayList<String>();
		NewPattern.PatternStr="/follow?";
		NewPattern.Type=TYPE_LINK;
		PatternList.add(NewPattern);
		//规则5，粉丝
		NewPattern=new UrlPattern();
		NewPattern.SourceStr=new ArrayList<String>();
		NewPattern.TargetStr=new ArrayList<String>();
		NewPattern.PatternStr="/fans?";
		NewPattern.Type=TYPE_LINK;
		PatternList.add(NewPattern);*/
		
		//规则7，下一页
		NewPattern=new UrlPattern();
		NewPattern.SourceStr=new ArrayList<String>();
		NewPattern.TargetStr=new ArrayList<String>();
		NewPattern.PatternStr="\\u4e0b\\u4e00\\u9875";
		NewPattern.Operation=SaveOp;
		NewPattern.Type=TYPE_LINK;
		PatternList.add(NewPattern);
		
		
		/*
		NewPattern=new UrlPattern();
		NewPattern.SourceStr=new ArrayList<String>();
		NewPattern.TargetStr=new ArrayList<String>();
		NewPattern.PatternStr="W_btn_a";
		NewPattern.SourceStr.add("aj_topiclist/small");
		NewPattern.TargetStr.add("");
		NewPattern.SourceStr.add("&__rnd=[0-9]+");
		NewPattern.TargetStr.add("");
		NewPattern.Operation=SaveOp;
		NewPattern.Type=TYPE_LINK;
		PatternList.add(NewPattern);*/
		
		//下载图片
		NewPattern=new UrlPattern();
		NewPattern.SourceStr=new ArrayList<String>();
		NewPattern.TargetStr=new ArrayList<String>();
		NewPattern.PatternStr="thumbnail";
		NewPattern.SourceStr.add("thumbnail");
		NewPattern.TargetStr.add("bmiddle");
		NewPattern.Operation=SaveOp;
		ImagePatternList.add(NewPattern);
	}
}
