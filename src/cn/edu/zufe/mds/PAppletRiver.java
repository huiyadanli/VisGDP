package cn.edu.zufe.mds;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PGraphics;

public class PAppletRiver extends PApplet {
	ProvinceName pName = new ProvinceName();
	public static int yearLen = 40;
	public static int h = 400;
	PGraphics pgRiver;
	public static RiverYear[] allYear = new RiverYear[Data.yearCount];
	boolean isDrawPG = false;
	public boolean drawNone = false; // 不画任何东西

	public void setup() {
		size(yearLen * Data.yearCount, h);
		pgRiver = createGraphics(yearLen * Data.yearCount, h);
		pgRiver.beginDraw();
		pgRiver.colorMode(HSB, 360, 100, 100); // 色彩模式HSB
		pgRiver.endDraw();
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

		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i].setBundle();
		}

		// 画出
		for (int i = 0; i < Data.yearCount; i++) {
			allYear[i].draw(pgRiver);
			allYear[i].connect(pgRiver);
		}
		pgRiver.endDraw();
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
		// public boolean inBeFound = false, outBeFound = false;
		// public float colorNIn = 0, colorDIn = 0, entropyIn = 0;
		// public float hIn = 0, sIn = 0, bIn = 0;
		// public float colorNOut = 0, colorDOut = 0, entropyOut = 0;
		// public float hOut = 0, sOut = 0, bOut = 0;
		public RiverLine prev, next;

		public RiverLine(int provIndex) {

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

		public void connect(PGraphics pg) {
			if (prev != null) {
				pg.noFill();
//				pg.bezier(x, y, x - Param.connectingLineLength / 3, y, prev.x
//						 + Param.lineLength + Param.connectingLineLength / 3, prev.y, prev.x
//						+ Param.lineLength, prev.y);
//
//				pg.bezier(x, y + h, x - Param.connectingLineLength / 3, y + h,
//						prev.x + Param.lineLength + Param.connectingLineLength / 3, prev.y
//								+ prev.h, prev.x + Param.lineLength, prev.y + prev.h);
				
				for(int i = 0;i<h+1;i++) {
					pg.bezier(x, y + i, x - Param.connectingLineLength / 3, y + i,
							prev.x + Param.lineLength + Param.connectingLineLength / 3, prev.y
									+ i, prev.x + Param.lineLength, prev.y + i);
				}
				
//				pg.line(x, y, prev.x+Param.lineLength, prev.y);
//				pg.line(x, y+h, prev.x+Param.lineLength, prev.y+prev.h);
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
	}

	public class RiverCluster {
		public int index = -1;
		public float x, y, w, h;
		public ArrayList<RiverLine> lineInCluster = new ArrayList<RiverLine>();
		public ArrayList<RiverBundle> bundlePrev = new ArrayList<RiverBundle>();
		public ArrayList<RiverBundle> bundleNext = new ArrayList<RiverBundle>();

		// public int beFound = 0;
		// public float entropyIn = 0, entropyOut = 0;

		public RiverCluster(int index) {
			this.index = index;
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

		public void draw(PGraphics pg) {
			pg.fill(233);
			pg.stroke(233);
			pg.rect(x, y, w, h);
		}

		// 连接 Now -> Prev
		public void connect(PGraphics pg) {
			for(RiverBundle b:bundlePrev) {
				b.connect(pg);
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
			for (int i = 0; i < clusters.size(); i++) {
				sClusters.add(new RiverCluster(yearIndex * 100 + i));
				for (int j = 0; j < clusters.get(i).size(); j++) {
					RiverLine tmpLine = new RiverLine(clusters.get(i).get(j));
					lineInYear.add(tmpLine);
					sClusters.get(i).lineInCluster.add(tmpLine);
				}
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
				c.draw(pg);
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

		public void highligh() {

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
	}
}
