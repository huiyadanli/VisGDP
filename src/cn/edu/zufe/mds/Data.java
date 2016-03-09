package cn.edu.zufe.mds;

import java.io.BufferedReader;
import java.io.FileReader;

import cn.edu.zufe.mds.ProvinceName;

public class Data {
	public static int yearCount = 36; // 1978~2013 //�����
	public static int industryCount = 13; // ��ҵ��
	public static int provinceCount = 32; // ʡ����
	public static int startYear = 1978; // ��ʼ���
	public static int indexIndustry[] = { 1, 2, 5, 12 }; // ��¼һ����������ҵ��Index
	public static AreaEconomicsInfor[] area = new AreaEconomicsInfor[32];
	ProvinceName pName = new ProvinceName();
	public static boolean isLoadData = false;

	public void LoadData(String urlFile) {
		// ��ʼ���Լ�����
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
				// ��ȡGDP����
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
						.parseDouble(str[18]); // �˾�����������ֵ
				area[index].industry[0][year - startYear] = area[index].industry[1][year
						- startYear]
						+ area[index].industry[2][year - startYear]
						+ area[index].industry[5][year - startYear]; // ���¼�����GDP Index = 0
				// ����ռ��
				area[index].percent[0][year - startYear] = 1; // ������ֵռ�Ȳ�����
				area[index].percent[industryCount - 1][year - startYear] = 1; // �˾�����������ֵռ�Ȳ�����

				// ���в�ҵռ��ֵ�ı���
				for (int i = 1; i < industryCount - 1; i++) {
					area[index].percent[i][year - startYear] = area[index].industry[i][year
							- startYear]
							/ area[index].industry[0][year - startYear];
				}

				/*
				 * //��һ����������ҵռ�� for (int i = 0; i < indexIndustry.length; i++) {
				 * area[index].percent[indexIndustry[i]][year-startYear] =
				 * area[index].industry[indexIndustry[i]][year-startYear] /
				 * area[index].industry[0][year-startYear]; }
				 * 
				 * //��һ����������ҵ��ϸռ�� //ס�޺Ͳ���ҵ������ for (int i = 0; i <
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
