package cn.edu.zufe.color;
import java.awt.Color;
import java.awt.Graphics;

public class HistogramEqualizationColorMapping extends ColorMapping
{
	public  double[][] HisColorData = new double[32][63];
	 
    public  int[] c = new int[256];
    public  int[] n = new int[256];
		//直方图均衡化 
	public void normalization(int l,int r)
	{	
		int[] tmpTimes = new int[256];
		findMaxAndMinValue();
		int sum=0;
		for(int i=l; i<=r; i++) tmpTimes[i]=0;	
		for (int i = 0; i < initData.length; i++)		//直接映射
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1.0 || initData[i][j] == 0.0)
					colorData[i][j] = -1;
				else
				{
					colorData[i][j] = 1.0*(initData[i][j] - minData)/ (maxData - minData) * (r - l) + l;
					int h = (int)colorData[i][j];
					tmpTimes[h]++;
					//System.out.println("年份: "+j+"省份: "+i +"   :"+colorData[i][j]);
					sum++;
				}
			}
		}
		
		c[0] = tmpTimes[0];			//直方图均衡化累计归一化思想
		for(int i=l; i<=r; i++){
			if(i==0) continue;
			c[i] = c[i-1]+tmpTimes[i];
		}
		int maxn = 0,minx = 0x3f3f3f3f; 
		for(int i=l; i<=r; i++){
			if(c[i]==0) continue;
			maxn = Math.max(maxn, c[i]);
			minx = Math.min(minx, c[i]);
		}
		
		int histogram[] = new int[256];		
		for(int i=l; i<=r; i++){
			if(c[i] == 0) continue;
			histogram[i] = (int)(1.0*(c[i]-minx)/(sum-minx)*(r-l));
		}
		
		//直方图均衡化
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1.0 || initData[i][j] == 0.0)
					colorData[i][j] = -1;
				else
				{
					int h=(int) colorData[i][j];

					colorData[i][j]=1.0*histogram[h]+l;
				}
			}
		}
	}
	public double getColorData(int provinceIndex, int year)
	{
		if (provinceIndex == -1 || year == -1)
			return -1;
		else
			return colorData[provinceIndex][year];
	}
	/*HistogramEqualization hisEqu=new HistogramEqualization();
	public  int histogram[];
	public void getData()
	{
		double tmpMinx=0x3fffffff,tmpMaxn=-1;
		for(int i=0;i<HistoData.econoValue.size();i++)
		{
			double value=(double)HistoData.econoValue.get(i);
			tmpMinx=Math.min(value,tmpMinx);
			tmpMaxn=Math.max(value, tmpMaxn);
		}
		int newTimes[]=new int[256];
		 newTimes=hisEqu.getNewTimes(HistoData.times,0,255,tmpMinx,tmpMaxn);
		histogram=hisEqu.newData;
	}
	
	public void normalization(int l, int r)
	{
		getData();
		findMaxAndMinValue();
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1)
					colorData[i][j] = -1;
				else
				{
					colorData[i][j] = (initData[i][j] - minData)
							/ (maxData - minData) * (r - l) + l;
					int tmp=(int)((initData[i][j] - minData)
							/ (maxData - minData)*255);
						
					colorData[i][j]=1.0*histogram[tmp]*(r-l)/255+l;
				}
			}
		}
	}*/
}
