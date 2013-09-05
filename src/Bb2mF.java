import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JFrame;

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
public class Bb2mF extends JFrame {
	
	static final BlockingQueue<Runnable> q = new LinkedBlockingQueue<Runnable>();
	
	public static void main(final String[] args) {
		final JFrame f = new Bb2mF();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(640, 480);
		f.repaint();
		f.show();
		final int procs = Runtime.getRuntime().availableProcessors();
		System.out.println("procs: " + procs);
		for (int n = 0; n < procs; n++) {
			final Thread t = new Thread(new WorkerRunnable());
			t.setName("Worker-" + n);
			t.setPriority(Thread.MIN_PRIORITY);
			t.setDaemon(true);
			t.start();
		}
	}
	
	public Bb2mF() {
		Bb2mC c = new Bb2mC();
		getContentPane().add(c);
		c.addPropertyChangeListener("title", new PropertyChangeListener() {
			@Override
			public void propertyChange (PropertyChangeEvent arg0) {
				setTitle(arg0.getNewValue().toString());
			}
		});
	}
	
}

class WorkerRunnable implements Runnable {
	
	@Override
	public void run() {
		while (true) {
			try {
				Bb2mF.q.take().run();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
