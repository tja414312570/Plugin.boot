package plugin.boot;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

//@Register
@WebInitParam(name = "", value = "")
@WebFilter(filterName = "tokenFilters", urlPatterns = "/*")
public class FilterTest implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("过滤器初始化");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("过滤器使用");
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) {
		
		int j = (1 << 10)-1;
		int d = j;
		System.out.println(Integer.toBinaryString(j));
		int bitCount = Integer.bitCount(j);
		System.out.println(bitCount);
		int iCount;
		for(int i = 2;i < 40;i++) {
			System.out.println();
			System.out.println(Integer.toBinaryString(i)+"---"+i);
			int c = (d*i);
			//位数是否为1
//			if((i & 1) == 1) {
//				iCount = Integer.bitCount(i);
//				System.out.println(iCount+"   lll");
//			}
			
			
//			if((j & 2) == 1) {
//				System.out.println((j<<=1)+"===>"+c);
//			}else {
//				System.out.println((j <<= 1)+"===>"+c);
//			}
			System.out.println(Integer.toBinaryString(j));
			System.out.println(Integer.toBinaryString(c));
			
		}
	}

}