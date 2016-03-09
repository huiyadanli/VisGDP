package cn.edu.zufe.mds;

public class Common {
	public static double getMaxValue(double[] value) {
		if (value.length <= 0)
			return -1;
		double maxnValue = value[0];
		for (int i = 1; i < value.length; i++) {
			maxnValue = Math.max(value[i], maxnValue);
		}
		return maxnValue;
	}

	public static double getMinValue(double[] value) {
		if (value.length <= 0)
			return -1;
		double minValue = value[0];
		for (int i = 0; i < value.length; i++) {
			minValue = Math.min(value[i], minValue);
		}
		return minValue;
	}

	public static float getMaxValue(float[] value) {
		if (value.length <= 0)
			return -1;
		float maxnValue = value[0];
		for (int i = 1; i < value.length; i++) {
			maxnValue = Math.max(value[i], maxnValue);
		}
		return maxnValue;
	}

	public static float getMinValue(float[] value) {
		if (value.length <= 0)
			return -1;
		float minValue = value[0];
		for (int i = 0; i < value.length; i++) {
			minValue = Math.min(value[i], minValue);
		}
		return minValue;
	}

	public static double getPow2Value(double value1, double value2) {
		return (value1 - value2) * (value1 - value2);
	}
}
