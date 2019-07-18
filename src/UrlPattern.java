import java.util.List;


public class UrlPattern {
	public String PatternStr;  //模式,网页中
	public List<String> SourceStr;   //链接模式
	public List<String> TargetStr;  //替换模式
	public int Operation;       //操作
	public int Type;   //link or anchor
}
