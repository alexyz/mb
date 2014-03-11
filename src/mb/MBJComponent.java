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
	
	private static int TILEWIDTH = 75;
	private static int TILEHEIGHT = 75;
	
	private int iterationDepth = 255;
	private double bound = 4;
	private MBFunction mbFunction;
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
		c.smul(x, y);
		c.sdiv(getWidth(), getHeight());
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
				final int x = n * TILEWIDTH, y = m * TILEHEIGHT;
				if (im != null) {
					g.drawImage(im, x, y, null);
				} else {
					g.setColor(Color.black);
					g.fillRect(x, y, TILEWIDTH, TILEHEIGHT);
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
	
	public BufferedImage export(int w, int h) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		final MBImage mbim = new MBImage(origin, size, mbFunction, iterationDepth, bound);
		mbim.calc(image, 0, 0, w, h);
		return image;
	}
	
	private void recalc() {
		System.out.println("recalc");
		MBJFrame.QUEUE.clear();
		
		final int w = getWidth(), h = getHeight();
		final int xi = (w + TILEWIDTH - 1) / TILEWIDTH;
		final int yi = (h + TILEHEIGHT - 1) / TILEHEIGHT;
		final Image[][] images = new Image[xi][yi];
		this.images = images;
		final MBImage mbim = new MBImage(origin, size, mbFunction, iterationDepth, bound);
		final List<Runnable> l = new ArrayList<>();
		
		for (int ix = 0; ix < images.length; ix++) {
			for (int iy = 0; iy < images[ix].length; iy++) {
				final int xo = ix * TILEWIDTH, yo = iy * TILEHEIGHT;
				final int fix = ix, fiy = iy;
				final int iw = Math.min(TILEWIDTH, w - xo);
				final int ih = Math.min(TILEHEIGHT, h - yo);
				final BufferedImage image = (BufferedImage) createImage(iw, ih);
				
				l.add(new Runnable() {
					@Override
					public void run() {
						// no field accesses
						mbim.calc(image, xo, yo, w, h);
						images[fix][fiy] = image;
						repaint();
					}
				});
			}
		}
		
		Collections.shuffle(l);
		MBJFrame.QUEUE.addAll(l);
	}
	
	public void setMBFunction (MBFunction f) {
		this.mbFunction = f;
	}

	public void setItDepth (int itdepth) {
		this.iterationDepth = itdepth;
	}
	
	public int getItdepth () {
		return iterationDepth;
	}
	
	public double getBound () {
		return bound;
	}
	
	public void setBound (double bound) {
		this.bound = bound;
	}
}
