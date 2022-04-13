package com.example.digitrecognizer.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class DrawView extends View {
    public static final float TOUCH_TOLERANCE = 10;
    private Bitmap bitmap; //save drawn pixels here
    private Canvas bitmapCanvas;
    private Paint paintScreen; //pencil used to draw on bitmap
    private Paint paintLine; //Line that we'll actually draw
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;

    public DrawView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

        //Instantiate Paint Screen
        init();
    }

    void init(){
        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true); //Makes sure line are smooth
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(23);
        paintLine.setStrokeCap(Paint.Cap.ROUND); //Make the end of the lines round or square

        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //Bucket of pixels
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        //ARGB allows Alpha, R,G,B

        //This canvas will know exactly how to draw the things
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0,0, paintScreen);

        //for to go through the Path Map, since it contains all points
        for(Integer key:pathMap.keySet()){
            canvas.drawPath(pathMap.get(key),paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Variables to hold values of getActionMasked and getActionIndex
        int action = event.getActionMasked(); // Event TYPE - Eg.ACTION_DOWN, ACTION_POINTER_UP, etc
        int actionIndex = event.getActionIndex(); //pointer(X,Y coordinates of finger/mouse)

        if(action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_UP) {
            touchStarted(event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        }
        else if(action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP){
            touchEnded(event.getPointerId(actionIndex));
        }
        else{
            touchMoved(event);
        }

        invalidate(); //redraws the screen

        return true;
    }

    private void touchMoved(MotionEvent event) {
        //loop through event
        for(int i = 0; i < event.getPointerCount(); i++){
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if(pathMap.containsKey(pointerId)){
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);

                //Calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                //If dist. is significant enough to be considered as a movement, then we keep moving
                if (deltaX >= TOUCH_TOLERANCE ||
                        deltaY >= TOUCH_TOLERANCE){
                    path.quadTo(point.x, point.y,
                            (newX+point.x)/2,
                            (newY+point.y)/2); //tracing from point x,y to new location

                    //store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    public void setDrawingColor(int color){
        paintLine.setColor(color);
    }

    public int getDrawingColor(){
        return paintLine.getColor();
    }

    public void setStrokeWidth(int width){
        paintLine.setStrokeWidth(width);
    }

    int getStrokeWidth(){
        return (int)paintLine.getStrokeWidth();
    }

    public void clear(){
        pathMap.clear(); //removes all of the paths
        bitmap.eraseColor(Color.WHITE);
        invalidate(); //refresh screen
    }

    private void touchEnded(int pointerId) {
        Path path = pathMap.get(pointerId); //get the corresponding map
        bitmapCanvas.drawPath(path, paintLine); //draw to bitmap object

        path.reset();
    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path; //stored path for giver touch
        Point point; //to store last point in path

        if(pathMap.containsKey(pointerId)){
            path = pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);
        }
        else{
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);
        }

        //move to the coordinates of the touch
        path.moveTo(x,y);
        point.x = (int)x;
        point.y = (int)y;
    }
}
