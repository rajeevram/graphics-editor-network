package model;
import java.awt.Color;
import java.awt.Point;
import java.io.*;
import javafx.scene.canvas.*;

/**
 * This is an abstract class for all PaintObjects. There are four concrete children:
 * Line, Rectangle, Oval, and Picture
 * 
 * @author Rajeev Ram, SL: Junting Lye, 02/22/18
 */
public abstract class PaintObject implements Serializable {

	// The default Serializable ID
	private static final long serialVersionUID = 1L;
	
	// The instance variables are protected
	protected Color color;
	protected Point to;
	protected Point from;
	
	// The parent constructor
	public PaintObject(Color color, Point to, Point from) {
		this.color = color; this.to = to; this.from = from;
	}
	
	// The accessor and mutator methods
	public Color getColor() {
		return this.color;
	}
	
	public Point getToPoint() {
		return this.to;
	}
	
	public Point getFromPoint() {
		return this.from;
	}
	
	// Will be implemented in child classes
	public abstract void draw(GraphicsContext gc);
	
	// Just for testing purposes
	@Override
	public String toString() {
		return (getClass()+" "+color+" ("+from.x+","+from.y+") ("+to.x+","+to.y+")");
	}
	
	// Check if two PaintObjects are the same
	@Override
	public boolean equals(Object object) {
		if ( (object instanceof PaintObject)==false ) {
			return false;
		}
		PaintObject compare = (PaintObject) object;
		if (this.getClass() != compare.getClass()) {
			return false;
		}
		if (this.getColor() != compare.getColor()) {
			return false;
		}
		if (this.getToPoint() != compare.getToPoint()) {
			return false;
		}
		if (this.getFromPoint() != compare.getFromPoint()) {
			return false;
		}
		return true;
		
	}
	
}
