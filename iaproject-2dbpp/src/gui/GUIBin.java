package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.util.Iterator;

import logic.Bin;
import logic.Packet;

/**
 * Da decidere con Nicola C.
 *
 */
public class GUIBin extends ResizableRawGraphics {

	private static final long serialVersionUID = -6647245677472183286L; 
	
	
	private final Bin singleBin;
	private final int defaultFontSize;
	
	public GUIBin (Bin s) {
		super(new Dimension(s.getWidth(), s.getHeight()));
		this.singleBin = s;
		this.defaultFontSize = 6;
	}
	
	@Override
	protected void doPaint(Graphics2D g2d, int factor) {
		
		// impostazioni carattere
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("Arial", Font.TRUETYPE_FONT, this.getFontSize());
		g2d.setFont(font);
		
		FontRenderContext frc = g2d.getFontRenderContext();
		
		// disegno bin
		Dimension dimBorderBin = new Dimension(
				singleBin.getWidth()*super.getMagnificationFactor(),
				singleBin.getHeight()*super.getMagnificationFactor());
		
		Rectangle borderBin = new Rectangle(dimBorderBin);
		super.setSize(dimBorderBin);
		g2d.setColor(Color.lightGray); // colore a piacere (per far risaltare la frontiera);
		g2d.fill(borderBin);
		g2d.draw(borderBin);
		
		// disegno packet
		Iterator<Packet> itPacket = singleBin.getIteratorList();
		
		while (itPacket.hasNext()) {
				
			Packet currentPacket = itPacket.next();
			// rettangolo packet
			g2d.setColor(currentPacket.getColor());
			Rectangle packetRect = buildRectangleFromPacket(currentPacket);
			g2d.fill(packetRect);
			g2d.setColor(Color.black);
			g2d.draw(packetRect);
			
			// stringa id packet
			String idString = Integer.toString(currentPacket.getId());
			float sw = (float)font.getStringBounds(idString, frc).getWidth();
			LineMetrics lm = font.getLineMetrics(idString, frc);
			float sh = lm.getAscent() + lm.getDescent();
			g2d.drawString(idString, (int)packetRect.getCenterX()-sw/2, (int)packetRect.getCenterY()+sh/2);
		}
	}

	private int getPosYCorrect(Packet p) {
		if (!(p.isRotate()))
			return (singleBin.getHeight()-p.getHeight())*super.getMagnificationFactor();
		else
			return (singleBin.getHeight()-p.getWidth())*super.getMagnificationFactor();
		
	}
	
	private Rectangle buildRectangleFromPacket(Packet p) {
		
		Dimension d;
		Point coordinate = new java.awt.Point(p.getPointX()*super.getMagnificationFactor(), getPosYCorrect(p));
		
		int height = p.getHeight()*super.getMagnificationFactor();
		int width = p.getWidth()*super.getMagnificationFactor();
		
		if (!(p.isRotate()))
			d = new Dimension(width, height);
		else
			d = new Dimension(height, width);
	
		Rectangle packetRect = new Rectangle(coordinate, d);
		
		return packetRect;
	}
	
	public String toString() {
		return Integer.toString(this.singleBin.getID());
	}
	
	private int getFontSize() {
		return (int)Math.round(this.defaultFontSize*super.getMagnificationFactor()/1.5);
	}
	
}
