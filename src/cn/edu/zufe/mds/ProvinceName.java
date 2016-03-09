package cn.edu.zufe.mds;
/**
 * 一个辅助类，进行省份的各个内容的相互转换
 * @author Gavin
 *
 */
public class ProvinceName {
	public String[] provinceChineseName = new String[32];
	public String[] provinceEnglishName = new String[32];

	public ProvinceName() {
		provinceChineseName[0] = "中国";
		provinceEnglishName[0] = "china";
		provinceChineseName[1] = "北京市";
		provinceEnglishName[1] = "Bei_Jing";
		provinceChineseName[2] = "上海市";
		provinceEnglishName[2] = "Shang_Hai";
		provinceChineseName[3] = "江苏省";
		provinceEnglishName[3] = "Jiang_Su";
		provinceChineseName[4] = "天津市";
		provinceEnglishName[4] = "Tian_Jin";
		provinceChineseName[5] = "河北省";
		provinceEnglishName[5] = "He_Bei";
		provinceChineseName[6] = "重庆市";
		provinceEnglishName[6] = "Chong_Qing";
		provinceChineseName[7] = "四川省";
		provinceEnglishName[7] = "Si_Chuan";
		provinceChineseName[8] = "河南省";
		provinceEnglishName[8] = "He_Nan";
		provinceChineseName[9] = "宁夏回族自治区";
		provinceEnglishName[9] = "Ning_Xia";
		provinceChineseName[10] = "安徽省";
		provinceEnglishName[10] = "An_Hui";
		provinceChineseName[11] = "浙江省";
		provinceEnglishName[11] = "Zhe_Jiang";
		provinceChineseName[12] = "江西省";
		provinceEnglishName[12] = "Jiang_Xi";
		provinceChineseName[13] = "贵州省";
		provinceEnglishName[13] = "Gui_Zhou";
		provinceChineseName[14] = "山东省";
		provinceEnglishName[14] = "Shan_Dong";
		provinceChineseName[15] = "山西省";
		provinceEnglishName[15] = "Shan1_Xi";
		provinceChineseName[16] = "陕西省";
		provinceEnglishName[16] = "Shan3_Xi";
		provinceChineseName[17] = "湖南省";
		provinceEnglishName[17] = "Hu_Nan";
		provinceChineseName[18] = "湖北省";
		provinceEnglishName[18] = "Hu_Bei";
		provinceChineseName[19] = "甘肃省";
		provinceEnglishName[19] = "Gan_Su";
		provinceChineseName[20] = "辽宁省";
		provinceEnglishName[20] = "Liao_Ning";
		provinceChineseName[21] = "吉林省";
		provinceEnglishName[21] = "Ji_Lin";
		provinceChineseName[22] = "黑龙江省";
		provinceEnglishName[22] = "Hei_Long_Jiang";
		provinceChineseName[23] = "广东省";
		provinceEnglishName[23] = "Guang_Dong";
		provinceChineseName[24] = "广西壮族自治区";
		provinceEnglishName[24] = "Guang_Xi";
		provinceChineseName[25] = "新疆维吾尔自治区";
		provinceEnglishName[25] = "Xin_Jiang";
		provinceChineseName[26] = "内蒙古自治区";
		provinceEnglishName[26] = "Nei_Meng_Gu";
		provinceChineseName[27] = "云南省";
		provinceEnglishName[27] = "Yun_Nan";
		provinceChineseName[28] = "青海省";
		provinceEnglishName[28] = "Qing_Hai";
		provinceChineseName[29] = "西藏自治区";
		provinceEnglishName[29] = "Xi_Zang";
		provinceChineseName[30] = "海南省";
		provinceEnglishName[30] = "Hai_Nan";
		provinceChineseName[31] = "福建省";
		provinceEnglishName[31] = "Fu_Jian";
	}
	
	public String GetProvinceEnglishName(String provinceChineseName) {
		int len = provinceEnglishName.length;
		for (int i = 0; i < len; i++) {
			if (provinceChineseName.equals(this.provinceChineseName[i])) {
				return provinceEnglishName[i];
			}
		}
		return null;
	}

	public String GetProvinceEnglishName(int index) {
		int len = provinceEnglishName.length;
		if (index < 0 || index >= len)
			return null;
		return provinceEnglishName[index];
	}

	public int GetProvinceIndex(String areaName) {
		int len = provinceEnglishName.length;
		for (int i = 0; i < len; i++) {
			if ((areaName.equals(provinceEnglishName[i]))
					|| (areaName.equals(provinceChineseName[i]))) {
				return i;
			}
		}
		return -1;
	}

	public String GetProvinceChineseName(String provinceEnglishName) {
		int len = provinceChineseName.length;
		for (int i = 0; i < len; i++) {
			if (provinceEnglishName.equals(this.provinceEnglishName[i])) {
				return provinceChineseName[i];
			}
		}
		return null;
	}

	public String GetProvinceChineseName(int index) {
		int len = provinceChineseName.length;
		if (index < 0 || index >= len)
			return null;
		return provinceChineseName[index];
	}
	public int GetProvinceCount(){
		return provinceChineseName.length;
	}
}
