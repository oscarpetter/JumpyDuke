import java.awt.*;

public class Obstacle {
    public String direction;
    public Rectangle rectObstacle;
    public int speed = 6;
    public int obstacleGap = 115;

    public Obstacle(String direction, int pos, int x) {
        this.direction = direction;
        int y = 0;
        int height = 400;
        int width = 66;

        if (direction.equals("floor")) {
            y = pos + obstacleGap;
        }
        else {
            height = pos - obstacleGap;
        }

        this.rectObstacle = new Rectangle(x, y, width, height);
    }

    public void reset(int pos) {
        int x = GameConstraints.PANEL_WIDTH + 2;

        if (direction.equals("floor")) {
            rectObstacle.y = pos + obstacleGap;
        }
        else {
            rectObstacle.height = pos - obstacleGap;
        }
        rectObstacle.x = x;
    }

    public boolean isOffScreen() {
        return (rectObstacle.x < -66);
    }

    public void update() {
        rectObstacle.x -= speed;
    }
}
