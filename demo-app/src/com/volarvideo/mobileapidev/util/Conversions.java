package com.volarvideo.mobileapidev.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**A utility class for conversions
 * 
 * @author Chris Allen
 */
public class Conversions {
	
	/**Converts the pixel value to the context-specific density pixels
	 * 
	 * @param ctx Context to determine screen density
	 * @param pixels Pixel value to convert to density pixels
	 * @return A float representation of the pixel value converted to density pixels
	 */
	public static float pixelsToDp(Context ctx, int pixels) {
        Resources r = ctx.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
        		pixels, r.getDisplayMetrics());
	}

	/**Converts the context-specific density pixels to a normal pixel value
	 * 
	 * @param ctx Context to determine screen density
	 * @param dips Density pixel value to convert to pixels
	 * @return An int representation of the density pixel value converted to pixels
	 */
	public static int dpToPixels(Context ctx, int dips) {
		Resources r = ctx.getResources();
	    DisplayMetrics metrics = r.getDisplayMetrics();
	    return (int) (dips / metrics.density);
	}
}