package com.zyxstyle.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class LightningView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path bolt = new Path();
    private float phase = 0f;

    public LightningView(Context context) {
        super(context);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void drawBolt(Canvas canvas, float x, float y, float scale, float angle, int color) {
        canvas.save();
        canvas.rotate(angle, x, y);

        paint.setColor(color);
        paint.setAlpha(220);
        paint.setStrokeWidth(2.5f * scale);

        bolt.reset();
        bolt.moveTo(x, y);
        bolt.lineTo(x + 7 * scale, y - 5 * scale);
        bolt.lineTo(x + 4 * scale, y + 2 * scale);
        bolt.lineTo(x + 12 * scale, y - 1 * scale);
        bolt.lineTo(x + 7 * scale, y + 8 * scale);

        canvas.drawPath(bolt, paint);
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        if (w <= 0 || h <= 0) return;

        phase += 0.045f;

        int red = Color.rgb(255, 45, 70);
        int blue = Color.rgb(0, 220, 255);

        drawBolt(canvas, 8 + (float)Math.sin(phase) * 5, 15, 0.8f, -20, red);
        drawBolt(canvas, w - 18 + (float)Math.cos(phase * 1.2f) * 5, 18, 0.75f, 25, blue);
        drawBolt(canvas, 18, h - 18 + (float)Math.cos(phase * 1.3f) * 4, 0.65f, 25, blue);
        drawBolt(canvas, w - 14, h - 20 + (float)Math.sin(phase * 1.1f) * 4, 0.8f, -25, red);

        postInvalidateOnAnimation();
    }
}
