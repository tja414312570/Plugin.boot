package plugin.boot;

import java.text.DecimalFormat;

import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.plugin.Environment;
import com.yanan.framework.plugin.annotations.Register;

@BootArgs(name="-boot-configure",value="boot-nacos.yc")
@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@PluginBoot
@Register
public class NacosCloudConfigTest {
	public static void main(String[] args) {
		PluginBootServer.run(args);
		System.out.println(Environment.getEnviroment().getConfig("clouds.nacos.bind.DEFULT_GROUP/prop-jdb"));
		
//		
//		ResourceLoader resourceLoader = new DefaultResourceLoader();
//		Resource resource = resourceLoader.getResource("nacos:DEFAULT_GROUP/boot-jdb");
//		Resource resource2 = resourceLoader.getResource("classpath:boot.cloud.yc");
//		Resource resource3 = resourceLoader.getResource("/Volumes/GENERAL/fusionaccess_mac.dmg");
//		Resource resource4 = resourceLoader.getResource("https://codeload.github.com/tja414312570/plugin.boot/zip/master");
//		try {
//			System.out.println("资源名称:"+resource.getName()+",大小:"+resource.size());
//			System.out.println("资源名称:"+resource2.getName()+",大小:"+resource2.size());
//			System.out.println("资源名称:"+resource3.getName()+",大小:"+resource3.size());
//			System.out.println("资源名称:"+resource4.getName()+",大小:"+resource4.size());
//			byte[] byts = new byte[10240];
//			File file = new File("/Users/yanan/Desktop/未命名文件夹/"+resource4.getName());
//			File tmp_File = new File(file.getAbsolutePath()+".tmp");
//			if(!tmp_File.exists())
//				tmp_File.createNewFile();
//			FileOutputStream fos = new FileOutputStream(tmp_File);
//			int len = -1;
//			long current = 0;
//			InputStream inputStream = resource4.getInputStream();
//			while((len = inputStream.read(byts))!= -1) {
//				fos.write(byts,0,len);
//				current += len;
//				System.out.println("download size:"+formetFileSize(current)+" ==> "+formetFileSize(resource4.size()));
//			}
//			tmp_File.renameTo(file);
//			String string = new String(byts);
//			System.out.println(string );
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		try {
			synchronized (NacosCloudConfigTest.class) {
				NacosCloudConfigTest.class.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String formetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
}