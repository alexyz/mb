package mb;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * TODO
 * menu (reset, itdepth, function, bb...)
 * right click to cancel zoom
 * pan/zoom controls [ < > ^ v + - r i w h ]
 * history stack
 * lightness control
 * image save
 * progressive render, e.g. itdepth 16, quarter resolution
 */
public class MBJFrame extends JFrame {
	
	public static final BlockingQueue<Runnable> QUEUE = new LinkedBlockingQueue<>();
	
	public static void main (final String[] args) {
		final JFrame f = new MBJFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(640, 480);
		f.repaint();
		f.show();
		final int procs = Runtime.getRuntime().availableProcessors();
		System.out.println("procs: " + procs);
		for (int n = 0; n < procs; n++) {
			new WorkerThread(n).start();
		}
	}
	
	public MBJFrame () {
		final MBJComponent c = new MBJComponent();
		final JSpinner s = new JSpinner(new SpinnerNumberModel(2.0, 1.01, 4.00, 0.01));
		s.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				double v = (Double) s.getValue();
				v = Math.round(v * 100) / 100.0;
				System.out.println("v=" + v);
				c.setMBFunction(v == 2 ? MBFunction.sqpc : MBFunction.powpc(v));
				c.reimage();
			}
		});
		c.setMBFunction(MBFunction.sqpc);
		JPanel q = new JPanel();
		q.add(new JLabel("Power"));
		q.add(s);
		JPanel p = new JPanel(new BorderLayout());
		p.add(q, BorderLayout.NORTH);
		p.add(c, BorderLayout.CENTER);
		getContentPane().add(p);
		c.addPropertyChangeListener("title", new PropertyChangeListener() {
			@Override
			public void propertyChange (PropertyChangeEvent arg0) {
				setTitle(arg0.getNewValue().toString());
			}
		});
		
	}
	
	private static class WorkerThread extends Thread {
		public WorkerThread (int n) {
			setName("Worker-" + n);
			setPriority(Thread.MIN_PRIORITY);
			setDaemon(true);
		}
		@Override
		public void run () {
			while (true) {
				try {
					QUEUE.take().run();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
