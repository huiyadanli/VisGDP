package cn.edu.zufe.mds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import processing.core.PApplet;
import processing.core.PGraphics;

public class PAppletStoryFlow extends PApplet {
	ProvinceName pName = new ProvinceName();
	public static int yearLen = 40;
	public static int h = 400;
	PGraphics pgStoryFlow;
	public static StoryYear[] allYear = new StoryYear[Data.yearCount];
	boolean isDrawPG = false;
	public boolean drawNone = false; // 不画任何东西
	StoryLine mouseOn = null;
	public static StoryCluster pressedIn = null;

	public void setup() {
		size(yearLen * Data.yearCount, h);
		pgStoryFlow = createGraphics(yearLen * Data.yearCount, h);
		pgStoryFlow.beginDraw();
		pgStoryFlow.colorMode(HSB, 360, 100, 100); // 色彩模式HSB
		pgStoryFlow.endDraw();
	}

	public void draw() {
		if (drawNone) {
			background(255);
			return;
		}
		if (!isDrawPG) {
			background(255);
			image(pgStoryFlow, 0, 0);
			isDrawPG = true;
		}
		if (isDrawPG && allYear[0] != null) {
			background(255);
			image(pgStoryFlow, 0, 0);
			highlightProv();
			allYear[GlobalVariables.year - Data.startYear].highligh();
			if (mouseOn != null) {
				mouseOn.showProvName();
			}
			if (pressedIn != null) {
				pressedIn.highlight();
			}
		}
	}

	public void mouseMoved() {
		// 显示省份信息
		if (allYear[0] == null) {
			return;
		}
		mouseOn = null;
		int index = (int) (mouseX / (StoryParm.connectingLineLength + StoryParm.lineLength));
		for (StoryLine line : allYear[index].lineInYear) {
			if (mouseY >= line.sPoint.y && mouseY <= line.sPoint.y + 4) {
				mouseOn = line;
				break;
			}
		}
	}

	public void mousePressed() {
		GlobalVariables.mdsOrStoryFlowClick = true;
		PAppletMDS.mouseIn = -1;
		if (allYear[0] == null) {
			return;
		}
		GlobalVariables.selectCityList3.clear();
		pressedIn = null;
		int index = (int) (mouseX / (StoryParm.connectingLineLength + StoryParm.lineLength));
		for (StoryCluster c : allYear[index].sClusters) {
			for (StoryLine line : c.lineInCluster) {
				if (mouseY >= line.sPoint.y && mouseY <= line.sPoint.y + 4) {
					pressedIn = c;
					// 选中类的省份的列表
					for (StoryLine l : c.lineInCluster) {
						GlobalVariables.selectCityList3.add(l.provIndex);
					}
					break;
				}
			}
		}
	}

	public void drawPG() {
		isDrawPG = false;
		pgStoryFlow.clear();
		pgStoryFlow.beginDraw();
		pgStoryFlow.background(0, 0, 100);
		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i] = new StoryYear(GlobalVariables.clusters[i], i
					+ Data.startYear);
			if (i > 0 && allYear[i - 1] != null) {
				allYear[i].prevYear = allYear[i - 1];
				allYear[i - 1].nextYear = allYear[i];
			}
		}
		// 着色
		// 计算信息熵
		float[] eMaxArr = new float[Data.yearCount];
		float[] eMinArr = new float[Data.yearCount];
		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i].coloring();
			eMaxArr[i] = allYear[i].entropyMax;
			eMinArr[i] = allYear[i].entropyMin;
		}
		// 计算最大最小值，归一化
		float eMax = Common.getMaxValue(eMaxArr);
		float eMin = Common.getMaxValue(eMinArr);
		// System.out.println(eMax + " " + eMin);
		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i].normalizationE(eMax, eMin);
		}

		// 画出
		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i].draw(pgStoryFlow);
			allYear[i].connect(pgStoryFlow);
		}
		pgStoryFlow.endDraw();
	}

	public void highlightProv() {
		// 高亮选中省份
		int index;
		if (GlobalVariables.selectCity != null) {
			index = pName.GetProvinceIndex(GlobalVariables.selectCity) - 1;
			for (StoryLine line : allYear[0].lineInYear) {
				if (line.provIndex == index) {
					StoryLine l = line;
					while (l != null) {
						l.highlight(0);
						l = l.next;
					}
					break;
				}
			}
			for (String provName : GlobalVariables.selectCityList1) {
				index = pName.GetProvinceIndex(provName) - 1;
				for (StoryLine line : allYear[0].lineInYear) {
					if (line.provIndex == index) {
						StoryLine l = line;
						while (l.next != null) {
							l.highlight(1);
							l = l.next;
						}
						break;
					}
				}
			}
			for (String provName : GlobalVariables.selectCityList2) {
				index = pName.GetProvinceIndex(provName) - 1;
				for (StoryLine line : allYear[0].lineInYear) {
					if (line.provIndex == index) {
						StoryLine l = line;
						while (l.next != null) {
							l.highlight(2);
							l = l.next;
						}
						break;
					}
				}
			}
		}
	}

	public static class StoryParm {
		public static float lineLength = 10;
		public static float connectingLineLength = 30;
		public static float lineWeight = 4;
		public static float connectingLineWeight = 4;
	}

	public class StoryPoint {
		public float x;
		public float y;

		public StoryPoint(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	public class StoryLine {

		public StoryPoint sPoint, ePoint;
		public String provName;
		public int provIndex;
		public StoryCluster clusterIndexPrevYear = null,
				clusterIndexNextYear = null;
		public boolean inBeFound = false, outBeFound = false;
		public float colorNIn = 0, colorDIn = 0, entropyIn = 0;
		public float hIn = 0, sIn = 0, bIn = 0;
		public float colorNOut = 0, colorDOut = 0, entropyOut = 0;
		public float hOut = 0, sOut = 0, bOut = 0;
		public StoryLine prev, next;

		public StoryLine(StoryPoint sPoint, int provIndex) {
			this.sPoint = new StoryPoint(sPoint.x, sPoint.y);
			this.ePoint = new StoryPoint(sPoint.x + StoryParm.lineLength,
					sPoint.y);
			this.provIndex = provIndex;
			if (provIndex < 31) {
				if (Language.languageType == 0) {
					provName = pName.provinceChineseName[provIndex + 1];
				} else if (Language.languageType == 1) {
					provName = pName.provinceEnglishName[provIndex + 1]
							.replace("_", "").replace("1", "").replace("3", "");
				}
			} else {
				this.provName = "未知";
				System.out.println(provIndex);
			}
		}

		public void draw(PGraphics pg) {
			pg.strokeWeight(StoryParm.lineWeight);

//			if (entropyIn < 0.5) {
//				hIn = 230;
//				sIn = (float) ((0.5 - entropyIn) * 60) + 20;
//				bIn = 100;
//			} else {
//				hIn = 360;
//				sIn = (float) ((entropyIn - 0.5) * 60) + 20;
//				bIn = 100;
//			}
//
//			if (entropyOut < 0.5) {
//				hOut = 230;
//				sOut = (float) ((0.5 - entropyOut) * 60) + 20;
//				bOut = 100;
//			} else {
//				hOut = 360;
//				sOut = (float) ((entropyOut - 0.5) * 60) + 20;
//				bOut = 100;
//			}

			 //250~340
			 hIn = 250 - entropyIn * 240;
			 sIn = 50;
			 bIn = 100;
			
			 hOut = 250 - entropyOut * 240;
			 sOut = 50;
			 bOut = 100;
			 
//			 hIn = entropyIn * 360;
//			 sIn = 100;
//			 bIn = 100;
//			
//			 hOut = entropyOut * 360;
//			 sOut = 100;
//			 bOut = 100;

			pg.stroke(hIn, sIn, bIn);
			// System.out.println("entropy = " + entropy);
			pg.line(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
			pg.stroke(0, 0, 0);
			pg.strokeWeight(1);
		}

		public void highlightNoCurve() {
			strokeWeight(4);
			stroke(129, 14, 142);
			line(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
			strokeWeight(1);
		}

		public void highlight(int degree) {
			strokeWeight(3);
			if (degree == 0) {
				stroke(129, 14, 142);
			} else if (degree == 1) {
				stroke(169, 88, 167);
			} else if (degree == 2) {
				stroke(202, 157, 244);
			}
			line(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
			if (prev != null) {
				fill(0, 0, 100, 0);
				bezier(sPoint.x, sPoint.y, sPoint.x
						- StoryParm.connectingLineLength / 2, sPoint.y,
						prev.ePoint.x + StoryParm.connectingLineLength / 2,
						prev.ePoint.y, prev.ePoint.x, prev.ePoint.y);
			}
			stroke(0);
			strokeWeight(1);
		}

		public void showProvName() {
			strokeWeight(StoryParm.lineWeight);
			stroke(129, 14, 142);
			line(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
			strokeWeight(1);
			fill(255);
			int len = 15;
			if (Language.languageType == 0) {
				len = 15;
			} else if (Language.languageType == 1) {
				len = 8;
			}

			int offsetX = 20, offsetY = 20;
			if (mouseX + 20 + provName.length() * len > yearLen
					* Data.yearCount) {
				offsetX = offsetX - 40 - provName.length() * len;
			}
			if (mouseY + 40 > h) {
				offsetY = -20;
			}

			rect(mouseX + offsetX, mouseY + offsetY, provName.length() * len,
					20);
			fill(0);
			text(provName, mouseX + offsetX + 5, mouseY + offsetY + 15);
		}

	}

	public class StoryCluster {
		public ArrayList<StoryLine> lineInCluster = new ArrayList<StoryLine>();
		public int beFound = 0;
		public int index = -1;
		public float entropyIn = 0, entropyOut = 0;

		public StoryCluster(int index) {
			this.index = index;
		}

		public boolean contain(StoryLine l) {
			for (StoryLine line : lineInCluster) {
				if (l.provIndex == line.provIndex) {
					return true;
				}
			}
			return false;
		}

		public void highlight() {
			for (StoryLine line : lineInCluster) {
				line.highlightNoCurve();
			}
			System.out.println(entropyIn + " " + entropyOut);
		}
	}

	public class StoryYear {

		private float clusterInterval = StoryParm.lineWeight;
		private float notClusterInterval = 8;
		public int year;
		public int yearIndex;
		public ArrayList<StoryLine> lineInYear = new ArrayList<StoryLine>();
		public ArrayList<StoryCluster> sClusters = new ArrayList<StoryCluster>();
		public float entropyMax = 0, entropyMin = 1;

		public StoryYear prevYear, nextYear;

		// private int searchTime = 1;

		// ArrayList<Integer> newCInThatYear = new ArrayList<Integer>();
		// ArrayList<Integer> newCInNextYear = new ArrayList<Integer>();

		public StoryYear(ArrayList<ArrayList<Integer>> clusters, int year) {

			this.year = year;
			this.yearIndex = year - Data.startYear;
			// 把输入的聚类链表排序
			if (GlobalVariables.sortMethod == 0) {
				Collections.sort(clusters, new SortBySize());
			} else if (GlobalVariables.sortMethod == 1) {
				Collections.sort(clusters,
						new SortByPerCapitaGDP(yearIndex, 12));
			} else if (GlobalVariables.sortMethod == 2) {
				Collections
						.sort(clusters, new SortByPerCapitaGDP(yearIndex, 0));
			}
			// 计算线的位置并实例化这些线
			StoryPoint p = new StoryPoint(0, 0);
			p.x = yearIndex
					* (StoryParm.lineLength + StoryParm.connectingLineLength)
					+ StoryParm.connectingLineLength / 2;
			p.y = 25; // 起始的高度
			for (int i = 0; i < clusters.size(); i++) {
				if (GlobalVariables.sortMethod == 0) {
					Collections.sort(clusters.get(i));
				} else if (GlobalVariables.sortMethod == 1) {
					Collections.sort(clusters.get(i), new SortByPerCapitaGDP2(
							yearIndex, 12));
				} else if (GlobalVariables.sortMethod == 2) {
					Collections.sort(clusters.get(i), new SortByPerCapitaGDP2(
							yearIndex, 0));
				}

				sClusters.add(new StoryCluster(yearIndex * 100 + i));
				for (int j = 0; j < clusters.get(i).size(); j++) {
					p.y += clusterInterval;
					StoryLine tmpLine = new StoryLine(p, clusters.get(i).get(j));
					lineInYear.add(tmpLine);
					sClusters.get(i).lineInCluster.add(tmpLine);
					// System.out.println(p.y +"  " + clusters.get(i).get(j));
				}
				// p.y += (h - clusterInterval * Data.provinceCount) /
				// clusters.size();
				p.y += notClusterInterval;
			}
		}

		public void draw(PGraphics pg) {
			/*
			 * for(StoryLine line:lineInYear) { line.draw(pg); }
			 */

			// 画出年份的轴
			float axisX = (StoryParm.connectingLineLength + StoryParm.lineLength)
					/ 2
					+ yearIndex
					* (StoryParm.lineLength + StoryParm.connectingLineLength);
			pg.stroke(100, 10, 50);
			pg.strokeWeight(1);
			pg.line(axisX, 20, axisX, h);
			pg.fill(100, 10, 50);
			pg.text(this.year, axisX - StoryParm.lineLength, 15);

			for (StoryCluster c : sClusters) {
				// System.out.println("------------");
				for (StoryLine line : c.lineInCluster) {
					line.entropyIn = c.entropyIn;
					line.entropyOut = c.entropyOut;
					line.draw(pg);
				}
				// System.out.println("------------");
			}

		}

		public void connect(PGraphics pg) {
			if (prevYear == null) {
				return;
			}
			for (StoryLine nowLine : lineInYear) {
				for (StoryLine prevLine : prevYear.lineInYear) {
					if (nowLine.provIndex == prevLine.provIndex) {
						nowLine.prev = prevLine;
						prevLine.next = nowLine;
						pg.strokeWeight(StoryParm.connectingLineWeight);
						pg.stroke(prevLine.hOut, prevLine.sOut, prevLine.bOut);
						pg.fill(0, 0, 100, 0); // 透明色，曲线需要
						pg.bezier(nowLine.sPoint.x, nowLine.sPoint.y,
								nowLine.sPoint.x
										- StoryParm.connectingLineLength / 2,
								nowLine.sPoint.y, prevLine.ePoint.x
										+ StoryParm.connectingLineLength / 2,
								prevLine.ePoint.y, prevLine.ePoint.x,
								prevLine.ePoint.y);
						// pg.line(nowLine.sPoint.x, nowLine.sPoint.y,
						// nowLine.sPoint.x + StoryParm.lineLength,
						// nowLine.sPoint.y);
						break;
					}
				}
			}
			pg.stroke(0, 0, 0);
		}

		public void highligh() {
			stroke(30, 88, 227, 50);
			fill(30, 88, 227, 50);
			float len = StoryParm.lineLength + StoryParm.connectingLineLength;
			rect(len * yearIndex,
					lineInYear.get(0).sPoint.y,
					len,
					lineInYear.get(lineInYear.size() - 1).ePoint.y
							- lineInYear.get(0).sPoint.y);
		}

		public StoryCluster getLineInWhatCluster(StoryLine l) {
			for (int i = 0; i < sClusters.size(); i++) {
				if (sClusters.get(i).contain(l)) {
					return sClusters.get(i);
				}
			}
			return null;
		}

		// 计算本年与前一年，和本年与后一年的信息熵
		public void coloring() {
			// 计算分母
			for (StoryCluster c : sClusters) {
				for (StoryLine line : c.lineInCluster) {
					line.colorDIn = c.lineInCluster.size();
					line.colorDOut = c.lineInCluster.size();
					if (prevYear != null) {
						line.clusterIndexPrevYear = prevYear
								.getLineInWhatCluster(line);
					}
					if (nextYear != null) {
						line.clusterIndexNextYear = nextYear
								.getLineInWhatCluster(line);
					}
				}
			}
			// 计算分子
			for (StoryCluster c : sClusters) {
				for (StoryLine l1 : c.lineInCluster) {
					if (!l1.inBeFound) {
						for (StoryLine l2 : c.lineInCluster) {
							if (l1.clusterIndexPrevYear == l2.clusterIndexPrevYear) {
								l1.colorNIn += 1;
								l2.inBeFound = true;
							}
						}
					}
					if (!l1.outBeFound) {
						for (StoryLine l2 : c.lineInCluster) {

							if (l1.clusterIndexNextYear == l2.clusterIndexNextYear) {
								l1.colorNOut += 1;
								l2.outBeFound = true;
							}
						}
					}
				}
			}

			entropyMax = 0;
			entropyMin = 1;
			// 计算类的颜色 -Σ pi*log pi
			for (StoryCluster c : sClusters) {
				float entropy = 0; // 信息熵
				for (StoryLine line : c.lineInCluster) {
					if (line.colorNIn != 0) {
						float pi = line.colorNIn / line.colorDIn;
						// System.out.println(c.index + " pi: " + pi);
						entropy = entropy - pi
								* (float) (Math.log(pi) / Math.log(2));
					}
				}
				//entropy *= c.lineInCluster.size();
				if (entropyMax < entropy) {
					entropyMax = entropy;
				}
				if (entropyMin > entropy) {
					entropyMin = entropy;
				}
				c.entropyIn = entropy;
				// System.out.println(c.index + " - " + entropy);
			}

			// for (StoryCluster c : sClusters) {
			// if (entropyMax == entropyMin) {
			// c.entropyIn = 0;
			// } else {
			// c.entropyIn = (c.entropyIn - entropyMin)
			// / (entropyMax - entropyMin);
			// }
			// }

			// entropyMax = 0;
			// entropyMin = 1;
			for (StoryCluster c : sClusters) {
				float entropy = 0; // 信息熵
				for (StoryLine line : c.lineInCluster) {
					if (line.colorNOut != 0) {
						float pi = line.colorNOut / line.colorDOut;
						// System.out.println(c.index + " pi: " + pi);
						entropy = entropy - pi
								* (float) (Math.log(pi) / Math.log(2));
					}
				}
				//entropy *= c.lineInCluster.size();
				if (entropyMax < entropy) {
					entropyMax = entropy;
				}
				if (entropyMin > entropy) {
					entropyMin = entropy;
				}
				c.entropyOut = entropy;
				// System.out.println(c.index + " - " + entropy);
			}
			// System.out.println(entropyMax + " | " + entropyMin);
			// 归一化
			// for (StoryCluster c : sClusters) {
			// if (entropyMax == entropyMin) {
			// c.entropyOut = 0;
			// } else {
			// c.entropyOut = (c.entropyOut - entropyMin)
			// / (entropyMax - entropyMin);
			// }
			// }
		}

		public void normalizationE(float eMax, float eMin) {
			// 归一化
			for (StoryCluster c : sClusters) {
				if (eMax == eMin) {
					c.entropyIn = 0;
				} else {
					c.entropyIn = (c.entropyIn - eMin) / (eMax - eMin);
				}
			}
			for (StoryCluster c : sClusters) {
				if (eMax == eMin) {
					c.entropyOut = 0;
				} else {
					c.entropyOut = (c.entropyOut - eMin) / (eMax - eMin);
				}
			}
		}

	}

	// 按ArrayList中的元素数量多少来排序(Size),数量相同时按省份索引值之和排序 都是大的在上
	public class SortBySize implements Comparator<ArrayList<Integer>> {
		@Override
		public int compare(ArrayList<Integer> a1, ArrayList<Integer> a2) {
			if (a1.size() > a2.size()) {
				return -1;
			} else if (a1.size() == a2.size()) {
				int s1 = 0, s2 = 0;
				for (int tmp : a1) {
					s1 += tmp;
				}
				for (int tmp : a2) {
					s2 += tmp;
				}
				if (s1 > s2) {
					return -1;
				}
			}
			return 1;
		}
	}

	public class SortByPerCapitaGDP implements Comparator<ArrayList<Integer>> {

		int yearIndex;
		int industryIndex; // 12人均GDP 0总GDP

		public SortByPerCapitaGDP(int yearIndex, int type) {
			this.yearIndex = yearIndex;
			this.industryIndex = type;
		}

		@Override
		public int compare(ArrayList<Integer> a1, ArrayList<Integer> a2) {
			double a1GDP = 0, a2GDP = 0;
			int a1ProvNum = 0, a2ProvNum = 0;
			for (int provIndex : a1) {
				a1GDP += Data.area[provIndex + 1].industry[industryIndex][yearIndex];
				a1ProvNum++;
			}
			for (int provIndex : a2) {
				a2GDP += Data.area[provIndex + 1].industry[industryIndex][yearIndex];
				a2ProvNum++;
			}

			if (a1GDP / a1ProvNum > a2GDP / a2ProvNum) {
				return -1;
			}
			return 1;
		}
	}

	public class SortByPerCapitaGDP2 implements Comparator<Integer> {

		int yearIndex;
		int industryIndex; // 12人均GDP 0总GDP

		public SortByPerCapitaGDP2(int yearIndex, int type) {
			this.yearIndex = yearIndex;
			this.industryIndex = type;
		}

		@Override
		public int compare(Integer i1, Integer i2) {
			if (Data.area[i1 + 1].industry[industryIndex][yearIndex] > Data.area[i2 + 1].industry[industryIndex][yearIndex]) {
				return -1;
			}
			return 1;
		}
	}
}
