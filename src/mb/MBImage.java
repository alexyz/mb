package mb;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class MBImage {
	
	public Complex origin;
	public Complex size;
	public MBFunction mbFunction;
	public int iterationDepth;
	public double bound;
	
	public MBImage () {
		mbFunction = MBFunction.sqpc;
		iterationDepth = 255;
		bound = 4;
		centre();
	}
	
	public MBImage (MBImage other) {
		bound = other.bound;
		iterationDepth = other.iterationDepth;
		mbFunction = other.mbFunction;
		origin = new Complex(other.origin);
		size = new Complex(other.size);
	}
	
	public void centre () {
		origin = new Complex(-3, -1.5);
		size = new Complex(4, 3);
	}
	
	public void calc (final BufferedImage image, final int xo, final int yo, final int w, final int h) {
		final Complex z = new Complex();
		final Complex c = new Complex();
		final int iw = image.getWidth();
		final int ih = image.getHeight();
		final double boundsq = bound * bound;
		final int[] a = new int[3];
		final WritableRaster r = image.getRaster();
		
		for (int y = 0; y < ih; y++) {
			for (int x = 0; x < iw; x++) {
				c.set(size);
				c.smul(xo + x, yo + y);
				c.sdiv(w, h);
				c.add(origin);
				z.set(c);
				int i;
				for (i = 0; i < iterationDepth; i++) {
					mbFunction.apply(z, c);
					if (z.getAbsSq() > boundsq) {
						break;
					}
				}
				i = (i * 255) / iterationDepth;
				// image.setRGB(x, y, i | i << 8 | i << 16);
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