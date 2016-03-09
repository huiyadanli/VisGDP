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
		//���ƽ���˾�GDP��ֵ
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1) continue;
				cnt++;
				aveData+=initData[i][j];
			}
		}
		aveData/=cnt; //ƽ���˾�GDP��ֵ
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{

				if (initData[i][j] == -1)
					colorData[i][j] = -1;
				//�˾�GDP��ƽ��ֵ�ģ�������ɫ->��ɫ �е�S����20->80��ӳ��
				/*if(initData[i][j]<aveData){
					colorData[i][j] = 1.0*l+1.0*(r-l)*(Math.log10(aveData)-Math.log10(initData[i][j]))
							/(Math.log10(aveData)-Math.log10(this.minData));
			    }
				else//�˾�GDP>=ƽ��ֵ�ģ����а�ɫ->��ɫ �е�S����20->80��ӳ��
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
