package model;
import java.awt.Point;
import javafx.scene.canvas.*;
import javafx.scene.image.*;

/**
 * The Picture class is one of the concrete children of the abstract PaintObject class.
 * @author Rajeev Ram, SL: Junting Lye, 02/22/18
 */
public class Picture extends PaintObject {

	// The default Serializable ID
	private static final long serialVersionUID = 1L;
	
	// The image file string
	private final String doge = "file:images/doge.jpeg";
	
	// Calls the super constructor
	public Picture(Point to, Point from) {
		super(null,to,from);
	}

	// Inherited draw method is implemented
	@Override
	public void draw(GraphicsContext gc) {
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
		gc.drawImage(new Image(doge), from.getX(), from.getY(), to.getX()-from.getX(), to.getY()-from.getY());
	}

}
