package plugin.boot;

import java.io.IOException;
import java.util.Properties;

import com.yanan.framework.boot.cloud.nacos.NacosConfigureFactory;
import com.yanan.utils.reflect.AppClassLoader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestClone {
	public static void main(String[] args) {
		//1,创建okHttpClient对象
		OkHttpClient mOkHttpClient = new OkHttpClient();
		//2,创建一个Request
		final Request request = new Request.Builder()
		                .url("https://codeload.github.com/tja414312570/YaNanFrame/zip/master")
		                .build();
		//3,新建一个call对象
		Call call = mOkHttpClient.newCall(request); 
		//4，请求加入调度，这里是异步Get请求回调
		call.enqueue(new Callback()
		        {
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						arg1.printStackTrace();
					}

					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						System.out.println(arg1.body().string());
					}
		        });   
	}
}
