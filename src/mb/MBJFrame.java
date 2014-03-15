package mb;

import java.awt.BorderLayout;
import java.awt.DisplayMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * TODO
 * menu (reset, itdepth, function, bb...)
 * right click to cancel zoom
 * pan/zoom controls [ < > ^ v + - r i w h ]
 * image save
 */
public class MBJFrame extends JFrame {
	
	public static final MBJFrame instance = new MBJFrame();
	
	public static void main (final String[] args) {
		instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		instance.setSize(640, 480);
		instance.repaint();
		instance.setVisible(true);
	}
	
	public final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
	private final MBJComponent mbComp;
	private final AtomicInteger running = new AtomicInteger();
	private String position;
	
	public MBJFrame () {
		mbComp = new MBJComponent();
		
		final JSpinner powerSpinner = new JSpinner(new SpinnerNumberModel(2.0, 1.01, 4.00, 0.001));
		powerSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				double v = (Double) powerSpinner.getValue();
				v = Math.round(v * 1000) / 1000.0;
				powerSpinner.setValue(v);
				mbComp.getMbImage().mbFunction = MBFunction.getFunction(v);
				mbComp.reimage();
			}
		});
		
		final JSpinner itSpinner = new JSpinner(new SpinnerNumberModel(mbComp.getMbImage().iterationDepth, 15, 255, 16));
		itSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				mbComp.getMbImage().iterationDepth = (Integer) itSpinner.getValue();
				mbComp.reimage();
			}
		});
		
		final JSpinner boundSpinner = new JSpinner(new SpinnerNumberModel(mbComp.getMbImage().bound, 0.5, 10, 0.1));
		boundSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				mbComp.getMbImage().bound = (Double) boundSpinner.getValue();
				mbComp.reimage();
			}
		});
		
		final JButton resetButton = new JButton("Centre");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				mbComp.recentre();
				mbComp.reimage();
			}
		});
		
		final JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent ae) {
				final DisplayMode dm = getGraphicsConfiguration().getDevice().getDisplayMode();
				queue.add(new Runnable() {
					@Override
					public void run () {
						long t = System.nanoTime();
						BufferedImage image = export(dm.getWidth(), dm.getHeight());
						t = System.nanoTime() - t;
						System.out.println("export time: " + (t / 1000000.0) + " ms");
						int n = 0;
						while (true) {
							File f = new File("mbimage" + n + ".png");
							if (!f.exists()) {
								try {
									System.out.println("write " + f.getAbsolutePath());
									ImageIO.write(image, "png", f);
									break;
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							}
							n++;
						}
					}
				});
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(new JLabel("Power"));
		buttonPanel.add(powerSpinner);
		buttonPanel.add(new JLabel("Depth"));
		buttonPanel.add(itSpinner);
		buttonPanel.add(new JLabel("Bound"));
		buttonPanel.add(boundSpinner);
		buttonPanel.add(resetButton);
		buttonPanel.add(exportButton);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(buttonPanel, BorderLayout.NORTH);
		contentPanel.add(mbComp, BorderLayout.CENTER);
		getContentPane().add(contentPanel);
		
		mbComp.addPropertyChangeListener("title", new PropertyChangeListener() {
			@Override
			public void propertyChange (PropertyChangeEvent arg0) {
				position = arg0.getNewValue().toString();
				updateTitle();
			}
		});
		
		final int procs = Runtime.getRuntime().availableProcessors();
		System.out.println("procs: " + procs);
		for (int n = 0; n < procs; n++) {
			new WorkerThread(n).start();
		}
	}
	
	private void updateTitle () {
		setTitle(String.format("MBEx [%s] [%d]", position, queue.size() + running.get()));
	}
	
	private BufferedImage export(int w, int h) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		new MBImage(mbComp.getMbImage()).calc(image, 0, 0, w, h);
		return image;
	}
	
	private class WorkerThread extends Thread {
		public WorkerThread (int n) {
			setName("Worker-" + n);
			setPriority(Thread.MIN_PRIORITY);
			setDaemon(true);
		}
		@Override
		public void run () {
			while (true) {
				try {
					Runnable r = queue.take();
					running.incrementAndGet();
					r.run();
					running.decrementAndGet();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run () {
							updateTitle();
						}
					});
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
