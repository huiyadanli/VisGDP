package cn.edu.zufe.color;

import java.util.ArrayList;
/**
 * 颜色映射方案3
 */
import java.util.Arrays;

import cn.edu.zufe.mds.GlobalVariables;

public class BinColorMapping extends ColorMapping
{
	public  double[][] HisColorData = new double[32][63];
	 
	public  double [][] tmpColorData = new double[32][63];
    public  int[] c = new int[256];
    public  int[] n = new int[256];
    public static ArrayList econoValue = new ArrayList<>();
    public 	int[] tmpTimes = new int[256];
    public 	int[] newTimes = new int[256];
		//直方图均衡化 
	public void normalization(int l,int r)
	{	
		findMaxAndMinValue();
		int sum=0;					//数据的初始化
		for(int i=l; i<=r; i++){ 
			tmpTimes[i]=0;	
			newTimes[i]=0;		
		}
		
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
					continue;
				else
				{
					int h=(int) colorData[i][j];
					colorData[i][j] = 1.0*histogram[h]+l;
					newTimes[ (int)colorData[i][j] ]++;			//直方图均衡化颜色映射之后的数据分布
				}
			}
		}
		
		
		int ll=0,rr=0;	//ll，rr为直方图每部分的左区间和右区间(左闭右开)
		for(int t=l; t<=r; t++){	//获得第一个左区间
			if(newTimes[t]!=0)
			{
				ll=t;
				break;
			}
		}
		boolean flag=true;	
								
		while(flag)		//对直方图区间进行log映射-----核心算法
		{
			for(int t=ll; t<=r; t++)	//寻找右区间
			{
				if(newTimes[t]!=0 && t-ll>1){	//t-ll>1 因为区间是左开右闭的
					rr=t;
					break;
				}
			}
			
			if(ll>=rr) break;	//左右区间重合，退出循环
			double dpMinData=0x3f3f3f3f ,dpMaxData=0;//获得区间最大最小值
			
			for (int i = 0; i < initData.length; i++)
			{
				for (int j = 0; j < initData[i].length; j++)
				{		
					if(colorData[i][j] == -1) continue;		//此时的colorData数组存储的是直方图映射之后的H值
					else
					{
						if(colorData[i][j] >= ll && colorData[i][j] <rr){	//如果colorData[i][j]存在于左开右闭区间之中
						dpMinData = Math.min(dpMinData, initData[i][j]);
						dpMaxData = Math.max(dpMaxData, initData[i][j]);
						}
					}
				}
			}
					
			//double Y=(dpMaxData - dpMinData) * GlobalVariables.N;	
			double Y=(dpMaxData - dpMinData) * GlobalVariables.N;	
					
			for (int i = 0; i < initData.length; i++)		//直方图区间取log
			{
				for (int j = 0; j < initData[i].length; j++)
				{		
					if(colorData[i][j] == -1) continue;
						else
						{
							//对于用直方图思想映射之后的位于[ll,rr)区间的原始数据，通过log方法映射到[ll,rr-1]这段区间内
							if(colorData[i][j] >= ll && colorData[i][j] <rr){	
								
							colorData[i][j] =ll+ 1.0*(rr-1-ll)*(Math.log10(initData[i][j]+Y)-
									Math.log10(dpMinData+Y))/(Math.log10(dpMaxData+Y)-Math.log10(dpMinData+Y)) ;
						}
					}
				}
			}
			
			ll=rr;
		}
	}

	
	public double getColorData(int provinceIndex, int year)
	{
		if (provinceIndex == -1 || year == -1)
			return -1;
		else
			return colorData[provinceIndex][year];
	}
	/*public int tmpMaxn, tmpMinx;
	public static int times2[] = new int[256];
	int T=0;
	public void normalization(int l, int r)
	{
		this.setInitData();
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
				}
			}
		}
		getHistoBins(this.minData,this.maxData,l,r);
		//System.out.println("Times = "+T);
		times2 = getNewHistogram(times2, l, r);
	}

	/*
	 * Computed by Zhuke on 2015/3/16
	 
	
	public void Equalization(int arr[],int l,int r,double minx,double maxn)
	{
		HistogramEqualization hist = new HistogramEqualization();
		int newData[]=new int[arr.length];
	    newData = hist.getColorNum(arr,l,r,minx,maxn);
		
		double tmpMinx=0x3fffffff,tmpMaxn=-1;
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{	
				if(initData[i][j]==-1||initData[i][j]==0) continue;
				if(initData[i][j]<minx||initData[i][j]>maxn) continue;
				tmpMinx=Math.min(tmpMinx, initData[i][j]);
				tmpMaxn=Math.max(tmpMaxn, initData[i][j]);
			}
		}
		//System.out.println("在均衡化时候的最大值和最小值分别为"+tmpMaxn+"    !     "+tmpMinx);
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{	
				if(initData[i][j]==-1||initData[i][j]==0) continue;
				if(initData[i][j]<minx||initData[i][j]>maxn) continue;
				int tmp = (int) (1.0 * (r-l)* (initData[i][j] - tmpMinx) / (tmpMaxn - tmpMinx));
				colorData[i][j] =newData[tmp] + l;
			}
		}
	}
	
	public void getExtremeValues(int l,int r)
	{
		tmpMaxn = -1;
		tmpMinx = 0x3fffffff;
		for (int i = 0; i < colorData.length; i++)
			for (int j = 0; j < colorData[i].length; j++)
			{
				if(colorData[i][j]==-1) continue;
				if(colorData[i][j]<l||colorData[i][j]>r) continue;
				tmpMaxn = (int) Math.max(tmpMaxn, colorData[i][j]);
				tmpMinx = (int) Math.min(tmpMinx, colorData[i][j]);
			}
	}

	public int[] getNewHistogram(int arr[], int l, int r)
	{
		for (int i = 0; i < arr.length; i++)
			arr[i] = 0;
		getExtremeValues(l,r);
		//System.out.println("MaxColor is = "+tmpMaxn+" MinColor is = "+tmpMinx);
		for (int i = 0; i < colorData.length; i++)
		{
			for (int j = 0; j < colorData[i].length; j++)
			{
				if(colorData[i][j]==-1) continue;
				if(colorData[i][j]<l||colorData[i][j]>r) continue;
				int tmp = (int) (1.0 * (r-l) * (colorData[i][j] - tmpMinx) / (tmpMaxn - tmpMinx));
				try{
					arr[tmp]++;
				}
				catch(Exception e)
				{
					//System.out.println("!!!!!!!!! tmp = "+tmp);
				}
			}
		}
		return arr;
	}

	public void getHistoBins( double minx,double maxn,int l, int r)
	{
		//System.out.println("This.maxData is = "+maxn);
		//System.out.println("This.minData is = "+minx);
		//System.out.println("This.LeftColor is = "+l);
		//System.out.println("This.RightColor is = "+r);
		if(maxn<=minx||r-l<=0) 	
		{
			if(r-l<=0&&maxn>minx)
			{
				for(int i=0;i<colorData.length;i++)
				{
					for(int j=0;j<colorData[i].length;j++)
					{
						double value=colorData[i][j];
						if(value<=maxn&&value>=minx)
							colorData[i][j]=r;
					}
				}
			}
			//System.out.println("返回上层！");
			return;
		}
		T+=1;
		double tempMaxn = -1, tempMinx = 0x3fffffff;
		tempMaxn=maxn;
		tempMinx=minx;
		int tmpBin[] = new int[r-l+1];//一开始的直方图
		for (int i = 0; i <=r-l; i++)
			tmpBin[i] = 0;
		// 得到当前直方图的分布值情况。
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if(initData[i][j]==-1) continue;
				if (initData[i][j] <= maxn&&initData[i][j]>=minx)
				{
					int tmpInt = (int) ((r-l) * (initData[i][j] - tempMinx) / (tempMaxn - tempMinx));
					try
					{
						tmpBin[tmpInt]++;
					} 
					catch (Exception e)
					{
						System.out
								.println("ArrayIndexOfOutBoundException! tmpBin["
										+ tmpInt + "] = " + tmpBin[tmpInt]);
					}
				}
			}
		}
		int sum = 0;
		for (int i = 0; i<=r-l; i++)
			sum += tmpBin[i];
		//System.out.println("Sum = "+sum);
		if (sum!=0&&tmpBin[0] > sum *GlobalVariables.Threshold&&(sum *GlobalVariables.Threshold)!=0)
		{
			//System.out.println("进入细分 "+"Sum = "+sum+"  Bin[0] = "+tmpBin[0]);
			tempMaxn=minx+(maxn-minx)/(r-l);
			tempMinx=minx;
			setColor(tempMinx,tempMaxn,l,(int)(l+(r-l)*GlobalVariables.Variance-1));
			setColor(tempMaxn+1,maxn,(int)(l+(r-l)*GlobalVariables.Variance),r);
			try
			{
				getHistoBins(tempMinx,tempMaxn,l,(int)(l+(r-l)*GlobalVariables.Variance-1));
			}
			catch(Exception e)
			{
				System.out.println("!!!!!!!!"+e.toString());
			}
			try
			{
				getHistoBins(tempMaxn+1,maxn,(int)(l+(r-l)*GlobalVariables.Variance),r);
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
		}
		else 
		{
			 Equalization(tmpBin,l,r,minx,maxn);
			// System.out.println("Not fitted ,return !");
			 return;
		}
	}

	public void setColor(double minx,double maxn, int l, int r)
	{
		double tempMaxn=maxn;
		double tempMinx=minx;
		for(int i=0;i<initData.length;i++)
		{
			for(int j=0;j<initData[i].length;j++)
			{
				if(initData[i][j]==-1) continue;
				if(initData[i][j]<=tempMaxn&&initData[i][j]>=tempMinx)
				{
					colorData[i][j]=(initData[i][j]-tempMinx)/(tempMaxn-tempMinx)*(r-l)+l;
				}
			}
		}
	}*/
}