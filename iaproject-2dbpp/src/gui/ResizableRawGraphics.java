package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.border.Border;

public abstract class ResizableRawGraphics extends JPanel {

	private static final long serialVersionUID = -4256966040512380886L;
	
	private int magnificationFactor = 3;
	private Dimension baseDimension;
	private Dimension magnifiedDimension;
	
	/**
	 * 
	 * @param baseDimension the dimension used as base dimension; in fact
	 * actual base dimension is 1px higher & wider because, otherwise, last
	 * row & column's pixel won't be painted
	 */
	public ResizableRawGraphics(Dimension baseDimension) {
		super();
		
		this.setOpaque(false);
		// the +1 is needed because, otherwise, the last pixel won't be painted
		this.baseDimension = new Dimension(baseDimension.width + 1, baseDimension.height + 1);
		this.magnifiedDimension = this.baseDimension;
	}
	
	@Override
	public final Dimension getPreferredSize() {
		return this.getSize();
	}
	
	@Override
	public final Dimension getMaximumSize() {
		return this.getSize();
	}
	
	@Override
	public final Dimension getMinimumSize() {
		return this.getSize();
	}
	
	@Override
	public final Dimension getSize() {
		return new Dimension(this.magnifiedDimension);
	}
	
	public final int getMagnificationFactor() {
		return this.magnificationFactor;
	}
	
	public final void setMagnificationFactor(int newFactor) {
		if (newFactor <= 0) {
			throw new IllegalArgumentException("Factor must be strictly positive");
		}
		
		if (newFactor != this.magnificationFactor) {
			this.magnificationFactor = newFactor;
			if (this.magnificationFactor == 1) {
				this.magnifiedDimension = this.baseDimension;
			} else {
				this.magnifiedDimension = new Dimension(this.baseDimension.width * newFactor, 
						this.baseDimension.height * newFactor);
			}
		}
	}
	
	public Dimension getBaseDimension() {
		return new Dimension(this.baseDimension);
	}
	
	@Override
	@Deprecated
	/**
	 * Don't use this method!! If you want to add border to this grapic panel,
	 * you better place it inside another panel and add border to it.
	 * See http://leepoint.net/notes-java/GUI-appearance/borders/10borders.html
	 * (shortly border has to be PAINTED by the doPaint method and this may be
	 * not so intuitive - there would be the need of an horizontal and vertical
	 * offset entirely managed by the doPaint implementation)
	 */
	public final void setBorder(Border border) {
		// doNothing
	}
	
	@Override
	protected final void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		doPaint(g2d, this.magnificationFactor);
	}

	/**
	 * 
	 * @param g2d {@link Graphics} object (already casted to {@link Graphics2D})
	 * that should be used to paint this component.
	 * @param factor Magnification factor that should be used in painting
	 * the component
	 */
	protected abstract void doPaint(Graphics2D g2d, int factor);
	
}
