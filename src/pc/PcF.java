package pc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.List;
import java.util.Properties;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import static java.lang.Math.*;

/**
 * draw a parametric curve
 */
public class PcF extends JFrame {
	public static void main (String[] args) throws Exception {
		CF f = new CF() {
			@Override
			void get (CR r, double p) {
				r.x = validate(sin(p * PI * 4) / 2);
				r.dx = validate(cos(p * PI * 4));
				r.y = validate(cos(p * PI * 2));
				r.dy = validate(-sin(p * PI * 2));
				r.w = ((sin(p * PI * 10) + 1) / 2) * 0.025 + 0.025;
			}
		};
		QO qo = run(f, 100);
		JAXBContext jc = JAXBContext.newInstance(QO.class);
		Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(qo, System.out);
		PcF fr = new PcF(qo);
		fr.show();
	}
	public PcF (QO qo) {
		super("PcF"); 
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		CC c = new CC(qo);
		JPanel p = new JPanel(new GridLayout(1, 2));
		p.add(c);
		setContentPane(p);
		setPreferredSize(new Dimension(800, 800));
		pack();
	}
	static QO run(CF f, int seg) {
		QO qo = new QO();
		P opt1 = null, opt2 = null;
		CR r = new CR();
		for (int n = 0; n <= seg; n++) {
			final double p = ((double)n) / seg;
			f.get(r, p);
			final double z = 0;
			final double h = hypot(r.dx, r.dy);
			final double dxn = r.dx / h;
			final double dyn = r.dy / h;
			final double a = atan2(dyn, dxn);
			final double s = sin(a) * -r.w;
			final double c = cos(a) * r.w;
			final P pt1 = new P(r.x + s, r.y + c, z);
			final P pt2 = new P(r.x - s, r.y - c, z);
			if (opt1 != null && opt2 != null) {
				Q q = new Q(opt1, opt2, pt1, pt2, Color.blue);
				q.s = "" + p;
				qo.l.add(q);
			}
			opt1 = pt1;
			opt2 = pt2;
		}
		return qo;
	}
}

class CC extends JComponent {
	private QO qo;
	public CC (QO qo) {
		this.qo = qo;
	}
	@Override
	protected void paintComponent (final Graphics g) {
		for (Q q : qo.l) {
			final int x1 = xtv(q.a.x);
			final int y1 = ytv(q.a.y);
			final int x2 = xtv(q.b.x);
			final int y2 = ytv(q.b.y);
			g.setColor(q.col);
			g.drawLine(x1, y1, x2, y2);
			g.drawString(q.s, x1, y1);
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








