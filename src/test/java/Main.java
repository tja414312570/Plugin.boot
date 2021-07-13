

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.resource.DefaultResourceLoader;
import com.yanan.framework.resource.ResourceLoader;
import com.yanan.utils.resource.Resource;

public class Main {
	public static void main(String[] args) throws IOException {
		PlugsFactory.init("classpath:plugin.yc");
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("/Volumes/GENERAL/os/y/hello.y");
		System.out.println(resource);
		List<String> content = IOUtils.readLines(resource.getInputStream());
		AtomicInteger lineCounts = new AtomicInteger(0);
		content.forEach(line->{
			System.err.println(lineCounts+"\t"+line);
			//去掉每行的开头的空格
			line = line.trim();
			if(line.startsWith("#")) {
				System.err.println("注释: "+line);
			}
			String[] tokens = line.split(" ");
			if(tokens.length>1) {
				for(int i = 0;i <tokens.length;i++) {
					System.err.println("\t"+tokens[i]);
				}
			}
			lineCounts.getAndIncrement();
		});
	}
}
