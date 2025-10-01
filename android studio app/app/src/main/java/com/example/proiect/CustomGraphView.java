package com.example.proiect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomGraphView extends View {
    private Paint paint;
    private Paint axisPaint;
    private double a, b, c, d;

    public CustomGraphView(Context context) {
        super(context);
        init();
    }

    public CustomGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);

        axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        // Draw x-axis
        canvas.drawLine(0, height / 2, width, height / 2, axisPaint);
        // Draw y-axis
        canvas.drawLine(width / 2, 0, width / 2, height, axisPaint);

        if (a != 0 || b != 0 || c != 0 || d != 0) {
            float previousX = -width / 2;
            float previousY = (float) (a * Math.pow(previousX / 100.0, 3) + b * Math.pow(previousX / 100.0, 2) + c * (previousX / 100.0) + d);
            for (float x = -width / 2; x <= width / 2; x++) {
                double y = a * Math.pow(x / 100.0, 3) + b * Math.pow(x / 100.0, 2) + c * (x / 100.0) + d;
                float screenX = x + width / 2;
                float screenY = height / 2 - (float) (y * 100);
                canvas.drawLine(previousX + width / 2, height / 2 - (previousY * 100), screenX, screenY, paint);
                previousX = x;
                previousY = (float) y;
            }
        }
    }

    public void setCoefficients(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        invalidate();
    }
}
