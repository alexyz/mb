import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.swing.JComponent;

public class Bb2mC extends JComponent {
	
	private static int TW = 100;
	private static int TH = 75;
	
	private int itdepth;
	private double bound;
	private MF f;
	private C origin, size;
	private Point p1, p2;
	private Image[][] images;
	
	void init() {
		System.out.println("reset");
		itdepth = 256;
		bound = 4;
		origin = new C(-3, -1.5);
		size = new C(4, 3);
		f = MF.powpc(2.0078125);
		images = null;
		repaint();
	}
	
	public Bb2mC() {
		init();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				p1 = e.getPoint();
			}
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					init();
				}
			}
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (p1 != null && p2 != null) {
					final C o2 = xyToC(p1.x, p1.y);
					final C s2 = xyToC(p2.x, p2.y);
					s2.sub(o2);
					System.out.println("o2=" + o2 + " s2=" + s2);
					origin = o2;
					size = s2;
					calc();
				}
				p1 = null;
				p2 = null;
				repaint();
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(final MouseEvent e) {
				final Point p = e.getPoint();
				//setTitle(xyToC(p.x, p.y).toString());
				firePropertyChange("title", null, xyToC(p.x, p.y).toString());
			}
			
			@Override
			public void mouseDragged(final MouseEvent e) {
				System.out.println("mouse dragged");
				if (p1 != null) {
					final Point p = e.getPoint();
					firePropertyChange("title", null, xyToC(p1.x, p1.y) + " - " + xyToC(p.x, p.y));
					p2 = p;
				}
				repaint();
			}
		});
	}
	
	private int reToX(final double r) {
		return (int) ((r - origin.r) * getWidth() / size.r);
	}
	
	private int imToY(final double i) {
		return (int) ((i - origin.i) * getHeight() / size.i);
	}
	
	private C xyToC(final int x, final int y) {
		final C c = new C(size);
		c.scale(x, y, getWidth(), getHeight());
		c.add(origin);
		return c;
	}
	
	@Override
	public void paintComponent(final Graphics g) {
		if (images == null) {
			calc();
		}
		for (int n = 0; n < images.length; n++) {
			for (int m = 0; m < images[n].length; m++) {
				final Image im = images[n][m];
				final int x = n * TW, y = m * TH;
				if (im != null) {
					g.drawImage(im, x, y, null);
				} else {
					g.setColor(Color.yellow);
					g.fillRect(x, y, TW, TH);
				}
			}
		}
		if (p1 != null && p2 != null) {
			g.setColor(Color.green);
			g.drawRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
		}
	}
	
	private void calc() {
		System.out.println("calc");
		Bb2mF.q.clear();
		
		final C origin = new C(this.origin);
		final C size = new C(this.size);
		final MF f = this.f;
		final int w = getWidth(), h = getHeight();
		final int itdepth = this.itdepth;
		final double bound = this.bound;
		
		final int xt = (w + TW - 1) / TW;
		final int yt = (h + TH - 1) / TH;
		final Image[][] images = new Image[xt][yt];
		this.images = images;
		
		final List<Runnable> l = new ArrayList<Runnable>();
		for (int n = 0; n < images.length; n++) {
			for (int m = 0; m < images[n].length; m++) {
				final int x = n * TW, y = m * TH;
				final int fn = n, fm = m;
				final int iw = Math.min(TW, w - x);
				final int ih = Math.min(TH, h - y);
				final BufferedImage im = (BufferedImage) createImage(iw, ih);
				l.add(new Runnable() {
					@Override
					public void run() {
						// no field accesses
						subcalc(origin, size, w, h, f, itdepth, bound, im, x, y);
						images[fn][fm] = im;
						repaint();
					}
				});
			}
		}
		
		Collections.shuffle(l);
		Bb2mF.q.addAll(l);
	}
	
	private static void subcalc(final C origin, final C size, final int w, final int h, final MF f, final int itdepth, final double bound, final BufferedImage im, final int xo, final int yo) {
//		System.out.println("subcalc " + xo + ", " + yo);
		final C z = new C();
		final C p = new C();
		final int iw = im.getWidth();
		final int ih = im.getHeight();
		final double boundsq = bound * bound;
		for (int x = 0; x < iw; x+=1) {
			for (int y = 0; y < ih; y+=1) {
				p.set(size);
				p.scale(xo + x, yo + y, w, h);
				p.add(origin);
				z.set(p);
				int i;
				for (i = 0; i < itdepth; i++) {
					f.apply(z, p);
					if (z.getAbsSq() > boundsq) {
						break;
					}
				}
				i = i * 255 / itdepth;
				im.setRGB(x, y, i | i << 8 | i << 16);
			}
		}
	}
}
