import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class GameConstraints extends JPanel implements ActionListener, KeyListener, MouseListener {
    private enum STATE {
        MENU,
        GAME
    };
    private STATE state = STATE.MENU;

    private final Birb birb = new Birb(200, 200);
    private java.util.List<Obstacle> obstacles;

    private Timer timer;
    AudioPlayer audioPlayer;

    // Panel size
    public static final int PANEL_WIDTH = 600;
    public static final int PANEL_HEIGHT = 525;

    public static boolean gameOver;
    public int highscore;
    public String highscoreValue = String.valueOf(highscore);
    public int score;
    public String scoreText = String.valueOf(score);

    JButton start;

    JLabel highScoreLabel = new JLabel("Session Highscore: " + highscoreValue);
    JLabel scorelabel = new JLabel("Current score: " + scoreText);

    /**
     * Loads in the images from lib catalog in a try-catch.
     * If that fails it will print an exception.
     */
    public GameConstraints() {

        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.ORANGE);

        createGameMenu();
        start.setVisible(true);

        creatingScoreLabels();

        this.obstacles = new ArrayList<>();
        audioPlayer = new AudioPlayer();
        // All key events
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);

        this.timer = new Timer(16, e -> {
            long time = System.nanoTime();
            birb.tick(time);
            update(time);
            repaint();
        });
        audioPlayer.playBackround();
    }

        /**
         * Some JLabels that that places a text string inside the game panel
         * with the players current score and Highscore
         */
    private void creatingScoreLabels() {
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 17));
        scorelabel.setFont(new Font("Arial", Font.BOLD, 17));
        this.add(scorelabel);
        this.add(highScoreLabel);
        scorelabel.setBounds(225, 0, 50, 30 );
        highScoreLabel.setBounds(450, 50, 40, 30);
        scorelabel.setLocation(220, 0);
        highScoreLabel.setLocation(PANEL_WIDTH - 50, 250);
        scorelabel.setVisible(true);
        highScoreLabel.setVisible(true);
    }

    private void setState(STATE state) {  //Switch case för hantering av game restart
        switch (state) {
            case GAME: {
                gameState();
                break;
            }
            case MENU: {
                menuState();
                break;
            }
        }
    }

    private void menuState() {
        gameOver = true;
        start.setVisible(true);
        this.state = STATE.MENU;
        timer.stop();
    }

    private void gameState() {
        birb.resetBirb();
        gameOver = false;
        start.setVisible(false);
        this.state = STATE.GAME;
        score = 0;
        scoreText = String.valueOf(score);
        scorelabel.setText("Current score: " + scoreText);
        obstacles = new ArrayList<>();
        timer.start();
    }

    public void update(long time) {
        if (gameOver) {
            setState(STATE.MENU);
        }
        moveObstacles();
        checkForCollisions();
        addObstacles();
    }

    private void createGameMenu() {
        start = new JButton("Start");
        this.add(start);
        start.setMnemonic(KeyEvent.VK_S);
        start.setActionCommand("Start");
        start.addActionListener(this);
        start.setFocusable(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Dimension d = this.getSize();

        Graphics2D g2D = (Graphics2D) g;

        birb.paint(g2D);
        drawPipes(g2D);
    }

    private void drawPipes(Graphics2D g2D) {
        for (Obstacle obstacle : obstacles) {
            g2D.setColor(Color.MAGENTA);
            g2D.fillRect((int) obstacle.rectObstacle.getX(), (int) obstacle.rectObstacle.getY(),
                            (int) obstacle.rectObstacle.getWidth(), (int) obstacle.rectObstacle.getHeight());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if("Start".equals(e.getActionCommand())){
            setState(STATE.GAME);
            final long time = System.nanoTime();
            birb.jump(time);
        }
        repaint();

    }

    private void moveObstacles() {
        for (Obstacle o : obstacles) {
            o.update();
        }
    }

    private void addObstacles() {
        if (obstacles.isEmpty()) {
            int pos = 100 + ThreadLocalRandom.current().nextInt(200);
            int pos2 = pos + ThreadLocalRandom.current().nextInt(-50, 50);

            obstacles.add(new Obstacle("ceiling", pos, GameConstraints.PANEL_WIDTH + 2));
            obstacles.add(new Obstacle("floor", pos, GameConstraints.PANEL_WIDTH + 2));

            obstacles.add(new Obstacle("ceiling", pos2, GameConstraints.PANEL_WIDTH + 300));
            obstacles.add(new Obstacle("floor", pos2, GameConstraints.PANEL_WIDTH + 300));

            return;
        }

        int pos = 100 + ThreadLocalRandom.current().nextInt(200);
        for(Obstacle obstacle : obstacles) {
            if (obstacle.isOffScreen()) {
                obstacle.reset(pos);
            }
        }
    }

    private boolean checkForCollisions() {
        for (Obstacle obstacle : obstacles) {

            if(obstacle.rectObstacle.intersects(Birb.birbRect)) {
                setState(STATE.MENU);
                audioPlayer.playDead();
                return true;
            }

            /**
             * "Its not the scoring jumpy birb deserves. But its the one it needs right now.
             * So we will tweak it. Because it can take it. Because its not our code." - Gordon
             */
            else if(obstacle.rectObstacle.y == 0 &&
                            birb.getPosX() + birb.getBirbWidth() / 2 > obstacle.rectObstacle.x + obstacle.rectObstacle.width / 2 - 4 &&
                            birb.getPosX() + birb.getBirbWidth() / 2 < obstacle.rectObstacle.x + obstacle.rectObstacle.width / 2 + 4) {
                audioPlayer.playScore();
                score++;
                scoreText = String.valueOf(score);
                scorelabel.setText("Current score: " + scoreText);
                if(score > highscore) {
                    highscore = score;
                    highscoreValue = String.valueOf(highscore);
                    highScoreLabel.setText("Session Highscore: " + highscoreValue);
                }
            }
        }

        if(birb.getPosY() >= PANEL_HEIGHT - birb.getBirbHeight() ||
                        birb.getPosY() < 0 ) { // sätter så man inte kan gå under golv eller tak
            setState(STATE.MENU);
            audioPlayer.playDead();
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1){
            final long time = System.nanoTime();
            birb.jump(time);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        final int kc = e.getKeyCode();
        if (kc == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        if (kc == KeyEvent.VK_SPACE) {
            final long time = System.nanoTime();
            birb.jump(time);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        final int kc = e.getKeyCode();
        if (kc == KeyEvent.VK_SPACE) {
            audioPlayer.playJump();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // not used, should be empty
    }


    @Override
    public void mousePressed(MouseEvent e) {
        // not used, should be empty
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // not used, should be empty
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // not used, should be empty
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // not used, should be empty
    }
}