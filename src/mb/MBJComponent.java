package mb;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import javax.swing.JComponent;

public class MBJComponent extends JComponent {
	
	public static final String POSITION = "position";
	
	private static final int TILEWIDTH = 75;
	private static final int TILEHEIGHT = 75;
	
	public MBImage image = new MBImage();
	
	private MBImage juliaImage;
	private Point p1, p2;
	private Image[][] images;
	private boolean selectJulia;
	private Point mp;
	
	public MBJComponent() {
		setPreferredSize(new Dimension(640, 480));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (!selectJulia) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						p1 = e.getPoint();
					}
				}
			}
			
			@Override
			public void mouseClicked (MouseEvent e) {
				if (selectJulia) {
					image = juliaImage;
					selectJulia = false;
					setCursor(Cursor.getDefaultCursor());
					firePropertyChange(POSITION, null, Math.random());
					recalc();
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					System.out.println("zoom in");
					image.centre = image.viewToModel(e.getX(), e.getY(), getWidth(), getHeight());
					image.size.div(2);
					recalc();
					
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					System.out.println("zoom out");
					image.size.mul(2);
					recalc();
				}
			}
			
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (!selectJulia) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (p1 != null && p2 != null) {
							int x1 = Math.min(p1.x, p2.x);
							int y1 = Math.min(p1.y, p2.y);
							int x2 = Math.max(p1.x, p2.x);
							int y2 = Math.max(p1.y, p2.y);
							final Complex tl = image.viewToModel(x1, y1, getWidth(), getHeight());
							final Complex s = image.viewToModel(x2, y2, getWidth(), getHeight()).sub(tl);
							final Complex c = new Complex(tl).add(s.re/2, s.im/2);
							System.out.println("o=" + tl);
							System.out.println("s=" + s);
							image.centre = c;
							image.size = s;
							firePropertyChange(POSITION, null, Math.random());
							recalc();
						}
						p1 = null;
						p2 = null;
						repaint();
					}
				}
			}
		});
		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(final MouseEvent e) {
				if (selectJulia) {
					final Point p = e.getPoint();
					juliaImage = new MBImage(image);
					juliaImage.julia = image.viewToModel(p.x, p.y, getWidth(), getHeight());
					juliaImage.centre();
					final BufferedImage image = (BufferedImage) createImage(TILEWIDTH, TILEHEIGHT);
					MBJFrame.instance.queue.add(new Runnable() {
						@Override
						public void run() {
							// XXX might not be thread safe
							juliaImage.calc(image.getRaster(), 0, 0, TILEWIDTH, TILEHEIGHT);
							images[0][0] = image;
							repaint();
						}
					});
				}
				mp = e.getPoint();
				repaint();
			}
			
			@Override
			public void mouseDragged(final MouseEvent e) {
				if (p1 != null) {
					final Point p = e.getPoint();
					p2 = p;
				}
				repaint();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized (ComponentEvent e) {
				refresh();
			}
		});
	}
	
	public void refresh() {
		System.out.println("component reimage");
		images = null;
		repaint();
	}
	
	@Override
	public void paintComponent(final Graphics g) {
		if (images == null) {
			System.out.println("first paint");
			g.setColor(Color.black);
			Graphics2D g2 = (Graphics2D) g;
			g2.fill(g.getClipBounds());
			recalc();
			return;
		}
		
		boolean bl = false;
		for (int n = 0; n < images.length; n++) {
			for (int m = 0; m < images[n].length; m++) {
				final Image im = images[n][m];
				final int x = n * TILEWIDTH, y = m * TILEHEIGHT;
				if (im != null) {
					g.drawImage(im, x, y, null);
				} else {
					g.setColor(Color.black);
					g.fillRect(x, y, TILEWIDTH, TILEHEIGHT);
					bl = true;
				}
			}
		}
		
		if (t != 0 && !bl) {
			long t2 = System.nanoTime() - t;
			System.out.println("time: " + (t2 / 1000000000.0) + " s");
			t = 0;
		}
		
		if (mp != null) {
			Complex m = image.viewToModel(mp.x, mp.y, getWidth(), getHeight());
			Point v = image.modelToView(m.re, m.im, getWidth(), getHeight());
			g.setColor(Color.blue);
			g.drawString("m=" + m, 10, 10);
		}
		
		if (grid) {
			Complex tl = new Complex(image.centre).sub(image.size.re/2,image.size.im/2);
			Complex br = new Complex(image.centre).add(image.size.re/2,image.size.im/2);
			g.setColor(Color.blue);
			for (double re = Math.floor(tl.re); re <= Math.ceil(br.re); re++) {
				for (double im = Math.floor(tl.im); im <= Math.ceil(br.im); im++) {
					Point p = image.modelToView(re, im, getWidth(), getHeight());
					g.drawLine(0, p.y, getWidth(), p.y);
					g.drawLine(p.x, 0, p.x, getHeight());
					g.drawString("" + new Complex(re, im), p.x, p.y);
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
	
	private long t;

	private boolean grid;
	
	private void recalc() {
		System.out.println("component recalc");
		
		MBJFrame.instance.queue.clear();
		t = System.nanoTime();
		if (this.image.function == null) {
			System.out.println("no function...");
			return;
		}
		
		final int w = getWidth(), h = getHeight();
		final int xi = (w + TILEWIDTH - 1) / TILEWIDTH;
		final int yi = (h + TILEHEIGHT - 1) / TILEHEIGHT;
		final Image[][] images = new Image[xi][yi];
		this.images = images;
		final List<Runnable> l = new ArrayList<>();
		
		for (int ix = 0; ix < images.length; ix++) {
			final int fix = ix;
			for (int iy = 0; iy < images[ix].length; iy++) {
				final int fiy = iy;
				final int xo = ix * TILEWIDTH, yo = iy * TILEHEIGHT;
				final int iw = Math.min(TILEWIDTH, w - xo);
				final int ih = Math.min(TILEHEIGHT, h - yo);
				final BufferedImage image = (BufferedImage) createImage(iw, ih);
				final MBImage mbImage = new MBImage(this.image);
				
				l.add(new Runnable() {
					@Override
					public void run() {
						mbImage.calc(image.getRaster(), xo, yo, w, h);
						images[fix][fiy] = image;
						repaint();
					}
				});
			}
		}
		
		Collections.shuffle(l);
		System.out.println("adding " + l.size() + " runnables");
		MBJFrame.instance.queue.addAll(l);
	}
	
	public void setJulia (boolean julia) {
		if (julia) {
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			selectJulia = true;
			image.julia = null;
			
		} else {
			setCursor(Cursor.getDefaultCursor());
			selectJulia = false;
			image.julia = null;
			recalc();
		}
	}

	public void setGrid (boolean grid) {
		this.grid = grid;
		repaint();
	}
	
}
