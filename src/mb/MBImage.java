package mb;

import java.awt.Point;
import java.awt.image.WritableRaster;

public class MBImage {
	
	public Complex centre;
	public Complex size;
	public MBFunctionParams params;
	public MBFunction function;
	public Complex julia;
	
	public MBImage () {
		centre();
	}
	
	public MBImage (MBImage other) {
		function = other.function;
		params = other.params.clone();
		centre = new Complex(other.centre);
		size = new Complex(other.size);
		julia = other.julia != null ? new Complex(other.julia) : null;
	}
	
	public void centre () {
		if (julia != null) {
			centre = new Complex();
			size = new Complex(4, 3);
		} else {
			centre = new Complex();
			size = new Complex(4, 3);
		}
	}
	
	public Complex viewToModel (final int x, final int y, final int w, final int h) {
		return new Complex(size).sub(2*centre.re, 2*centre.im).scale(w,h).sub(2*size.re*x,2*size.im*y).unscale(2*w,2*h).neg();
	}
	
	public Point modelToView (final Complex m, final int w, final int h) {
		return modelToView(m.re, m.im, w, h);
	}
	
	public Point modelToView (final double re, final double im, final int w, final int h) {
		return new Complex(size).add(re*2,im*2).sub(centre.re*2, centre.im*2).scale(w, h).unscale(size.re*2,size.im*2).toPoint();
	}
	
	public void calc (final WritableRaster r, final int xo, final int yo, final int w, final int h) {
		final int iw = r.getWidth();
		final int ih = r.getHeight();
		final int[] a = new int[3];
		final int id = params.depth;
		
		for (int y = 0; y < ih; y++) {
			for (int x = 0; x < iw; x++) {
				Complex c = viewToModel(xo + x, yo + y, w, h);
				// z[0] = c
				Complex z = new Complex(c);
				int i = function.iterate(z, julia != null ? julia : c, params);
				if (id != 255) {
					i = (i * 255) / id;
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