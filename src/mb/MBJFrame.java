package mb;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

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
	
	public MBJFrame () {
		mbComp = new MBJComponent();
		
		final JComboBox<MBIteration> functionCombo = new JComboBox<>(new DefaultComboBoxModel<>(MBIteration.all()));
		functionCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged (ItemEvent e) {
				mbComp.getMbImage().mbIteration = (MBIteration) e.getItem();
				mbComp.reimage();
			}
		});
		
//		final JSpinner powerSpinner = new JSpinner(new SpinnerNumberModel(2.0, 1.01, 4.00, 0.001));
//		powerSpinner.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged (ChangeEvent e) {
//				double v = (Double) powerSpinner.getValue();
//				v = Math.round(v * 1000) / 1000.0;
//				powerSpinner.setValue(v);
//				mbComp.getMbImage().mbIteration = MBIteration.getMBIteration(v);
//				mbComp.reimage();
//			}
//		});
		
		final JSpinner itSpinner = new JSpinner(new SpinnerNumberModel(mbComp.getMbImage().iterationDepth, 31, 255, 32));
		itSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				mbComp.getMbImage().iterationDepth = (Integer) itSpinner.getValue();
				mbComp.reimage();
			}
		});
		
		final JSpinner boundSpinner = new JSpinner(new SpinnerNumberModel(mbComp.getMbImage().bound, 0.5, 16, 0.1));
		boundSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged (ChangeEvent e) {
				double v = (Double) boundSpinner.getValue();
				v = Math.round(v * 10) / 10.0;
				mbComp.getMbImage().bound = (Double) boundSpinner.getValue();
				mbComp.reimage();
			}
		});
		
		final JButton centreButton = new JButton("Centre");
		centreButton.setMargin(new Insets(0,0,0,0));
		centreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				mbComp.getMbImage().centre();
				mbComp.reimage();
			}
		});
		
		final JButton exportButton = new JButton("Export");
		exportButton.setMargin(new Insets(0,0,0,0));
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent ae) {
				queue.add(new Runnable() {
					@Override
					public void run () {
						long t = System.nanoTime();
						BufferedImage image = export();
						t = System.nanoTime() - t;
						System.out.println("export time: " + (t / 1000000.0) + " ms");
						String date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
						File f = new File("mbimage." + date + ".png");
						try {
							ImageIO.write(image, "png", f);
							System.out.println("wrote " + f);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(MBJFrame.this, "could not write " + f + ":\n" + e.toString());
						}
					}
				});
				updateTitle();
			}
		});
		
		JToggleButton juliaButton = new JToggleButton("Julia");
		juliaButton.setMargin(new Insets(0,0,0,0));
		juliaButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged (ItemEvent e) {
				mbComp.setJulia(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(new JLabel("Function"));
		buttonPanel.add(functionCombo);
//		buttonPanel.add(new JLabel("Power"));
//		buttonPanel.add(powerSpinner);
		buttonPanel.add(new JLabel("Depth"));
		buttonPanel.add(itSpinner);
		buttonPanel.add(new JLabel("Bound"));
		buttonPanel.add(boundSpinner);
		buttonPanel.add(centreButton);
		buttonPanel.add(juliaButton);
		buttonPanel.add(exportButton);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(buttonPanel, BorderLayout.NORTH);
		contentPanel.add(mbComp, BorderLayout.CENTER);
		getContentPane().add(contentPanel);
		
		mbComp.addPropertyChangeListener(MBJComponent.POSITION, new PropertyChangeListener() {
			@Override
			public void propertyChange (PropertyChangeEvent arg0) {
				updateTitle();
			}
		});
		
		updateTitle();
		
		final int procs = Runtime.getRuntime().availableProcessors();
		System.out.println("procs: " + procs);
		for (int n = 0; n < Math.max(1, procs - 1); n++) {
			new WorkerThread(n).start();
		}
	}
	
	private void updateTitle () {
		MBImage i = mbComp.getMbImage();
		Complex o = new Complex(i.topLeft);
		Complex s = new Complex(i.size);
		s.div(2, 0);
		o.add(s);
		String p = o + ", " + i.size;
		if (i.julia != null) {
			p += ", " + i.julia;
		}
		setTitle(String.format("MBEx [%s] [%d]", p, queue.size() + running.get()));
	}
	
	private BufferedImage export() {
		int w = 1920;
		int h = 1080;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		MBImage mbImage = new MBImage(mbComp.getMbImage());
		mbImage.bound = 16;
		mbImage.iterationDepth = 255;
		mbImage.calc(image.getRaster(), 0, 0, w, h);
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
