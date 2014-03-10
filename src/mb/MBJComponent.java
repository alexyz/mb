package mb;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class MBJComponent extends JComponent {
	
	private static int TW = 100;
	private static int TH = 75;
	
	private int itdepth = 255;
	private double bound = 4;
	private MBFunction f;
	private Complex origin, size;
	private Point p1, p2;
	private Image[][] images;
	
	void reset() {
		System.out.println("reset");
		origin = new Complex(-3, -1.5);
		size = new Complex(4, 3);
		reimage();
	}
	
	void reimage() {
		images = null;
		repaint();
	}
	
	public MBJComponent() {
		reset();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup(e);
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					p1 = e.getPoint();
				}
			}
			
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup(e);
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					if (p1 != null && p2 != null) {
						int x1 = Math.min(p1.x, p2.x);
						int y1 = Math.min(p1.y, p2.y);
						int x2 = Math.max(p1.x, p2.x);
						int y2 = Math.max(p1.y, p2.y);
						final Complex o = viewToModel(x1, y1);
						final Complex s = viewToModel(x2, y2);
						s.sub(o);
						System.out.println("o=" + o);
						System.out.println("s=" + s);
						origin = o;
						size = s;
						recalc();
					}
					p1 = null;
					p2 = null;
					repaint();
				}
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(final MouseEvent e) {
				final Point p = e.getPoint();
				//setTitle(xyToC(p.x, p.y).toString());
				firePropertyChange("title", null, viewToModel(p.x, p.y).toString());
			}
			
			@Override
			public void mouseDragged(final MouseEvent e) {
				if (p1 != null) {
					final Point p = e.getPoint();
					firePropertyChange("title", null, viewToModel(p1.x, p1.y) + ", " + viewToModel(p.x, p.y));
					p2 = p;
				}
				repaint();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized (ComponentEvent e) {
				reimage();
			}
		});
	}
	private void popup (MouseEvent e) {
		System.out.println("popup");
		JPopupMenu menu = new JPopupMenu();
		JMenuItem resetItem = new JMenuItem("Reset");
		resetItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				reset();
			}
		});
		menu.add(resetItem);
		menu.show(this, e.getX(), e.getY());
	}
	
	private Complex viewToModel(final int x, final int y) {
		final Complex c = new Complex(size);
		c.scale(x, y, getWidth(), getHeight());
		c.add(origin);
		return c;
	}
	
	@Override
	public void paintComponent(final Graphics g) {
		if (images == null) {
			g.setColor(Color.black);
			Graphics2D g2 = (Graphics2D) g;
			g2.fill(g.getClipBounds());
			recalc();
			return;
		}
		
		for (int n = 0; n < images.length; n++) {
			for (int m = 0; m < images[n].length; m++) {
				final Image im = images[n][m];
				final int x = n * TW, y = m * TH;
				if (im != null) {
					g.drawImage(im, x, y, null);
				} else {
					g.setColor(Color.black);
					g.fillRect(x, y, TW, TH);
				}
			}
		}
		
		if (p1 != null && p2 != null) {
			g.setColor(Color.green);
			int x = Math.min(p1.x, p2.x);
			int y = Math.min(p1.y, p2.y);
			int w = Math.abs(p1.x - p2.x);
			int h = Math.abs(p1.y - p2.y);
			g.drawRect(x, y, w, h);
		}
	}
	
	private void recalc() {
		System.out.println("recalc");
		MBJFrame.QUEUE.clear();
		
		// TODO parameter object
		
		final Complex origin = new Complex(this.origin);
		final Complex size = new Complex(this.size);
		final MBFunction f = this.f;
		final int w = getWidth(), h = getHeight();
		final int itdepth = this.itdepth;
		final double bound = this.bound;
		
		final int xt = (w + TW - 1) / TW;
		final int yt = (h + TH - 1) / TH;
		final Image[][] images = new Image[xt][yt];
		this.images = images;
		
		final List<Runnable> l = new ArrayList<>();
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
		MBJFrame.QUEUE.addAll(l);
	}
	
	private static void subcalc(final Complex origin, final Complex size, final int w, final int h, final MBFunction f, final int itdepth, final double bound, final BufferedImage im, final int xo, final int yo) {
		//		System.out.println("subcalc " + xo + ", " + yo);
		final Complex z = new Complex();
		final Complex p = new Complex();
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
				i = (i * 255) / itdepth;
				im.setRGB(x, y, i | i << 8 | i << 16);
			}
		}
	}

	public void setMBFunction (MBFunction f) {
		this.f = f;
	}

	public void setItDepth (int itdepth) {
		this.itdepth = itdepth;
	}
	
	public int getItdepth () {
		return itdepth;
	}
	
	public double getBound () {
		return bound;
	}
	
	public void setBound (double bound) {
		this.bound = bound;
	}
}
