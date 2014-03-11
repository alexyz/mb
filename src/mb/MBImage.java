package mb;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class MBImage {
	
	private final Complex origin;
	private final Complex size;
	private final MBFunction mbFunction;
	private final int iterationDepth;
	private final double bound;
	
	public MBImage (final Complex origin, final Complex size, final MBFunction mbFunction, final int iterationDepth, final double bound) {
		this.origin = origin;
		this.size = size;
		this.mbFunction = mbFunction;
		this.iterationDepth = iterationDepth;
		this.bound = bound;
	}
	
	public void calc (final BufferedImage image, final int xo, final int yo, final int w, final int h) {
		final Complex z = new Complex();
		final Complex p = new Complex();
		final int iw = image.getWidth();
		final int ih = image.getHeight();
		final double boundsq = bound * bound;
		final int[] a = new int[3];
		final WritableRaster r = image.getRaster();
		
		for (int y = 0; y < ih; y++) {
			for (int x = 0; x < iw; x++) {
				p.set(size);
				p.smul(xo + x, yo + y);
				p.sdiv(w, h);
				p.add(origin);
				z.set(p);
				int i;
				for (i = 0; i < iterationDepth; i++) {
					mbFunction.apply(z, p);
					if (z.getAbsSq() > boundsq) {
						break;
					}
				}
				i = (i * 255) / iterationDepth;
				//image.setRGB(x, y, i | i << 8 | i << 16);
				a[0] = i;
				a[1] = i;
				a[2] = i;
				r.setPixel(x, y, a);
			}
		}
	}
	
}