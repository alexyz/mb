package pc;

import java.awt.Color;

abstract class CF {
	double validate(double v) {
		if (v < -1.0 || v > 1.0) {
			throw new RuntimeException("v=" + v);
		}
		return v;
	}
	abstract void get(CR r, double p);
}

class CR {
	public double x, y, dx, dy, w;
	public Color c;
}