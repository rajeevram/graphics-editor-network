package controller_view;
import java.awt.Color;
import java.awt.Point;
import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import model.ColorTypeConverter;
import model.Line;
import model.Oval;
import model.PaintObject;
import model.Picture;
import model.Rectangle;

/**
 * A BorderPane for NetPaint that has all paint objects drawn on it. A Canvas 
 * exists in this BorderPane that will draw all PaintObjects stored in this 
 * hardCoded Vector<PaintObject> object.
 * 
 * @author Rajeev Ram, SL: Junting Lye, 02/27/18
 * (starter code provided by Rick Mercer)
 */
public class Client extends Application {

/*––––––––––The Instance Variables, Main, and Start––––––––––*/
	
	// The group of radio buttons and its toggle group
	final private RadioButton line = new RadioButton("Line");
	final private RadioButton rectangle = new RadioButton("Rectangle");
	final private RadioButton oval = new RadioButton("Oval"); 
	final private RadioButton picture = new RadioButton("Picture");
	private ToggleGroup buttonGroup = new ToggleGroup();
	
	// This image is used for the ghost shapes
	final private Image doge = new Image("file:images/doge.jpeg");

	// The color picker, canvas, and bottom HBox
	private Canvas canvas;
	private ColorPicker colorPicker = new ColorPicker();
	private HBox controlBox = new HBox();

	// The vector, socket, and input and output streams
	private Vector<PaintObject> paintObjectVector;
	private final static int PORTNUMBER = 4001;
	private Socket server;
	private ObjectInputStream inputFromServer;
	private ObjectOutputStream outputToServer;
	
	
	// Current state of the application
	private String currentType;
	private Color currentColor;
	private int currentFromX, currentFromY, currentToX, currentToY;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {	
		paintObjectVector = new Vector<PaintObject>();
		BorderPane pane = new BorderPane();

		canvas = new Canvas(800, 650);
		canvas.getGraphicsContext2D().setGlobalAlpha(1.0);
		pane.setTop(canvas);
		clearTheCanvas(canvas);

		pane.setBottom(controlBox);
		createControlBox();
		
		currentFromX = currentFromY = -1;
		canvas.setOnMouseMoved(new MoveDraw());
		canvas.setOnMouseClicked(new ClickDraw());
		canvas.setOnMouseExited(new ExitDraw());
		
		connectToServer();

		Scene scene = new Scene(pane, 800, 700);
		stage.setScene(scene);
		stage.setTitle("The CSc335 NetPaint Project – Rajeev Ram");
		stage.show();
	}
	
/*––––––––––The Construction & Styling Methods––––––––––*/	
	
	/**
	 * This method connects the Client to the Server through a Socket. It
	 * also starts a new Thread on a ServerReader Runnable.
	 */
	private void connectToServer() {
		try {
			//System.out.println("Trying to find the server.");
			server = new Socket("localhost", PORTNUMBER);
			//System.out.println("Found the server.");	
			Thread thread = new Thread(new ServerHandler());
			thread.start();
			//System.out.println("A new server reader thread has been started.");	
		} 
		catch (IOException e) {
			System.err.println("Could not connect to server.");
		}	
	}
		
	/**
	 * This method sets the background of the Canvas to Color.WHITE
	 * @param canvas – private field is passed a parameter to this method
	 */
	private void clearTheCanvas(Canvas canvas) {
		canvas.getGraphicsContext2D().setFill(javafx.scene.paint.Color.WHITE);
		canvas.getGraphicsContext2D().fillRect(0,0,800,650);
	}
	
	/**
	 * This method creates the bottom control panel with the radio buttons
	 * and the Color Picker.
	 */
	private void createControlBox() {

		controlBox.setPadding(new Insets(10,0,10,100));
		controlBox.setSpacing(40);

		ShapeHandler shapeHandler = new ShapeHandler();
		
		RadioButton[] shapesButtons = { line, rectangle, oval, picture };
		for (int i = 0; i < shapesButtons.length; i++) {                 
			shapesButtons[i].setToggleGroup(buttonGroup);
			shapesButtons[i].setFont(new Font("Verdana",13));
			shapesButtons[i].setOnAction(shapeHandler);
			controlBox.getChildren().add(shapesButtons[i]);
		}

		controlBox.getChildren().add(colorPicker);
		colorPicker.setValue(javafx.scene.paint.Color.BLACK);
		currentColor = ColorTypeConverter.Fx2Awt(colorPicker.getValue());
		colorPicker.setOnAction(new ColorHandler());
		HBox.setMargin(colorPicker, new Insets(-6,0,0,40));

	}

/*––––––––––The Paint Object Methods––––––––––*/
	
	/**
	 * This method creates a new PaintObject and adds it to the Vector. It also sends
	 * this new PaintObject to the server.
	 * @param fromX – the smaller x-value
	 * @param fromY – the smaller y-value
	 * @param toX – the larger x-value
	 * @param toY – the larger y-value
	 */
	private void createPaintObject(int fromX, int fromY, int toX, int toY) {
		PaintObject newPaintObject = null;
		switch(currentType) {
			case "LINE":
				newPaintObject = new Line(currentColor, new Point(toX,toY), new Point(fromX,fromY));
				addPaintObject(newPaintObject);
				break;
			case "RECTANGLE":
				newPaintObject = new Rectangle(currentColor, new Point(toX,toY), new Point(fromX,fromY));
				addPaintObject(newPaintObject);
				break;
			case "OVAL":
				newPaintObject = new Oval(currentColor, new Point(toX,toY), new Point(fromX,fromY));
				addPaintObject(newPaintObject);
				break;
			case "PICTURE":
				newPaintObject = new Picture(new Point(toX,toY), new Point(fromX,fromY));
				addPaintObject(newPaintObject);
				break;
		}
		// Send the new PainObject to the server
		try {
			outputToServer.writeObject(newPaintObject);
		} 
		catch (IOException e) {
			System.err.println("Something went wrong with sending the new paint object.");
			e.printStackTrace();
		}
	}
	
	/**
	 * The helper method for createPaintObject()
	 * @param paintObject – the new PaintObject to be added to the Vector
	 */
	private void addPaintObject(PaintObject paintObject) {
		paintObjectVector.add(paintObject);
	}
	
	/**
	 * This draws all PaintObjects that currently exists onto the Canvas
	 * @param paintObjectsVector – private field is passed as a parameter to this method
	 */
	private void drawAllPaintObjects(Vector<PaintObject> paintObjectsVector) {
		for (PaintObject paintObject : paintObjectsVector) {
			paintObject.draw(canvas.getGraphicsContext2D());
		}
		canvas.getGraphicsContext2D().setStroke(colorPicker.getValue());
		canvas.getGraphicsContext2D().setFill(colorPicker.getValue());
	}
	
	/**
	 * This method draws the ghost shapes as the mouse moves around
	 * @param fromX – the smaller x-value
	 * @param fromY – the smaller y-value
	 * @param toX – the larger x-value
	 * @param toY – the larger y-value
	 */
	private void drawGhostShape(double fromX, double fromY, double toX, double toY) {
		switch(currentType) {
			case "LINE":
				canvas.getGraphicsContext2D().strokeLine(fromX, fromY, toX, toY);
				break;
			case "RECTANGLE":
				// The from values should always be less than the to values
				if (toX < fromX) {
					double temporary = toX; toX = fromX;
					fromX = temporary;
				}
				if (toY < fromY) {
					double temporary = toY; toY = fromY;
					fromY = temporary;
				}
				canvas.getGraphicsContext2D().fillRect(fromX, fromY, toX-fromX, toY-fromY);
				break;
			case "OVAL":
				// The from values should always be less than the to values
				if (toX < fromX) {
					double temporary = toX; toX = fromX;
					fromX = temporary;
				}
				if (toY < fromY) {
					double temporary = toY; toY = fromY;
					fromY = temporary;
				}
				canvas.getGraphicsContext2D().fillOval(fromX, fromY, toX-fromX, toY-fromY);
				break;
			case "PICTURE":
				// The from values should always be less than the to values
				if (toX < fromX) {
					double temporary = toX; toX = fromX;
					fromX = temporary;
				}
				if (toY < fromY) {
					double temporary = toY; toY = fromY;
					fromY = temporary;
				}
				canvas.getGraphicsContext2D().drawImage(doge, fromX, fromY, toX-fromX, toY-fromY);
		}
	}

/*––––––––––The Event Handlers––––––––––*/
	
	/**
	 * This event handler is for picking the shape to be drawn
	 * @author Rajeev Ram
	 */
	private class ShapeHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			RadioButton pressed = (RadioButton) buttonGroup.getSelectedToggle();
			if (pressed != null) {
				currentType = pressed.getText().toUpperCase();
			}
		}
	}
	
	/**
	 * This event handler is for picking the color used
	 * @author Rajeev Ram
	 */
	private class ColorHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			currentColor = ColorTypeConverter.Fx2Awt(colorPicker.getValue());
		}
	}
	
	/**
	 * This event handler is for creating a new PaintObject after two clicks
	 * @author Rajeev Ram
	 */
	private class ClickDraw implements EventHandler<MouseEvent> {
		
		@Override
		public void handle(MouseEvent event) {
			if (currentType != null) {
				if ( (currentFromX<0) && (currentFromY<0) ) {
					currentFromX = (int) event.getX();
					currentFromY = (int) event.getY();
					currentToX = currentToY = -1;
				}
				else if ( (currentToX<0) && (currentToY<0) ) {
					currentToX = (int) event.getX();
					currentToY = (int) event.getY();
					if ( (currentToX==currentFromX) || (currentToY==currentFromY) ) {
						currentToX = currentToY = -1; return;
					}
					createPaintObject(currentFromX,currentFromY,currentToX,currentToY);
					drawAllPaintObjects(paintObjectVector);
					currentFromX = currentFromY = -1;
				}
			}
		}
		
	}
	
	/**
	 * This event handler is for drawing the ghost shapes after one click
	 * @author Rajeev Ram
	 */
	private class MoveDraw implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {		
			if (currentType != null) {
				if ( (currentFromX >= 0) && (currentFromY >= 0) ) {
					clearTheCanvas(canvas);
					drawAllPaintObjects(paintObjectVector);
					drawGhostShape(currentFromX,currentFromY,event.getX(),event.getY());
				}
			}	
		}
	}
	
	/**
	 * This event handler is for when the mouse exits the Canvas
	 * @author Rajeev Ram
	 */
	private class ExitDraw implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			drawAllPaintObjects(paintObjectVector);
			clearTheCanvas(canvas);
			drawAllPaintObjects(paintObjectVector);
		}
	}
	
	/**
	 * This runnable is used to receive the updated PaintObject Vector from
	 * the server.
	 * @author Rajeev Ram
	 */
	private class ServerHandler implements Runnable {
		
		// Create input and output streams from the server
		ServerHandler() {
			try {
				inputFromServer = new ObjectInputStream(server.getInputStream());
				outputToServer = new ObjectOutputStream(server.getOutputStream());
				//System.out.println("The server streams have been set up.");	
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// While true loop receives updated vector continuously
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			while (true) {
				try {
					paintObjectVector = (Vector<PaintObject>) inputFromServer.readObject();
					//System.out.println("The updated paint object vector has been received.");	
					drawAllPaintObjects(paintObjectVector);
				} 
				catch (IOException | ClassNotFoundException e) {
					System.err.println("Something went wrong in the server handler.");
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
