package cn.edu.zufe.color;

/**
 * ��ɫӳ�䷽��1 --- ֱ��ӳ��
 */
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class DirectColorMapping extends ColorMapping
{
	public  double[][] colorData = new double[32][63];

	public void normalization(int l,int r)
	{	
		findMaxAndMinValue();
		
		for (int i = 0; i < initData.length; i++)
		{
			for (int j = 0; j < initData[i].length; j++)
			{
				if (initData[i][j] == -1.0 || initData[i][j] == 0.0)
					colorData[i][j] = -1;
				else
				{
					colorData[i][j] = 1.0*(initData[i][j] - minData)/ (maxData - minData) * (r - l) + l;
					//System.out.println("���: "+j+"ʡ��: "+i +"   :"+colorData[i][j]);
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
