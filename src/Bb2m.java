import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/*
 * TODO
 * menu (reset, itdepth, function, bb...)
 * multithreading, progress bar
 * right click to cancel zoom
 * pan/zoom controls [ < > ^ v + - r i w h ] 
 * history stack
 * lightness control
 * image save
 */
public class Bb2m extends JFrame {
	
	private static final BlockingQueue q = new LinkedBlockingQueue();
	private static int TW = 100;
	private static int TH = 75;
	
	public static void main(String[] args) {
		JFrame f = new Bb2m();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(640, 480);
		f.show();
	}

	C origin, size;
	Point p1, p2;
	Image[][] images;
	
	void init() {
		System.out.println("reset");
		origin = new C(-2, -1.5);
		size = new C(4, 3);
		calc(origin, size, MF.sqpc);
	}

	public Bb2m() {
		init();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				p1 = e.getPoint();
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) {
					init();
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (p1 != null && p2 != null) {
					C o2 = xyToC(p1.x, p1.y);
					C s2 = xyToC(p2.x, p2.y);
					s2.sub(o2);
					System.out.println("o2=" + o2 + " s2=" + s2);
					origin = o2;
					size = s2;
					calc(origin, size, MF.sqpc);
				}
				p1 = null;
				p2 = null;
				repaint();
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				setTitle(xyToC(p.x, p.y).toString());
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				System.out.println("mouse dragged");
				if (p1 != null) {
					Point p = e.getPoint();
					setTitle(xyToC(p1.x, p1.y) + " - " + xyToC(p.x, p.y));
					p2 = p;
				}
				repaint();
			}
		});
	}
	
	private int reToX(double r) {
		return (int) (((r - origin.r) * getWidth()) / size.r);
	}
	
	private int imToY(double i) {
		return (int) (((i - origin.i) * getHeight()) / size.i);
	}
	
	private C xyToC(int x, int y) {
		C c = new C(size);
//		c.setxy(origin, size, getWidth(), getHeight(), x, y);
		c.scale(x, y, getWidth(), getHeight());
		c.add(origin);
		return c;
	}

	@Override
	public void paint(Graphics g) {
		if (images == null) {
			return;
		}
		for (int n = 0; n < images.length; n++) {
			for (int m = 0; m < images[n].length; m++) {
				Image im = images[n][m];
				if (im != null) {
					final int x = n * TW, y = m * TH;
					g.drawImage(im, x, y, null);
				}
			}
		}
		if (p1 != null && p2 != null) {
			g.setColor(Color.green);
			g.drawRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
		}
	}
	
	private void calc(final C origin, final C size, final MF f) {
		System.out.println("calc");
		final int w = getWidth(), h = getHeight();
		final int itdepth = 256;
		final double bound = 4;
		int xa = (w + TW - 1) / TW;
		int ya = (h + TH - 1) / TH;
		images = new Image[xa][ya];
		
		for (int n = 0; n < images.length; n++) {
			for (int m = 0; m < images[n].length; m++) {
				final int x = n * TW, y = m * TH;
				final int fn = n, fm = m;
				final BufferedImage im = (BufferedImage) createImage(Math.min(TW, w - x), Math.min(TH, h - y));
				// TODO queue these
				new Thread() {
					public void run() {
						subcalc(im, x, y, itdepth, bound, f, w, h, size, origin);
						images[fn][fm] = im;
						repaint();
					}
				}.start();
			}
		}
	}
	
	private void subcalc(BufferedImage im, int xo, int yo, int itdepth, double bound, MF f, int w, int h, C size, C origin) {
		System.out.println("subcalc " + xo + ", " + yo);
		final C z = new C();
		final C p = new C();
		int iw = im.getWidth();
		int ih = im.getHeight();
		for (int x = 0; x < iw; x++) {
			for (int y = 0; y < ih; y++) {
				p.set(size);
				p.scale(xo + x, yo + y, w, h);
				p.add(origin);
				z.set(p);
				int i;
				for (i = 0; i < itdepth; i++) {
					f.apply(z, p);
					if (z.getAbs() > bound) {
						break;
					}
				}
				i = (i * 255) / itdepth;
				im.setRGB(x, y, i | (i << 8) | (i << 16));
			}
		}
	}
}
