package cn.edu.zufe.mds;

/**
 * ������Ǵ��һ��ʡ�������Ϣ����
 */
import cn.edu.zufe.mds.Data;

public class AreaEconomicsInfor {
	public String province; //ʡ������
	public double industry[][] = new double[Data.industryCount][Data.yearCount];  //��ҵ��ֵ
	public double percent[][] = new double[Data.industryCount][Data.yearCount];  //��ҵռ��
};