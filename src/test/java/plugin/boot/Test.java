package plugin.boot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.PushBuilder;

import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.token.Token;
import com.yanan.framework.webmvc.annotations.RequestMapping;
import com.yanan.framework.webmvc.parameter.annotations.PathVariable;
import com.yanan.framework.webmvc.parameter.annotations.RequestParam;

//@Register
public class Test implements ServletContextListener{
//	@Authentication(roles="root")
	@RequestMapping("/{path**}")
	public void sayHello(HttpServletRequest request,@PathVariable("path") String path,
			HttpServletResponse response,@RequestParam Map<String,String> param) throws ClientProtocolException, IOException {
		
		System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.LogFactoryImpl");
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.simplelog.defaultlog", "error");System.out.println(path);
		System.out.println(request.getQueryString());
		String pa =request.getQueryString()==null?"" :"?"+request.getQueryString();
		String urlStr = "http://58.42.247.88/ls/"+path+pa;
		System.out.println(urlStr);
		OutputStream out = response.getOutputStream(); 
		if(urlStr.indexOf(".css")>-1) {
			response.setContentType("text/css");
		}
		else if(urlStr.indexOf(".js")>-1) {
			response.setContentType("application/javascript");
		}else if(urlStr.indexOf(".jpg")>-1
				|| urlStr.indexOf(".svg")>-1
				|| urlStr.indexOf(".png")>-1) {
			response.setContentType("image/jpg");
			System.out.println("urlStr=" + urlStr);  
			URL url = new URL(urlStr);  
	        URLConnection con = url.openConnection();  
	        InputStream is = con.getInputStream();
	        byte[] bytes = new byte[1024];
	        int len;
	        while((len = is.read(bytes))!= -1) {
	        	 out.write(bytes,0,len);
	        }
	        System.out.println("urlStr=" + urlStr);  
	        out.flush();  
	        out.close();  
		}
		else {
			response.setContentType("text/html");
		}
		String res = testGet(urlStr);  
		res = res.replace("172.17.19.210:8086/api","12j463i096.zicp.vip/ls/api");
		res = res.replace("172.17.19.210:8086","12j463i096.zicp.vip");
        out.write(res.getBytes(),0,res.getBytes().length);
        
	}

public String testGet(String url) throws ClientProtocolException, IOException {
		// TODO Auto-generated constructor stub
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		if(response!=null)
		{
			HttpEntity entity = response.getEntity();
			String strResult = EntityUtils.toString(entity,"UTF-8");
			return strResult;
		}
		return url;
	}
	@RequestMapping("/push")
	public String testPush(HttpServletRequest request,Token token) {
		System.out.println(Token.getToken().getId());
		System.out.println(PlugsFactory.getPluginsInstanceList(ServletContextListener.class));
		PushBuilder pushBuilder  = request.newPushBuilder();
		System.out.println(request.getRequestURL());
		token.addRole("root");
		System.out.println(pushBuilder);
		if (pushBuilder != null) {
			   pushBuilder.path("images/hero-banner.jpg").push();
			   pushBuilder.path("css/menu.css").push();
			   pushBuilder.path("js/marquee.js").push();
			}
		
		return request.getRequestURL().toString()+"  "+token.getId();
	}
}