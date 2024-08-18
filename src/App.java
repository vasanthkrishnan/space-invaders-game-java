import javax.swing.*;

public class App 
{
    public static void main(String[] args) throws Exception 
    {
        int rows = 16;
        int columns = 16;
        int tileSize = 32;
        int boardWidth = tileSize * columns;
        int boardHeight = tileSize * rows;
        JFrame jFrame = new JFrame("Space Invaders");
        // jFrame.setVisible(true);
        jFrame.setSize(boardWidth, boardHeight);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvaders spaceInvaders = new SpaceInvaders();
        jFrame.add(spaceInvaders);
        jFrame.pack();
        spaceInvaders.requestFocus();
        jFrame.setVisible(true);


    }
}
