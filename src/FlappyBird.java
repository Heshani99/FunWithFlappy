import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth =360;
    int boardHeight = 640;

    //Adding Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 35;
    int birdHeight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; //scalled by 1/6
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;  
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }

    }

    //game logic
    Bird bird;
    int velocityX = -4; //move pipes to the left speed (simulates bird moving right)
    int velocityY = 0;
    int gravity = 1;  //every sec bird is ginna slow down by 1 pixel
    
    ArrayList<Pipe> pipes;
    Random randome = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;


    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true); //make sure this class takes all the key events
        addKeyListener(this); //check key three functions

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe> ();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this);  //100/60 = 16.6
        gameLoop.start();  //to draw the frames for our game continuosly 

    }

    public void placePipes(){ // place new pipes
        
        int randomePipeY = (int )(pipeY - pipeHeight/4 -Math.random()*(pipeHeight/2));  // (0-1) * pipeHeight/2 -> 0-256 randome number  ---> 1/4 - 3/4 pipeHeight
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomePipeY;
        pipes.add(topPipe);

        Pipe bottomePipe = new Pipe(bottomPipeImg);
        bottomePipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomePipe);

    }

    public void paintComponent(Graphics g){   
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes - draw the pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 25));
        if(gameOver){
            g.drawString("GAME OVER: " + String.valueOf((int) score), 10, 35);
        }else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move(){
        //bird
        velocityY+= gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); //0 is the very top of the screen, this is to stop bird moving at the upper corner

        //pipes - move the pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX; 

            if (!pipe.passed && bird.x > pipe.x +pipeWidth) {
                pipe.passed = true;
                score += 0.5; //because there are two pipes, so one mark for each set of pipes
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b){
        return  a.x < b.x + b.width &&  //birds top left corner not touching pipes beginning
                a.x + a.width > b.x &&  //birds top right corner not touching pipes end 
                a.y < b.y + b.height && 
                a.y + a.height > b.y;   
    }


    @Override
    public void actionPerformed(ActionEvent e) { //action performed every 16 mil secs
        move();
        repaint(); //this calls paint component
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }

    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                //restart thegame by resetting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
