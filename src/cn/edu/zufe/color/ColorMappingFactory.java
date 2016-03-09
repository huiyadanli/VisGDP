package cn.edu.zufe.color;

/**
 * 颜色映射工厂，运用工厂模式，得到具体的颜色映射方案
 * 
 * @author Gavin
 * 
 */
public class ColorMappingFactory {
	public ColorMapping getColorMappingStyle(int type) {
		/*if (type == 1)
			return new ColorMapping();
		else if (type == 3)
			return new BinColorMapping();
		else if(type==2)
			return new HistogramEqualizationColorMapping();
		else if(type==4)
			return new LogColorMapping();
		
		else return null;*/
		
		if (type == 1)
			return new DirectColorMapping();
		else if(type==2)
			return new HistogramEqualizationColorMapping();
		else if(type==3)
			return new LogColorMapping();
		else if (type == 4)
			return new BinColorMapping();
		else return null;
	}
}
