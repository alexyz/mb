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
		set(Math.log(getAbs()), Math.atan2(im, re));
	}
	
	public final void exp () {
		double er = Math.exp(re);
		set(er * Math.cos(im), er * Math.sin(im));
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
	
	public final void mul (Complex c) {
		set(re * c.re - im * c.im, im * c.re + re * c.im);
	}
	
	public final void mul (double re2, double im2) {
		set(re * re2 - im * im2, im * re2 + re * im2);
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
	
	@Override
	public String toString () {
		return String.format("%f%+fi", re, im);
	}

}
