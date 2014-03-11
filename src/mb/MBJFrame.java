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

import javax.imageio.ImageIO;
import javax.swing.JButton;
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
 * image save
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
		final MBJComponent mbComp = new MBJComponent();
		mbComp.setMBFunction(MBFunction.sqpc);
		
		final JSpinner powerSpinner = new JSpinner(new SpinnerNumberModel(2.0, 1.01, 4.00, 0.001));
		powerSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				double v = (Double) powerSpinner.getValue();
				v = Math.round(v * 1000) / 1000.0;
				powerSpinner.setValue(v);
				mbComp.setMBFunction(v == 2 ? MBFunction.sqpc : MBFunction.powpc(v));
				mbComp.reimage();
			}
		});
		
		final JSpinner itSpinner = new JSpinner(new SpinnerNumberModel(mbComp.getItdepth(), 15, 255, 16));
		itSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				mbComp.setItDepth((Integer) itSpinner.getValue());
				mbComp.reimage();
			}
		});
		
		final JSpinner boundSpinner = new JSpinner(new SpinnerNumberModel(mbComp.getBound(), 0.5, 10, 0.1));
		boundSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				mbComp.setBound((Double) boundSpinner.getValue());
				mbComp.reimage();
			}
		});
		
		final JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				mbComp.reset();
			}
		});
		
		final JButton saveButton = new JButton("Export");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent ae) {
				DisplayMode dm = getGraphicsConfiguration().getDevice().getDisplayMode();
				long t = System.nanoTime();
				BufferedImage image = mbComp.export(dm.getWidth(), dm.getHeight());
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
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(new JLabel("Power"));
		buttonPanel.add(powerSpinner);
		buttonPanel.add(new JLabel("Depth"));
		buttonPanel.add(itSpinner);
		buttonPanel.add(new JLabel("Bound"));
		buttonPanel.add(boundSpinner);
		buttonPanel.add(saveButton);
		buttonPanel.add(resetButton);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(buttonPanel, BorderLayout.NORTH);
		contentPanel.add(mbComp, BorderLayout.CENTER);
		getContentPane().add(contentPanel);
		mbComp.addPropertyChangeListener("title", new PropertyChangeListener() {
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
