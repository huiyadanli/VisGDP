package cn.edu.zufe.mds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.edu.zufe.mds.PAppletStoryFlow.StoryLine;
import processing.core.PApplet;
import processing.core.PGraphics;

public class PAppletRiver extends PApplet {
	ProvinceName pName = new ProvinceName();
	public static int yearLen = 40;
	public static int h = 400;
	PGraphics pgRiver, pgTop;
	public static RiverYear[] allYear = new RiverYear[Data.yearCount];
	boolean isDrawPG = false;
	public boolean drawNone = false; // 不画任何东西
	public static RiverCluster pressedIn = null;

	public void setup() {
		size(yearLen * Data.yearCount, h);
		pgRiver = createGraphics(yearLen * Data.yearCount, h);
		pgRiver.beginDraw();
		pgRiver.colorMode(HSB, 360, 100, 100); // 色彩模式HSB
		pgRiver.endDraw();
		// 高层 PGraphics
		pgTop = createGraphics(yearLen * Data.yearCount, h);
		pgTop.beginDraw();
		pgTop.colorMode(HSB, 360, 100, 100); // 色彩模式HSB
		pgTop.background(0, 0, 0, 0); // 透明
		pgTop.endDraw();
	}

	public void draw() {
		if (drawNone) {
			background(255);
			return;
		}
		if (!isDrawPG) {
			background(255);
			isDrawPG = true;
		}
		if (isDrawPG && allYear[0] != null) {
			background(255);
			image(pgRiver, 0, 0);
			allYear[GlobalVariables.year - Data.startYear].highligh();
		}
		if (pressedIn != null) {
			pgTop.beginDraw();
			pgTop.clear();
			for (int i = 0; i < Data.yearCount; i++) {
				allYear[i].connect(pgTop, pressedIn);
			}
			pgTop.endDraw();
			image(pgTop, 0, 0);
		}
	}

	public void drawPG() {
		isDrawPG = false;
		pgRiver.clear();
		pgRiver.beginDraw();
		pgRiver.background(0, 0, 100);
		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i] = new RiverYear(GlobalVariables.clusters[i], i
					+ Data.startYear);
			if (i > 0 && allYear[i - 1] != null) {
				allYear[i].prevYear = allYear[i - 1];
				allYear[i - 1].nextYear = allYear[i];
			}
		}

		// 生成聚类中的小类
		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i].setBundle();
		}

		// 着色
		// 计算信息熵
		float[] eMaxArr = new float[Data.yearCount];
		float[] eMinArr = new float[Data.yearCount];
		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i].computeE();
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
			allYear[i].coloring();
			allYear[i].draw(pgRiver);
			// allYear[i].connect(pgRiver);
		}
		pgRiver.endDraw();
	}

	public void mousePressed() {
		PAppletMDS.mouseIn = -1;
		if (allYear[0] == null) {
			return;
		}
		GlobalVariables.selectCityList3.clear();
		pressedIn = null;
		int index = (int) (mouseX / (Param.connectingLineLength + Param.lineLength));
		for (RiverCluster c : allYear[index].sClusters) {
//			ArrayList<RiverBundle> bs;
//			if (c.bundlePrev.size() > 0) {
//				bs = c.bundlePrev;
//			} else {
//				bs = c.bundleNext;
//			}
//
//			for (RiverBundle b : bs) {
//				if (b.PointDetect(mouseX, mouseY)) {
//					pressedIn = b;
//					// 选中类的省份的列表
//					for (RiverLine l : b.lineInBundle) {
//						GlobalVariables.selectCityList3.add(l.provIndex);
//					}
//					break;
//				}
//			}
			if(c.PointDetect(mouseX, mouseY)) {
				pressedIn = c;
				// 选中类的省份的列表
				for (RiverLine l : c.lineInCluster) {
					GlobalVariables.selectCityList3.add(l.provIndex);
				}
				break;
			}
		}
	}

	public static class Param {
		public static float lineLength = 10;
		public static float connectingLineLength = 30;
		public static float lineWeight = 4;
		public static float connectingLineWeight = 4;
	}

	public class RiverPoint {
		public float x;
		public float y;

		public RiverPoint(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	public class RiverLine {
		public RiverPoint sPoint, ePoint;
		public String provName;
		public int provIndex;
		public RiverCluster clusterIndexPrevYear = null,
				clusterIndexNextYear = null;
		public boolean inBeFound = false, outBeFound = false;
		public float colorNIn = 0, colorDIn = 0, entropyIn = 0;
		public float hIn = 0, sIn = 0, bIn = 0;
		public float colorNOut = 0, colorDOut = 0, entropyOut = 0;
		public float hOut = 0, sOut = 0, bOut = 0;
		public RiverLine prev, next;

		public RiverLine(RiverPoint sPoint, int provIndex) {
			this.sPoint = new RiverPoint(sPoint.x, sPoint.y);
			this.ePoint = new RiverPoint(sPoint.x + Param.lineLength, sPoint.y);
			this.provIndex = provIndex;
			// 省份编号转省份名
			if (provIndex < 31) {
				if (Language.languageType == 0) {
					provName = pName.provinceChineseName[provIndex + 1];
				} else if (Language.languageType == 1) {
					provName = pName.provinceEnglishName[provIndex + 1]
							.replace("_", "").replace("1", "").replace("3", "");
				}
			} else {
				// 此处可以删除，已经确定数据的准确性
				this.provName = "未知";
				System.out.println(provIndex);
			}
		}

		public void coloring() {
			// 250~340
			hIn = 250 - entropyIn * 240;
			sIn = 50;
			bIn = 100;

			hOut = 250 - entropyOut * 240;
			sOut = 50;
			bOut = 100;
		}

		public void draw(PGraphics pg) {
			pg.strokeWeight(Param.lineWeight);
			pg.stroke(hIn, sIn, bIn);
			pg.line(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
			pg.stroke(0, 0, 0);
			pg.strokeWeight(1);
		}
	}

	// 聚类中的一批类
	public class RiverBundle {
		public float x, y, h;
		public RiverBundle prev = null, next = null;
		public RiverCluster relevantCluster = null;
		public ArrayList<RiverLine> lineInBundle = new ArrayList<RiverLine>();

		public void setPos(float x, float y) {
			this.x = x;
			this.y = y;
			this.h = Param.lineWeight * lineInBundle.size();
		}

		public void draw(PGraphics pg) {
			pg.ellipseMode(CENTER);
			pg.fill(0, 100, 100);
			pg.strokeWeight(1);
			pg.stroke(0, 100, 100);
			pg.ellipse(x + Param.lineLength / 2, y + h / 2, h, h);
		}

		public void connect(PGraphics pg) {
			if (prev != null) {
				RiverLine l = relevantCluster.lineInCluster.get(0);
				pg.fill(l.hIn, l.sIn, l.bIn);
				pg.strokeWeight(1);
				pg.stroke(l.hIn, l.sIn, l.bIn);
				pg.beginShape();

				pg.vertex(x, y);
				pg.bezierVertex(x - Param.connectingLineLength / 2, y, prev.x
						+ Param.lineLength + Param.connectingLineLength / 2,
						prev.y, prev.x + Param.lineLength, prev.y);
				pg.vertex(prev.x + Param.lineLength, prev.y);

				pg.vertex(prev.x + Param.lineLength, prev.y + prev.h);
				pg.bezierVertex(prev.x + Param.lineLength
						+ Param.connectingLineLength / 3, prev.y + prev.h, x
						- Param.connectingLineLength / 3, y + h, x, y + h);
				pg.vertex(x, y + h);
				pg.endShape(CLOSE);

				// pg.bezier(x, y, x - Param.connectingLineLength / 3, y, prev.x
				// + Param.lineLength + Param.connectingLineLength / 3,
				// prev.y, prev.x + Param.lineLength, prev.y);
				//
				// pg.bezier(prev.x + Param.lineLength, prev.y + prev.h, prev.x
				// + Param.lineLength + Param.connectingLineLength / 3,
				// prev.y + prev.h, x - Param.connectingLineLength / 3, y
				// + h, x, y + h);

				// pg.line(x, y, prev.x+Param.lineLength, prev.y);
				// pg.line(x, y+h, prev.x+Param.lineLength, prev.y+prev.h);
			}
		}

		public boolean contain(RiverLine l) {
			for (RiverLine line : lineInBundle) {
				if (l.provIndex == line.provIndex) {
					return true;
				}
			}
			return false;
		}

		public boolean PointDetect(float mx, float my) {
			float cx = x + Param.lineLength / 2;
			float cy = y + h / 2;
			float d = (float) Math.sqrt((cx - mx) * (cx - mx) + (cy - my)
					* (cy - my));
			if (d < h / 2) {
				return true;
			} else {
				return false;
			}
		}

	}

	public class RiverCluster {
		public int index = -1;
		public float x, y, w, h;
		public ArrayList<RiverLine> lineInCluster = new ArrayList<RiverLine>();
		public ArrayList<RiverBundle> bundlePrev = new ArrayList<RiverBundle>();
		public ArrayList<RiverBundle> bundleNext = new ArrayList<RiverBundle>();

		public int beFound = 0;
		public float entropyIn = 0, entropyOut = 0;

		public RiverCluster(int index) {
			this.index = index;
		}

		public void coloring() {
			for (RiverLine line : lineInCluster) {
				line.entropyIn = entropyIn;
				line.entropyOut = entropyOut;
				line.coloring();
			}
		}

		public void setPos(float x, float y) {
			this.x = x;
			this.y = y;
			this.w = Param.lineLength;
			this.h = Param.lineWeight * lineInCluster.size();

			float by = y;
			for (RiverBundle b : bundlePrev) {
				b.setPos(x, by);
				by += b.h;
			}

			by = y;
			for (RiverBundle b : bundleNext) {
				b.setPos(x, by);
				by += b.h;
			}
		}

		public void drawRect(PGraphics pg) {
			pg.fill(233);
			pg.stroke(233);
			pg.rect(x, y, w, h);
		}

		public void drawEllipse(PGraphics pg) {
			RiverLine l = lineInCluster.get(0);

			pg.fill(l.hIn, l.sIn, l.bIn);
			pg.stroke(l.hIn, l.sIn, l.bIn);
			// pg.ellipseMode(CORNER);
			// pg.ellipse(x, y, w, h);
			pg.ellipseMode(CENTER);
			float ew = h; //椭圆宽度
			if(h > Param.lineLength / 2) {
				ew = h / 128 * (Param.lineLength + Param.connectingLineLength);
			}
			pg.ellipse(x + Param.lineLength / 2, y + h / 2, ew, h);

			// if (bundlePrev.size() > 0) {
			// for (RiverBundle b : bundlePrev) {
			// b.draw(pg);
			// }
			// } else {
			// for (RiverBundle b : bundleNext) {
			// b.draw(pg);
			// }
			// }
		}

		// 连接 Now -> Prev
		public void connect(PGraphics pg) {
			for (RiverBundle b : bundlePrev) {
				b.connect(pg);
			}
		}

		// 连接 Now -> Prev (带条件)
		public void connect(PGraphics pg, RiverBundle b1) {
			for (RiverLine l : b1.lineInBundle) {
				for (RiverBundle b2 : bundlePrev) {
					if (b2.contain(l)) {
						b2.connect(pg);
					}
				}
			}
		}
		
		// 连接 Now -> Prev (带条件2)
		public void connect(PGraphics pg, RiverCluster c) {
			for (RiverLine l : c.lineInCluster) {
				for (RiverBundle b2 : bundlePrev) {
					if (b2.contain(l)) {
						b2.connect(pg);
					}
				}
			}
		}

		public boolean contain(RiverLine l) {
			for (RiverLine line : lineInCluster) {
				if (l.provIndex == line.provIndex) {
					return true;
				}
			}
			return false;
		}

		public RiverBundle bundleContain(RiverCluster c, boolean prevOrNext) {
			ArrayList<RiverBundle> rb = bundlePrev;
			if (!prevOrNext) {
				rb = bundleNext;
			}
			for (RiverBundle b : rb) {
				if (b.relevantCluster == c) {
					return b;
				}
			}
			return null;
		}

		public RiverBundle bundleContain(RiverLine l, boolean prevOrNext) {
			ArrayList<RiverBundle> rb = bundlePrev;
			if (!prevOrNext) {
				rb = bundleNext;
			}
			for (RiverBundle b : rb) {
				if (b.contain(l)) {
					return b;
				}
			}
			return null;
		}

		public boolean PointDetect(float mx, float my) {
			float cx = x + Param.lineLength / 2;
			float cy = y + h / 2;
			float d = (float) Math.sqrt((cx - mx) * (cx - mx) + (cy - my)
					* (cy - my));
			if (d < h / 2) {
				return true;
			} else {
				return false;
			}
		}
	}

	public class RiverYear {

		public int year;
		public int yearIndex;
		public ArrayList<RiverLine> lineInYear = new ArrayList<RiverLine>();
		public ArrayList<RiverCluster> sClusters = new ArrayList<RiverCluster>();
		public float entropyMax = 0, entropyMin = 1;

		public RiverYear prevYear, nextYear;

		public RiverYear(ArrayList<ArrayList<Integer>> clusters, int year) {

			this.year = year;
			this.yearIndex = year - Data.startYear;

			// 添加省份和聚类
			// for (int i = 0; i < clusters.size(); i++) {
			// sClusters.add(new RiverCluster(yearIndex * 100 + i));
			// for (int j = 0; j < clusters.get(i).size(); j++) {
			// RiverLine tmpLine = new RiverLine(clusters.get(i).get(j));
			// lineInYear.add(tmpLine);
			// sClusters.get(i).lineInCluster.add(tmpLine);
			// }
			// }

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
			RiverPoint p = new RiverPoint(0, 0);
			p.x = yearIndex * (Param.lineLength + Param.connectingLineLength)
					+ Param.connectingLineLength / 2;
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

				RiverCluster c = new RiverCluster(yearIndex * 100 + i);
				sClusters.add(c);
				for (int j = 0; j < clusters.get(i).size(); j++) {
					p.y += Param.lineWeight; // 一个省份所占的高度
					RiverLine tmpLine = new RiverLine(p, clusters.get(i).get(j));
					lineInYear.add(tmpLine);
					c.lineInCluster.add(tmpLine);
				}
				p.y += 8; // 类间距
			}
		}

		public void draw(PGraphics pg) {
			// 画出年份的轴
			float axisX = (Param.connectingLineLength + Param.lineLength) / 2
					+ yearIndex
					* (Param.lineLength + Param.connectingLineLength);
			pg.stroke(100, 10, 50);
			pg.strokeWeight(1);
			pg.line(axisX, 20, axisX, h);
			pg.fill(100, 10, 50);
			pg.text(this.year, axisX - Param.lineLength, 15);

			float y = 25; // 起始高度
			for (RiverCluster c : sClusters) {
				c.setPos(axisX - Param.lineLength / 2, y);
				y += c.h + 8; // 类间距
			}

			for (RiverCluster c : sClusters) {
				c.drawEllipse(pg);
			}
		}

		public void connect(PGraphics pg) {
			if (prevYear == null) {
				return;
			}
			for (RiverCluster c : sClusters) {
				c.connect(pg);
			}
		}

		public void connect(PGraphics pg, RiverBundle b) {
			if (prevYear == null) {
				return;
			}
			for (RiverCluster c : sClusters) {
				c.connect(pg, b);
			}
		}
		
		public void connect(PGraphics pg, RiverCluster c1) {
			if (prevYear == null) {
				return;
			}
			for (RiverCluster c2 : sClusters) {
				c2.connect(pg, c1);
			}
		}

		public RiverCluster getLineInWhatCluster(RiverLine l) {
			for (int i = 0; i < sClusters.size(); i++) {
				if (sClusters.get(i).contain(l)) {
					return sClusters.get(i);
				}
			}
			return null;
		}

		public void setBundle() {
			for (RiverCluster c : sClusters) {
				for (RiverLine line : c.lineInCluster) {
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

			// bundle 新分类
			for (RiverCluster c : sClusters) {
				for (RiverLine line : c.lineInCluster) {
					if (prevYear != null) {
						RiverBundle b = c.bundleContain(
								line.clusterIndexPrevYear, true);
						if (b == null) {
							RiverBundle bTmp = new RiverBundle();
							c.bundlePrev.add(bTmp);
							bTmp.relevantCluster = line.clusterIndexPrevYear;
							bTmp.lineInBundle.add(line);
						} else {
							b.lineInBundle.add(line);
						}
					}

					if (nextYear != null) {
						RiverBundle b = c.bundleContain(
								line.clusterIndexNextYear, false);
						if (b == null) {
							RiverBundle bTmp = new RiverBundle();
							c.bundleNext.add(bTmp);
							bTmp.relevantCluster = line.clusterIndexNextYear;
							bTmp.lineInBundle.add(line);
						} else {
							b.lineInBundle.add(line);
						}
					}
				}
			}
			// bundle 前后年关联
			for (RiverCluster c : sClusters) {
				if (prevYear != null) {
					for (RiverBundle b : c.bundlePrev) {
						for (RiverCluster cPrev : prevYear.sClusters) {
							RiverBundle bPrev = cPrev.bundleContain(
									b.lineInBundle.get(0), false);
							if (bPrev != null) {
								b.prev = bPrev;
								break;
							}
						}
					}
				}

				if (nextYear != null) {
					for (RiverBundle b : c.bundleNext) {
						for (RiverCluster cNext : nextYear.sClusters) {
							RiverBundle bNext = cNext.bundleContain(
									b.lineInBundle.get(0), true);
							if (bNext != null) {
								b.next = bNext;
								break;
							}
						}
					}
				}
			}
			// --
		}

		public void highligh() {
			stroke(30, 88, 227, 50);
			fill(30, 88, 227, 50);
			float len = Param.lineLength + Param.connectingLineLength;
			rect(len * yearIndex, 0, len,
					lineInYear.get(lineInYear.size() - 1).ePoint.y + 25);
		}

		// 计算本年与前一年，和本年与后一年的信息熵
		public void computeE() {
			// 计算分母
			for (RiverCluster c : sClusters) {
				for (RiverLine line : c.lineInCluster) {
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
			for (RiverCluster c : sClusters) {
				for (RiverLine l1 : c.lineInCluster) {
					if (!l1.inBeFound) {
						for (RiverLine l2 : c.lineInCluster) {
							if (l1.clusterIndexPrevYear == l2.clusterIndexPrevYear) {
								l1.colorNIn += 1;
								l2.inBeFound = true;
							}
						}
					}
					if (!l1.outBeFound) {
						for (RiverLine l2 : c.lineInCluster) {

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
			for (RiverCluster c : sClusters) {
				float entropy = 0; // 信息熵
				for (RiverLine line : c.lineInCluster) {
					if (line.colorNIn != 0) {
						float pi = line.colorNIn / line.colorDIn;
						// System.out.println(c.index + " pi: " + pi);
						entropy = entropy - pi
								* (float) (Math.log(pi) / Math.log(2));
					}
				}
				// entropy *= c.lineInCluster.size();
				if (entropyMax < entropy) {
					entropyMax = entropy;
				}
				if (entropyMin > entropy) {
					entropyMin = entropy;
				}
				c.entropyIn = entropy;
				// System.out.println(c.index + " - " + entropy);
			}

			// for (RiverCluster c : sClusters) {
			// if (entropyMax == entropyMin) {
			// c.entropyIn = 0;
			// } else {
			// c.entropyIn = (c.entropyIn - entropyMin)
			// / (entropyMax - entropyMin);
			// }
			// }

			// entropyMax = 0;
			// entropyMin = 1;
			for (RiverCluster c : sClusters) {
				float entropy = 0; // 信息熵
				for (RiverLine line : c.lineInCluster) {
					if (line.colorNOut != 0) {
						float pi = line.colorNOut / line.colorDOut;
						// System.out.println(c.index + " pi: " + pi);
						entropy = entropy - pi
								* (float) (Math.log(pi) / Math.log(2));
					}
				}
				// entropy *= c.lineInCluster.size();
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
			// for (RiverCluster c : sClusters) {
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
			for (RiverCluster c : sClusters) {
				if (eMax == eMin) {
					c.entropyIn = 0;
				} else {
					c.entropyIn = (c.entropyIn - eMin) / (eMax - eMin);
				}
			}
			for (RiverCluster c : sClusters) {
				if (eMax == eMin) {
					c.entropyOut = 0;
				} else {
					c.entropyOut = (c.entropyOut - eMin) / (eMax - eMin);
				}
			}
		}

		public void coloring() {
			for (RiverCluster c : sClusters) {
				c.coloring();
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
