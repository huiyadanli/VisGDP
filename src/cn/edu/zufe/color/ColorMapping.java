package cn.edu.zufe.color;

/**
 * 颜色映射方案1
 */
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import cn.edu.zufe.mds.*;

public class ColorMapping
{
	double[][] initData = new double[32][63];
	public static double[][] colorData = new double[32][63];
	double maxData;
	double minData;

	public void setInitData()
	{
		// 初始化 数据
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				initData[i][j] = -1;
			}
		}
		for (int i = 0; i < Data.area.length; i++)
		{
			// 如果该省名称是中国，做归一化时跳过
			if (Data.area[i].province.equals("中国"))
				continue;
			for (int j = 0; j < Data.yearCount; j++) 
			{
				// 显示GDP
				initData[i][j] = Data.area[i].industry[0][j];
			}
		}
	}

	// 归一化到范围 [l,r]
	public void normalization(int l, int r)
	{
		findMaxAndMinValue();
		int cnt=0;
		/*double aveData=0;
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1) continue;
				cnt++;
				aveData+=initData[i][j];
			}
		}
		aveData/=cnt;*/
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1)
					colorData[i][j] = -1;
				/*if(initData[i][j]<aveData){
					colorData[i][j] = 1.0*l+1.0*(r-l)*(aveData-initData[i][j])
							/(aveData-minData);
			    }
				else
				{
					colorData[i][j] = 100+1.0*l+1.0*(r-l)*(initData[i][j]-aveData)
							/(this.maxData-aveData);
				}*/
				else
				{
					colorData[i][j] = (initData[i][j] - minData)
							/ (maxData - minData) * (r - l) + l;
				}
			}
		}
	}

	protected void findMaxAndMinValue()
	{
		boolean isFristNumber = true;
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1||initData[i][j]==0.0)
					continue;
				
				if (isFristNumber)
				{
					maxData = minData = initData[i][j];
					
					isFristNumber = false;
				} 
				else
				{
					if (maxData < initData[i][j])
						maxData = initData[i][j];
					if (minData > initData[i][j])
						minData = initData[i][j];
				}
			}
		}
		//System.out.println("最大值为："+this.maxData+" 最小值为："+this.minData);
	}
	public void transferColorToHsl(int l,int r)
	{
		double minx=0x3fffffff;
		double maxn=-1;
		for(int i=0;i<colorData.length;i++)
		{
			for(int j=0;j<colorData[i].length;j++)
			{
				if(colorData[i][j]<0) continue;
				maxn=Math.max(maxn, colorData[i][j]);
				minx=Math.min(minx, colorData[i][j]);
			}
		}
		double midn=0.5*(maxn+minx);
		for(int i=0;i<colorData.length;i++)
		{
			for(int j=0;j<colorData[i].length;j++)
			{
				if(colorData[i][j]<0) continue;
				if(colorData[i][j]<midn){
					colorData[i][j]=1.0*(colorData[i][j]-minx)/(midn-minx)*(r-l)+l;
					//colorData[i][j]=r-colorData[i][j];
			    }
				else
				{
					colorData[i][j]=1.0*(colorData[i][j]-midn)/(maxn-midn)*(r-l)+l;
					colorData[i][j]=r-colorData[i][j]+100;
				}
				//colorData[i][j]=1.0*(colorData[i][j]-minx)/(maxn-minx)*(r-l)+l;
				//colorData[i][j]=r-colorData[i][j];
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

	public double[] getDataFromColor(int colorValueL, int colorValueR)
	{
		double dataL = -1, dataR = -1;
		for (int colorIndex = colorValueR; colorIndex >= colorValueL; colorIndex--)
		{
			for (int i = 0; i < colorData.length; i++)
			{
				for (int j = 0; j < colorData[i].length; j++)
				{
					if (colorData[i][j] == -1)
						continue;
					if (colorData[i][j] >= colorValueL
							&& colorData[i][j] <= colorValueR)
					{
						if ((dataL > initData[i][j]) || (dataL == -1))
							dataL = initData[i][j];
						if (dataR < initData[i][j])
							dataR = initData[i][j];
					}
				}
			}
		}
		return new double[]
		{ dataL, dataR };
	}

	public double getMaxInitData()
	{
		double temMax = -1;
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1)
					continue;
				if (temMax < initData[i][j])
					temMax = initData[i][j];
			}
		}
		return temMax;
	}

	public double getMinInitData()
	{
		double temMin = -1;
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1)
					continue;
				if ((temMin > initData[i][j]) || (temMin == -1))
					temMin = initData[i][j];
			}
		}
		return temMin;
	}

	public double getCurrentInitData(int provinceIndex, int yearIndex)
	{
		return initData[provinceIndex][yearIndex];
	}

	public int getOneYearColorNumber(int year)
	{
		int[][][] colorNumber = new int[256][256][256];
		int sum = 0;
		for (int i = 1; i < colorData.length; i++)
		{
			int yearIndex = year - Data.startYear;
			if (yearIndex == -1)
				continue;
			if (colorData[i][yearIndex] == -1)
				continue;
			double temMax = this.getMaxInitData();
			double temMin = this.getMinInitData();
			double s = (initData[i][yearIndex] - temMin) / (temMax - temMin);
			s = s * 0.4 + 0.3;
			int[] temColor = HSLAndRGB.HSL2RGB(colorData[i][yearIndex], s, s);
			colorNumber[temColor[0]][temColor[1]][temColor[2]] = 1;
		}
		for (int i = 0; i < colorNumber.length; i++)
		{
			for (int j = 0; j < colorNumber[i].length; j++)
			{
				for (int t = 0; t < colorNumber[i][j].length; t++)
				{
					sum += colorNumber[i][j][t];
				}
			}
		}
		return sum;
	}
	
	/*
	//得到一年颜色的方差
	public double  getOneYearColorVariance(int year)
	{
		ArrayList temVariance=new ArrayList<>();
		for (int i = 1; i < colorData.length; i++)
		{
			int yearIndex = Data.area[i].getYearIndex(year);
			if (yearIndex == -1)
				continue;
			if (colorData[i][yearIndex] == -1)
				continue;
			temVariance.add(colorData[i][yearIndex]);
		}
		return calcVariance(temVariance);

	}

	public int getOneProvinceColorNumber(String provinceName)
	{
		ProvinceName pName = new ProvinceName();
		int[][][] colorNumber = new int[256][256][256];
		int provinceIndex = pName.GetProvinceIndex(provinceName);
		int sum = 0;
		if (provinceIndex < 0 || provinceIndex >= colorData.length)
			return -1;
		for (int j = 0; j < colorData[provinceIndex].length; j++)
		{
			if (colorData[provinceIndex][j] == -1)
				continue;
			double temMax = this.getMaxInitData();
			double temMin = this.getMinInitData();
			double s = (initData[provinceIndex][j] - temMin)
					/ (temMax - temMin);
			s = s * 0.4 + 0.3;
			int[] temColor = HSLAndRGB.HSL2RGB(colorData[provinceIndex][j], s,
					s);
			colorNumber[temColor[0]][temColor[1]][temColor[2]] = 1;
		}
		for (int i = 0; i < colorNumber.length; i++)
		{
			for (int j = 0; j < colorNumber[i].length; j++)
			{
				for (int t = 0; t < colorNumber[i][j].length; t++)
				{
					sum += colorNumber[i][j][t];
				}
			}
		}
		return sum;
	}


	public double getOneProvinceColorVariance(String provinceName)
	{
		ProvinceName pName = new ProvinceName();
		int provinceIndex = pName.GetProvinceIndex(provinceName);
		if (provinceIndex < 0 || provinceIndex >= colorData.length)
			return -1;
		ArrayList temVariance=new ArrayList<>();
		for (int j = 0; j < colorData[provinceIndex].length; j++)
		{
			if (colorData[provinceIndex][j] == -1)
				continue;
			temVariance.add(colorData[provinceIndex][j]);
		}
		return calcVariance(temVariance);
	}

	// 求方差
	public double calcVariance(ArrayList arr)
	{
		double ave = 0;
		for (int i = 0; i < arr.size(); i++)
		{
			ave += (double)arr.get(i);
		}
		ave /= arr.size();
		//System.out.println("The Average Value is = "+ave);
		double var = 0;
		for (int i = 0; i < arr.size(); i++)
		{
			double tmp=(double)arr.get(i);
			var = var + (tmp-ave) * (tmp - ave);
		}
		var /= arr.size();
		return var;
	}
	*/
}
