import java.awt.*;
import java.time.LocalTime;
import java.util.Random;


public class WobblyClock {
    static Random random = new Random();
    static RandomColor randCol = new RandomColor();

    int x, y;
    int radius;
    boolean allowResize;
    int frameW, frameH;
    int xAdj, yAdj;
    Color cCol, hCol, mCol, sCol;
    int xDir, yDir, rDir;

    float xWob, yWob;
    float radiusWob;


    public WobblyClock(int frameW, int frameH)
    {
        this.x = random.nextInt(frameW-radius);
        this.y = random.nextInt(frameH-30-radius);
        this.radius = 25+(random.nextInt(frameH-50-radius)/4);

        this.frameW = frameW;
        this.frameH = frameH;
        this.xAdj = 1 + random.nextInt(2);
        this.yAdj = 1 + random.nextInt(2);
        this.allowResize = true;

        this.resetColours();

        if(random.nextInt(2) == 0)
            this.xDir = -1;
        else
            this.xDir = 1;

        if(random.nextInt(2) == 0)
            this.yDir = -1;
        else
            this.yDir = 1;

        if(random.nextInt(2) == 0)
            this.rDir = -1;
        else
            this.rDir = 1;

        this.xWob = (float)this.x;
        this.yWob = (float)this.y;
        this.radiusWob = (float)this.radius;
    }

    //
    public void resetColours()
    {
        this.cCol = randCol.getRandColor(Color.BLACK); // make Circle a bright colour
        this.hCol = randCol.getRandColor(cCol);
        this.mCol = randCol.getRandColor(cCol,hCol);
        this.sCol = randCol.getRandColor(hCol, mCol); // ok if same as cCOl
    }

    public int getXWob() {
        if(xDir == -1 && xWob < (int)radiusWob) {
            xDir = 1;
            xAdj = 1+random.nextInt(3);
            setRadiusRandomDirection();
        }
        else if(xDir == 1 && xWob > (frameW-(int)radiusWob)) {
            xDir = -1;
            xAdj = 1+random.nextInt(3);
            setRadiusRandomDirection();
        }
        xWob += (xAdj * xDir);
        return (int)xWob;
    }

    public int getYWob() {
        if(yDir == -1 && yWob < (int)radiusWob) {
            yDir = 1;
            yAdj = 1+random.nextInt(3);
            setRadiusRandomDirection();
        }
        else if(yDir == 1 && yWob > (frameH-(int)radiusWob)) {
            yDir = -1;
            yAdj = 1+random.nextInt(3);
            setRadiusRandomDirection();
        }
        yWob += (yAdj * yDir *0.5);
        return (int)yWob;
    }

    public int getRadiousWob() {

        radiusWob += (0.25*rDir);
        if(radiusWob < 8.0F) {
            radiusWob = 8.0F;
            this.rDir = 1;
        }
        else if(radiusWob > (float)(frameH * 0.6F)) {
            radiusWob = (float)(frameH * 0.6F);
            this.rDir = -1;
        }
        return (int)radiusWob;
    }


    public void setRadiusRandomDirection()
    {
        // check if its suitable to change direction of radios
        if(radiusWob > (radius*1.2) && rDir == -1)
            return;
        if(radiusWob < (radius*0.8) && rDir == 1)
            return;

        if(random.nextInt(2) == 0)
            this.rDir = -1;
        else
            this.rDir = 1;
    }


    public void draw(Graphics2D g2d)
    {
        LocalTime currentTime = LocalTime.now();
        int x = getXWob();
        int y = getYWob();
        int radius = getRadiousWob();

        // Get Hours Minutes and Seconds
        int nSec = currentTime.getSecond();
        int nMin = currentTime.getMinute();
        int nHr = currentTime.getHour();
        String sAMPM =  (nHr >= 12) ? "p.m." : "a.m.";
        nHr %= 12;

        // Convert Hours, Minutes and Seconds in to line degrees
        float hrDg  = (30F * ((float)nHr + ((float)nMin / 60F)));
        float minDg = (6F * ((float)nMin + ((float)nSec / 60F)));
        float secDg = (6F * (float)nSec);

        // Draw a circle
        drawCircle(g2d, x, y, radius, (float)(radius*0.05f), cCol);

        //text p.m. / a.m.
        drawClockText(g2d, x, y, radius, sAMPM, (float)(radius*0.05f), cCol);

        // Draw Lines (Clock hands)
        drawClockHand(g2d, (int)(radius*0.67), (int)hrDg, x, y, (float)(radius*0.07f), hCol);
        drawClockHand(g2d, (int)(radius*0.87), (int)minDg, x, y, (float)(radius*0.04f), mCol);
        //Draw small dial for circle for second hand
        //drawCircle(g2d, x, y, (int)(radius*0.07), (float)(radius*0.005f), sCol);
        drawClockHand(g2d, (int)(radius*0.94), (int)secDg, x, y, (float)(radius*0.02f), sCol);
        // Add Center Point
        plotPoint(g2d, x, y, (int)(radius*0.03), Color.BLACK);
    }

    private void plotPoint(Graphics2D g2d, int x, int y, int size, Color col) {
        g2d.setColor(col);
        g2d.fillOval(x-1, y-1, size, size);  // Draws a small filled circle to represent a point
    }

    private void drawLine(Graphics2D g2d, int x1, int y1, int x2, int y2, Color col) {
        g2d.setColor(col);
        g2d.drawLine(x1, y1, x2, y2);
    }

    private void drawCircle(Graphics2D g2d, int x, int y, int radius, float lineThickness, Color col) {
        g2d.setColor(col);
        g2d.setStroke(new BasicStroke(lineThickness));

        g2d.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    private void drawClockText(Graphics2D g2d, int x, int y, int radius, String sText, float lineThickness, Color col) {
        g2d.setColor(col);
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, (int)(radius*0.14F)));
        g2d.drawString(sText,x - (radius*0.03F*sText.length()), y + (radius*0.25F) );
    }

    public void drawClockHand(Graphics2D g2d, int radius, int angleInDegrees, int x, int y, float lineThickness, Color col)
    {
        Point p2 = new Point(x, y);
        Point newP1 = PointOnCircle((int)(radius*0.15F), angleInDegrees, p2, true);
        Point newP2 = PointOnCircle(radius, angleInDegrees, p2, false);
        g2d.setColor(col);
        g2d.setStroke(new BasicStroke(lineThickness));

        g2d.drawLine(newP1.x, newP1.y, newP2.x, newP2.y);
    }

    public Point PointOnCircle(int radius, int angleInDegrees, Point origin, boolean bMirrorProjection)
    {
        float fMir = (bMirrorProjection) ? -1.0F : 1.0F;
        // Convert from degrees to radians via multiplication by PI/180
        int x = (int)(radius * (Math.sin(angleInDegrees * Math.PI / 180F)) * fMir) + origin.x;
        // using -Math.cos to reverse Y to match clock where up is 0 degrees
        int y = (int)(radius * (-Math.cos(angleInDegrees * Math.PI / 180F)) * fMir) + origin.y;

        return new Point(x, y);
    }

};


