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
		instance.setVisible(true);
	}
	
	public final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
	
	private final MBJComponent mbComp = new MBJComponent();
	private final AtomicInteger running = new AtomicInteger();
	private final JComboBox<MBFunction> functionCombo = new JComboBox<>(new DefaultComboBoxModel<>(MBFunction.all()));
	private final JSpinner powerReSpinner = new JSpinner(new SpinnerNumberModel(2, -10, 10, 0.001));
	private final JSpinner powerImSpinner = new JSpinner(new SpinnerNumberModel(0, -10, 10, 0.001));
	private final JComboBox<Integer> depthCombo = new JComboBox(new Integer[] { 16, 255, 1000, 5000, 25000, 100000, 250000, 1000000 });
	private final JSpinner boundSpinner = new JSpinner(new SpinnerNumberModel(2, 0.1, 100, 0.1));
	private final JButton centreButton = new JButton("Centre");
	private final JButton exportButton = new JButton("Export");
	private final JToggleButton juliaButton = new JToggleButton("Julia");
	private final JToggleButton gridButton = new JToggleButton("Grid");
	
	public MBJFrame () {
		
		functionCombo.addItemListener(e -> functionChanged(e));
		
		mbComp.image.function = (MBFunction) functionCombo.getModel().getSelectedItem();
		mbComp.image.params = new MBFunctionParams();
		
		depthCombo.setSelectedIndex(1);
		depthCombo.addItemListener(e -> depthChanged());
		mbComp.image.params.depth = ((Number) depthCombo.getSelectedItem()).intValue();
		
		boundSpinner.addChangeListener(e -> boundChanged());
		mbComp.image.params.bound = ((Number)boundSpinner.getValue()).doubleValue();

		powerReSpinner.addChangeListener(e -> powerChanged());
		powerReSpinner.setEnabled(mbComp.image.function.usesPower());
		
		powerImSpinner.addChangeListener(e -> powerChanged());
		powerImSpinner.setEnabled(mbComp.image.function.usesPower());
		
		double re = ((Number)powerReSpinner.getValue()).doubleValue();
		double im = ((Number)powerImSpinner.getValue()).doubleValue();
		mbComp.image.params.power = new Complex(re, im);
		
		centreButton.setMargin(new Insets(0,0,0,0));
		centreButton.addActionListener(e -> centre());
		
		exportButton.setMargin(new Insets(0,0,0,0));
		exportButton.addActionListener(ae -> exportPressed());
		
		juliaButton.setMargin(new Insets(0,0,0,0));
		juliaButton.addItemListener(e -> juliaToggled(e));
		
		gridButton.setMargin(new Insets(0,0,0,0));
		gridButton.addItemListener(e -> gridToggled(e));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(functionCombo);
		buttonPanel.add(new JLabel("Depth"));
		buttonPanel.add(depthCombo);
		buttonPanel.add(new JLabel("Bound"));
		buttonPanel.add(boundSpinner);
		buttonPanel.add(new JLabel("Power"));
		buttonPanel.add(powerReSpinner);
		buttonPanel.add(powerImSpinner);
		buttonPanel.add(centreButton);
		buttonPanel.add(juliaButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(gridButton);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(buttonPanel, BorderLayout.NORTH);
		contentPanel.add(mbComp, BorderLayout.CENTER);
		getContentPane().add(contentPanel);
		
		mbComp.addPropertyChangeListener(MBJComponent.POSITION, e -> updateTitle());
		
		updateTitle();
		
		final int procs = Runtime.getRuntime().availableProcessors();
		System.out.println("procs: " + procs);
		for (int n = 0; n < Math.max(1, procs - 1); n++) {
			new WorkerThread(n).start();
		}
		
		pack();
	}

	private void functionChanged (ItemEvent e) {
		MBFunction i = (MBFunction) e.getItem();
		powerReSpinner.setEnabled(i.usesPower());
		powerImSpinner.setEnabled(i.usesPower());
		mbComp.image.function = i;
		mbComp.refresh();
	}

	private void juliaToggled (ItemEvent e) {
		mbComp.setJulia(e.getStateChange() == ItemEvent.SELECTED);
	}
	
	private void gridToggled (ItemEvent e) {
		mbComp.setGrid(e.getStateChange() == ItemEvent.SELECTED);
	}

	private void exportPressed () {
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

	private void centre () {
		mbComp.image.centre();
		mbComp.refresh();
	}

	private void powerChanged () {
		double re = ((Number)powerReSpinner.getValue()).doubleValue();
		re = Math.round(re * 1000) / 1000.0;
		powerReSpinner.setValue(re);
		double im = ((Number)powerImSpinner.getValue()).doubleValue();
		im = Math.round(im * 1000) / 1000.0;
		powerImSpinner.setValue(im);
		mbComp.image.params.power = new Complex(re, im);
		mbComp.refresh();
	}

	private void depthChanged () {
		mbComp.image.params.depth = ((Number) depthCombo.getSelectedItem()).intValue();
		mbComp.refresh();
	}

	private void boundChanged () {
		double v = ((Number) boundSpinner.getValue()).doubleValue();
		v = Math.round(v * 10) / 10.0;
		boundSpinner.setValue(v);
		mbComp.image.params.bound = v;
		mbComp.refresh();
	}
	
	private void updateTitle () {
		setTitle(String.format("MBEx [%s, %s]", queue.size(), running.get()));
	}
	
	private BufferedImage export() {
		int w = 1920;
		int h = 1080;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		MBImage mbImage = new MBImage(mbComp.image);
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
