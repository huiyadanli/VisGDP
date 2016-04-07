package cn.edu.zufe.mds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 全局变量类，保存所有的全局变量的信息
 *
 */
public class GlobalVariables {
	public static String  selectCity = null; //选中的省份
	public static List<String> selectCityList1 =new CopyOnWriteArrayList<String>(); //选中的省份 1度
	public static List<String> selectCityList2 =new CopyOnWriteArrayList<String>(); //选中的省份 2度
	public static List<Integer> selectCityList3 =new CopyOnWriteArrayList<Integer>(); //一个聚类中的省份
	//public static String citySelect = "北京市"; //选中的城市
	public static int year = Data.startYear; //年份
	public static boolean[] industryFlag =new boolean[Data.industryCount]; //记录那些产业被选中
	public static int productOrProport = 0; //0产值,1占比
	public static int provOrYear = 0; //0显示一年中的不同省份,1显示一省中的不同年份
	public static int sortMethod = 1; //第三窗口StoryLine排序--- 0按类元素的多少排序,1按人均GDP排序,2按总GDP排序
	
	public static int pointOrPie = 0; //0点图显示，1饼图显示
	
	public static double N = 0.0005; //颜色映射时的一个阈值
	
	public static ArrayList<ArrayList<Integer>>[] clusters =new ArrayList[Data.yearCount];
	public static int colorMappingStyle=1; //颜色映射模式
	
	public static boolean mdsOrStoryFlowClick = false;
	
}
