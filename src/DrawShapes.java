/*public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
*/
//import sun.lwawt.macosx.CSystemTray;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Mutux - one read will block another so using ReadWriteLock instead
// import java.util.concurrent.locks.Lock;
// import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.util.Random;

import java.awt.Graphics;


public class DrawShapes extends JPanel {
    //Random random = new Random();
    static final int frameW = 600;
    static final int frameH = 400;

    static ArrayList<WobblyClock> wobblyClocks = new ArrayList<WobblyClock>();
    private static Lock mutex_lock = new ReentrantLock();
    //private static Lock mutex_lock = new ReentrantLock();
    //private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private Random random = new Random();
    public DrawShapes(int numInitialClocks) {

        for(int x = 0 ; x < numInitialClocks ; x ++ ) {
            wobblyClocks.add(new WobblyClock(frameW, frameH-30));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //System.out.println("paintComponent");
        Graphics2D g2d = (Graphics2D) g;

        // Fixed Clock
        // plotClock(g2d, 300, 200, 90, Color.MAGENTA, Color.CYAN, Color.BLUE, Color.LIGHT_GRAY);
        // drawImage(g2d, 300, 200, "./img/tillygreen");
        // drawImage(g2d, 150, 100, "./img/cricketgreen"); //Senan.jpg");
        // drawImage(g2d, 420, 100, "./img/tillygreen");   //Evelyn.jpg");

        //mutex_lock.lock();
        rwLock.readLock().lock(); // Acquire read lock
        try {
            wobblyClocks.forEach( (wc) -> {
                //plotClock(g2d, wc.getXWob(), wc.getYWob(), wc.getRadiousWob(), wc.cCol, wc.hCol, wc.mCol, wc.sCol);
                wc.draw(g2d);
                //if( random.nextInt(2)== 0)
                //    drawImage(g2d, wc.getXWob(), wc.getYWob(), "./img/alicegreen");
                //else
                //    drawImage(g2d, wc.getXWob(), wc.getYWob(), "./img/cricketgreen");

            });
        }
        finally {
            // mutex_lock.unlock();
            rwLock.readLock().unlock(); // Release read lock
        }

    }
/*
//from w w  w.  j a v  a2s  . co  m
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel {

  public void paint(Graphics g) {
    Image img = createImage();
    g.drawImage(img, 20,20,400,400,this);
  }
  private Image createImage(){
    BufferedImage bufferedImage = new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);
    Graphics g = bufferedImage.getGraphics();
    g.drawString("www.java2s.com", 20,20);

    return bufferedImage;
  }
  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.add(new Main());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(20,20, 500,500);
    frame.setVisible(true);
  }
}
 */


    private BufferedImage getImage(String strName) {

        try {
            File img = new File(strName);
            BufferedImage image = ImageIO.read(img );
            //System.out.println(image);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void drawImage(Graphics2D g2d, int x, int y, String sImagePath) {
        BufferedImage image = getImage(sImagePath);
        g2d.drawImage(image,x, y, null);
    }


    public void showClockFrame(JFrame frame) {
        //DrawShapes drawShapes = new DrawShapes();
        frame.add(this);
        frame.setSize(frameW, frameH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /*
    public void resize(Graphics2D g2d, String inputImagePath,

                       String outputImagePath, int scaledWidth, int scaledHeight)
            throws IOException {
        // reads input image
        //File inputFile = new File(inputImagePath);
        //BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image

        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));
    }
  */

    // Entry point
    public static void main(String[] args) {
        // Create a ScheduledExecutorService with a single thread
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        JFrame frame = new JFrame("Wobbly Clocks");
        final int MinClocks = 1;
        final int MaxClocks = 24;
        DrawShapes drawShapes = new DrawShapes(MinClocks);
        drawShapes.showClockFrame(frame);

        // Define the task to run every 10 seconds
        Runnable task = new Runnable() {
            boolean addingClocks = true;
            final int ShortDelayCycles = 20;
            final int LongDelayCycles = 600;

            int AddRemoveEveryXCycles = LongDelayCycles; // initial long delay
            int addRemoveCycle = 0;
            @Override
            public void run() {
                //System.out.println("Task is running at: " + System.currentTimeMillis());
                frame.repaint();

                if(addRemoveCycle++ >= AddRemoveEveryXCycles) {
                    addRemoveCycle = 0;
                    AddRemoveEveryXCycles = ShortDelayCycles; // shorter delay while adding/removing
                    int shapeCounter = wobblyClocks.size();
                    if (addingClocks && shapeCounter >= MaxClocks) {
                        addingClocks = false;

                        // swap first two clocks so initial clock is different each cycle
                        WobblyClock keep2ndClock = wobblyClocks.get(1);
                        wobblyClocks.set(1, wobblyClocks.get(0));
                        wobblyClocks.set(0, keep2ndClock);

                        AddRemoveEveryXCycles = LongDelayCycles; // longer delay when swapping direction
                        frame.setTitle("Wobbly Clocks");
                    }
                    else if (!addingClocks && shapeCounter <= MinClocks) {
                        addingClocks = true;

                        AddRemoveEveryXCycles = LongDelayCycles; // longer delay when swapping direction
                        frame.setTitle("Wobbly Clocks");
                    }
                    else {
                        //mutex_lock.lock();
                        rwLock.writeLock().lock(); // Acquire write lock
                        try {
                            if (addingClocks) {
                                wobblyClocks.add(new WobblyClock(frameW, frameH - 30));
                            } else {
                                wobblyClocks.remove(wobblyClocks.size()-1); // remove last one
                            }
                        }
                        finally {
                            //mutex_lock.unlock();
                            rwLock.writeLock().unlock(); // Release write lock
                        }
                        frame.setTitle("Wobbly Clocks (" + (addingClocks ? '+' : '-') + ")");
                    }
                }
            }
        };

        // Schedule the task with a fixed rate of 10 seconds
        scheduler.scheduleAtFixedRate(task, 0, 20, TimeUnit.MILLISECONDS);

        // Optionally, add a shutdown hook to gracefully stop the scheduler on application exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }));
    }
}
/* can use this to convert image files (in a seporate app)
public void resize(String inputImagePath,
            String outputImagePath, int scaledWidth, int scaledHeight)
            throws IOException {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath
                .lastIndexOf(".") + 1);

        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));
    }
 */