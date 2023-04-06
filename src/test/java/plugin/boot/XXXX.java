package plugin.boot;

import com.yanan.framework.plugin.annotations.Register;

@Register
public class XXXX {

	public String test() {
		System.err.println("hello world");
		int x = 1/0;
		return "xxxxxx";
	}
}
