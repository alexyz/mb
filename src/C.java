
final class C {
	public double r, i;

	public C(double r, double i) {
		this.r = r;
		this.i = i;
	}

	public C() {
		//
	}
	
	public C(C c) {
		this.r = c.r;
		this.i = c.i;
	}
	
	public void log() {
		set(Math.log(getAbs()), Math.atan2(i, r));
	}
	
	public void exp() {
		double er = Math.exp(r);
		set(er * Math.cos(i), er * Math.sin(i));
	}
	
	@Deprecated
	public void setxy (C origin, C size, int width, int height, int x, int y) {
		r = origin.r + ((size.r * x) / width);
		i = origin.i + ((size.i * y) / height);
	}
	
	public void scale(int x, int y, int w, int h) {
		r = (r * x) / w;
		i = (i * y) / h;
	}

	public void conj() {
		i = -i;
	}
	
	public void pow(double n) {
		log();
		mul(n);
		exp();
	}

	@Deprecated
	public double getAbs() {
		return Math.hypot(r, i);
	}
	
	public double getAbsSq() {
		return r * r + i * i;
	}

	public void set(C c) {
		this.r = c.r;
		this.i = c.i;
	}

	public void set(double r, double i) {
		this.r = r;
		this.i = i;
	}

	public void mul(C c) {
		set(r * c.r - i * c.i, i * c.r + r * c.i);
	}
	
	public void mul(double s) {
		r *= s;
		i *= s;
	}
	
	public void scale(double x, double y) {
		r *= x;
		i *= y;
	}

	public void add(C c) {
		r += c.r;
		i += c.i;
	}

	public void sub(C c) {
		r -= c.r;
		i -= c.i;
	}

	public String toString() {
		if (i >= 0) {
			return String.format("%f+%fi", r, i);
		} else {
			return String.format("%f-%fi", r, -i);
		}
	}
}