package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public abstract class ResizableRawGraphics extends JPanel {

	private static final long serialVersionUID = -4256966040512380886L;
	
	private int magnificationFactor = 1;
	private Dimension baseDimension;
	private Dimension magnifiedDimension;
	
	public ResizableRawGraphics(Dimension baseDimension) {
		super();
		
		this.setOpaque(false);
		this.baseDimension = new Dimension(baseDimension);
		this.magnifiedDimension = this.baseDimension;
		
		assert this.baseDimension != null : "baseDimension is null";
	}
	
	@Override
	public Dimension getPreferredSize() {
		return this.getSize();
	}
	
	@Override
	public Dimension getMaximumSize() {
		return this.getSize();
	}
	
	@Override
	public Dimension getMinimumSize() {
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
