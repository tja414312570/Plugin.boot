package plugin.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yanan.framework.boot.BootArgs;
import com.yanan.framework.boot.PluginBootServer;
import com.yanan.framework.resource.DefaultResourceLoader;
import com.yanan.framework.resource.ResourceLoader;
import com.yanan.utils.resource.Resource;

@BootArgs(name="-environment-boot",value="com.yanan.framework.boot.StandEnvironmentBoot")
@BootArgs(name="-boot-configure",value="boot.yc")
public class UniverstSearch {
	static StringBuffer sb = new StringBuffer();
	static int lastScore = 384;
	static int lastLeve = 0;
	static int score = 366;
	static int level = 129454;
	static int levelDelat = 50000;
	static int scoreDelat = 30;
//	土木工程
//	电气工程及自动化
//	信息工程
//	工程造价
//	工程测量技术
//	新能源汽车技术
//	建筑工程技术
//	小学教育
	static String[] keys = {"计算机","土木","土","电","信","工","小学","学前","建筑","能源","建"};
	public static void main(String[] args) throws IOException {
		PluginBootServer.run();
		String[] provices = {"贵州52","四川51","云南53","重庆50"};
		appendTitle(provices);
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Map<String,List<?>> allSchool = new LinkedHashMap<>(); 
		for(String provice : provices) {
			String proviceId = provice.substring(2);
			System.err.println("正在获取:"+provice+"的学校数据");
			String url = "https://api.eol.cn/gkcx/api/?access_token=&admissions=&central=&department=&dual_class=&f211=&f985=&is_doublehigh=&is_dual_class=&is_recruitment=1&keyword=&nature=&page=1"
					+ "&province_id="+proviceId
					+ "&ranktype="
					+ "&request_type=1"
					+ "&school_batch=10"
					+ "&school_type=6001"
					+ "&signsafe="
					+ "&size=100"
					+ "&sort=view_total&top_school_id=[3549]&type=&uri=apidata/api/gk/school/lists";
			Resource resource = resourceLoader.getResource(url);
//			Executor executor =  Executors.newFixedThreadPool(5);
			String content = IOUtils.toString(resource.getInputStream());
			Map<String,Object> json = new Gson().fromJson(content, new TypeToken<Map<String,Object>>() {
			}.getType());
			Map<String,Object> data = (Map<String, Object>) json.get("data");
			List<?> school_list = (List<?>) data.get("item");
			appendProvice(provice+"-共("+school_list.size()+")所");
			allSchool.put(provice.substring(0,2),school_list);
			//遍历学校的专业
			for(Map<String,Object> item : (List<Map<String,Object>>)school_list) {
				System.err.println(item);
				String special = "https://static-data.eol.cn/www/2.0/schoolspecialindex/2020/"+((Double)item.get("school_id")).intValue()+"/52/1/10/1.json";
				System.err.println(special);
				resource = resourceLoader.getResource(special);
//				Executor executor =  Executors.newFixedThreadPool(5);
				content = IOUtils.toString(resource.getInputStream());
				System.err.println(content);
				try {
					json = new Gson().fromJson(content, new TypeToken<Map<String,Object>>() {}.getType());
					data = (Map<String, Object>) json.get("data");
					List<?> lists = (List<?>) data.get("item");
					boolean ok = false;
					for(Map<String,Object> item2 : (List<Map<String,Object>>)lists) {
						if(isMatch(item2)) {
							if(!ok) {
								sb.append("<h5>名字:"+item.get("name")+"</h5><h5>地址:"+item.get("address")+"</h5><h5>学校代码:"+((Double)item.get("school_id")).intValue()+"</h5>");
								sb.append("<table cellspacing=\"0\" cellpadding=\"0\"><tr><th>类别</th><th>专业</th><th>最低分</th><th>最低名次</th></tr>");
								ok = true;
							}
							appendSpecialLine(item2.get("level3_name")+"",item2.get("spname")+"",item2.get("min")+"",item2.get("min_section")+"");
						}
					}
					System.err.println(json);
					System.err.println("===========");
					sb.append("</table>");
					sb.append("</p>");
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
//					break;
				}
				sb.append("</p>");
			}
//			break;
		}
		System.out.println(sb);
		//遍历每个学校的专业数据
		output(sb.toString());
	}
	private static boolean isMatch(Map<String, Object> item2) {
		int min = Integer.valueOf((String) item2.get("min")) ;
		int min_sec = Integer.valueOf((String) item2.get("min_section"));
		String type = (String) item2.get("level3_name");
		String spname = (String) item2.get("spname");
		if(Math.abs(min_sec - level)>levelDelat) {
			return false;
		}
		for(String key : keys) {
			int index = type.indexOf(key);
			if(index>-1) {
				item2.put("level3_name", type.substring(0,index)+"<b style='color:red'>"+key+"</b>"+type.substring(index+key.length()));
				return true;
			}
			index = spname.indexOf(key);
			if(index>-1) {
				item2.put("level3_name", spname.substring(0,index)+"<b style='color:red'>"+key+"</b>"+spname.substring(index+key.length()));
				return true;
			}
		}
		return false;
	}
	private static void appendProvice(String provice) {
		sb.append("<h4>\t"+provice+"\t</h4>");
	}
	private static void appendSpecialLine(String type,String spe,String sco,String rat) {
		sb.append("<tr><td>"+type+"</td><td>"+spe+"</td><td>"+sco+"</td><td>"+rat+"</td></tr>");
	}
	private static void appendTitle(String[] provices) {
		sb.append("<style>h1{text-align:center;}table{width:100%; border-collapse: collapse;font-size: 0.83em;}th,td{border:1px solid #cec8c8;width:25%}p{magin:0 4px;padding:0;}h5{margin:8px 0px}</style>");
		sb.append(" <meta charset=\"utf-8\">");
		sb.append("<meta name=\"viewport\" content=\"initial-scale=1.0, maximum-scale=1.0, user-scalable=no\" />");
		sb.append("<h1>查询学校和专业分数[2020年]</h1>");
		sb.append("<h5>查询地区:"+Arrays.toString(provices)+"</h5>");
		sb.append("<h5>查询关键字:"+Arrays.toString(keys)+"</h5>");
		sb.append("<h5>查询名次:"+level+",查询范围:"+levelDelat+"</h5>");
		sb.append("<p style=\" font-size: 12px;color: #ca4f4f;\">数据说明:查询排名为当年考生排名，范围就是往年排名和当年排名的误差值小于查询误差范围"+"</p>");
	}
	public  static void  output(String content) throws IOException {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("/Users/yanan/Desktop/个人文件夹/学校.html");
		IOUtils.copy(new ByteArrayInputStream(content.getBytes()), resource.getOutputStream());
	}
}
