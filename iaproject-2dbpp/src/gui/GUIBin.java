package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.Bin;
import logic.Packet;

/**
 * Da decidere con Nicola C.
 *
 */
public class GUIBin extends ResizableRawGraphics {

	private static final long serialVersionUID = -6647245677472183286L; 
	
	private final Bin singleBin;
	private static final int defaultFontSize = 16;
	private static final int minFontSize = 6;
	
	public GUIBin (Bin s) {
		super(new Dimension(s.getWidth(), s.getHeight()));
		this.singleBin = s;
		this.setToolTipText("<HTML>"+
				"<p>N. packets = "+s.getNPackets()+"</p>"+
				"<p>Density = "+s.getDensity()+"</p>"+
				"</HTML>"
				);
	}
	
	@Override
	protected void doPaint(Graphics2D g2d, int factor) {
		// impostazioni rendering
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		Font font = new Font(getTextAttributes(GUIBin.defaultFontSize));
		g2d.setFont(font);
		
		// disegno bin
		Dimension dimBorderBin = new Dimension(
				singleBin.getWidth()*super.getMagnificationFactor(),
				singleBin.getHeight()*super.getMagnificationFactor());
		
		Rectangle borderBin = new Rectangle(dimBorderBin);
		
		g2d.setColor(Color.lightGray); // colore a piacere (per far risaltare la frontiera);
		g2d.fill(borderBin);
		g2d.setColor(Color.BLACK);
		g2d.draw(borderBin);
		
		// disegno packet
		List<Packet> listPacket = singleBin.getList();
		
		for (Packet currentPacket: listPacket) {
						
			// rettangolo packet
			g2d.setColor(currentPacket.getColor());
			Rectangle packetRect = buildRectangleFromPacket(currentPacket);
			g2d.fill(packetRect);
			g2d.setColor(Color.black);
			g2d.draw(packetRect);
			
			// stringa id packet
			drawNumberIntoPacket(g2d, font, currentPacket, packetRect);
		}
		
	}

	private int getPosYCorrect(Packet p) {
		//if (!(p.isRotate()))
		return (singleBin.getHeight()-p.getHeight()-p.getPointY())*super.getMagnificationFactor();
		/*else
			return (singleBin.getHeight()-p.getWidth()-p.getPointY())*super.getMagnificationFactor();*/
	}
	
	private Rectangle buildRectangleFromPacket(Packet p) {
		
		Dimension d;
		Point coordinate = new java.awt.Point(p.getPointX()*super.getMagnificationFactor(), getPosYCorrect(p));
		
		int height = p.getHeight()*super.getMagnificationFactor();
		int width = p.getWidth()*super.getMagnificationFactor();
		
		//if (!(p.isRotate()))
		d = new Dimension(width, height);
		/*else
			d = new Dimension(height, width);*/
	
		Rectangle packetRect = new Rectangle(coordinate, d);
		
		return packetRect;
	}
	
	public String getID() {
		return Integer.toString(this.singleBin.getID());
	}
	
	public Bin getBin() {
		return this.singleBin;
	}
	
	/**
	 * Two {@link GUIBin} are the same when have same magnification factor and
	 * contained {@link Bin}.
	 */
	@Override
	public boolean equals(Object gb) {
		if (gb == null)
			return false;
		
		if (this == gb)
			return true;
		
		if (!(gb instanceof GUIBin))
			return false;
		
		GUIBin gbp = (GUIBin) gb;
		return this.getMagnificationFactor() == gbp.getMagnificationFactor() && 
				this.singleBin.equals(gbp.getBin());
	}
	
	private Map<TextAttribute, Object> getTextAttributes(int size) {
		Map<TextAttribute, Object> atbs = new HashMap<TextAttribute, Object>();
		
		atbs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		atbs.put(TextAttribute.SIZE, size);
		atbs.put(TextAttribute.FAMILY, "Arial");
		return atbs;
	}
	
	private void drawNumberIntoPacket(Graphics2D g2d, Font font, Packet currentPacket, Rectangle packetRect) {
		
		int textWidth, textHeight;
		String idString;
	
		FontRenderContext frc = g2d.getFontRenderContext();
		
		// resize if necessary
		do {
			idString = Integer.toString(currentPacket.getId());
			textWidth = (int)font.getStringBounds(idString, frc).getWidth();
			textHeight = (int)font.getStringBounds(idString, frc).getHeight();
			font = new Font(getTextAttributes(font.getSize()-2));
		} while (
				Math.min(packetRect.getWidth() - textWidth, packetRect.getHeight()-textHeight)<0 && // piccolo
				font.getSize()>GUIBin.minFontSize // ma non troppo
				);
		
		g2d.setColor(Color.black);
		g2d.setFont(font);
		g2d.drawString(idString, (int)packetRect.getCenterX()-textWidth/2, (int)packetRect.getCenterY()+textHeight/2);
	}
	
}
