package cn.edu.zufe.mds;

public class Language {
	public static int languageType = 1; //0中文，1英文
	
	public String year;
	public String clusterthreshold;
	public String product;
	public String proportion;
	public String[] industry = new String[12];
	public String alone;
	public String neighborhood;
	public String[] sort = new String[3];
	public String point, pieChart;
	public Language() {
		if(languageType == 0) {
			year = "年份：";
			clusterthreshold = "聚类阈值：";
			product = "产值";
			proportion = "占比";
			industry[0] = "第一产业";
			industry[1] = "第二产业";
			industry[2] = "├ 工业";
			industry[3] = "└ 建筑业";
			industry[4] = "第三产业";
			industry[5] = "├ 邮政业";
			industry[6] = "├ 批发和零售业";
			industry[7] = "├ 住宿和餐饮业";
			industry[8] = "├ 金融业";
			industry[9] = "├ 房地产业";
			industry[10] = "└ 其他服务业";
			industry[11] = "人均GPD";
			alone = "无";
			neighborhood = "一度省份";
			sort[0] = "按类大小排序";
			sort[1] = "按人均GDP排序";
			sort[2] = "按总GDP排序";
			point = "点图";
			pieChart = "饼图";
		} else if(languageType == 1) {
			year = "Year:";
			clusterthreshold = "Distance：";
			product = "Product";
			proportion = "Proportion";
			industry[0] = "Primary";
			industry[1] = "Secondary";
			industry[2] = "Industry";
			industry[3] = "Building";
			industry[4] = "Tertiary";
			industry[5] = "Post";
			industry[6] = "Business";
			industry[7] = "住宿和餐饮业";
			industry[8] = "Financial";
			industry[9] = "Real estate";
			industry[10] = "Others";
			industry[11] = "Per Capita GDP";
			alone = "Alone";
			neighborhood = "Neighborhood";
			sort[0] = "Cluster size";
			sort[1] = "PCGDP value";
			sort[2] = "GDP value";
			point = "Point Chart";
			pieChart = "Pie Chart";
		}
	}
}
