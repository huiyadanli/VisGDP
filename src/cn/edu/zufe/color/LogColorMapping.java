package cn.edu.zufe.color;

import cn.edu.zufe.mds.GlobalVariables;

//import colormapping.HistoData;

public class LogColorMapping extends ColorMapping
{
	public void normalization(int l, int r)
	{
		findMaxAndMinValue();

		double Y = GlobalVariables.N * (this.maxData - this.minData);
		double aveData=0;
		int cnt=0;
		//求得平均人均GDP数值
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1) continue;
				cnt++;
				aveData+=initData[i][j];
			}
		}
		aveData/=cnt; //平均人均GDP数值
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{

				if (initData[i][j] == -1)
					colorData[i][j] = -1;
				//人均GDP＜平均值的，进行蓝色->白色 中的S进行20->80的映射
				/*if(initData[i][j]<aveData){
					colorData[i][j] = 1.0*l+1.0*(r-l)*(Math.log10(aveData)-Math.log10(initData[i][j]))
							/(Math.log10(aveData)-Math.log10(this.minData));
			    }
				else//人均GDP>=平均值的，进行白色->红色 中的S进行20->80的映射
				{
					colorData[i][j] = 100+1.0*l+1.0*(r-l)*(Math.log10(initData[i][j])-Math.log10(aveData))
							/(Math.log10(this.maxData)-Math.log10(aveData));
				}*/
				else
				{
					//colorData[i][j] = r-1.0*(r-l)*(Math.log10(initData[i][j])-Math.log10(this.minData))/(Math.log10(this.maxData)-Math.log10(this.minData));
					colorData[i][j] = l+1.0*(r-l)*(Math.log10(initData[i][j]+Y)-Math.log10(this.minData+Y))/(Math.log10(this.maxData+Y)-Math.log10(this.minData+Y));
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
}
