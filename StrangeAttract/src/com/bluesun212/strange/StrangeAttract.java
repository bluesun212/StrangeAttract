package com.bluesun212.strange;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class StrangeAttract extends JComponent implements Runnable, KeyListener {
	private static final long serialVersionUID = -4181909964048001644L;
	private BufferedImage screen;
	private Graphics2D gfx;
	private int[] raster;
	private int color;
	private double zoom = 40;
	
	private Random r = new Random();
	private double a;
	private double b;
	private double c;
	private double x;
	private double y;
	private int i;
	
	private int itColor = 10000;
	private int itSleep = 10000;
	private int itReset = 1000000;
	private long sleepTime = 50l;
	
	public static void main(String[] args) {
		new StrangeAttract();
	}
	
	public StrangeAttract() {
		// Create fullscreen window
		JFrame jf = new JFrame();
		jf.setUndecorated(true);
		setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		
		// Hide cursor
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		jf.getContentPane().setCursor(blankCursor);
		
		// Listen for ESC
		jf.addKeyListener(this);
		
		// Start up the window
		jf.getContentPane().add(this);
		jf.pack();
		jf.setVisible(true);
		
		// Set up the renderer
		screen = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
		raster = ((DataBufferInt)screen.getRaster().getDataBuffer()).getData();
		
		reset();
		new Thread(this).start();
	}
	
	public void reset() {
		// Randomize constants
		a = r.nextDouble();
		b = r.nextDouble();
		c = r.nextDouble();
		i = 0;
		x = 0;
		y = 0;
		
		// Clear buffer
		for (int xx = 0; xx < screen.getWidth(); xx++) {
			for (int yy = 0; yy < screen.getHeight(); yy++) {
				raster[yy * screen.getWidth() + xx] = 0;
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// Double buffering
		g.setColor(Color.black);
		g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		g.drawImage(screen, 0, 0, null);
	}
	
	@Override
	public void run() {
		while (true) {
			if (i++ % itColor == 0) { // Color changes
				color = r.nextInt();
			}
			
			// Get raw pixel coordinates
			double y2 = a - x;
			x = y - Math.signum(x) * Math.sqrt(Math.abs(b * x - c));
			y = y2;
			
			// Scale, center, and clamp
			int dx = (int)(x*zoom)+screen.getWidth()/2;
			int dy = (int)(y*zoom)+screen.getHeight()/2;
			drawPixel(dx, dy);
			//drawPixel(dx + 1, dy);
			//drawPixel(dx + 1, dy + 1);
			//drawPixel(dx, dy + 1);
			
			if (i % itSleep == 0) { // Time to sleep
				try {
					repaint();
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {}
			}
			
			if (i % itReset == 0 && i != 0) { // Time to reset
				reset();
			}
		}
	}
	
	public void drawPixel(int dx, int dy) {
		if (dx >= 0 && dx < screen.getWidth() && 
			dy >= 0 && dy < screen.getHeight() &&
			raster[dy * screen.getWidth() + dx] == 0) {
			raster[dy * screen.getWidth() + dx] = color;
		}
	}
	
	// Handle ESC presses
	public void keyPressed(KeyEvent arg0) {
		System.exit(0);
	}
	
	public void keyTyped(KeyEvent arg0) {}
	public void keyReleased(KeyEvent arg0) {}
}
