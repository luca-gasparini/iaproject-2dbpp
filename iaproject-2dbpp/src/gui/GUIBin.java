package gui;

import logic.Bin;
import logic.Packet;

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
import javax.swing.JPanel;

/**
 * Da decidere con Nicola C.
 *
 */
public class GUIBin extends JPanel {

	private static final long serialVersionUID = -6647245677472183286L; 

	private Bin singleBin;
			
	public GUIBin (Bin s) {
		this.singleBin = s;
	}
	
	protected void paintComponent(Graphics g) {
	
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Dimension dimBorderBin = new Dimension(singleBin.getWidth(), singleBin.getHeight());
		Rectangle borderBin = new Rectangle(dimBorderBin);
		super.setSize(dimBorderBin);
		g2d.setColor(Color.lightGray); // colore a piacere (per far risaltare la frontiera);
		g2d.fill(borderBin);
		g2d.draw(borderBin);
		
		Iterator<Packet> itPacket = singleBin.getIteratorList();
		
		while (itPacket.hasNext()) {
				
			Packet currentPacket = itPacket.next();
				
			g2d.setColor(currentPacket.getColor());
			Rectangle packetRect = buildRectangleFromPacket(currentPacket);
			g2d.fill(packetRect);
			g2d.draw(packetRect);
		}
	}

	private int getPosYCorrect(Packet p) {
		if (!(p.isRotate()))
			return singleBin.getHeight()-p.getHeight();
		else
			return singleBin.getHeight()-p.getWidth();
	}
	
	private Rectangle buildRectangleFromPacket(Packet p) {
		
		Dimension d;
		Point coordinate = new java.awt.Point(p.getPointX(), getPosYCorrect(p));
		
		if (!(p.isRotate()))
			d = new Dimension(p.getWidth(), p.getHeight());
		else
			d = new Dimension(p.getHeight(), p.getWidth());
	
		Rectangle packetRect = new Rectangle(coordinate, d);
		return packetRect;
	}
	
	public String toString() {
		return Integer.toString(this.singleBin.getID());
	}
}
