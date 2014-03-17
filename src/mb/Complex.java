package mb;

/**
 * it's a complex number type
 */
final class Complex {
	public double re, im;
	
	public Complex (double r, double i) {
		this.re = r;
		this.im = i;
	}
	
	public Complex () {
		//
	}
	
	public Complex (Complex c) {
		this.re = c.re;
		this.im = c.im;
	}
	
	public final void log () {
		double re2 = Math.log(getAbs());
		double im2 = Math.atan2(im, re);
		re = re2;
		im = im2;
	}
	
	public final void exp () {
		double er = Math.exp(re);
		double re2 = er * Math.cos(im);
		double im2 = er * Math.sin(im);
		re = re2;
		im = im2;
	}
	
	public final void smul (double x, double y) {
		re *= x;
		im *= y;
	}
	
	public final void conj () {
		im = -im;
	}
	
	public final void pow (double n) {
		log();
		smul(n);
		exp();
	}
	
	/** very slow */
	public double getAbs () {
		return Math.hypot(re, im);
	}
	
	public double getAbsSq () {
		return re * re + im * im;
	}
	
	public final void set (Complex c) {
		this.re = c.re;
		this.im = c.im;
	}
	
	public final void set (double r, double i) {
		this.re = r;
		this.im = i;
	}
	
	public final void sq () {
		double re2 = re * re - im * im;
		double im2 = 2.0 * re * im;
		re = re2;
		im = im2;
	}
	
	public final void mul (Complex c) {
		double re2 = re * c.re - im * c.im;
		double im2 = im * c.re + re * c.im;
		re = re2;
		im = im2;
	}
	
	public final void mul (double re2, double im2) {
		double re3 = re * re2 - im * im2;
		double im3 = im * re2 + re * im2;
		re = re3;
		im = im3;
	}
	
	public final void smul (double s) {
		re *= s;
		im *= s;
	}
	
	public final void sdiv (double x, double y) {
		re /= x;
		im /= y;
	}
	
	public final void add (Complex c) {
		re += c.re;
		im += c.im;
	}
	
	public final void add (double re2, double im2) {
		re += re2;
		im += im2;
	}
	
	public final void sub (Complex c) {
		re -= c.re;
		im -= c.im;
	}
	
	public final void sin () {
		double re2 = Math.sin(re) * Math.cosh(im);
		double im2 = Math.cos(re) * Math.sinh(im);
		re = re2;
		im = im2;
	}
	
	@Override
	public String toString () {
		return String.format("%f%+fi", re, im);
	}

}
