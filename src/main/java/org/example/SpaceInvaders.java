package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 30;
    private static final int BULLET_WIDTH = 5;
    private static final int BULLET_HEIGHT = 15;
    private static final int INVADER_WIDTH = 40;
    private static final int INVADER_HEIGHT = 40;

    private int playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
    private int playerY = HEIGHT - 50;
    private int playerVelocity = 0;

    private List<Rectangle> bullets;
    private List<Rectangle> invaders;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private Timer timer;

    private int score = 0;

    public SpaceInvaders() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        bullets = new ArrayList<>();
        invaders = new ArrayList<>();

        timer = new Timer(10, this);
        timer.start();

        spawnInvaders();
    }

    private void spawnInvaders() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                invaders.add(new Rectangle(100 + j * (INVADER_WIDTH + 10), 50 + i * (INVADER_HEIGHT + 10), INVADER_WIDTH, INVADER_HEIGHT));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over!", WIDTH / 2 - 150, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString("Press Space to Restart", WIDTH / 2 - 170, HEIGHT / 2 + 50);
            return;
        }


        g.setColor(Color.GREEN);
        g.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);


        g.setColor(Color.RED);
        for (Rectangle bullet : bullets) {
            g.fillRect(bullet.x, bullet.y, BULLET_WIDTH, BULLET_HEIGHT);
        }

        // Invaderlarni chizish
        g.setColor(Color.CYAN);
        for (Rectangle invader : invaders) {
            g.fillRect(invader.x, invader.y, INVADER_WIDTH, INVADER_HEIGHT);
        }

        // Skorni chizish
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        // O'yinchi harakati
        playerX += playerVelocity;

        // O'yinchi chekkaga yetib bormasin
        if (playerX < 0) {
            playerX = 0;
        }
        if (playerX > WIDTH - PLAYER_WIDTH) {
            playerX = WIDTH - PLAYER_WIDTH;
        }

        // O'qni harakatlantirish
        List<Rectangle> newBullets = new ArrayList<>();
        for (Rectangle bullet : bullets) {
            bullet.y -= 10;
            if (bullet.y > 0) {
                newBullets.add(bullet);
            }
        }
        bullets = newBullets;

        // Invaderlarni harakatlantirish
        List<Rectangle> newInvaders = new ArrayList<>();
        for (Rectangle invader : invaders) {
            invader.y += 1;  // Invaderlar pastga tushadi
            if (invader.y < HEIGHT) {
                newInvaders.add(invader);
            }
        }
        invaders = newInvaders;

        // O'q va invaderlar urilishi
        List<Rectangle> toRemove = new ArrayList<>();
        for (Rectangle invader : invaders) {
            for (Rectangle bullet : bullets) {
                if (invader.intersects(bullet)) {
                    toRemove.add(invader);
                    toRemove.add(bullet);
                    score += 10;
                    break;
                }
            }
        }
        invaders.removeAll(toRemove);
        bullets.removeAll(toRemove);

        // Agar invaderlar ekran pastiga yetib borsa, o'yin tugaydi
        for (Rectangle invader : invaders) {
            if (invader.y + INVADER_HEIGHT >= HEIGHT) {
                gameOver = true;
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame(); // O'yinni qayta boshlash
            } else {
                shootBullet(); // O'qni otish
            }
        }
        if (key == KeyEvent.VK_LEFT) {
            playerVelocity = -5; // Chapga harakatlanish
        }
        if (key == KeyEvent.VK_RIGHT) {
            playerVelocity = 5; // O'ngga harakatlanish
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            playerVelocity = 0;  // Harakatni to'xtatish
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void shootBullet() {
        bullets.add(new Rectangle(playerX + PLAYER_WIDTH / 2 - BULLET_WIDTH / 2, playerY, BULLET_WIDTH, BULLET_HEIGHT));
    }

    private void resetGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        playerY = HEIGHT - 50;
        invaders.clear();
        spawnInvaders();
        bullets.clear();
        score = 0;
        gameOver = false;
        gameStarted = false;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Invaders");
        SpaceInvaders gamePanel = new SpaceInvaders();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
