package cn.edu.zufe.color;
/**
 * hsl×ª»»Îªrgb
 * @author Gavin
 *
 */
public class HSLAndRGB {
	public static int[] HSL2RGB(double h, double s, double l) {
		int r, g, b;
		int[] ret = new int[3];
		int max, min;
		double h1 = h;

		if (l > 0.5) {
			max = double2int((1.0 - (1.0 - l) * (1.0 - s)) * 255);
			min = double2int(2 * 255 * l) - max;
		} else {
			min = double2int((l * (1.0 - s)) * 255);
			max = double2int(2 * 255 * l) - min;
		}

		if (h <= 60.0) {
			h1 /= 60;
			b = min;
			r = max;
			g = double2int(h1 * (r - b) + b);
		} else if (h > 60.0 && h <= 120.0) {
			h1 /= 60;
			h1 -= 2.0;
			b = min;
			g = max;
			r = double2int(-h1 * (g - b) + b);
		} else if (h > 120.0 && h <= 180.0) {
			h1 /= 60;
			h1 -= 2.0;
			r = min;
			g = max;
			b = double2int(h1 * (g - r) + r);
		} else if (h > 180.0 && h <= 240.0) {
			h1 /= 60;
			h1 -= 4.0;
			r = min;
			b = max;
			g = double2int(-h1 * (b - r) + r);
		} else if (h > 240.0 && h <= 300.0) {
			h1 /= 60;
			h1 -= 4.0;
			g = min;
			b = max;
			r = double2int(h1 * (b - g) + g);
		} else {
			h1 /= 60;
			h1 -= 6.0;
			g = min;
			r = max;
			b = double2int(-h1 * (r - g) + g);
		}

		ret[0] = r;
		ret[1] = g;
		ret[2] = b;

		return ret;
	}

	public static int double2int(double d) {
		double mod = d;
		int ret;

		if (d < 0.0) {
			mod *= -1;
		}

		double error = mod - (int) mod;

		if (0.5 > error) {
			ret = (int) mod;
		} else {
			ret = (int) mod + 1;
		}

		if (d < 0.0) {
			ret *= -1;
		}

		return ret;

	}
	
	public static double doubleWithoutInt(double d){
		return d-(int)d;
	}
}
