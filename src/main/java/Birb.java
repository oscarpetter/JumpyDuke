import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

@SuppressWarnings("java:S106")
public class Birb implements Serializable {
    private static final long serialVersionUID = 126058262246325L;

    private static final double GRAVITY = 70;
    private static final double TIME_SCALE = 12L;
    private static final double NANOSECOND = 1_000_000_000L;

    private final Rectangle box = new Rectangle(220, 180, 40, 40);
    static Rectangle birbRect;
    private double velocity = 0;
    private long jumpStart = 0;

    private boolean started = false;

    private int posY = 200;

    private BufferedImage birdImageSprite;

    private int birbWidth;


    private int birbHeight;

    public Birb(int posX, int posY) {
        int margin = 5;

        try {
            birdImageSprite = ImageIO.read(new File("lib/javaduke.png"));
        } catch (IOException ex) {
            System.out.println(ex + " Unable to load image");
        }
        this.posY = posY;
        this.birbWidth = birdImageSprite.getWidth();
        this.birbHeight = birdImageSprite.getHeight();
        this.birbRect = new Rectangle(posX, posY, birbWidth - margin, birbHeight);
    }

    public void resetBirb(){
        started = false;
        birbRect.y = posY;
        velocity = 0;
        jumpStart = 0;
    }

    public void jump(long time) {
        started = true;

        // using this if we can only jump on the way down
        if (velocity <= 0) {
            jumpStart = time;

            // ökar hastigheten uppåt
            velocity = 100;
        }
    }

    // Rending the jump
    public void tick(long time) {
        if (!started) {
            return;
        }

        double deltaTime = ((time - jumpStart) / NANOSECOND) / TIME_SCALE;
        birbRect.y -= velocity * deltaTime;
        velocity -= GRAVITY * deltaTime;
    }

    public void paint(Graphics2D g2D) {

        if (birdImageSprite != null) {
            g2D.drawImage(birdImageSprite, (int) birbRect.getX(), (int) birbRect.getY(), null);

        } else {
            g2D.setColor(Color.MAGENTA);
            g2D.fillRect((int)box.getX(), (int)box.getY(), (int)box.getWidth(), (int)box.getHeight());
        }
    }

    public int getBirbWidth() {
        return birbWidth;
    }

    public int getBirbHeight() {
        return birbHeight;
    }

    public int getPosX() {
        return birbRect.x;
    }

    public int getPosY() {
        return birbRect.y;
    }
}
