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
	
	PAppletChinaMap pChinaMap = new PAppletChinaMap(this); //processing�й���ͼ����(��һ����)
	PAppletMDS pMDS = new PAppletMDS(); //��άMDS(�ڶ�����)
	//PAppletISMDS pISMDS = new PAppletISMDS(); //ʱ�ջ�Ͽ��ӻ�����
//	PAppletStoryFlow pStory = new PAppletStoryFlow();//(��������)
	PAppletRiver pRiver = new PAppletRiver();//(��������_�ع�)

	Language lan = new Language();
	int w,h; 
	
	JPanel centerPanel = new JPanel();
	JPanel mapPanel = new JPanel();
	JPanel mdsPanel = new JPanel();
	JLabel labelYear = new JLabel(lan.year + Data.startYear);
	JLabel labelThreshold = new JLabel(lan.clusterthreshold + "50");
	JCheckBox[] checkBox = new JCheckBox[12];;
	JRadioButton rbtnProduction;// �鿴�����ֵ
	JRadioButton rbtnProportion;// �鿴ռ��
	JRadioButton rbtnNone,rbtnOne,rbtnTwo;  //����ʡ��
	JRadioButton rbtnOneYearAllProv,rbtnOneProvAllYear; //��ǰ��ݸ���ʡ�ݵĲ��죬��ǰʡ���������Ĳ���
	JRadioButton rbtnSortBySize,rbtnSortByPerCapitaGDP,rbtnSortByGDP;//�������ڵ�StoryLine����ʽ
	JRadioButton rbtnPoint, rbtnPie; //��ͼ����ͼ
	JSlider sliderYear,sliderThreshold; //������
	
	Data data = new Data();
	public static int provinceCount=Data.provinceCount-1; //ȥ�����й�����-1
	public static double[][][] industry = new double[Data.industryCount][Data.yearCount][provinceCount];
	public static double[][][] percent = new double[Data.industryCount][Data.yearCount][provinceCount];
	public static double[][][] industry_year = new double[Data.industryCount][Data.yearCount][provinceCount]; //һ���ڹ�һ��
	public static double[][][] industry_prov = new double[Data.industryCount][provinceCount][Data.yearCount]; //һʡ�ڹ�һ��
	double[][][] allYearAllProvData = new double[Data.yearCount][provinceCount][provinceCount];//������ݵ�����ʡ�� ���ƾ���
	double[][] oneProvAllYearData = new double[Data.yearCount][Data.yearCount];//һ��ʡ�ݵĲ�ͬ��� ���ƾ���
	double[][] oneYearAllProvData = new double[provinceCount][provinceCount];//һ��ʡ�ݵĲ�ͬ��� ���ƾ���
	
    public FrameMain(){
    	initData();
    	init();
    	addMenu();
    	//Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    	this.setSize(1650,1000);
		this.setVisible(true);
		this.setLayout(null);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	//this.setExtendedState(JFrame.MAXIMIZED_BOTH);  //���
    	
    	menuItemColorMappingStyle1.addActionListener(this);//��ɫӳ�����¼����
		menuItemColorMappingStyle2.addActionListener(this);
		menuItemColorMappingStyle3.addActionListener(this);
		menuItemColorMappingStyle4.addActionListener(this);
    }
    public void addMenu(){
    	menuBar = new JMenuBar();
    	menuColorStyle = new JMenu("��ɫӳ�䷽��ѡ��");
    	menuItemColorMappingStyle1 = new JMenuItem("ֱ��ӳ��");
		menuItemColorMappingStyle2 = new JMenuItem("ֱ��ͼ���⻯ӳ��");
		menuItemColorMappingStyle3 =new JMenuItem("Logȡ����ӳ��");
		menuItemColorMappingStyle4 =new JMenuItem("��ɫӳ�䷽����");
		menuColorStyle.add(menuItemColorMappingStyle1);
		menuColorStyle.add(menuItemColorMappingStyle2);
		menuColorStyle.add(menuItemColorMappingStyle3);
		menuColorStyle.add(menuItemColorMappingStyle4);
		menuBar.add(menuColorStyle);

		this.setJMenuBar(menuBar);
    }
    @Override	//������ɫӳ�䷽���л�
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

    	//�й���ͼ + ��ݻ�����
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
    	
		//��άMDS + ��ֵ������
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
		//ʱ�ջ�Ͽ��ӻ�����
		pISMDS.setPreferredSize(new Dimension(PAppletISMDS.yearLen*Data.yearCount, 300));
		centerPanel.add(pISMDS);
		pISMDS.init();
		pISMDS.start();
		*/
		
		//�����߻������µ�������
//		pStory.setPreferredSize(new Dimension(PAppletStoryFlow.yearLen * Data.yearCount, 400));
//		centerPanel.add(pStory);
//		pStory.init();
//		pStory.start();
		//���������ĵ������� //�����޸�ΪԲ
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
		
		//Ϊ���������ؼ�
		JPanel panel = new JPanel(); //����һ�����
		panel.setPreferredSize(new Dimension(170, this.HEIGHT));
		panel.setBackground(Color.WHITE);
		// �����ֵ��ռ��
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
		// ��ѡ�����ƶȵ�ѡ����
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


		panel.add(rbtnProduction);//����м���ؼ�
		panel.add(rbtnProportion);
		for (int i = 0; i < checkBox.length; i++) {
			if(i == 7) {
				continue; //ס�޺Ͳ���ҵ����ʾ
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

			checkBox[i].setBackground(Color.WHITE); //˳��ĸ���ɫ
			checkBox[i].addItemListener(itemListener);
			panel.add(checkBox[i]);
		}
		//checkBox[0].setSelected(true);
		//�ָ���
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
		//1,2��ʡ��ѡ��
		ButtonGroup bGroup2 = new ButtonGroup();
		rbtnNone = new JRadioButton(lan.alone);
		rbtnOne = new JRadioButton(lan.neighborhood);
		rbtnTwo = new JRadioButton("����ʡ��");
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
		//�ָ���2
		JLabel j2 = new JLabel("________________________");
		j2.setPreferredSize(new Dimension(170, 30));
		panel.add(j2);

		ButtonGroup bGroup3 = new ButtonGroup();
		rbtnOneYearAllProv = new JRadioButton("�鿴һ��");
		rbtnOneProvAllYear = new JRadioButton("�鿴һʡ");
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
		
		//�ָ���3
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
		//��ť
		btnMDS = new JButton("OK");
		btnMDS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		    	//����MDS����
				frameIS = new FrameISMDS();
				frameIS.setVisible(true);
			}
		});
		btnMDS.setBounds(1206, 132, 49, 27);
		panel.add(btnMDS);
		*/
		this.add(panel,BorderLayout.EAST);
		//�ı�ؼ���ɫ(JRadioButton)
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
    	//�õ�����
		for(int i=0;i<provinceCount;i++) {
			for(int j=0;j<Data.industryCount;j++) {
				for(int k=0;k<Data.yearCount;k++) {
					industry[j][k][i] = Data.area[i+1].industry[j][k];
					percent[j][k][i] = Data.area[i+1].percent[j][k];
				}
			}
		}
		//����ڹ�һ��
		for (int k = 0; k < industry.length; k++) {
			for (int i = 0; i < industry[k].length; i++) {
				double max = Common.getMaxValue(industry[k][i]);
				double min = Common.getMinValue(industry[k][i]);
	
				for (int j = 0; j < industry[k][i].length; j++) {
					industry_year[k][i][j] = (industry[k][i][j] - min) / (max - min);
				}
			}
		}
		//һ��ʡ�ڹ�һ��
		//ͼ����ת����������
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
		//������ݵ�����ʡ�� ���ƾ���
		for (int i = 0; i < industry_year[0].length; i++) {
			for (int j = 0; j < industry_year[0][i].length; j++) {
				for (int t = 0; t < industry_year[0][i].length; t++) {
					//�������ƾ���
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
		//һ����ݵĲ�ͬʡ�� ���ƾ���
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
