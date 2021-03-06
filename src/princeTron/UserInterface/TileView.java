/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package princeTron.UserInterface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;


/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 * 
 */
public class TileView extends View {

    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawables will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */

    protected static int mTileSize;

    protected static int mXTileCount;
    protected static int mYTileCount;

    private static int mXOffset;
    private static int mYOffset;


    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray; 

    /**
     * A two-dimensional array of integers in which the number represents the
     * index of the tile that should be drawn at that locations
     */
    private int[][] mTileGrid;

    private final Paint mPaint = new Paint();

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);

        mTileSize = a.getInt(R.styleable.TileView_tileSize, 12);
        a.recycle();
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);

//        mTileSize = a.getInt(R.styleable.TileView_tileSize, 12);
        mTileSize = a.getInt(R.styleable.TileView_tileSize, 6);

        a.recycle();
    }



    /**
     * Rests the internal array of Bitmaps used for drawing tiles, and
     * sets the maximum index of tiles to be inserted
     * 
     * @param tilecount
     */

    public void resetTiles(int tilecount) {
        mTileArray = new Bitmap[tilecount];
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        /*mXTileCount = (int) Math.floor(w / mTileSize);
        mYTileCount = (int) Math.floor(h / mTileSize);
        mXTileCount = Math.min(mXTileCount, mYTileCount);
        mYTileCount = mXTileCount;*/
    	
    	mXTileCount = princeTron.Engine.GameEngine.X_SCALE;
    	mYTileCount = princeTron.Engine.GameEngine.Y_SCALE;
    	
    	int min = Math.min(w, h);
    	mTileSize = (int) Math.floor(min/100);

        mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2);

        mTileGrid = new int[mXTileCount+2][mYTileCount+2];
        Log.i("TileView", "xTileCount: "+ mXTileCount);
        Log.i("TileView", "yTileCount: " + mYTileCount);
        clearTiles();
    }

    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     */
    public void loadTile(int key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, mTileSize, mTileSize);
        tile.draw(canvas);

        mTileArray[key] = bitmap;
    }

    /**
     * Resets all tiles to 0 (empty)
     * 
     */
    public void clearTiles() {
        for (int x = 0; x < mTileGrid.length; x++) {
            for (int y = 0; y < mTileGrid.length; y++) {
                setTile(0, x, y);
            }
        }
    }

    /**
     * Used to indicate that a particular tile (set with loadTile and referenced
     * by an integer) should be drawn at the given x/y coordinates during the
     * next invalidate/draw cycle.
     * 
     * @param tileindex
     * @param x
     * @param y
     */
    public void setTile(int tileindex, int x, int y) {
    	tileindex = tileindex % (mTileArray.length);
    	try {
    		x = mapX(x);
    		y = mapY(y);
    		mTileGrid[x][y] = tileindex;
    	}
    	catch (Exception e) {
    		//e.printStackTrace();
    	}
    }

    private int mapX(int x) {
    	//double proportion = x/100.0;
    	//return (int) Math.round(proportion*mXTileCount);
    	return x;
    }
    
    private int mapY(int y) {
    	//double proportion = y/100.0;
    	//return (mYTileCount - (int) Math.round(proportion*mYTileCount) - 1);
    	return mTileGrid.length - y - 1;
    }

    @Override
    public void onDraw(Canvas canvas) {
    	int count = Math.min(mXTileCount, mYTileCount);
    	mXTileCount = count;
    	mYTileCount = count;
    	//Log.i("TileView", ""+count);
    	super.onDraw(canvas);
    	int x = 0, y = 0;
    	try {
    		for (x = 0; x < mTileGrid.length; x += 1) {
    			for (y = 0; y < mTileGrid.length; y += 1) {
    				if (mTileGrid[x][y] > 0) {
    					int z = mTileGrid[x][y];
    					//System.out.println("z: " + z);
    					Bitmap b = mTileArray[z];
    					canvas.drawBitmap(b, 
    							mXOffset + x * mTileSize,
    							mYOffset + y * mTileSize,
    							mPaint);
    				}
    			}
    		}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		/*System.out.println(x);
    		System.out.println(y);*/
    	}
    }

}