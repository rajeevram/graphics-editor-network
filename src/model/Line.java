package model;
import java.awt.Color;
import java.awt.Point;
import javafx.scene.canvas.*;

/**
 * The Line class is one of the concrete children of the abstract PaintObject class.
 * @author Rajeev Ram, SL: Junting Lye, 02/22/18
 */
public class Line extends PaintObject {
	
	// The default Serializable ID
	private static final long serialVersionUID = 1L;

	// Calls the super constructor
	public Line(Color color, Point to, Point from) {
		super(color, to, from);
	}

	// Inherited draw method is implemented
	@Override
	public void draw(GraphicsContext gc) {
		gc.setStroke(ColorTypeConverter.Awt2Fx(this.color));
		gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
	}

}
