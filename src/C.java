
class C {
	public double r, i;

	public C(double r, double i) {
		this.r = r;
		this.i = i;
	}

	public C() {
		//
	}
	
	public void log() {
		set(Math.log(abs()), Math.atan2(i, r));
	}
	
	public void exp() {
		double er = Math.exp(r);
		set(er * Math.cos(i), er * Math.sin(i));
	}
	
	public void setxy(C origin, C size, int width, int height, int x, int y) {
		r = origin.r + ((size.r * x) / width);
		i = origin.i + ((size.i * y) / height);
	}

	public void conj() {
		i = -i;
	}
	
	public void pow(double n) {
		log();
		muls(n);
		exp();
	}

	public double abs() {
		return Math.hypot(r, i);
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
	
	public void muls(double s) {
		r *= s;
		i *= s;
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