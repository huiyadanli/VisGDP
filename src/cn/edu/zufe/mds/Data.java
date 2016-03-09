package cn.edu.zufe.mds;

import java.io.BufferedReader;
import java.io.FileReader;

import cn.edu.zufe.mds.ProvinceName;

public class Data {
	public static int yearCount = 36; // 1978~2013 //年份数
	public static int industryCount = 13; // 产业数
	public static int provinceCount = 32; // 省份数
	public static int startYear = 1978; // 起始年份
	public static int indexIndustry[] = { 1, 2, 5, 12 }; // 记录一、二、三产业的Index
	public static AreaEconomicsInfor[] area = new AreaEconomicsInfor[32];
	ProvinceName pName = new ProvinceName();
	public static boolean isLoadData = false;

	public void LoadData(String urlFile) {
		// 初始化以及清零
		for (int i = 0; i < provinceCount; i++) {
			area[i] = new AreaEconomicsInfor();
			area[i].province = "";
			for (int j = 0; j < industryCount; j++) {
				for (int k = 0; k < yearCount; k++) {
					area[i].industry[j][k] = 0;
				}
			}
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(urlFile));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] str = line.split(",");
				// 读取GDP数据
				int year = Integer.parseInt(str[0]);
				if (year < startYear) {
					continue;
				}
				int index = pName.GetProvinceIndex(str[2]);
				area[index].province = str[2];
				for (int j = 0; j < industryCount - 1; j++) {
					area[index].industry[j][year - startYear] = Double
							.parseDouble(str[j + 3]);
				}
				area[index].industry[industryCount - 1][year - startYear] = Double
						.parseDouble(str[18]); // 人均地区生产总值
				area[index].industry[0][year - startYear] = area[index].industry[1][year
						- startYear]
						+ area[index].industry[2][year - startYear]
						+ area[index].industry[5][year - startYear]; // 重新计算总GDP Index = 0
				// 计算占比
				area[index].percent[0][year - startYear] = 1; // 生产总值占比不计算
				area[index].percent[industryCount - 1][year - startYear] = 1; // 人均地区生产总值占比不计算

				// 所有产业占总值的比例
				for (int i = 1; i < industryCount - 1; i++) {
					area[index].percent[i][year - startYear] = area[index].industry[i][year
							- startYear]
							/ area[index].industry[0][year - startYear];
				}

				/*
				 * //第一、二、三产业占比 for (int i = 0; i < indexIndustry.length; i++) {
				 * area[index].percent[indexIndustry[i]][year-startYear] =
				 * area[index].industry[indexIndustry[i]][year-startYear] /
				 * area[index].industry[0][year-startYear]; }
				 * 
				 * //第一、二、三产业明细占比 //住宿和餐饮业有问题 for (int i = 0; i <
				 * indexIndustry.length-1; i++) { for (int j =
				 * indexIndustry[i]+1; j < indexIndustry[i+1]; j++) {
				 * area[index].percent[j][year-startYear] =
				 * area[index].industry[j][year-startYear] /
				 * area[index].industry[indexIndustry[i]][year-startYear]; } }
				 */

			}
			isLoadData = true;
			reader.close();
		} catch (Exception e) {
			isLoadData = false;
			e.printStackTrace();
		}

	}
}
