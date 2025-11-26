package pt.ubi.pdm.votoinformado.classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

    private Paint axisPaint;
    private Paint gridPaint;
    private Paint dotPaint;
    private Paint textPaint;
    private double scoreX = 0; // -10 to 10
    private double scoreY = 0; // -10 to 10
    private java.util.List<CompassCandidate> candidates;
    private android.graphics.Bitmap[] candidateBitmaps;

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(5);
        axisPaint.setStyle(Paint.Style.STROKE);

        gridPaint = new Paint();
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(2);
        gridPaint.setStyle(Paint.Style.STROKE);

        dotPaint = new Paint();
        dotPaint.setColor(Color.RED);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    public void setCandidates(java.util.List<CompassCandidate> candidates) {
        this.candidates = candidates;
        if (candidates != null) {
            candidateBitmaps = new android.graphics.Bitmap[candidates.size()];
            for (int i = 0; i < candidates.size(); i++) {
                android.graphics.drawable.Drawable d = androidx.core.content.ContextCompat.getDrawable(getContext(), candidates.get(i).getImageResId());
                if (d != null) {
                    android.graphics.Bitmap rawBitmap = drawableToBitmap(d);
                    candidateBitmaps[i] = getCircularBitmap(rawBitmap);
                }
            }
        }
        invalidate();
    }

    private android.graphics.Bitmap drawableToBitmap(android.graphics.drawable.Drawable drawable) {
        if (drawable instanceof android.graphics.drawable.BitmapDrawable) {
            return ((android.graphics.drawable.BitmapDrawable) drawable).getBitmap();
        }
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private android.graphics.Bitmap getCircularBitmap(android.graphics.Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        android.graphics.Bitmap output = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final android.graphics.Rect rect = new android.graphics.Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);
        
        // Add white border
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(size * 0.05f); // 5% of size as border
        borderPaint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - borderPaint.getStrokeWidth()/2, borderPaint);

        return output;
    }

    public void setScore(double x, double y) {
        this.scoreX = x;
        this.scoreY = y;
        invalidate(); // Redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        // Draw Quadrant Backgrounds
        Paint quadPaint = new Paint();
        quadPaint.setStyle(Paint.Style.FILL);

        // Auth Left (Red)
        quadPaint.setColor(Color.parseColor("#F44336"));
        quadPaint.setAlpha(50); // Softer alpha
        canvas.drawRect(0, 0, centerX, centerY, quadPaint);

        // Auth Right (Blue)
        quadPaint.setColor(Color.parseColor("#2196F3"));
        quadPaint.setAlpha(50);
        canvas.drawRect(centerX, 0, width, centerY, quadPaint);

        // Lib Left (Green)
        quadPaint.setColor(Color.parseColor("#4CAF50"));
        quadPaint.setAlpha(50);
        canvas.drawRect(0, centerY, centerX, height, quadPaint);

        // Lib Right (Purple)
        quadPaint.setColor(Color.parseColor("#9C27B0"));
        quadPaint.setAlpha(50);
        canvas.drawRect(centerX, centerY, width, height, quadPaint);

        // Draw Grid
        gridPaint.setColor(Color.parseColor("#BDBDBD")); // Lighter grey
        for (int i = 1; i < 10; i++) {
            float x = i * (width / 10f);
            float y = i * (height / 10f);
            canvas.drawLine(x, 0, x, height, gridPaint);
            canvas.drawLine(0, y, width, y, gridPaint);
        }

        // Draw Axes
        axisPaint.setStrokeWidth(8); // Thicker axes
        canvas.drawLine(centerX, 0, centerX, height, axisPaint); // Y Axis
        canvas.drawLine(0, centerY, width, centerY, axisPaint); // X Axis

        // Draw Labels
        textPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));
        canvas.drawText("Authoritarian", centerX, 60, textPaint);
        canvas.drawText("Libertarian", centerX, height - 40, textPaint);
        canvas.drawText("Left", 60, centerY - 20, textPaint);
        canvas.drawText("Right", width - 60, centerY - 20, textPaint);

        // Draw Candidates
        if (candidates != null && candidateBitmaps != null) {
            for (int i = 0; i < candidates.size(); i++) {
                CompassCandidate c = candidates.get(i);
                if (candidateBitmaps[i] != null) {
                    float pX = (float) (centerX + (c.getX() / 10.0) * centerX);
                    float pY = (float) (centerY - (c.getY() / 10.0) * centerY);
                    
                    // Draw image centered at pX, pY, scaled to 80x80 (slightly larger)
                    int iconSize = 80;
                    android.graphics.Rect dst = new android.graphics.Rect((int)pX - iconSize/2, (int)pY - iconSize/2, (int)pX + iconSize/2, (int)pY + iconSize/2);
                    
                    // Draw shadow for depth
                    Paint shadowPaint = new Paint();
                    shadowPaint.setShadowLayer(10.0f, 0.0f, 2.0f, 0xFF000000);
                    canvas.drawCircle(pX, pY, iconSize/2f, shadowPaint);
                    
                    canvas.drawBitmap(candidateBitmaps[i], null, dst, null);
                }
            }
        }

        // Draw User Dot
        // Map -10..10 to 0..width/height
        // X: -10 -> 0, 0 -> centerX, 10 -> width
        // Y: -10 -> height, 0 -> centerY, 10 -> 0 (Note: Y is inverted in canvas)
        
        float dotX = (float) (centerX + (scoreX / 10.0) * centerX);
        float dotY = (float) (centerY - (scoreY / 10.0) * centerY);

        canvas.drawCircle(dotX, dotY, 20, dotPaint);
        
        // Draw coordinates text near dot
        String coords = String.format("Você (%.1f, %.1f)", scoreX, scoreY);
        canvas.drawText(coords, dotX, dotY - 30, textPaint);
    }
}
