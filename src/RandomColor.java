import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class RandomColor {
    static Random random = new Random();
    ArrayList<Color> colours = new ArrayList<Color>();
    int numCols;

    private void addWideRangeColours()
    {
        colours.add(Color.BLUE);
        colours.add(Color.BLACK);
        colours.add(Color.CYAN);
        colours.add(Color.ORANGE);
        colours.add(Color.LIGHT_GRAY);
        colours.add(Color.GREEN);
        colours.add(Color.RED);
        colours.add(Color.DARK_GRAY);
        colours.add(Color.PINK);
        colours.add(Color.YELLOW);
    }
    private void addbrightRangeColours()
    {
        colours.add(Color.BLUE);
        colours.add(Color.CYAN);
        colours.add(Color.ORANGE);
        colours.add(Color.GREEN);
        colours.add(Color.PINK);
    }

    public RandomColor() {
        // initial Later
        addWideRangeColours();
        // second layer (make ratio of bright colours higher) - removed dull colours
        addbrightRangeColours();
        // third layer (make ratio of bright colours higher)
        addWideRangeColours();

        numCols = colours.size(); // maybe faster to read only once
    }
    /*
    public Color getRandColor() {
        int idx = random.nextInt(numCols);
        return colours.get(idx);
    }
    */

    public Color getRandColor(Color colEx1) {
        Color c = colours.get(random.nextInt(numCols));
        for(int i = 0 ; i < 20 && c == colEx1; i++ ) {
            c = colours.get(random.nextInt(numCols));
        }
        return c;
    }

    public Color getRandColor(Color colEx1, Color colEx2) {
        Color c = colours.get(random.nextInt(numCols));
        for(int i = 0 ; i < 20 && (c == colEx1 || c == colEx2) ; i++ ) {
            c = colours.get(random.nextInt(numCols));
        }
        return c;
    }

    public Color getRandColor(Color colEx1, Color colEx2, Color colEx3) {
        Color c = colours.get(random.nextInt(numCols));
        // try get different colours from the exclusions
        for(int i = 0 ; i < 20 && (c == colEx1 || c == colEx2 || c == colEx3) ; i++ ) {
            c = colours.get(random.nextInt(numCols));
        }
        return c;
    }
}
