package cn.edu.zufe.mds;

public class Language {
	public static int languageType = 1; //0���ģ�1Ӣ��
	
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
			year = "��ݣ�";
			clusterthreshold = "������ֵ��";
			product = "��ֵ";
			proportion = "ռ��";
			industry[0] = "��һ��ҵ";
			industry[1] = "�ڶ���ҵ";
			industry[2] = "�� ��ҵ";
			industry[3] = "�� ����ҵ";
			industry[4] = "������ҵ";
			industry[5] = "�� ����ҵ";
			industry[6] = "�� ����������ҵ";
			industry[7] = "�� ס�޺Ͳ���ҵ";
			industry[8] = "�� ����ҵ";
			industry[9] = "�� ���ز�ҵ";
			industry[10] = "�� ��������ҵ";
			industry[11] = "�˾�GPD";
			alone = "��";
			neighborhood = "һ��ʡ��";
			sort[0] = "�����С����";
			sort[1] = "���˾�GDP����";
			sort[2] = "����GDP����";
			point = "��ͼ";
			pieChart = "��ͼ";
		} else if(languageType == 1) {
			year = "Year:";
			clusterthreshold = "Distance��";
			product = "Product";
			proportion = "Proportion";
			industry[0] = "Primary";
			industry[1] = "Secondary";
			industry[2] = "Industry";
			industry[3] = "Building";
			industry[4] = "Tertiary";
			industry[5] = "Post";
			industry[6] = "Business";
			industry[7] = "ס�޺Ͳ���ҵ";
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
