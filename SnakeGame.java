import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int DOT_SIZE = 20;
    private static final int ALL_DOTS = (WIDTH * HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private static final int RAND_POS = 29; // To ensure food is within bounds
    private static final int DELAY = 100; // Milliseconds

    private LinkedList<Point> snake;
    private Point food;
    private boolean movingLeft = false, movingRight = true, movingUp = false, movingDown = false;
    private boolean inGame = true;
    private Timer timer;
    private Random random;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        
        random = new Random();
        snake = new LinkedList<>();
        snake.add(new Point(100, 100));

        spawnFood();
        
        timer = new Timer(DELAY, this);
        timer.start();
        
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT && !movingRight) {
                    movingLeft = true; movingRight = false; movingUp = false; movingDown = false;
                }
                if (key == KeyEvent.VK_RIGHT && !movingLeft) {
                    movingRight = true; movingLeft = false; movingUp = false; movingDown = false;
                }
                if (key == KeyEvent.VK_UP && !movingDown) {
                    movingUp = true; movingDown = false; movingLeft = false; movingRight = false;
                }
                if (key == KeyEvent.VK_DOWN && !movingUp) {
                    movingDown = true; movingUp = false; movingLeft = false; movingRight = false;
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            move();
            checkCollisions();
            checkFood();
        }
        repaint();
    }

    private void move() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        if (movingLeft) newHead.x -= DOT_SIZE;
        if (movingRight) newHead.x += DOT_SIZE;
        if (movingUp) newHead.y -= DOT_SIZE;
        if (movingDown) newHead.y += DOT_SIZE;

        snake.addFirst(newHead);  // Move snake head
        snake.removeLast();  // Remove tail unless food eaten
    }

    private void checkCollisions() {
        Point head = snake.getFirst();

        // Check for wall collisions
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            inGame = false;
        }

        // Check for collisions with itself
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                inGame = false;
            }
        }
    }

    private void checkFood() {
        Point head = snake.getFirst();

        // Check if snake eats food
        if (head.equals(food)) {
            snake.addLast(food);  // Grow snake
            spawnFood();  // Generate new food
        }
    }

    private void spawnFood() {
        int x = random.nextInt(RAND_POS) * DOT_SIZE;
        int y = random.nextInt(RAND_POS) * DOT_SIZE;
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            g.setColor(Color.green);
            for (Point segment : snake) {
                g.fillRect(segment.x, segment.y, DOT_SIZE, DOT_SIZE);
            }

            g.setColor(Color.red);
            g.fillRect(food.x, food.y, DOT_SIZE, DOT_SIZE);
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String message = "Game Over! Press 'R' to Restart";
        Font font = new Font("Arial", Font.BOLD, 30);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(message, (WIDTH - metrics.stringWidth(message)) / 2, HEIGHT / 2);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    restartGame();
                }
            }
        });
    }

    private void restartGame() {
        snake.clear();
        snake.add(new Point(100, 100));
        spawnFood();
        movingLeft = false;
        movingRight = true;
        movingUp = false;
        movingDown = false;
        inGame = true;
        timer.restart();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
