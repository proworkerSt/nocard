package cagtc.pay.job.utils;


import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import cagtc.pay.job.constant.Constant;

/**   
* @Title: HttpClientUtil.java 
* @Package com.zfpt.trans 
* @Description: TODO(发送HTTPS请求客户端工具类) 
* @author linxy
* @date 2015-8-13 下午02:55:45 
* @version V1.0   
*/
public class HttpClientUtil {

	
	public static String doGet(String URL) {
		
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod();
		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER
				,new DefaultHttpMethodRetryHandler(3, false));  
		String response = "";
		try {
			get.setURI(new URI(URL));
			int  statusCode = client.executeMethod(get);   
		 	// 读取 HTTP 响应内容，这里简单打印网页内容   
			byte [] responseBody = get.getResponseBody(); // 读取为字节数组   
			response =  new  String(responseBody, Constant.UNICODE);   
		}catch(URIException e){
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	} 
}
