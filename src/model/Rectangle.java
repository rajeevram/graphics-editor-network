package model;
import java.awt.Color;
import java.awt.Point;
import javafx.scene.canvas.*;

/**
 * The Rectangle class is one of the concrete children of the abstract PaintObject class.
 * @author Rajeev Ram, SL: Junting Lye, 02/22/18
 */
public class Rectangle extends PaintObject {
	
	// The default Serializable ID
	private static final long serialVersionUID = 1L;
	
	// Calls the super constructor
	public Rectangle(Color color, Point to, Point from) {
		super(color, to, from);
	}
	
	// Inherited draw method is implemented
	@Override
	public void draw(GraphicsContext gc) {
		gc.setFill(ColorTypeConverter.Awt2Fx(this.color));
		// The fromX should always be smaller than the toX
		if (to.x < from.x) {
			int temporary = from.x; 
			from.x = to.x; 
			to.x = temporary;
		}
		// The fromY should always be smaller than the toY
		if (to.y < from.y) {
			int temporary = from.y; 
			from.y = to.y; 
			to.y = temporary;
		}
		gc.fillRect(from.getX(), from.getY(), to.getX()-from.getX(), to.getY()-from.getY());
	}

}
