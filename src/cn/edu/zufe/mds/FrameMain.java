package cn.edu.zufe.mds;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class FrameMain extends JFrame implements ActionListener, ChangeListener,WindowListener{
	
	JMenuBar menuBar;
	JMenu menuColorStyle;
	JMenuItem menuItemColorMappingStyle1, menuItemColorMappingStyle2, menuItemColorMappingStyle3,
	menuItemColorMappingStyle4;

	ProvinceName pName = new ProvinceName();
	
	PAppletChinaMap pChinaMap = new PAppletChinaMap(this); //processing中国地图窗体(第一窗口)
	PAppletMDS pMDS = new PAppletMDS(); //二维MDS(第二窗口)
	//PAppletISMDS pISMDS = new PAppletISMDS(); //时空混合可视化窗口
//	PAppletStoryFlow pStory = new PAppletStoryFlow();//(第三窗口)
	PAppletRiver pRiver = new PAppletRiver();//(第三窗口_重构)

	Language lan = new Language();
	int w,h; 
	
	JPanel centerPanel = new JPanel();
	JPanel mapPanel = new JPanel();
	JPanel mdsPanel = new JPanel();
	JLabel labelYear = new JLabel(lan.year + Data.startYear);
	JLabel labelThreshold = new JLabel(lan.clusterthreshold + "50");
	JCheckBox[] checkBox = new JCheckBox[12];;
	JRadioButton rbtnProduction;// 查看具体产值
	JRadioButton rbtnProportion;// 查看占比
	JRadioButton rbtnNone,rbtnOne,rbtnTwo;  //几度省份
	JRadioButton rbtnOneYearAllProv,rbtnOneProvAllYear; //当前年份各个省份的差异，当前省份历年来的差异
	JRadioButton rbtnSortBySize,rbtnSortByPerCapitaGDP,rbtnSortByGDP;//第三窗口的StoryLine排序方式
	JRadioButton rbtnPoint, rbtnPie; //点图，饼图
	JSlider sliderYear,sliderThreshold; //滑动块
	
	Data data = new Data();
	public static int provinceCount=Data.provinceCount-1; //去掉了中国所以-1
	public static double[][][] industry = new double[Data.industryCount][Data.yearCount][provinceCount];
	public static double[][][] percent = new double[Data.industryCount][Data.yearCount][provinceCount];
	public static double[][][] industry_year = new double[Data.industryCount][Data.yearCount][provinceCount]; //一年内归一化
	public static double[][][] industry_prov = new double[Data.industryCount][provinceCount][Data.yearCount]; //一省内归一化
	double[][][] allYearAllProvData = new double[Data.yearCount][provinceCount][provinceCount];//所有年份的所有省份 相似矩阵
	double[][] oneProvAllYearData = new double[Data.yearCount][Data.yearCount];//一个省份的不同年份 相似矩阵
	double[][] oneYearAllProvData = new double[provinceCount][provinceCount];//一个省份的不同年份 相似矩阵
	
    public FrameMain(){
    	initData();
    	init();
    	addMenu();
    	//Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    	this.setSize(1650,1000);
		this.setVisible(true);
		this.setLayout(null);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	//this.setExtendedState(JFrame.MAXIMIZED_BOTH);  //最大化
    	
    	menuItemColorMappingStyle1.addActionListener(this);//颜色映射点击事件添加
		menuItemColorMappingStyle2.addActionListener(this);
		menuItemColorMappingStyle3.addActionListener(this);
		menuItemColorMappingStyle4.addActionListener(this);
    }
    public void addMenu(){
    	menuBar = new JMenuBar();
    	menuColorStyle = new JMenu("颜色映射方案选择");
    	menuItemColorMappingStyle1 = new JMenuItem("直接映射");
		menuItemColorMappingStyle2 = new JMenuItem("直方图均衡化映射");
		menuItemColorMappingStyle3 =new JMenuItem("Log取对数映射");
		menuItemColorMappingStyle4 =new JMenuItem("颜色映射方案四");
		menuColorStyle.add(menuItemColorMappingStyle1);
		menuColorStyle.add(menuItemColorMappingStyle2);
		menuColorStyle.add(menuItemColorMappingStyle3);
		menuColorStyle.add(menuItemColorMappingStyle4);
		menuBar.add(menuColorStyle);

		this.setJMenuBar(menuBar);
    }
    @Override	//四种颜色映射方案切换
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==menuItemColorMappingStyle1){
			GlobalVariables.colorMappingStyle =1;
			pChinaMap.colorStyle();
		}
		else if(e.getSource()==menuItemColorMappingStyle2){
			GlobalVariables.colorMappingStyle =2;
			pChinaMap.colorStyle();
		}
		else if(e.getSource()==menuItemColorMappingStyle3){
			GlobalVariables.colorMappingStyle =3;
			pChinaMap.colorStyle();
		}
		else if(e.getSource()==menuItemColorMappingStyle4){
			GlobalVariables.colorMappingStyle =4;
			pChinaMap.colorStyle();
		}
	}
    public void init() {
    	this.add(centerPanel,BorderLayout.CENTER);
    	centerPanel.setPreferredSize(new Dimension(1400, this.HEIGHT));
    	//centerPanel.setBackground(Color.WHITE);

    	//中国地图 + 年份滑动条
    	mapPanel.setLayout(new BorderLayout(0, 0));
    	mapPanel.setPreferredSize(new Dimension(768, 540));
    	pChinaMap.setPreferredSize(new Dimension(768, 540));
    	pChinaMap.init();
		pChinaMap.start();
		mapPanel.add(pChinaMap, BorderLayout.CENTER);

		JPanel sliderYearPanel = new JPanel();
		labelYear.setPreferredSize(new Dimension(100, 17));
		sliderYearPanel.add(labelYear);
    	sliderYear = new JSlider(JSlider.HORIZONTAL, Data.startYear, Data.startYear + Data.yearCount - 1, Data.startYear);
		sliderYear.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				GlobalVariables.year = (int) sliderYear.getValue();
				labelYear.setText(lan.year + GlobalVariables.year);
				if(GlobalVariables.provOrYear == 0) {
					drawMDS();
				}
			}
		});
		sliderYear.setPreferredSize(new Dimension(630, 17));
		sliderYearPanel.add(sliderYear);
		mapPanel.add(sliderYearPanel, BorderLayout.SOUTH);
    	centerPanel.add(mapPanel);
    	
		//二维MDS + 阈值滑动条
		mdsPanel.setLayout(new BorderLayout(0, 0));
		mdsPanel.setPreferredSize(new Dimension(668, 540));
		pMDS.setPreferredSize(new Dimension(668,520));
		pMDS.init();
		pMDS.start();
		mdsPanel.add(pMDS, BorderLayout.CENTER);
		
		JPanel sliderThresholdPanel = new JPanel();
		labelThreshold.setPreferredSize(new Dimension(100, 17));
		sliderThresholdPanel.add(labelThreshold);
		sliderThreshold = new JSlider(JSlider.HORIZONTAL, 0, 200, 50);
		sliderThreshold.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pMDS.ClusterDis = sliderThreshold.getValue();
				labelThreshold.setText(lan.clusterthreshold + sliderThreshold.getValue());
				pMDS.startCluster();
				drawStoryFlow();
			}
		});
		
		sliderThreshold.setPreferredSize(new Dimension(510, 17));
		sliderThresholdPanel.add(sliderThreshold);
		mdsPanel.add(sliderThresholdPanel, BorderLayout.SOUTH);
		
		centerPanel.add(mdsPanel);
		
		/*
		//时空混合可视化窗口
		pISMDS.setPreferredSize(new Dimension(PAppletISMDS.yearLen*Data.yearCount, 300));
		centerPanel.add(pISMDS);
		pISMDS.init();
		pISMDS.start();
		*/
		
		//故事线画法的新第三窗口
//		pStory.setPreferredSize(new Dimension(PAppletStoryFlow.yearLen * Data.yearCount, 400));
//		centerPanel.add(pStory);
//		pStory.init();
//		pStory.start();
		//河流画法的第三窗口 //聚类修改为圆
		pRiver.setPreferredSize(new Dimension(PAppletStoryFlow.yearLen * Data.yearCount, 400));
		centerPanel.add(pRiver);
		pRiver.init();
		pRiver.start();
		
		//---------------
		ItemListener itemListener = new ItemListener() {
            JCheckBox jCheckBox;
 
            public void itemStateChanged(ItemEvent e) {
                jCheckBox = (JCheckBox) e.getSource();
                for (int i = 0; i < checkBox.length; i++) {
                	if(checkBox[i].isSelected()) {
                		GlobalVariables.industryFlag[i+1] = true;
                	} else {
                		GlobalVariables.industryFlag[i+1] = false;
                	}
                }
                
                drawStoryFlow();
				drawMDS();
                
                /*
                if(pISMDS!=null) {
                	pISMDS.stop();
                	pISMDS.dispose();
                	centerPanel.remove(pISMDS);
                }
                pISMDS = new PAppletISMDS();
                pISMDS.init();
        		pISMDS.start();
        		pISMDS.setPreferredSize(new Dimension(PAppletISMDS.yearLen*Data.yearCount, 300));
        		centerPanel.add(pISMDS);
                validate();
                repaint();
                */
            }
        };
		//---------------
		
		//为本窗体加入控件
		JPanel panel = new JPanel(); //声明一个面板
		panel.setPreferredSize(new Dimension(170, this.HEIGHT));
		panel.setBackground(Color.WHITE);
		// 具体产值和占比
		rbtnProduction = new JRadioButton(lan.product);
		rbtnProduction.setPreferredSize(new Dimension(70, 30));
		rbtnProduction.setSelected(true);
		rbtnProportion = new JRadioButton(lan.proportion);
		rbtnProportion.setPreferredSize(new Dimension(90, 30));
		ButtonGroup bGroup1 = new ButtonGroup();
		bGroup1.add(rbtnProduction);
		bGroup1.add(rbtnProportion);
		rbtnProduction.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.productOrProport = 0;
				drawStoryFlow();
				drawMDS();
			}
		});
		rbtnProportion.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.productOrProport = 1;
				drawStoryFlow();
				drawMDS();
			}
		});
		// 复选框，相似度的选择项
		checkBox[0] = new JCheckBox(lan.industry[0]);
		checkBox[1] = new JCheckBox(lan.industry[1]);
		checkBox[2] = new JCheckBox(lan.industry[2]);
		checkBox[3] = new JCheckBox(lan.industry[3]);
		checkBox[4] = new JCheckBox(lan.industry[4]);
		checkBox[5] = new JCheckBox(lan.industry[5]);
		checkBox[6] = new JCheckBox(lan.industry[6]);
		checkBox[7] = new JCheckBox(lan.industry[7]);
		checkBox[8] = new JCheckBox(lan.industry[8]);
		checkBox[9] = new JCheckBox(lan.industry[9]);
		checkBox[10] = new JCheckBox(lan.industry[10]);
		checkBox[11] = new JCheckBox(lan.industry[11]);


		panel.add(rbtnProduction);//面板中加入控件
		panel.add(rbtnProportion);
		for (int i = 0; i < checkBox.length; i++) {
			if(i == 7) {
				continue; //住宿和餐饮业不显示
			}
			int j;
			for(j = 0;j<Data.indexIndustry.length;j++) {
				if(i == Data.indexIndustry[j] - 1) {
					checkBox[i].setPreferredSize(new Dimension(170, 30));
					break;
				}
			}
			if(j == Data.indexIndustry.length) {
				checkBox[i].setPreferredSize(new Dimension(120, 30));
			}

			checkBox[i].setBackground(Color.WHITE); //顺便改个底色
			checkBox[i].addItemListener(itemListener);
			panel.add(checkBox[i]);
		}
		//checkBox[0].setSelected(true);
		//分割线
		JLabel jl = new JLabel("________________________");
		jl.setPreferredSize(new Dimension(170, 30));
		panel.add(jl);
		
		ButtonGroup bGroupPointOrPie = new ButtonGroup();
		rbtnPoint = new JRadioButton(lan.point);
		rbtnPie = new JRadioButton(lan.pieChart);
		rbtnPoint.setPreferredSize(new Dimension(170, 30));
		rbtnPie.setPreferredSize(new Dimension(170, 30));
		bGroupPointOrPie.add(rbtnPoint);
		bGroupPointOrPie.add(rbtnPie);
		panel.add(rbtnPoint);
		panel.add(rbtnPie);
		rbtnPoint.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.pointOrPie = 0;
			}
		});
		rbtnPie.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.pointOrPie = 1;
			}
		});
		rbtnPoint.setSelected(true);
		
		JLabel j5 = new JLabel("________________________");
		j5.setPreferredSize(new Dimension(170, 30));
		panel.add(j5);
		//1,2度省份选择
		ButtonGroup bGroup2 = new ButtonGroup();
		rbtnNone = new JRadioButton(lan.alone);
		rbtnOne = new JRadioButton(lan.neighborhood);
		rbtnTwo = new JRadioButton("二度省份");
		rbtnNone.setPreferredSize(new Dimension(170, 30));
		rbtnOne.setPreferredSize(new Dimension(170, 30));
		rbtnTwo.setPreferredSize(new Dimension(170, 30));
		bGroup2.add(rbtnNone);
		bGroup2.add(rbtnOne);
		bGroup2.add(rbtnTwo);
		rbtnNone.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				pChinaMap.changeDegree(0);
			}
		});
		rbtnOne.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				pChinaMap.changeDegree(1);
			}
		});
		rbtnTwo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				pChinaMap.changeDegree(2);
			}
		});
		panel.add(rbtnNone);
		panel.add(rbtnOne);
		//panel.add(rbtnTwo);
		
		rbtnNone.setSelected(true); 
		
		/*
		//分割线2
		JLabel j2 = new JLabel("________________________");
		j2.setPreferredSize(new Dimension(170, 30));
		panel.add(j2);

		ButtonGroup bGroup3 = new ButtonGroup();
		rbtnOneYearAllProv = new JRadioButton("查看一年");
		rbtnOneProvAllYear = new JRadioButton("查看一省");
		rbtnOneYearAllProv.setPreferredSize(new Dimension(170, 30));
		rbtnOneProvAllYear.setPreferredSize(new Dimension(170, 30));
		bGroup3.add(rbtnOneYearAllProv);
		bGroup3.add(rbtnOneProvAllYear);
		rbtnOneYearAllProv.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.provOrYear = 0;
				drawMDS();
			}
		});
		rbtnOneProvAllYear.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.provOrYear = 1;
				drawMDS();
			}
		});
		panel.add(rbtnOneYearAllProv);
		panel.add(rbtnOneProvAllYear);
		rbtnOneYearAllProv.setSelected(true); 
		*/
		
		//分割线3
		JLabel j3 = new JLabel("________________________");
		j3.setPreferredSize(new Dimension(170, 30));
		panel.add(j3);
		
		JLabel jos = new JLabel("Ordering Schemes");
		jos.setPreferredSize(new Dimension(170, 30));
		panel.add(jos);
		
		ButtonGroup bGroup4 = new ButtonGroup();
		rbtnSortBySize = new JRadioButton(lan.sort[0]);
		rbtnSortByPerCapitaGDP = new JRadioButton(lan.sort[1]);
		rbtnSortByGDP = new JRadioButton(lan.sort[2]);
		rbtnSortBySize.setPreferredSize(new Dimension(170, 30));
		rbtnSortByPerCapitaGDP.setPreferredSize(new Dimension(170, 30));
		rbtnSortByGDP.setPreferredSize(new Dimension(170, 30));
		bGroup4.add(rbtnSortBySize);
		bGroup4.add(rbtnSortByPerCapitaGDP);
		bGroup4.add(rbtnSortByGDP);
		rbtnSortBySize.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.sortMethod = 0;
				drawStoryFlow();
			}
		});
		rbtnSortByPerCapitaGDP.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.sortMethod = 1;
				drawStoryFlow();
			}
		});
		rbtnSortByGDP.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlobalVariables.sortMethod = 2;
				drawStoryFlow();
			}
		});
		panel.add(rbtnSortBySize);
		panel.add(rbtnSortByPerCapitaGDP);
		panel.add(rbtnSortByGDP);
		rbtnSortByPerCapitaGDP.setSelected(true);
		
		JLabel j4 = new JLabel("________________________");
		j4.setPreferredSize(new Dimension(170, 30));
		panel.add(j4);
		
		JLabel logo = new JLabel(new ImageIcon("src\\logo.png"));
		panel.add(logo);
		
		/*
		//按钮
		btnMDS = new JButton("OK");
		btnMDS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		    	//弹出MDS窗体
				frameIS = new FrameISMDS();
				frameIS.setVisible(true);
			}
		});
		btnMDS.setBounds(1206, 132, 49, 27);
		panel.add(btnMDS);
		*/
		this.add(panel,BorderLayout.EAST);
		//改变控件底色(JRadioButton)
		for (int i = 0; i < panel.getComponentCount(); i++) {
			Object obj = panel.getComponent(i);
			if(obj instanceof JRadioButton){
				((JRadioButton) obj).setBackground(Color.WHITE);
			}
		}
    }
    
    public void drawStoryFlow() {
//    	int selectIndustryNum = 0;
//    	for(int k = 0; k < GlobalVariables.industryFlag.length; k++) {
//			if(GlobalVariables.industryFlag[k]) {
//				selectIndustryNum++;
//			}
//    	}
//    	if(selectIndustryNum < 2) {
//    		pStory.drawNone = true;
//    		return;
//    	}
//    	else {
//    		pStory.drawNone = false;
//	        for(int i = 0; i < Data.yearCount; i++) {
//	        	getSimilarityMatrix_OneYearAllProv(i + Data.startYear);
//	            pMDS.setPositionNoDisplay(oneYearAllProvData, i);
//	        }
//	        pStory.stop();
//	        pStory.drawPG();
//	        pStory.start();
//    	}
    	
    	int selectIndustryNum = 0;
    	for(int k = 0; k < GlobalVariables.industryFlag.length; k++) {
			if(GlobalVariables.industryFlag[k]) {
				selectIndustryNum++;
			}
    	}
    	if(selectIndustryNum < 2) {
    		pRiver.drawNone = true;
    		return;
    	}
    	else {
    		pRiver.drawNone = false;
	        for(int i = 0; i < Data.yearCount; i++) {
	        	getSimilarityMatrix_OneYearAllProv(i + Data.startYear);
	            pMDS.setPositionNoDisplay(oneYearAllProvData, i);
	        }
	        pRiver.stop();
	        pRiver.drawPG();
	        pRiver.start();
    	}
    } 
    
    public void drawMDS() {
    	if(GlobalVariables.provOrYear == 0) {
    		getSimilarityMatrix_OneYearAllProv(GlobalVariables.year);
    		pMDS.stop();
            pMDS.setPosition(oneYearAllProvData);
            pMDS.start();
    	} else {
            getSimilarityMatrix_OneProvAllYear();
            pMDS.stop();
            pMDS.setPosition(oneProvAllYearData);
            pMDS.start();
    	}
    } 
    
    public void initData() {
    	data.LoadData("src\\CRE_Gdp01.csv");
    	//得到数据
		for(int i=0;i<provinceCount;i++) {
			for(int j=0;j<Data.industryCount;j++) {
				for(int k=0;k<Data.yearCount;k++) {
					industry[j][k][i] = Data.area[i+1].industry[j][k];
					percent[j][k][i] = Data.area[i+1].percent[j][k];
				}
			}
		}
		//年份内归一化
		for (int k = 0; k < industry.length; k++) {
			for (int i = 0; i < industry[k].length; i++) {
				double max = Common.getMaxValue(industry[k][i]);
				double min = Common.getMinValue(industry[k][i]);
	
				for (int j = 0; j < industry[k][i].length; j++) {
					industry_year[k][i][j] = (industry[k][i][j] - min) / (max - min);
				}
			}
		}
		//一个省内归一化
		//图方便转换了下数据
		for (int k = 0; k < industry.length; k++) {
			for (int i = 0; i < industry[k].length; i++) {
				for (int j = 0; j < industry[k][i].length; j++) {
					industry_prov[k][j][i] = industry[k][i][j];
				}
			}
		}
		for (int k = 0; k < industry_prov.length; k++) {
			for (int i = 0; i < industry_prov[k].length; i++) {
				double max = Common.getMaxValue(industry_prov[k][i]);
				double min = Common.getMinValue(industry_prov[k][i]);
	
				for (int j = 0; j < industry_prov[k][i].length; j++) {
					//System.out.println(industry_prov[k][i][j] + " min: " + min + " max:" + max);
					industry_prov[k][i][j] = (industry_prov[k][i][j] - min) / (max - min);
				}
			}
		}

    }
    
	public void getSimilarityMatrix() {
		//所有年份的所有省份 相似矩阵
		for (int i = 0; i < industry_year[0].length; i++) {
			for (int j = 0; j < industry_year[0][i].length; j++) {
				for (int t = 0; t < industry_year[0][i].length; t++) {
					//计算相似矩阵
					double sum=0;
					for(int k = 0; k < GlobalVariables.industryFlag.length; k++) {
						if(GlobalVariables.industryFlag[k]) {
							double tmp = 0;
							if(GlobalVariables.productOrProport == 0) {
								tmp = industry_year[k][i][j] - industry_year[k][i][t];
							}
							else if(GlobalVariables.productOrProport == 1) {
								tmp = percent[k][i][j] - percent[k][i][t];
							}
							sum+=tmp*tmp;
						}
					}
					allYearAllProvData[i][j][t]=Math.sqrt(sum);
				}
			}
		}
	}
    
	public void getSimilarityMatrix_OneProvAllYear() {
		//System.out.println("!" + GlobalVariables.selectCity);
		if(GlobalVariables.selectCity != "" && GlobalVariables.selectCity != null) {
			int cityIndex = pName.GetProvinceIndex(GlobalVariables.selectCity) - 1;
			
			for (int i = 0; i < Data.yearCount; i++) {
				for (int j = 0; j < Data.yearCount; j++) {
					
					double tmp = 0;
					for(int k = 0; k < GlobalVariables.industryFlag.length; k++) {
						if(GlobalVariables.industryFlag[k]) {
							if(GlobalVariables.productOrProport == 0) {
								tmp += Common.getPow2Value(industry_prov[k][cityIndex][i], industry_prov[k][cityIndex][j]);
							}
							else if(GlobalVariables.productOrProport == 1) {
								tmp += Common.getPow2Value(percent[k][i][cityIndex], percent[k][j][cityIndex]);
							}
						}
					}
					oneProvAllYearData[i][j] = Math.sqrt(tmp);
				}
			}
		} else {
			for (int i = 0; i < Data.yearCount; i++) {
				for (int j = 0; j < Data.yearCount; j++) {
					oneProvAllYearData[i][j] = 0;
				}
			}
		}
	}
	
	public void getSimilarityMatrix_OneYearAllProv(int year) {
		//一个年份的不同省份 相似矩阵
		for (int i = 0; i < provinceCount; i++) {
			for (int j = 0; j < provinceCount; j++) {
				
				double tmp = 0;
				for(int k = 0; k < GlobalVariables.industryFlag.length; k++) {
					if(GlobalVariables.industryFlag[k]) {
						if(GlobalVariables.productOrProport == 0) {
							tmp += Common.getPow2Value(industry_year[k][year - Data.startYear][i], industry_year[k][year - Data.startYear][j]);
							//System.out.println(industry_prov[k][i][0]);
						}
						else if(GlobalVariables.productOrProport == 1) {
							tmp += Common.getPow2Value(percent[k][year - Data.startYear][i], percent[k][year - Data.startYear][j]);
						}
					}
				}
				oneYearAllProvData[i][j] = Math.sqrt(tmp);
				
			}
		}
	}
	
    public static void main(String arg[]) {
    	FrameMain frameChinaMap=new FrameMain();
    	frameChinaMap.addWindowListener(frameChinaMap);
    }
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
}
