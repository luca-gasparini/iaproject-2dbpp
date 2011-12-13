package gui;

import logic.Bin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.RenderingHints;

import java.util.Iterator;

import javax.swing.JApplet;

import gui.GUIPacket;

/**
 * Da decidere con Nicola C.
 *
 */
public class GUIBin extends Bin {

	private final Rectangle contorno;
	
	public GUIBin(int id, int width, int height) {
		super(id, width, height);
		this.contorno = new Rectangle(0, 0, width, height);
	}
	
	protected class GUIBinJApplet extends JApplet {

		private static final long serialVersionUID = -6351046062490712529L;

		public void init() {
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
		}
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Dimension d = getSize();
			
			BasicStroke stroke = new BasicStroke(1.0f); // per disegnare linea bordo rettangolo
			
			g2.setStroke(stroke);
			g2.draw(contorno);
						
			Iterator<GUIPacket> itPacket = getIteratorList();
			
			while (itPacket.hasNext()) {
				
				GUIPacket currentPacket = itPacket.next();
				
				g2.setPaint(currentPacket.getColor());
				Rectangle packetRect = currentPacket.getRectangle();
				packetRect.setLocation(currentPacket.getPointX(), getPosYCorrect(currentPacket));
				g2.fill(currentPacket.getRectangle());
			}
			
		}
	}
	
	public int getPosYCorrect(GUIPacket p) {
		return this.getHeight()-p.getHeight();
	}

	public JApplet getJAppletBin () {
		return new GUIBinJApplet();
	}
}
