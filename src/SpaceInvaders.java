import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class SpaceInvaders extends JPanel implements ActionListener, KeyListener
{
    class Block 
    {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, Image img)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;

    ArrayList<Image> alienImageArray;

    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * columns / 2 - tileSize;
    int shipY = boardHeight - tileSize * 2;
    int shipVelocityX = 5;
    Block ship;

    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10;

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    boolean moveRight = false;
    boolean moveLeft = false;
    int bulletCount = 500;

    SpaceInvaders()
    {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();

        alienImageArray = new ArrayList<>();
        alienImageArray.add(alienImg);
        alienImageArray.add(alienCyanImg);
        alienImageArray.add(alienYellowImg);
        alienImageArray.add(alienMagentaImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<>();

        bulletArray = new ArrayList<>();

        gameLoop = new Timer(1000 / 60, this);
        createAliens();
        gameLoop.start();

        bulletCount = 500;

    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);

    }

    public void draw(Graphics g)
    {
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        for(int i = 0; i < alienArray.size(); i++)
        {
            Block alien = alienArray.get(i);
            if(alien.alive)
            {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        g.setColor(Color.white);
        for(int i = 0; i < bulletArray.size(); i++)
        {
            Block bullet = bulletArray.get(i);
            if(!bullet.used)
            {
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString("Score: " + score, 10, 35);
        g.drawString("Bullets: " + bulletCount, boardWidth - 200, 35);
        if(gameOver)
        {
            g.setColor(Color.red);
            g.drawString("Game Over", boardWidth / 2 - 80, boardHeight / 2);
            g.drawString("Final Score: " + score, boardWidth / 2 - 110, boardHeight / 2 + 40);
        }
    }

    public void move()
    {
        for(int i = 0; i < alienArray.size(); i++)
        {
            Block alien = alienArray.get(i);
            if(alien.alive)
            {
                alien.x += alienVelocityX;

                if(alien.x + alien.width >= boardWidth || alien.x <= 0)
                {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;

                    for(int j = 0; j < alienArray.size(); j++)
                    {
                        alienArray.get(j).y += alienHeight;
                    }
                }
                if(alien.y >= ship.y)
                {
                    gameOver = true;
                }
            }
        }

        for(int i = 0; i < bulletArray.size(); i++)
        {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;
            
            for(int j = 0; j < alienArray.size(); j++)
            {
                Block alien = alienArray.get(j);
                if(!bullet.used && alien.alive && detectCollision(bullet, alien))
                {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                }
            }
        }
        while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) 
        {
            bulletArray.remove(0);    
        }

        if(alienCount == 0)
        {
            score += alienColumns * alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, columns / 2 - 2);
            alienRows = Math.min(alienRows + 1, rows - 6);
            alienArray.clear();
            bulletArray.clear();
            alienVelocityX = 1;
            createAliens();
        }
    }

    public void createAliens()
    {
        Random random = new Random();
        for(int r = 0; r < alienRows; r++)
        {
            for(int c = 0; c < alienColumns; c++)
            {
                int randomImgIndex = random.nextInt(alienImageArray.size());
                Block alien = new Block(
                    alienX + c * alienWidth,
                    alienY + r * alienHeight,
                    alienWidth, alienHeight, alienImageArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b)
    {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        move();
        bulletArray.removeIf(bullet -> bullet.used || bullet.y < 0);

        if(bulletCount <= 0 && bulletArray.isEmpty())
        {
            gameOver = true;
        }

        if (moveLeft && ship.x - shipVelocityX >= 0) 
        {
            ship.x -= shipVelocityX;
        }
        if (moveRight && ship.x + ship.width + shipVelocityX <= boardWidth) 
        {
            ship.x += shipVelocityX;
        }
        repaint();
        if (gameOver)
        {
            gameLoop.stop();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) 
    {
        if(gameOver)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) 
            {
                resetGame();
            }
        }
        else
        {
            if(e.getKeyCode() == KeyEvent.VK_LEFT)
            {
                moveLeft = true;
            }
            if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            {
                moveRight = true;
            }
            if(e.getKeyCode() == KeyEvent.VK_SPACE && bulletCount > 0)
            {
                Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
                bulletArray.add(bullet);
                bulletCount--;
            }
        }
    }    

    public void resetGame()
    {
        ship.x = shipX;
        alienArray.clear();
        bulletArray.clear();
        score = 0;
        alienVelocityX = 1;
        alienColumns = 3;
        alienRows = 2;
        bulletCount = 500;
        gameOver = false;
        createAliens();
        gameLoop.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) 
    {
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            moveLeft = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            moveRight = false;
        }
    }
}
