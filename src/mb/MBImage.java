package mb;

import java.awt.image.WritableRaster;

public class MBImage {
	
	public Complex topLeft;
	public Complex size;
	public int iterationDepth;
	public double bound;
	public MBIteration mbIteration;
	public Complex julia;
	
	public MBImage () {
		mbIteration = MBIteration.SQUARE_ADD_C;
		iterationDepth = 255;
		// 2 is max required for square+c
		// other pow might need more
		bound = 4;
		centre();
	}
	
	public MBImage (MBImage other) {
		bound = other.bound;
		iterationDepth = other.iterationDepth;
		mbIteration = other.mbIteration;
		topLeft = Complex.copy(other.topLeft);
		size = Complex.copy(other.size);
		julia = Complex.copy(other.julia);
	}
	
	public void centre () {
		if (julia != null) {
			topLeft = new Complex(-2, -1.5);
		} else {
			topLeft = new Complex(-3, -1.5);
		}
		size = new Complex(4, 3);
	}
	
	public Complex viewToModel(final int x, final int y, final int w, final int h) {
		return viewToModel(new Complex(), x, y, w, h);
	}
	
	public Complex viewToModel(Complex c, final int x, final int y, final int w, final int h) {
		// c = (size / [w,h]) * [x,y]
		c.set(size);
		c.mulr(x, y);
		c.divr(w, h);
		c.add(topLeft);
		return c;
	}
	
	public void calc (final WritableRaster r, final int xo, final int yo, final int w, final int h) {
		final Complex z = new Complex();
		final Complex c = new Complex();
		final int iw = r.getWidth();
		final int ih = r.getHeight();
		final int[] a = new int[3];
		
		for (int y = 0; y < ih; y++) {
			for (int x = 0; x < iw; x++) {
//				c.set(size);
//				c.mulr(xo + x, yo + y);
//				c.divr(w, h);
//				c.add(origin);
				viewToModel(c, xo + x, yo + y, w, h);
				// z[0] = c
				z.set(c);
				int i = mbIteration.iterate(z, julia != null ? julia : c, iterationDepth, bound);
				if (iterationDepth != 255) {
					i = (i * 255) / iterationDepth;
				}
				a[0] = i;
				a[1] = i;
				a[2] = i;
				r.setPixel(x, y, a);
			}
		}
	}
	
//	public void calcbb (final BufferedImage finalImage) {
//		final Complex z = new Complex();
//		final Complex c = new Complex();
//		final int iw = image.getWidth();
//		final int ih = image.getHeight();
//		final double boundsq = bound * bound;
//		final int[] a = new int[3];
//		final WritableRaster r = image.getRaster();
//		final short[] xp = new short[iw*ih*iterationDepth];
//		final short[] yp = new short[xp.length];
//		final short[] txp = new short[iterationDepth];
//		final short[] typ = new short[txp.length];
//		
//		for (double y = 0; y < ih; y++) {
//			for (double x = 0; x < iw; x++) {
//				double re = origin.re + ((size.re * x) / iw);
//				double im = origin.im + ((size.im * y) / ih);
//				c.set(re, im);
//				z.set(c);
//				int i;
//				loop: {
//					for (i = 0; i < iterationDepth; i++) {
//						mbFunction.apply(z, c);
//						if (z.getAbsSq() > boundsq) {
//							break loop;
//						}
//						// store x, y
//						txp[i] = 0; // model to view
//						typ[i] = 0;
//					}
//					// merge txp in xp
//				}
//				i = (i * 255) / iterationDepth;
//				// image.setRGB(x, y, i | i << 8 | i << 16);
//				a[0] = i;
//				a[1] = i;
//				a[2] = i;
//				r.setPixel((int) x, (int) y, a);
//			}
//		}
//	}
	
}