package name.jugglerdave.minimalindego.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;
import android.content.Context;

import android.util.AttributeSet;
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
        currentPaint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawRect)
        {
            left = getLeft();
            right = getRight();
            top = getTop();
            bottom = getBottom();

            canvas.drawRect(left, top, right, bottom, currentPaint);

            currentPaint.setColor(Color.BLACK);
            currentPaint.setStrokeWidth(3);
            canvas.drawRect(left, top, right,bottom, currentPaint);
            currentPaint.setStrokeWidth(0);
            currentPaint.setColor(Color.CYAN);
            canvas.drawRect(left+3, 30, right-3, bottom-3, currentPaint );
            currentPaint.setColor(Color.YELLOW);
            canvas.drawRect(left+3, top+3, right-3, 30, currentPaint );
        }
    }
}