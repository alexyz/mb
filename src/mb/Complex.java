package mb;

/**
 * it's a complex number type
 */
final class Complex {
	public double r, i;
	
	public Complex (double r, double i) {
		this.r = r;
		this.i = i;
	}
	
	public Complex () {
		//
	}
	
	public Complex (Complex c) {
		this.r = c.r;
		this.i = c.i;
	}
	
	public final void log () {
		set(Math.log(getAbs()), Math.atan2(i, r));
	}
	
	public final void exp () {
		double er = Math.exp(r);
		set(er * Math.cos(i), er * Math.sin(i));
	}
	
	public final void scale (int x, int y, int w, int h) {
		r = (r * x) / w;
		i = (i * y) / h;
	}
	
	public final void conj () {
		i = -i;
	}
	
	public final void pow (double n) {
		log();
		mul(n);
		exp();
	}
	
	public double getAbs () {
		return Math.hypot(r, i);
	}
	
	public double getAbsSq () {
		return r * r + i * i;
	}
	
	public final void set (Complex c) {
		this.r = c.r;
		this.i = c.i;
	}
	
	public final void set (double r, double i) {
		this.r = r;
		this.i = i;
	}
	
	public final void mul (Complex c) {
		set(r * c.r - i * c.i, i * c.r + r * c.i);
	}
	
	public final void mul (double s) {
		r *= s;
		i *= s;
	}
	
	public final void scale (double x, double y) {
		r *= x;
		i *= y;
	}
	
	public final void add (Complex c) {
		r += c.r;
		i += c.i;
	}
	
	public final void sub (Complex c) {
		r -= c.r;
		i -= c.i;
	}
	
	@Override
	public String toString () {
		return String.format("%f%+fi", r, i);
	}
	
}
