package mb;

/**
 * it's a complex number type
 */
final class Complex {
	public double re, im;
	
	public Complex (final double r, final double i) {
		re = r;
		im = i;
	}
	
	public Complex () {
		//
	}
	
	public Complex (final Complex c) {
		re = c.re;
		im = c.im;
	}
	
	public final void log () {
		final double re2 = Math.log(getAbs());
		final double im2 = Math.atan2(im, re);
		re = re2;
		im = im2;
	}
	
	public final void exp () {
		final double er = Math.exp(re);
		final double re2 = er * Math.cos(im);
		final double im2 = er * Math.sin(im);
		re = re2;
		im = im2;
	}
	
	public final void smul (final double x, final double y) {
		re *= x;
		im *= y;
	}
	
	public final void conj () {
		im = -im;
	}
	
	public final void pow (final double n) {
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
	
	public final void set (final Complex c) {
		re = c.re;
		im = c.im;
	}
	
	public final void set (final double r, final double i) {
		re = r;
		im = i;
	}
	
	public final void sq () {
		final double re2 = re * re - im * im;
		final double im2 = 2.0 * re * im;
		re = re2;
		im = im2;
	}
	
	public final void cu () {
		// aaa - 3abb + i(3aab - bbb)
		final double re2 = re * re * re - 3 * re * im * im;
		final double im2 = 3 * re * re * im - im * im * im;
		re = re2;
		im = im2;
	}
	
	public final void mul (final Complex c) {
		final double re2 = re * c.re - im * c.im;
		final double im2 = im * c.re + re * c.im;
		re = re2;
		im = im2;
	}
	
	public final void mul (final double re2, final double im2) {
		final double re3 = re * re2 - im * im2;
		final double im3 = im * re2 + re * im2;
		re = re3;
		im = im3;
	}
	
	public final void smul (final double s) {
		re *= s;
		im *= s;
	}
	
	public final void div (Complex c) {
		div(c.re, c.im);
	}
	
	public final void div (final double re2, final double im2) {
		final double re3 = re * re2 + im * im2;
		final double im3 = im * re2 - re * im2;
		final double abssq2 = re2 * re2 + im2 * im2;
		re = re3 / abssq2;
		im = im3 / abssq2;
	}
	
	public final void sdiv (final double x, final double y) {
		re /= x;
		im /= y;
	}
	
	public final void add (final Complex c) {
		re += c.re;
		im += c.im;
	}
	
	public final void add (final double re2, final double im2) {
		re += re2;
		im += im2;
	}
	
	public final void sub (final Complex c) {
		re -= c.re;
		im -= c.im;
	}
	
	public final void sin () {
		final double re2 = Math.sin(re) * Math.cosh(im);
		final double im2 = Math.cos(re) * Math.sinh(im);
		re = re2;
		im = im2;
	}
	
	@Override
	public String toString () {
		return String.format("%f%+fi", re, im);
	}
	
}
