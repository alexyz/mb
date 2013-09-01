import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

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
public class Bb2 extends JFrame {
	
	public static void mainf(String[] args) throws Exception {
		System.out.println(System.getProperty("user.dir"));
		int w = 640, h = 480;
		C o = new C(-2, -1.5);
		C s = new C(4, 3);
		BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int i = 0, n = 100; n < 400; i++, n++) {
			calcmb(im, o, s, MF.powpc(n / 100.0));
			File f = new File("powimg/image" + i + ".png");
			ImageIO.write(im, "png", f);
			System.out.println("wrote " + f);
		}
	}
	
	public static void main(String[] args) {
		JFrame f = new Bb2();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(640, 480);
		f.show();
	}

	C origin, size;
	Point p1, p2;
	BufferedImage im;
	
	void init() {
		System.out.println("reset");
		origin = new C(-2, -1.5);
		size = new C(4, 3);
		im = null;
		repaint();
	}

	public Bb2() {
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
					im = null;
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
		C c = new C();
		c.setxy(origin, size, getWidth(), getHeight(), x, y);
		return c;
	}

	@Override
	public void paint(Graphics g) {
		final int w = getWidth(), h = getHeight();
		if (im == null || im.getWidth() != w || im.getHeight() != h) {
			System.out.println("recalc");
			im = (BufferedImage) createImage(w, h);
			calcmb(im, origin, size, MF.sqpc);
			//calc2();
		}
		System.out.println("redraw");
		g.drawImage(im, 0, 0, null);
		if (p1 != null && p2 != null) {
			g.setColor(Color.green);
			g.drawRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
		}
		System.out.println("done");
	}

	private static void calcmb(BufferedImage im, C origin, C size, MF f) {
		final int w = im.getWidth(), h = im.getHeight();
		final int itdepth = 256;
		final double bound = 4;
		final C z = new C();
		final C p = new C();
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				p.setxy(origin, size, w, h, x, y);
				z.set(p);
				int i;
				for (i = 0; i < itdepth; i++) {
					//z.mul(z);
					//z.pow(1.5);
					//z.add(p);
					//z.conj();
					f.apply(z, p);
					if (z.abs() > bound) {
						break;
					}
				}
				i = (i * 255) / itdepth;
				im.setRGB(x, y, i | (i << 8) | (i << 16));
			}
		}
	}
	
	// calculate buddabrot
	private void calc2() {
		int w = im.getWidth(), h = im.getHeight();
		final int itdepth = 1024;
		// xy of each point in iteration
		int[] va = new int[itdepth];
		// screen points
		int[][] pca = new int[w][h];
		// max screen point count
		int pcmax = 0;
		int vs = 0;
		C z = new C();
		C p = new C();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				vs = 0;
				p.setxy(origin, size, w, h, x, y);
				z.set(p);
				test: {
					int i;
					for (i = 0; i < itdepth; i++) {
						//z.mul(z);
						//z.mul(z);
						z.pow(2.5);
						z.add(p);
						if (z.abs() > 1e3) {
							break test;
						}
						va[vs++] = (reToX(z.r) << 16) | imToY(z.i);
					}
					
					// p is in M
					Arrays.sort(va, 0, vs);
					int vp = -1;
					for (i = 0; i < vs; i++) {
						int v = va[i];
						if (v != vp) {
							vp = v;
							int x2 = v >>> 16;
							int y2 = v & 0xffff;
							if (x2 >= 0 && x2 < w && y2 >= 0 && y2 < h) {
								int pc = ++pca[x2][y2];
								if (pc > pcmax) {
									pcmax = pc;
								}
							}
						}
					}
				}
			}
		}
		System.out.println("pcmax=" + pcmax);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int i = (int) ((((double)pca[x][y])*255) / pcmax);
				im.setRGB(x, y, (i << 16) | (i << 8) | i);
			}
		}
	}

}
