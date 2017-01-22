package mb;

import java.awt.Point;

/**
 * it's a complex number type
 */
final class Complex {
	
	public double re, im;
	
	public Complex(final double r, final double i) {
		re = r;
		im = i;
	}
	
	public Complex() {
		//
	}
	
	public Complex(final Complex c) {
		re = c.re;
		im = c.im;
	}
	
	public final Complex log () {
		return set(Math.log(getAbs()), Math.atan2(im, re));
	}
	
	public final Complex exp () {
		final double er = Math.exp(re);
		return set(er * Math.cos(im), er * Math.sin(im));
	}
	
	public final Complex scale (final double x, final double y) {
		re *= x;
		im *= y;
		return this;
	}
	
	public final Complex conj () {
		im = -im;
		return this;
	}
	
	public final Complex pow (final double re2) {
		log();
		mul(re2);
		return exp();
	}
	
	public final Complex pow (Complex c) {
		return pow(c.re, c.im);
	}
	
	public final Complex pow (final double re2, final double im2) {
		log();
		mul(re2, im2);
		return exp();
	}
	
	/** very slow */
	public double getAbs () {
		return Math.hypot(re, im);
	}
	
	public double getAbsSq () {
		return re * re + im * im;
	}
	
	public final Complex set (final Complex c) {
		return set(c.re, c.im);
	}
	
	public final Complex set (final double r, final double i) {
		re = r;
		im = i;
		return this;
	}
	
	public final Complex sq () {
		return set(re * re - im * im, 2.0 * re * im);
	}
	
	public final Complex cu () {
		// aaa - 3abb + i(3aab - bbb)
		final double resq = re * re;
		final double imsq = im * im;
		return set(resq * re - 3 * re * imsq, 3 * resq * im - imsq * im);
	}
	
	public final Complex mul (final Complex c) {
		return mul(c.re, c.im);
	}
	
	public final Complex mul (final double re2) {
		return set(re * re2, im * re2);
	}
	
	public final Complex mul (final double re2, final double im2) {
		return set(re * re2 - im * im2, im * re2 + re * im2);
	}
	
	public final Complex div (Complex c) {
		return div(c.re, c.im);
	}
	
	public final Complex div (final double re2) {
		final double re3 = re * re2;
		final double im3 = im * re2;
		final double re2sq = re2 * re2;
		return set(re3 / re2sq, im3 / re2sq);
	}
	
	public final Complex div (final double re2, final double im2) {
		final double re3 = re * re2 + im * im2;
		final double im3 = im * re2 - re * im2;
		final double abssq2 = re2 * re2 + im2 * im2;
		re = re3 / abssq2;
		im = im3 / abssq2;
		return this;
	}

	public final Complex unscale (final double x, final double y) {
		re /= x;
		im /= y;
		return this;
	}
	
	public final Complex add (final Complex c) {
		return add(c.re, c.im);
	}
	
	public final Complex add (final double re2, final double im2) {
		re += re2;
		im += im2;
		return this;
	}
	
	public final Complex sub (final Complex c) {
		return sub(c.re, c.im);
	}
	
	public final Complex sub (final double re2, final double im2) {
		re -= re2;
		im -= im2;
		return this;
	}
	
	public final Complex sin () {
		return set(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
	}
	
	public final Complex cos () {
		return set(Math.cos(re) * Math.cosh(im), Math.sin(re) * Math.sinh(im));
	}
	
	public Complex neg () {
		return mul(-1, 0);
	}
	
	public Complex floor () {
		re = Math.floor(re);
		im = Math.floor(im);
		return this;
	}
	
	public Complex ceil () {
		re = Math.ceil(re);
		im = Math.ceil(im);
		return this;
	}
	
	public final Point toPoint () {
		return new Point((int) re, (int) im);
	}
	
	@Override
	public String toString () {
		return fd(re) + "+" + fd(im) + "i";
	}
	
	private static String fd (double v) {
		String r = String.format("%f", v);
		while (r.contains(".") && r.endsWith("0")) {
			r = r.substring(0, r.length() - 1);
		}
		if (r.endsWith(".")) {
			r = r.substring(0, r.length() - 1);
		}
		return r;
	}
	
}
