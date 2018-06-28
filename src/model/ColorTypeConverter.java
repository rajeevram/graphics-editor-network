package model;
import java.awt.Color;

/**
 * This is a class to convert JavaFX Colors to JavaAWT Colors and vice-versa
 * 
 * @author Rick Mercer
 */

public class ColorTypeConverter {

//	public static void main(String[] args) {
//		javafx.scene.paint.Color fxColor = Awt2Fx(new Color(100, 150, 250));
//		System.out.println(fxColor.getRed());
//		System.out.println(fxColor.getBlue());
//		System.out.println(fxColor.getGreen());
//
//		java.awt.Color awtColor = Fx2Awt(fxColor);
//
//		System.out.println(awtColor.getRed());
//		System.out.println(awtColor.getBlue());
//		System.out.println(awtColor.getGreen());
//
//		fxColor = Awt2Fx(awtColor);
//
//		System.out.println(fxColor.getRed());
//		System.out.println(fxColor.getBlue());
//		System.out.println(fxColor.getGreen());
//
//		awtColor = Fx2Awt(fxColor);
//
//		System.out.println(awtColor.getRed());
//		System.out.println(awtColor.getBlue());
//		System.out.println(awtColor.getGreen());
//	}

	/**
	 * This converts a JavaFX Color to a JavaAWT Color
	 * @param fxColor – the JavaFX Color
	 * @return the JavaAWT Color
	 */
	public static Color Fx2Awt(javafx.scene.paint.Color fxColor) {
		int r = (int) (255 * fxColor.getRed());
		int g = (int) (255 * fxColor.getGreen());
		int b = (int) (255 * fxColor.getBlue());
		java.awt.Color awtColor = new java.awt.Color(r, g, b);
		return awtColor;
	}
	
	/**
	 * This converts a JavaAWT Color to a JavaFX Color
	 * @param awtColor – the JavaAWT Color
	 * @return the JavaFX Color
	 */
	public static javafx.scene.paint.Color Awt2Fx(Color awtColor) {
		int r = awtColor.getRed();
		int g = awtColor.getGreen();
		int b = awtColor.getBlue();
		javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b); // , opacity); 
		return fxColor;
	}
}
