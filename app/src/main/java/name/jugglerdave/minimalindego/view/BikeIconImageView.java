package name.jugglerdave.minimalindego.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.content.Context;

import android.util.AttributeSet;

import name.jugglerdave.minimalindego.R;

/**
 * Created by dtorok on 5/29/2015.
 */
public class BikeIconImageView extends ImageView {

    public BikeIconImageView(Context context) {
        super(context);

    }
    private Paint currentPaint;
    public boolean drawRect = true;
    public float left = 20;
    public float top = 20;
    public float right = 20;
    public float bottom = 20;

    public BikeIconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(0xFF00CC00);  // alpha.r.g.b
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(3);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawRect) {


            left = getLeft();
            right = getRight();
            top = getTop();
            bottom = getBottom();

            //Bitmap border = BitmapFactory.decodeResource(getResources(), R.drawable.bicycleicon);
            int width = (int)(right-left);
            int height = (int)(right-left);
           // Bitmap scaledBorder = Bitmap.createScaledBitmap(border,width/2,height/2, false);
            //canvas.drawBitmap(scaledBorder, 20, 20,null);

            canvas.drawRect(left, top, right, bottom, currentPaint);

            currentPaint.setColor(Color.BLACK);
            currentPaint.setStrokeWidth(3);
            canvas.drawRect(left, top, right, bottom, currentPaint);
            currentPaint.setStrokeWidth(5);
            currentPaint.setColor(Color.CYAN);
            canvas.drawRect(left + 3, bottom-30, right - 3, bottom - 3, currentPaint);
            currentPaint.setColor(Color.YELLOW);
            canvas.drawRect(left + 3, top + 3, right - 3, top+30, currentPaint);
           // setImageBitmap(scaledBorder);

        }
    }
}