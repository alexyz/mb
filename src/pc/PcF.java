package pc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.*;

import static java.lang.Math.*;

/**
 * draw a parametric curve
 */
public class PcF extends JFrame {
	public static void main (String[] args) {
		PcF f = new PcF();
		f.show();
	}
	public PcF () {
		super("PcF"); 
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		CF f = new CF() {
			@Override
			double getX (double p) {
				return validate(sin(p * PI * 4) / 2);
			}
			@Override
			double getDX (double p) {
				return validate(cos(p * PI * 4));
			}
			@Override
			double getY (double p) {
				return validate(cos(p * PI * 2));
			}
			@Override
			double getDY (double p) {
				return validate(-sin(p * PI * 2));
			}
			@Override
			double getW (double p) {
				return 0.1;
			}
		};
		CC c = new CC(f);
		JPanel p = new JPanel(new GridLayout(1, 2));
		p.add(c);
		setContentPane(p);
		setPreferredSize(new Dimension(800, 800));
		pack();
	}
}

abstract class CF {
	double validate(double v) {
		if (v < -1.0 || v > 1.0) {
			throw new RuntimeException("v=" + v);
		}
		return v;
	}
	abstract double getX(double p);
	abstract double getY(double p);
	abstract double getDX(double p);
	abstract double getDY(double p);
	abstract double getW(double p);
}

class CC extends JComponent {
	private CF f;
	public CC (CF f) {
		this.f = f;
	}
	@Override
	protected void paintComponent (Graphics g) {
		int opx = 0, opy = 0;
		int seg = 100;
		for (double n = 0; n <= seg; n++) {
			final double p = n / seg;
			final double x = f.getX(p);
			final double y = f.getY(p);
			final int px = xtv(x);
			final int py = ytv(y);
			if (n > 0) {
				g.setColor(Color.blue);
				g.drawLine(opx, opy, px, py);
				if ((n % 6) == 0) {
					final double dx = f.getDX(p);
					final double dy = f.getDY(p);
					final double h = hypot(dx, dy);
					final double dxn = dx / h;
					final double dyn = dy / h;
					g.setColor(Color.black);
					g.drawString(String.format("%.2f: %.2f, %.2f", p, dxn, dyn), px, py);
					
					final double a = atan2(dyn, dxn);
					final double s = sin(a) * -0.05;
					final double c = cos(a) * 0.05;
					final int vx1 = xtv(x + s);
					final int vy1 = ytv(y + c);
					final int vx2 = xtv(x - s);
					final int vy2 = ytv(y - c);
					g.setColor(Color.red);
					g.drawLine(vx1, vy1, vx2, vy2);
				}
			}
			opx = px;
			opy = py;
		}
	}
	private int xtv (double v) {
		return mtv(v, getWidth());
	}
	private int ytv (double v) {
		return mtv(v, getHeight());
	}
	private static int mtv (double v, int m) {
		return (int) (((v + 1) * (m - 50)) / 2) + 25;
	}
}













