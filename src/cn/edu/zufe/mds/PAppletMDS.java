package cn.edu.zufe.mds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.zufe.mds.*;
import cn.edu.zufe.mds.PAppletRiver.SortBySize;
import cn.edu.zufe.mds.PAppletRiver.Param;
import cn.edu.zufe.mds.PAppletRiver.RiverLine;
import mdsj.MDSJ;
import processing.core.PApplet;
import processing.core.PGraphics;

public class PAppletMDS extends PApplet {
	double[][] pos = new double[63][2];
	private ProvinceName pName = new ProvinceName();
	String[] areaName = new String[63];
	int cnt = Data.yearCount;
	boolean isShowOneYear = true;
	int w, h;
	int overallX = 50;
	public static int mouseOn = -1, mouseIn = -1;
	PGraphics pgColorBar;

	ArrayList<DataPoint> dpoints = new ArrayList<DataPoint>();// �����е㼯����
	List<Cluster> clusters = new ArrayList<Cluster>();// ��������
	double ClusterDis = 50; // ������ֹ����ŷ����¾���

	public void setup() {
		w = 500;
		h = 500;
		size(w + overallX, h);
		colorMode(HSB, 360, 100, 100);

		pgColorBar = createGraphics(150, 400);
		drawColorBar(pgColorBar);
	}

	public void draw() {
		background(0, 0, 100);
		image(pgColorBar, 580, 210);

		List<DataPoint> clickedCluster = null;
		// ���������Բ
		for (Cluster cl : clusters) {
			List<DataPoint> tempDps = cl.getDataPoints();
			double px = 0, py = 0;// ����㼯��������
			int len = 0;
			for (DataPoint tempdp : tempDps) {
				px += tempdp.dimensioin[0];
				py += tempdp.dimensioin[1];
				len++;
			}
			boolean clicked = false;
			double disR = 0;
			px /= len;
			py /= len;
			double percent[] = new double[3];
			boolean haveHaiNan = false;
			for (DataPoint tempdp : tempDps) {
				if (GlobalVariables.pointOrPie == 0) {
					fill(0, 0, 0);
					ellipse((float) (overallX + px), (float) py, 5, 5); // ����Ȧ���ĵ�
					stroke(100, 0, 50); // ���������ߵ���ɫ
					// ��֮�������
					line((float) (overallX + px), (float) py,
							(float) (overallX + tempdp.dimensioin[0]),
							(float) tempdp.dimensioin[1]);
				}

				double tmpDis = Math.sqrt((tempdp.dimensioin[0] - px)
						* (tempdp.dimensioin[0] - px)
						+ (tempdp.dimensioin[1] - py)
						* (tempdp.dimensioin[1] - py));
				if (disR < tmpDis) {
					disR = tmpDis;
				}

				int provIndex = Integer.parseInt(tempdp.getDataPointName());
				if (mouseIn == provIndex) {
					clickedCluster = tempDps;
					clicked = true;
				}
				// �ۼӵ�һ����������ҵ��ռ��

				percent[0] += Data.area[provIndex + 1].percent[1][GlobalVariables.year
						- Data.startYear];
				percent[1] += Data.area[provIndex + 1].percent[2][GlobalVariables.year
						- Data.startYear];
				percent[2] += Data.area[provIndex + 1].percent[5][GlobalVariables.year
						- Data.startYear];
				//�ж���û�к���ʡ
				if(provIndex == 29 && GlobalVariables.year < 1987) {
					haveHaiNan= true;
				}
			}

			if (GlobalVariables.pointOrPie == 0) {
				// ����Ȧ����ɫ
				if (clicked) {
					GlobalVariables.selectCityList3.clear();
					for (DataPoint tempdp : clickedCluster) {
						GlobalVariables.selectCityList3.add(Integer
								.parseInt(tempdp.getDataPointName()));
					}
					fill(40, 100, 90, 40);
					stroke(40, 100, 90, 40);
				} else {
					fill(200, 100, 90, 40);
					stroke(200, 100, 90, 40);
				}
				// ��������Ȧ
				ellipse((float) (overallX + px), (float) py,
						(float) disR * 2 + 40, (float) disR * 2 + 40);
			} else {
				int provNum = tempDps.size();
				if(haveHaiNan) {
					provNum -=1;
				}
				float angle1 = (float) (2 * PI * percent[0] / provNum);
				float angle2 = angle1 + (float) (2 * PI * percent[1] / provNum);
				float angle3 = angle2 + (float) (2 * PI * percent[2] / provNum);
				fill(200, 100, 90, 40);
				arc((float) (overallX + px), (float) py, (float) disR * 2 + 40,
						(float) disR * 2 + 40, 0, angle1, PIE);
				fill(20, 100, 90, 40);
				arc((float) (overallX + px), (float) py, (float) disR * 2 + 40,
						(float) disR * 2 + 40, angle1, angle2, PIE);
				fill(120, 100, 90, 40);
				arc((float) (overallX + px), (float) py, (float) disR * 2 + 40,
						(float) disR * 2 + 40, angle2, angle3, PIE);
			}

		}

		ArrayList<RiverLine> lines = new ArrayList<RiverLine>();
		boolean exist = false;
		if (PAppletRiver.allYear[0] != null) {
			lines = (ArrayList<RiverLine>) PAppletRiver.allYear[GlobalVariables.year
					- Data.startYear].lineInYear.clone();
			Collections.sort(lines, new SortByProvIndex());
			exist = true;
		}

		// ����
		if (GlobalVariables.pointOrPie == 0) {
			if (GlobalVariables.provOrYear == 0) {
				for (int i = 0; i < Data.provinceCount - 1; i++) {
					int posX = (int) (pos[i][0] * (w - 60) + 10);
					int posY = (int) (pos[i][1] * (h - 60) + 10);
					// �����ɫ
					if (exist) {
						fill(lines.get(i).hIn, lines.get(i).sIn,
								lines.get(i).bIn);
					} else {
						// fill(255,153,0);
						fill(30, 100, 100);
					}

					if (GlobalVariables.selectCity != null
							&& i == pName
									.GetProvinceIndex(GlobalVariables.selectCity) - 1) {
						fill(293, 90, 55); // ѡ��ʡ����ɫ
					}
					if (GlobalVariables.selectCityList1.size() > 0) {
						for (int j = 0; j < GlobalVariables.selectCityList1
								.size(); j++) {
							if (i == pName
									.GetProvinceIndex(GlobalVariables.selectCityList1
											.get(j)) - 1) {
								fill(302, 47, 66); // һ��
								break;
							}
						}
					}
					if (GlobalVariables.selectCityList2.size() > 0) {
						for (int j = 0; j < GlobalVariables.selectCityList2
								.size(); j++) {
							if (i == pName
									.GetProvinceIndex(GlobalVariables.selectCityList2
											.get(j)) - 1) {
								fill(271, 35, 95); // ����
								break;
							}
						}
					}
					// System.out.println("  " + pos[i][0] + " " + pos[i][1]);
					ellipse(overallX + posX, posY, 20, 20);
					fill(0, 0, 0);
				}
			} else if (GlobalVariables.provOrYear == 1) {
				for (int i = 0; i < Data.yearCount; i++) {
					int posX = (int) (pos[i][0] * (w - 60) + 10);
					int posY = (int) (pos[i][1] * (h - 60) + 10);
					// �����ɫ
					fill(30, 100, 100);

					if (i == GlobalVariables.year - Data.startYear) {
						fill(293, 90, 55); // ѡ�������ɫ
					}

					ellipse(overallX + posX, posY, 20, 20);
					fill(0, 0, 0);
				}
			}
		}

		// ��ʾʡ������
		if (mouseOn != -1 && GlobalVariables.pointOrPie == 0) {
			String provName = pName.provinceChineseName[mouseOn + 1];
			int len = 15;
			if (Language.languageType == 0) {
				provName = pName.provinceChineseName[mouseOn + 1];
				len = 15;
			} else if (Language.languageType == 1) {
				provName = pName.provinceEnglishName[mouseOn + 1]
						.replace("_", "").replace("1", "").replace("3", "");
				len = 8;
			}
			strokeWeight(1);
			int offsetX = 20, offsetY = 20;
			if (mouseX + 20 + provName.length() * len > w) {
				offsetX = offsetX - 40 - provName.length() * len;
			}
			if (mouseY + 40 > h) {
				offsetY = -20;
			}

			fill(0, 0, 100);
			rect(mouseX + offsetX, mouseY + offsetY, provName.length() * len,
					20);
			fill(0);
			text(provName, mouseX + offsetX + 5, mouseY + offsetY + 15);
		}
	}

	public void mouseMoved() {
		// ��ʾʡ����Ϣ
		mouseOn = -1;
		for (int i = 0; i < Data.provinceCount - 1; i++) {
			int posX = (int) (pos[i][0] * (w - 60) + 10);
			int posY = (int) (pos[i][1] * (h - 60) + 10);
			double dis = Math.sqrt((mouseX - posX - overallX)
					* (mouseX - posX - overallX) + (mouseY - posY)
					* (mouseY - posY));
			if (dis <= 10) {
				mouseOn = i;
				break;
			}
		}
	}

	public void mousePressed() {
		PAppletRiver.pressedIn = null;
		GlobalVariables.selectCityList3.clear();
		mouseIn = -1;
		if (mouseButton == LEFT) {
			for (int i = 0; i < Data.provinceCount - 1; i++) {
				int posX = (int) (pos[i][0] * (w - 60) + 10);
				int posY = (int) (pos[i][1] * (h - 60) + 10);
				double dis = Math.sqrt((mouseX - posX - overallX)
						* (mouseX - posX - overallX) + (mouseY - posY)
						* (mouseY - posY));
				if (dis <= 10) {
					mouseIn = i;
					break;
				}
			}
		} else {
			mouseIn = -1;
		}
	}

	public void startCluster() {

		if (GlobalVariables.provOrYear == 0) {
			cnt = Data.provinceCount - 1;
		} else if (GlobalVariables.provOrYear == 1) {
			cnt = Data.yearCount;
		}
		dpoints.clear(); // ����ÿ��ѡȡ֮ǰ�����

		for (int i = 0; i < cnt; i++) // pos�������
		{
			double[] tmp = { pos[i][0] * (w - 60) + 10,
					pos[i][1] * (h - 60) + 10 };
			// System.out.println(tmp[0] + "  " + tmp[1]);
			dpoints.add(new DataPoint(tmp, i + ""));
		}

		ClusterAnalysis ca = new ClusterAnalysis();
		clusters = ca.startAnalysis(dpoints); // ��ʼ����
	}

	public void setPosition(double[][] input) {
		int n = input[0].length; // number of data objects
		// System.out.println("!!!! n = " + n + " !!! " + input.length);
		double temMaxn = input[0][0];
		double temMinn = input[0][0];
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				if (temMaxn < input[i][j])
					temMaxn = input[i][j];
				if (temMinn > input[i][j])
					temMinn = input[i][j];
				// System.out.println(input[i][j]);
			}
		}
		// System.out.println("maxn = " + temMaxn + " minn = " + temMinn);
		double[][] output = MDSJ.classicalScaling(input); // apply MDS

		double maxPosX = Common.getMaxValue(output[0]);
		double minPosX = Common.getMinValue(output[0]);
		double maxPosY = Common.getMaxValue(output[1]);
		double minPosY = Common.getMinValue(output[1]);
		// System.out.println(maxPosX + " !! " + minPosX);
		// System.out.println(maxPosY + " ## " + minPosY);
		for (int i = 0; i < n; i++) { // output all coordinates
			// System.out.println(output[0][i] + " " + output[1][i]);
			pos[i][0] = (output[0][i] - minPosX) / (maxPosX - minPosX);
			pos[i][1] = (output[1][i] - minPosY) / (maxPosY - minPosY);
			// System.out.println("pos["+i+"][0] = "+pos[i][0]+" pos["+i+"][1] = "+pos[i][1]);
		}

		startCluster();
	}

	public void setPositionNoDisplay(double[][] input, int opr) {
		// ר�����ڵ������ڵļ���
		// if (GlobalVariables.selectCity == null) {
		// return;
		// }
		double[][] posTmp = new double[63][2];
		int n = input[0].length; // number of data objects
		// System.out.println("!!!! n = " + n + " !!! " + input.length);
		double temMaxn = input[0][0];
		double temMinn = input[0][0];
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				if (temMaxn < input[i][j])
					temMaxn = input[i][j];
				if (temMinn > input[i][j])
					temMinn = input[i][j];
				// System.out.println(input[i][j]);
			}
		}
		// System.out.println("maxn = " + temMaxn + " minn = " + temMinn);
		double[][] output = MDSJ.classicalScaling(input); // apply MDS

		double maxPosX = Common.getMaxValue(output[0]);
		double minPosX = Common.getMinValue(output[0]);
		double maxPosY = Common.getMaxValue(output[1]);
		double minPosY = Common.getMinValue(output[1]);
		// System.out.println(maxPosX + " !! " + minPosX);
		// System.out.println(maxPosY + " ## " + minPosY);
		for (int i = 0; i < n; i++) { // output all coordinates
			// System.out.println(output[0][i] + " " + output[1][i]);
			posTmp[i][0] = (output[0][i] - minPosX) / (maxPosX - minPosX);
			posTmp[i][1] = (output[1][i] - minPosY) / (maxPosY - minPosY);
			System.out.println("pos["+i+"][0] = "+pos[i][0]+" pos["+i+"][1] = "+pos[i][1]);
		}

		ArrayList<DataPoint> dpoints = new ArrayList<DataPoint>();// �����е㼯����
		List<Cluster> clusters = new ArrayList<Cluster>();// ��������
		int cntTmp = Data.provinceCount - 1; // ���ֵ��ȷ����
		dpoints.clear(); // ����ÿ��ѡȡ֮ǰ�����

		for (int i = 0; i < cntTmp; i++) // pos�������
		{
			double[] tmp = { posTmp[i][0] * (w - 60) + 10,
					posTmp[i][1] * (h - 60) + 10 };
			// System.out.println(tmp[0] + "  " + tmp[1]);
			dpoints.add(new DataPoint(tmp, i + ""));
		}

		ClusterAnalysis ca = new ClusterAnalysis();
		clusters = ca.startAnalysis(dpoints); // ��ʼ����

		if (opr != -1) {
			GlobalVariables.clusters[opr] = new ArrayList<ArrayList<Integer>>();
			// ԭ���������������������ڰ���ת�����������ʽ���棬�ɹ����Ĵ���ʹ��
			for (Cluster cl : clusters) {
				// System.out.println("------"+cl.getClusterName()+"------");
				List<DataPoint> tempDps = cl.getDataPoints();
				ArrayList<Integer> cluster = new ArrayList<Integer>();
				for (DataPoint tempdp : tempDps) {
					// System.out.println(tempdp.getDataPointName()+"---"+tempdp.dimensioin[0]+"---"+tempdp.dimensioin[1]);
					cluster.add(Integer.parseInt(tempdp.getDataPointName()));
				}
				GlobalVariables.clusters[opr].add(cluster);
				// System.out.println(cluster);
			}
		}

	}

	// DataPoint ��
	public class DataPoint {
		String dataPointName; // ��������
		Cluster cluster; // �������������
		private double dimensioin[]; // �������ά��

		public DataPoint() {

		}

		public DataPoint(double[] dimensioin, String dataPointName) {
			this.dataPointName = dataPointName;
			this.dimensioin = dimensioin;
		}

		public double[] getDimensioin() {
			return dimensioin;
		}

		public void setDimensioin(double[] dimensioin) {
			this.dimensioin = dimensioin;
		}

		public Cluster getCluster() {
			return cluster;
		}

		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}

		public String getDataPointName() {
			return dataPointName;
		}

		public void setDataPointName(String dataPointName) {
			this.dataPointName = dataPointName;
		}
	}

	// ������
	public class Cluster {
		private List<DataPoint> dataPoints = new ArrayList<DataPoint>(); // ����е�������
		private String clusterName; // ��������

		public List<DataPoint> getDataPoints() {
			return dataPoints;
		}

		public void setDataPoints(List<DataPoint> dataPoints) {
			this.dataPoints = dataPoints;
		}

		public String getClusterName() {
			return clusterName;
		}

		public void setClusterName(String clusterName) {
			this.clusterName = clusterName;
		}

	}

	public class ClusterAnalysis {

		public List<Cluster> startAnalysis(List<DataPoint> dataPoints) {
			List<Cluster> finalClusters = new ArrayList<Cluster>();

			List<Cluster> originalClusters = initialCluster(dataPoints); // ��ʼ������
			finalClusters = originalClusters;
			boolean flag = true; // ������ֹ��Ƿ�
			while (flag) {
				double min = Double.MAX_VALUE;
				int mergeIndexA = 0;
				int mergeIndexB = 0;
				for (int i = 0; i < finalClusters.size(); i++) {
					for (int j = 0; j < finalClusters.size(); j++) {
						if (i != j) {

							Cluster clusterA = finalClusters.get(i);
							Cluster clusterB = finalClusters.get(j);

							List<DataPoint> dataPointsA = clusterA
									.getDataPoints();
							List<DataPoint> dataPointsB = clusterB
									.getDataPoints();

							double tmpAx = 0, tmpAy = 0; // ��A��������
							double tmpBx = 0, tmpBy = 0; // ��B��������
							for (int m = 0; m < dataPointsA.size(); m++) {
								DataPoint dpA = dataPointsA.get(m);
								double[] dimA = dpA.getDimensioin();
								tmpAx += dimA[0];
								tmpAy += dimA[1];
							}
							tmpAx /= dataPointsA.size();
							tmpAy /= dataPointsA.size();

							for (int m = 0; m < dataPointsB.size(); m++) {
								DataPoint dpB = dataPointsB.get(m);
								double[] dimB = dpB.getDimensioin();
								tmpBx += dimB[0];
								tmpBy += dimB[1];
							}
							tmpBx /= dataPointsB.size();
							tmpBy /= dataPointsB.size();

							double tempDis = Math.sqrt((tmpAx - tmpBx)
									* (tmpAx - tmpBx)
									+ ((tmpAy - tmpBy) * (tmpAy - tmpBy)));
							if (tempDis < min) {
								min = tempDis;
								mergeIndexA = i;
								mergeIndexB = j;
								flag = false;
							}
							if (min > ClusterDis) // ��������֮����ӽ��ĵ��ŷ����¾����������ֵ����������
							{
								flag = false;
							} else {
								flag = true;
							}
							/*
							 * for(int m=0;m<dataPointsA.size();m++){ for(int
							 * n=0;n<dataPointsB.size();n++){ double
							 * tempDis=getDistance
							 * (dataPointsA.get(m),dataPointsB.get(n));
							 * if(tempDis<min){ min=tempDis; mergeIndexA=i;
							 * mergeIndexB=j; } } } if(min > ClusterDis)
							 * //��������֮����ӽ��ĵ��ŷ����¾����������ֵ���������� {flag=false; }
							 * else {flag=true; }
							 */
						}
					} // end for j
				}// end for i
					// �ϲ�cluster[mergeIndexA]��cluster[mergeIndexB]
				if (flag == false)
					break;
				finalClusters = mergeCluster(finalClusters, mergeIndexA,
						mergeIndexB);

			}// end while

			return finalClusters;
		}

		private List<Cluster> mergeCluster(List<Cluster> clusters,
				int mergeIndexA, int mergeIndexB) {
			if (mergeIndexA != mergeIndexB) {
				// ��cluster[mergeIndexB]�е�DataPoint���뵽 cluster[mergeIndexA]
				Cluster clusterA = clusters.get(mergeIndexA);
				Cluster clusterB = clusters.get(mergeIndexB);

				List<DataPoint> dpA = clusterA.getDataPoints();
				List<DataPoint> dpB = clusterB.getDataPoints();

				for (DataPoint dp : dpB) {
					DataPoint tempDp = new DataPoint();
					tempDp.setDataPointName(dp.getDataPointName());
					tempDp.setDimensioin(dp.getDimensioin());
					tempDp.setCluster(clusterA);
					dpA.add(tempDp);
				}

				clusterA.setDataPoints(dpA);

				// List<Cluster> clusters���Ƴ�cluster[mergeIndexB]
				clusters.remove(mergeIndexB);
			}

			return clusters;
		}

		// ��ʼ�����
		private List<Cluster> initialCluster(List<DataPoint> dataPoints) {
			List<Cluster> originalClusters = new ArrayList<Cluster>();
			for (int i = 0; i < dataPoints.size(); i++) {
				DataPoint tempDataPoint = dataPoints.get(i);
				List<DataPoint> tempDataPoints = new ArrayList<DataPoint>();
				tempDataPoints.add(tempDataPoint);

				Cluster tempCluster = new Cluster();
				tempCluster.setClusterName("Cluster " + String.valueOf(i));
				tempCluster.setDataPoints(tempDataPoints);

				tempDataPoint.setCluster(tempCluster);
				originalClusters.add(tempCluster);
			}

			return originalClusters;
		}

		// ��������������֮���ŷ����þ���
		private double getDistance(DataPoint dpA, DataPoint dpB) {
			double distance = 0;
			double[] dimA = dpA.getDimensioin();
			double[] dimB = dpB.getDimensioin();

			if (dimA.length == dimB.length) {
				for (int i = 0; i < dimA.length; i++) {
					double temp = Math.pow((dimA[i] - dimB[i]), 2);
					distance = distance + temp;
				}
				distance = Math.pow(distance, 0.5);
			}

			return distance;
		}
	}

	public class SortByProvIndex implements Comparator<RiverLine> {
		@Override
		public int compare(RiverLine s1, RiverLine s2) {
			if (s1.provIndex < s2.provIndex) {
				return -1;
			}
			return 1;
		}
	}

	/*
	 * public void drawColorBar(PGraphics pg) { pg.beginDraw();
	 * pg.colorMode(HSB, 360, 100, 100); pg.strokeWeight(0); for (int i = 0; i <
	 * 6; i++) { pg.fill(360, 80 - i * 10, 100); pg.rect(20, i * 20, 40, 20); }
	 * for (int i = 2; i < 8; i++) { pg.fill(230, i * 10, 100); pg.rect(20, (i -
	 * 2) * 20 + 120, 40, 20); }
	 * 
	 * pg.fill(100, 10, 50); pg.text("1", 72, 10); pg.text("0", 72, 240);
	 * pg.text("NE", 31, 260);
	 * 
	 * pg.stroke(100, 10, 50); pg.strokeWeight(2); pg.line(61, 1, 65, 1); // С����
	 * ���ߣ� pg.line(61, 238, 65, 238); // С���� ���ͣ� pg.endDraw(); }
	 */
	public void drawColorBar(PGraphics pg) {
		pg.beginDraw();
		pg.colorMode(HSB, 360, 100, 100);
		int weight = 1;
		pg.strokeWeight(1);
		for (int i = 0; i < 240; i++) {
			pg.stroke(i - 10, 50, 100);
			for (int j = 0; j < weight; j++) {
				pg.line(20, i * weight - j, 60, i * weight - j);
			}
		}

		pg.fill(100, 10, 50);
		pg.text("1", 72, 10);
		pg.text("0", 72, 240);
		pg.text("NE", 31, 260);

		pg.stroke(100, 10, 50);
		pg.strokeWeight(2);
		pg.line(61, 1, 65, 1); // С���� ���ߣ�
		pg.line(61, 238, 65, 238); // С���� ���ͣ�
		pg.endDraw();
	}
}
