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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.BinConfiguration;

/**
 * Da decidere con Nicola C.
 *
 */
public class GUIBin extends ResizableRawGraphics {

	private static final long serialVersionUID = -6647245677472183286L; 
	
	private static final int defaultFontSize = 16;
	private static final int minFontSize = 6;
	
	private final BinConfiguration binConf;
	private final int binID;
	private final float density;
	private final List<GUIPacket> packets;
	
	public GUIBin (BinConfiguration binConf, int binID, float density, List<GUIPacket> packets) {
		super(binConf.getSize());
		
		if (packets == null) {
			throw new NullPointerException("null packets");
		}
		
		this.binConf = binConf;
		this.binID = binID;
		this.density = density;
		
		this.packets = Collections.unmodifiableList(new ArrayList<GUIPacket>(packets));
		
		this.setToolTipText("<HTML>"+
				"<p>N. packets = "+packets.size()+"</p>"+
				"<p>Density = "+density+"</p>"+
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
				binConf.getWidth()*super.getMagnificationFactor(),
				binConf.getHeight()*super.getMagnificationFactor());
		
		Rectangle borderBin = new Rectangle(dimBorderBin);
		
		g2d.setColor(Color.lightGray); // colore a piacere (per far risaltare la frontiera);
		g2d.fill(borderBin);
		g2d.setColor(Color.BLACK);
		g2d.draw(borderBin);
		
		// disegno packet
		for (GUIPacket currentPacket: this.packets) {
						
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

	private int getPosYCorrect(GUIPacket p) {
		//if (!(p.isRotate()))
		return (binConf.getHeight()-p.getHeight()-p.getPointY())*super.getMagnificationFactor();
		/*else
			return (singleBin.getHeight()-p.getWidth()-p.getPointY())*super.getMagnificationFactor();*/
	}
	
	private Rectangle buildRectangleFromPacket(GUIPacket p) {
		
		Dimension d;
		Point coordinate = new Point(p.getPointX()*super.getMagnificationFactor(), getPosYCorrect(p));
		
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
		return Integer.toString(binID);
	}
	
	public int getNPackets() {
		return this.packets.size();
	}
	
	private Map<TextAttribute, Object> getTextAttributes(int size) {
		Map<TextAttribute, Object> atbs = new HashMap<TextAttribute, Object>();
		
		atbs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		atbs.put(TextAttribute.SIZE, Integer.valueOf(size));
		atbs.put(TextAttribute.FAMILY, "Arial");
		return atbs;
	}
	
	private void drawNumberIntoPacket(Graphics2D g2d, Font font, GUIPacket currentPacket, Rectangle packetRect) {
		
		int textWidth, textHeight;
		String idString;
	
		FontRenderContext frc = g2d.getFontRenderContext();
		
		// resize if necessary
		do {
			idString = Integer.toString(currentPacket.getID());
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((binConf == null) ? 0 : binConf.hashCode());
		result = prime * result + binID;
		result = prime * result + Float.floatToIntBits(density);
		result = prime * result + ((packets == null) ? 0 : packets.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof GUIBin)) {
			return false;
		}
		GUIBin other = (GUIBin) obj;
		if (!binConf.equals(other.binConf)) {
			return false;
		}
		if (binID != other.binID) {
			return false;
		}
		if (Float.floatToIntBits(density) != Float
				.floatToIntBits(other.density)) {
			return false;
		}
		
		if (this.packets.size() != other.packets.size()) {
			return false;
		}
		for (GUIPacket packet : other.packets) {
			if (!this.packets.contains(packet)) {
				return false;
			}
		}
		
		return true;
	}

	public float getDensity() {
		return this.density;
	}

	public BinConfiguration getBinConfiguration() {
		return this.binConf;
	}
	
	public List<GUIPacket> getPackets() {
		return this.packets;
	}
	
}
