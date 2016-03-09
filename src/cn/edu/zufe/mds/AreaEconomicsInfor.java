package cn.edu.zufe.mds;

/**
 * 这个类是存放一个省份相关信息的类
 */
import cn.edu.zufe.mds.Data;

public class AreaEconomicsInfor {
	public String province; //省份名称
	public double industry[][] = new double[Data.industryCount][Data.yearCount];  //产业数值
	public double percent[][] = new double[Data.industryCount][Data.yearCount];  //产业占比
};