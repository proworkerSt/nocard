package cagtc.pay.job.constant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class Constant {

	/**
	 * 最大个数
	 */
	public final static int MAX_ACTIVE_LIMIT = 500;
	
	public final static String JOB_STATUS_1 = "1"; //启用
	public final static String JOB_STATUS_2 = "2";//停用
	public final static String JOB_STATUS_0 = "0";//删除
	
	/**
	 * 喜付定时作业组名称
	 */
	public final static String JOB_GROUP_NAME = "XFPAY_TRIGGER_JOB";

	
	/**
	 * 编码
	 */
	public final static String UNICODE = "UTF-8";
	
	public static void main(String[] args)throws Exception {
		
		String url1 = "http://foodmall.com/home.htm";
		
		URL url=new URL(url1);//取得资源对象
		URLConnection uc=url.openConnection();//生成连接对象
		uc.setDoOutput(true);
		uc.connect(); //发出连接
		String temp;
		StringBuffer sb = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(
		url.openStream(),"utf-8")); 
		while ((temp = in.readLine()) != null) {
		//sb.append("\n");
		sb.append(temp);
		}
		in.close();
		System.out.println(sb);
		
		
		
	}
	
}
