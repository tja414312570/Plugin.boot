package plugin.boot;

import java.util.Arrays;

import com.yanan.frame.plugin.PlugsFactory;
import com.yanan.frame.plugin.annotations.Register;
import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.PluginBoot;
import com.yanan.framework.boot.PluginBootServer;

//@BootArgs(name="-boot-configure",value="boot-nacos.yc")
@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@PluginBoot
@Register
public class NacosCloudConfigTest {
//	@Variable("includ")
//	private String boot;
//	@Variable("plugins")
//	private List<Config> config;
//	@Variable("jdb.id")
//	private String id;
	
	 public int findPeak(int[] A) {
	        return findNext(A,0,A.length);
	    }
	    public int findNext(int[] B,int min,int max){
	    	int pos = Math.abs((min+max)/2);
	    	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        int front = B[pos-1];
	        int current = B[pos];
	        System.out.println(pos+"  ===> "+current);
	        int before = B[pos+1];
	        if(front<current && before <current)
	            return pos;
	        if(front > current) {
	  	        max = pos;
	        }else {
	        	min = pos;
	        	
	        }
	        return findNext(B,min,max);
	    }
	    public int[] FinalDiscountedPrice(int[] prices) {
	    	int[] find = findMin(1,prices);
	    	for(int i = 0;i<prices.length;i++) {
	    		
	    		find = findMin(i+1,prices);
    			System.out.println(i+"==>"+Arrays.toString(find));
    			System.out.println(prices[i] +" cnm"+ find[1]);
    	    	if(prices[i] >= find[1] && i < find[0])
    	    		prices[i] = prices[i] - find[1];
	    	}
	    	System.out.println("==>"+Arrays.toString(prices));
	       return prices;
	    }
	private int[] findMin(int pos,int[] prices) {
		int[] find = new int[] {0,prices[pos-1]};
		for(int i = pos;i<prices.length;i++) {
			if(prices[i] <= find[1]) {
				find[1] = prices[i];
				find[0] = i;
				break;
			}
		}
		return find;
	}
	public static void main(String[] args) {
		PluginBootServer.run(args);
		NacosCloudConfigTest nacosCloudConfigTest = PlugsFactory.getPluginsInstance(NacosCloudConfigTest.class);
		System.out.println("================");
//		System.out.println(nacosCloudConfigTest.boot);
//		System.out.println(nacosCloudConfigTest.config);
//		System.out.println(nacosCloudConfigTest.id);
		System.out.println(nacosCloudConfigTest.FinalDiscountedPrice(new int[]{2,3,6,10,9,7,3,9,3,5}));
//		try {
//			synchronized (NacosCloudConfigTest.class) {
//				NacosCloudConfigTest.class.wait();
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}