package cn.edu.zufe.mds;

import geomerative.RG;
import geomerative.RPoint;
import geomerative.RShape;
import processing.core.PApplet;
import processing.core.PGraphics;

import cn.edu.zufe.color.ColorMapping;
import cn.edu.zufe.color.ColorMappingFactory;

public class PAppletChinaMap extends PApplet {

	FrameMain fMain = null;

	ColorMappingFactory colorFactory = new ColorMappingFactory();
	ColorMapping colorMapping = null;
	PGraphics pgMap, pgColorBar;
	int prevYear = -1;

	float hue = 3843;
	RShape chinaMap, provinceMap;
	String cityCompare = null;
	ProvinceName pName = new ProvinceName();
	String clickedArea = "", overArea = "";
	int clickedAreaIndex = 0;

	String cityName[] = { "Xin_Jiang", "Xi_Zang", "Qing_Hai", "Yun_Nan",
			"Gan_Su", "Si_Chuan", "Ning_Xia", "Chong_Qing", "Gui_Zhou",
			"Guang_Xi", "Shan3_Xi", "Hai_Nan", "Nei_Meng_Gu", "Hu_Nan",
			"Guang_Dong", "He_Nan", "Hu_Bei", "He_Bei", "Jiang_Xi", "Bei_Jing",
			"Shan_Dong", "Tian_Jin", "An_Hui", "Jiang_Su", "Fu_Jian",
			"Zhe_Jiang", "Shang_Hai", "Liao_Ning", "Ji_Lin", "Hei_Long_Jiang",
			"Shan1_Xi", "Tai_Wan" };
	// public static List<String> selectCityList =new
	// CopyOnWriteArrayList<String>();
	// 省份邻接矩阵
	int[][] AdjacencyCity1 = { { 1, 2, 4, -1 }, { 0, 2, 5, 3, -1 },
			{ 0, 1, 5, 4, -1 }, { 1, 5, 8, 9, -1 }, { 0, 2, 12, 6, 10, 5, -1 },
			{ 3, 1, 2, 4, 10, 7, 8, -1 }, { 4, 12, 10, -1 },
			{ 8, 5, 10, 13, 16, -1 }, { 9, 3, 5, 7, 13, -1 },
			{ 3, 8, 13, 14, -1 }, { 7, 4, 5, 6, 16, 12, 30, 15, -1 },
			{ 14, 9, -1 }, { 29, 28, 27, 17, 30, 10, 6, 4, -1 },
			{ 14, 9, 18, 16, 8, 7, -1 }, { 24, 18, 13, 9, 11, -1 },
			{ 16, 10, 30, 17, 20, 22, -1 }, { 7, 10, 15, 22, 18, 13, -1 },
			{ 15, 30, 12, 19, 21, 20, -1 }, { 14, 13, 22, 16, 25, 24, -1 },
			{ 21, 17, -1 }, { 23, 22, 15, 17, -1 }, { 17, 19, -1 },
			{ 25, 23, 20, 15, 16, 18, -1 }, { 26, 25, 22, 20, -1 },
			{ 25, 18, 14, -1 }, { 26, 23, 22, 18, 24, -1 }, { 23, 25, -1 },
			{ 28, 12, 17, -1 }, { 29, 27, 12, -1 }, { 28, 12, -1 },
			{ 15, 10, 17, 12, -1 }, { 25, 24, 14, -1 } };
	int[][] AdjacencyCity2 = { { 12, 6, 10, 5, 3, -1 }, { 4, 10, 7, 8, 9, -1 },
			{ 12, 6, 10, 7, 8, 3, -1 }, { 0, 2, 4, 10, 7, 13, 14, -1 },
			{ 1, 3, 8, 7, 16, 15, 30, -1 },
			{ 0, 12, 6, 30, 15, 16, 13, 9, -1 },
			{ 0, 2, 5, 7, 30, 16, 15, 29, 28, 27, 17, -1 },
			{ 9, 3, 1, 2, 4, 6, 12, 30, 15, 22, 18, 14, -1 },
			{ 14, 18, 16, 10, 4, 2, 1, -1 }, { 24, 18, 16, 7, 5, 1, -1 },
			{ 8, 13, 18, 22, 20, 17, 2, 0, 1, 3, -1 }, { 24, 18, 13, 9, -1 },
			{ 21, 19, 20, 15, 7, 16, 5, 0, 2, -1 },
			{ 11, 24, 25, 22, 15, 10, 5, 3, -1 }, { 25, 22, 16, 7, 8, 3, -1 },
			{ 18, 13, 7, 25, 23, 19, 21, 12, 5, 4, -1 },
			{ 9, 14, 24, 25, 23, 20, 17, 30, 12, 6, 4, 5, 8, -1 },
			{ 23, 22, 16, 10, 29, 28, 10, 4, 6, 0, -1 },
			{ 26, 23, 31, 11, 9, 8, 7, 10, 15, 20, -1 },
			{ 15, 30, 12, 20, -1 },
			{ 26, 25, 18, 16, 10, 30, 19, 21, 27, 12, -1 },
			{ 15, 30, 12, 20, -1 }, { 24, 14, 13, 7, 10, 30, 17, 26, -1 },
			{ 24, 18, 15, 16, 17, -1 }, { 26, 23, 22, 16, 13, 9, 11, -1 },
			{ 14, 31, 13, 16, 15, 20, -1 }, { 24, 18, 22, 20, -1 },
			{ 19, 21, 29, 30, 10, 6, 4, -1 }, { 17, 29, 30, 10, 6, 4, -1 },
			{ 27, 17, 29, 30, 10, 6, 4, -1 },
			{ 29, 28, 27, 19, 21, 20, 22, 16, 7, 5, 6, 4, -1 },
			{ 25, 18, 14, -1 } };

	int Degree;

	public PAppletChinaMap(FrameMain f) {
		fMain = f;
	}

	public void setup() {
		size(700, 540);
		Degree = 1;
		RG.init(this);
		chinaMap = RG.loadShape("src\\China.svg");

		pgMap = createGraphics(700, 540);
		pgMap.beginDraw();
		pgMap.colorMode(HSB, 360, 100, 100); // 色彩模式HSB
		pgMap.background(0, 0, 100);
		pgMap.endDraw();

		pgColorBar = createGraphics(200, 400);
		drawColorBar(pgColorBar);
	}

	public void draw() {

		background(255);

		if (prevYear != GlobalVariables.year) {
			drawMap(pgMap);
			prevYear = GlobalVariables.year;
		}
		image(pgMap, 0, 0);

		highlightArea();
		
		if (GlobalVariables.selectCityList3.size() > 0) {
			fill(129, 14, 142);
			for (int i = 0; i < GlobalVariables.selectCityList3.size(); i++) {
				provinceMap = chinaMap
						.getChild(pName.provinceEnglishName[GlobalVariables.selectCityList3
								.get(i) + 1]);
				RG.shape(provinceMap);
			}
		}
		
		//stroke(238);
		//strokeWeight(5);
		//line(640,0,640,600);
		image(pgColorBar, 645, 210);
		//stroke(0);
		//strokeWeight(1);

		showProvName();
	}

	// 颜色映射的种类 此时为第一种映射方案，后面陆续继承colorMapping类，使colorMapping按照工厂模式运行
	public void colorStyle() {
		if (Data.isLoadData == false)
			return;
		colorMapping = colorFactory.getColorMappingStyle(GlobalVariables.colorMappingStyle);
		colorMapping.setInitData();
		colorMapping.normalization(0, 120);
		System.out.println("当前颜色映射方案为：" + GlobalVariables.colorMappingStyle);
		// colorMapping.transferColorToHsl(0, 250);
		drawMap(pgMap);
	}

	public void drawMap(PGraphics pg) {
		pg.beginDraw();
		// 如果还没有颜色映射，进行颜色映射
		if (colorMapping == null)
			colorStyle();
		// RG.shape(chinaMap,0,0,500,500);
		pg.stroke(40);
		for (int i = 0; i < Data.area.length; i++) {
			pg.fill(128);
			String provinceChineseName = Data.area[i].province;
			String provinceEnglishName = pName
					.GetProvinceEnglishName(provinceChineseName);
			provinceMap = chinaMap.getChild(provinceEnglishName);
			if (provinceMap != null) {
				provinceMap.draw(pg);
			}
		}
		for (int i = 0; i < Data.area.length; i++) {
			// 实际年份
			int year = GlobalVariables.year;
			double b = colorMapping.getColorData(i, year - Data.startYear);
			if (b == -1)
				continue;
			double s = 0.5;
			double temMax = colorMapping.getMaxInitData();
			double temMin = colorMapping.getMinInitData();
			int provinceIndex = pName.GetProvinceIndex(Data.area[i].province);
			int yearIndex = year - Data.startYear;
			double temData = colorMapping.getCurrentInitData(provinceIndex,
					yearIndex);
			s = (temData - temMin) / (temMax - temMin);
			s = s * 0.4 + 0.3;
			String provinceChineseName = Data.area[i].province;
			String provinceEnglishName = pName
					.GetProvinceEnglishName(provinceChineseName);

			// hsb根据映射得到的H来填充颜色
			int hh = (int) b;
			hh=120-hh;//(由colorstyle)中的映射区间决定
			// System.out.println("Point:"+"---"+hh);

			pg.fill(hh, 100, 100);

			// int[] col = HSLAndRGB.HSL2RGB(b, 1, 0.5);
			// fill(col[0], col[1], col[2]);

			// hsb根据映射得到的S来填充颜色
			/*
			 * int ss = (int)b; if(ss < 100) { pg.fill(230, ss, 100); } else {
			 * pg.fill(360, ss-100, 100); }
			 */

			provinceMap = chinaMap.getChild(provinceEnglishName);
			if (provinceMap != null) {
				provinceMap.draw(pg);
			}
		}
		pgMap.endDraw();
	}

	public void highlightArea() {
		if (cityCompare == null) {
			return;
		}

		provinceMap = chinaMap.getChild(cityCompare);
		fill(129, 14, 142); // 选中颜色
		RG.shape(provinceMap);

		int i, j;
		for (i = 0; i < 34; i++) {
			if (cityCompare == cityName[i])
				break;
		}

		boolean dg1 = true, dg2 = true;
		for (j = 0; j < 20; j++) {
			if (Degree == 1) {
				if (AdjacencyCity1[i][j] >= 0) {
					provinceMap = chinaMap
							.getChild(cityName[AdjacencyCity1[i][j]]);
					fill(169, 88, 167); // 一度省份颜色
					RG.shape(provinceMap);
				} else
					break;
			} else if (Degree == 2) {
				if (dg1) {
					if (AdjacencyCity1[i][j] >= 0) {
						provinceMap = chinaMap
								.getChild(cityName[AdjacencyCity1[i][j]]);
						fill(169, 88, 167); // 一度省份颜色
						RG.shape(provinceMap);
					} else {
						dg1 = false;
					}
				}
				if (dg2) {
					if (AdjacencyCity2[i][j] >= 0) {
						provinceMap = chinaMap
								.getChild(cityName[AdjacencyCity2[i][j]]);
						fill(202, 157, 244); // 二度省份颜色
						RG.shape(provinceMap);
					} else {
						dg2 = false;
					}
				}
			} else
				break;
		}
	}

	public void changeDegree(int x) {
		Degree = x;
		GlobalVariables.selectCity = null;
		GlobalVariables.selectCityList1.clear();
		GlobalVariables.selectCityList2.clear();

		GlobalVariables.selectCity = clickedArea;
		if (Degree == 1) {
			for (int j = 0; j < 10; j++) {
				if (AdjacencyCity1[clickedAreaIndex][j] >= 0) {
					GlobalVariables.selectCityList1
							.add(cityName[AdjacencyCity1[clickedAreaIndex][j]]);
				} else
					break;
			}
		} else if (Degree == 2) {
			for (int j = 0; j < 10; j++) {
				if (AdjacencyCity1[clickedAreaIndex][j] >= 0) {
					GlobalVariables.selectCityList1
							.add(cityName[AdjacencyCity1[clickedAreaIndex][j]]);
				} else
					break;
			}
			for (int j = 0; j < 20; j++) {
				if (AdjacencyCity2[clickedAreaIndex][j] >= 0) {
					GlobalVariables.selectCityList2
							.add(cityName[AdjacencyCity2[clickedAreaIndex][j]]);
				} else
					break;
			}
		}
		// fMain.drawMDS();
	}

	public void mouseMoved() {
		RPoint p = new RPoint(mouseX, mouseY);
		int i;
		for (i = 0; i < chinaMap.countChildren(); i++) {
			if (chinaMap.children[i].contains(p)) {
				overArea = cityName[i];
				break;
			}
		}
		if (i == chinaMap.countChildren()) {
			overArea = "";
		}
	}

	public void mousePressed() {
		// 选中城市后通过全局变量GlobalVariables.selectCityList传送到MDS窗口
		GlobalVariables.selectCity = null;
		GlobalVariables.selectCityList1.clear();
		GlobalVariables.selectCityList2.clear();
		if (mouseButton == RIGHT) {
			clickedArea = null;
			cityCompare = null;
			// 在选择展示一个省份中的所有年份MDS时,刷新重绘
			if (GlobalVariables.provOrYear == 1) {
				fMain.drawMDS();
			}
			return;
		}
		RPoint p = new RPoint(mouseX, mouseY);
		for (int i = 0; i < chinaMap.countChildren(); i++) {
			if (chinaMap.children[i].contains(p)) {
				if (i == cityName.length - 1) {
					System.out.println(cityName.length - 1 + " 台湾的点击不给予反应");
					continue;
				}
				clickedArea = cityName[i];
				cityCompare = cityName[i];
				clickedAreaIndex = i;

				GlobalVariables.selectCity = cityName[i];

				if (Degree == 1) {
					for (int j = 0; j < 10; j++) {
						if (AdjacencyCity1[i][j] >= 0) {
							GlobalVariables.selectCityList1
									.add(cityName[AdjacencyCity1[i][j]]);
						} else
							break;
					}
				} else if (Degree == 2) {
					for (int j = 0; j < 10; j++) {
						if (AdjacencyCity1[i][j] >= 0) {
							GlobalVariables.selectCityList1
									.add(cityName[AdjacencyCity1[i][j]]);
						} else
							break;
					}
					for (int j = 0; j < 20; j++) {
						if (AdjacencyCity2[i][j] >= 0) {
							GlobalVariables.selectCityList2
									.add(cityName[AdjacencyCity2[i][j]]);
						} else
							break;
					}
				}
				if (GlobalVariables.provOrYear == 1) {
					fMain.drawMDS();
				}
			}
		}
	}

	public void showProvName() {
		if (overArea != "") {
			String provName = "";
			int len = 15;
			if (Language.languageType == 0) {
				provName = pName.GetProvinceChineseName(overArea);
				len = 15;
			} else if (Language.languageType == 1) {
				provName = overArea.replace("_", "").replace("1", "")
						.replace("3", "");
				len = 8;
			}

			if (provName != null) {
				stroke(0);
				strokeWeight(1);
				fill(255);
				rect(mouseX + 20, mouseY + 20, provName.length() * len, 20);
				fill(0);
				text(provName, mouseX + 25, mouseY + 35);
			}
		}
	}

	public void drawColorBar(PGraphics pg) {
		pg.beginDraw();
		pg.colorMode(HSB, 360, 100, 100);
		int weight = 2;
		pg.strokeWeight(1);
		for (int i = 0; i < 120; i++) {
			pg.stroke(i, 100, 100);
			for (int j = 0; j < weight; j++) {
				pg.line(60, i * weight- j, 100, i * weight - j);
			}
		}

		pg.fill(100, 10, 50);
		pg.text("9,9000", 10, 10);
		pg.text("         0", 10, 120 * weight);
		pg.text("PCGDP", 60, 260);

		pg.stroke(100, 10, 50);
		// pg.line(60, 0, 60, 119 * weight); // 竖线
		pg.strokeWeight(2);
		pg.line(59, 1, 55, 1); // 小横线 （高）
		pg.line(59, 118 * weight + 1, 55, 118 * weight + 1); // 小横线 （低）
		pg.endDraw();
	}
}
