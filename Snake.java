import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class Snake {
    public static void main(String[] args) {

        Deque<Integer> snakeX = new LinkedList<Integer>();
        Deque<Integer> snakeY = new LinkedList<Integer>();
        int[] food = { 3, 3 };

        snakeX.addLast(3);
        snakeY.addLast(1);

        snakeX.addLast(2);
        snakeY.addLast(1);

        snakeX.addLast(1);
        snakeY.addLast(1);

        Game f1 = new Game();
        f1.init(food, snakeX, snakeY);
    }
}

class Game extends JFrame {
    GamePanel panel = new GamePanel();
    Deque<Integer> snakeX = new LinkedList<Integer>();
    Deque<Integer> snakeY = new LinkedList<Integer>();
    int[] food = new int[2];
    final int WIDTH = 300;
    final int HEIGHT = 300;
    final int APPLE_SIZE = HEIGHT / 30;
    public boolean gameOver = false;
    public static int[] appleLocation = { 1, 1 };
    public static int[] direction = { 1, 0 };

    public Game() {
        this.addKeyListener((KeyListener) new KeyboardListener());
    }

    public void init(int[] f, Deque<Integer> sX, Deque<Integer> sY) {

        food = f;
        snakeX = sX;
        snakeY = sY;

        add(panel);
        setTitle("Snake");
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        startLoop();
    }

    public void startLoop() {
        try {
            placeApple();
            while (!gameOver) {
                Thread.sleep(100);
                moveSnake();
                checkCollision();
                panel.refresh(food, direction, snakeX, snakeY);
            }
        } catch (Exception e) {
        }
    }

    private void moveSnake() {
        // System.out.println("direction: " + direction);
        Integer newX = snakeX.getFirst() + direction[0];
        Integer newY = snakeY.getFirst() + direction[1];

        snakeX.addFirst(newX);
        snakeY.addFirst(newY);

        snakeX.removeLast();
        snakeY.removeLast();
    }

    private void placeApple() {

        int xLimit = WIDTH / 10;
        int yLimit = HEIGHT / 10;

        appleLocation[0] = -1;
        appleLocation[1] = -1;

        while (appleLocation[0] < 0 || snakeX.contains(appleLocation[0]) || appleLocation[1] < 0
                || snakeY.contains(appleLocation[1])) {
            int randomX = 10 * (int) (Math.random() * xLimit);
            int randomY = 10 * (int) (Math.random() * yLimit);
            appleLocation[0] = randomX - 3 * APPLE_SIZE;
            appleLocation[1] = randomY - 5 * APPLE_SIZE;
        }
    }

    private void checkCollision() {
        // apple collision
        if (10 * snakeX.getFirst() == appleLocation[0] && 10 * snakeY.getFirst() == appleLocation[1]) {
            System.out.println("Apple eaten");
            placeApple();
            feedSnake();
        }

        // body collision
        int headX = snakeX.pollFirst();
        int headY = snakeY.pollFirst();
        boolean hasCollidedWithBody = false;

        Iterator<Integer> x = snakeX.iterator();
        Iterator<Integer> y = snakeY.iterator();
        while (x.hasNext() && y.hasNext() && !hasCollidedWithBody) {
            int xVal = x.next();
            int yVal = y.next();
            if (xVal == headX && yVal == headY) {
                hasCollidedWithBody = true;
            }
        }

        if (hasCollidedWithBody) {
            System.out.println("Collided with body");
            gameOver = true;
        }
        snakeX.addFirst(headX);
        snakeY.addFirst(headY);

        // wall collision
        if (headX > 28 || headY > 26 || headX < 0 || headY < 0) {
            System.out.println("Collided with wall");
            gameOver = true;
        }
    }

    private void feedSnake() {
        int newX = 0, newY = 0;

        if (direction[0] == 1) {
            newX = snakeX.getLast() - 1;
            newY = snakeY.getLast();
        } else if (direction[0] == -1) {
            newX = snakeX.getLast() + 1;
            newY = snakeY.getLast();
        } else if (direction[1] == 1) {
            newX = snakeX.getLast();
            newY = snakeY.getLast() - 1;
        } else if (direction[1] == -1) {
            newX = snakeX.getLast();
            newY = snakeY.getLast() + 1;
        }

        snakeX.addLast(newX);
        snakeY.addLast(newY);
    }
}

class GamePanel extends JPanel {
    int xPos = 10;
    int yPos = 10;
    int BLOCK_SIZE = 7;

    Deque<Integer> snakeX = new LinkedList<Integer>();
    Deque<Integer> snakeY = new LinkedList<Integer>();
    int[] food = new int[2];
    int[] direction = { 1, 0 };

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphic2d = (Graphics2D) g;

        Iterator<Integer> x = snakeX.iterator();
        Iterator<Integer> y = snakeY.iterator();
        // render apple
        //System.out.println("apple location: " + Game.appleLocation[0] + " " + Game.appleLocation[0]);
        graphic2d.setColor(Color.RED);
        graphic2d.fillRect(Game.appleLocation[0], Game.appleLocation[1], BLOCK_SIZE, BLOCK_SIZE);
        // render snake
        graphic2d.setColor(Color.BLUE);
        while (x.hasNext() && y.hasNext()) {
            int xVal = x.next();
            int yVal = y.next();
            graphic2d.fillRect(xVal * 10, yVal * 10, BLOCK_SIZE, BLOCK_SIZE);
        }
    }

    public void refresh(int[] f, int[] dir, Deque<Integer> sX, Deque<Integer> sY) {
        food = f;
        snakeX = sX;
        snakeY = sY;
        direction = dir;
        repaint();
    }
}

class KeyboardListener extends KeyAdapter {

    public void keyPressed(KeyEvent e) {
        // System.out.println(e.getKeyCode());
        switch (e.getKeyCode()) {
            case 39: // -> Right
                     // if it's not the opposite direction
                if (Game.direction[0] != -1) {
                    Game.direction[0] = 1;
                    Game.direction[1] = 0;
                }
                break;
            case 38: // -> Top
                if (Game.direction[1] != 1) {
                    Game.direction[0] = 0;
                    Game.direction[1] = -1;
                }
                break;
            case 37: // -> Left
                if (Game.direction[0] != 1) {
                    Game.direction[0] = -1;
                    Game.direction[1] = 0;
                }
                break;
            case 40: // -> Bottom
                if (Game.direction[1] != -1) {
                    Game.direction[0] = 0;
                    Game.direction[1] = 1;
                }
                break;
            default:
                break;
        }
    }

}