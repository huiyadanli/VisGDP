package cn.edu.zufe.mds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ȫ�ֱ����࣬�������е�ȫ�ֱ�������Ϣ
 *
 */
public class GlobalVariables {
	public static String  selectCity = null; //ѡ�е�ʡ��
	public static List<String> selectCityList1 =new CopyOnWriteArrayList<String>(); //ѡ�е�ʡ�� 1��
	public static List<String> selectCityList2 =new CopyOnWriteArrayList<String>(); //ѡ�е�ʡ�� 2��
	public static List<Integer> selectCityList3 =new CopyOnWriteArrayList<Integer>(); //һ�������е�ʡ��
	//public static String citySelect = "������"; //ѡ�еĳ���
	public static int year = Data.startYear; //���
	public static boolean[] industryFlag =new boolean[Data.industryCount]; //��¼��Щ��ҵ��ѡ��
	public static int productOrProport = 0; //0��ֵ,1ռ��
	public static int provOrYear = 0; //0��ʾһ���еĲ�ͬʡ��,1��ʾһʡ�еĲ�ͬ���
	public static int sortMethod = 1; //��������StoryLine����--- 0����Ԫ�صĶ�������,1���˾�GDP����,2����GDP����
	
	public static int pointOrPie = 0; //0��ͼ��ʾ��1��ͼ��ʾ
	
	public static double N = 0.0005; //��ɫӳ��ʱ��һ����ֵ
	
	public static ArrayList<ArrayList<Integer>>[] clusters =new ArrayList[Data.yearCount];
	public static int colorMappingStyle=1; //��ɫӳ��ģʽ
	
	public static boolean mdsOrStoryFlowClick = false;
	
}
