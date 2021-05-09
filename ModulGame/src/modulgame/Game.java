/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.beans.Statement;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Fauzan
 */
public class Game extends Canvas implements Runnable{
    Window window;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    private int score = 0;
    private int total_score = 0;
    private String username;
    
    private java.sql.Statement stm;
    
    private int time = 10;
    private int duration = 0;
    
   
    
    
    private Thread thread;
    private boolean running = false;
    
    private Handler handler;
    
    public enum STATE{
        Game,
        GameOver
    };
    
    URL url;
    AudioInputStream audioIn;
    Clip clip;
    
    public STATE gameState = STATE.Game;
    
    public Game(){
        window = new Window(WIDTH, HEIGHT, "Modul praktikum 5", this);
        
        handler = new Handler();
        
        this.addKeyListener(new KeyInput(handler, this));
        
        if(gameState == STATE.Game){
            handler.addObject(new Items(100,150, ID.Item));
            handler.addObject(new Items(200,350, ID.Item));
            handler.addObject(new Items(250,150, ID.Item));

            handler.addObject(new Player(200,200, ID.Player));
            handler.addObject(new Enemy(700, 500, ID.Enemy));
        }
                playSound2("/gup.wav");
        //        playSound2("/artillery_march.wav");
//                playSound2("/anthem.wav");
        
        //BGM
//        try {
//            // Open an audio input stream.
//            url = this.getClass().getResource("/artillery_march.wav");
//            audioIn = AudioSystem.getAudioInputStream(url);
//            // Get a sound clip resource.
//            clip = AudioSystem.getClip();
//            clip.open(audioIn);
//            
//        } catch (UnsupportedAudioFileException e) {
//           e.printStackTrace();
//        } catch (IOException e) {
//           e.printStackTrace();
//        } catch (LineUnavailableException e) {
//           e.printStackTrace();
//        }
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public int getScore(){
        return score;
    }
    
    public int getDuration(){
        return duration;
    }
    
    public int getTotalScore(){
        return total_score;
    }
    
    public java.sql.Statement getStm(){
        return stm;
    } 
    
    public void setStm(java.sql.Statement stm){
        this.stm = stm;
    } 

    public synchronized void start(){
        thread = new Thread(this);
//        playBGM();

        thread.start();

        running = true;

    }
    
    public synchronized void stop(){

        try{
//            stopBGM();
//            stopSound2();
            thread.join();

            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
//            if(duration==0){
//                playSound("/artillery_march.wav");
//            }
            while(delta >= 1){
                if(handler.object.size()==2){
                    renderFood();
                    frames++;
//                    System.out.println("go");
                }
                enemyMove();
                tick();
                delta--;
            }
            
            
            if(running){
                render();
                
                frames++;
            }
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                
//                System.out.println(System.currentTimeMillis());
                frames = 0;
                if(gameState == STATE.Game){
                    duration ++;
                    if(time>0){
                        time--;
                    }else{
                        gameState = STATE.GameOver;
                        stopSound2();
                    }
                }
            }
        }
        stop();

    }
    
    void enemyMove(){
        int min=1;
        int max=4;
        int val = (int)Math.floor(Math.random()*(max-min+1)+min);
        int range = (int)Math.floor(Math.random()*(20-10+1)+10);
        if(gameState == STATE.Game){
            for(int i = 0;i<handler.object.size();i++){
                if(handler.object.get(i).getId() == ID.Enemy){
                    GameObject tempObject = handler.object.get(i);
                    switch (val){
                        case 1:
                           tempObject.setVel_x(-(range));
                           break;
                        case 2:
                            tempObject.setVel_x(+(range));
                            break;
                        case 3:
                            tempObject.setVel_y(-(range));
                            break;
                        case 4:
                            tempObject.setVel_y(+(range));
                            break;
                    }
                }     
            }
             
        }
    }
    
    private void tick(){
        handler.tick();
        int min_score=1;
        int min_time=5;
        int random_score;
        int random_time;
        if(gameState == STATE.Game){
            GameObject playerObject = null;
            for(int i=0;i< handler.object.size(); i++){
                if(handler.object.get(i).getId() == ID.Player){
                   playerObject = handler.object.get(i);
                }
            }
            if(playerObject != null){
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Item){
                        if(checkCollision(playerObject, handler.object.get(i))){
                            playSound("/Eat.wav");
                            handler.removeObject(handler.object.get(i));
                            random_score = (int)Math.floor(Math.random()*(15-min_score+1)+min_score);  //range(1-10)
                            random_time = (int)Math.floor(Math.random()*(10-min_time+1)+min_time);     //range(1-5)
                            score = score + random_score;
                            time = time + random_time;
                            break;
                        }
                    }
                    else if(handler.object.get(i).getId() == ID.Enemy){
                         if(checkCollision(playerObject, handler.object.get(i))){
//                            playSound2("/laser.wav");
//                              playSound("/Eat.wav");
//                              playSound("/buzzer.wav");
                            gameState = STATE.GameOver;
                            stopSound2();
                                

                            break;
                        }
                    }
                }
            }
        }
    }
    
    public static boolean checkCollision(GameObject player, GameObject item){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeItem = 20;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int itemLeft = item.x;
        int itemRight = item.x + sizeItem;
        int itemTop = item.y;
        int itemBottom = item.y + sizeItem;
        
        if((playerRight > itemLeft ) &&
        (playerLeft < itemRight) &&
        (itemBottom > playerTop) &&
        (itemTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    private void renderFood(){
        int min=10;
        int max_X = WIDTH - 10;
        int max_Y = HEIGHT - 10;
        int random_x = (int)Math.floor(Math.random()*(max_X-min+1)+min);
        int random_y = (int)Math.floor(Math.random()*(max_Y-min+1)+min);

        handler.addObject(new Items(random_x, random_y, ID.Item));
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.decode("#F1f3f3"));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if(gameState ==  STATE.Game){
            handler.render(g);

        }
        g.dispose();
        bs.show();
    }
    
    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        
        g.setColor(Color.decode("#F1f3f3"));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        
        
        if(gameState ==  STATE.Game){
            handler.render(g);
            
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), 20, 20);

            g.setColor(Color.BLACK);
            g.drawString("Time: " +Integer.toString(time), WIDTH-120, 20);
            g.drawString("Duration: " +Integer.toString(this.duration), WIDTH-120, 50);
            
        }else{
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", WIDTH/2 - 120, HEIGHT/2 - 30);

            currentFont = g.getFont();
            Font newScoreFont = currentFont.deriveFont(currentFont.getSize() * 0.5F);
            g.setFont(newScoreFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), WIDTH/2 - 50, HEIGHT/2 - 10);
            
            g.setColor(Color.BLACK);
            g.drawString("Press Space to Continue", WIDTH/2 - 100, HEIGHT/2 + 30);
            
            g.setColor(Color.BLACK);
            g.drawString("Duration: " +Integer.toString(this.duration), WIDTH/2 - 70, HEIGHT/2 + 10);
        }
            
        g.dispose();
        bs.show();
    }
    
    
    
    public static int clamp(int var, int min, int max){
        if(var >= max){
            return var = max;
        }else if(var <= min){
            return var = min;
        }else{
            return var;
        }
    }
    
    public void close(){
        window.CloseWindow();
    }
    
    public void playSound(String filename){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    
    }
    
    public void playSound2(String filename){
//         Clip klip = null;

        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
        
//        return klip;
    
    }
    
    public void stopSound2(){
        // Open an audio input stream.
//            URL url = this.getClass().getResource(filename);
//            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
//            // Get a sound clip resource.
//            Clip clip = AudioSystem.getClip();
//            // Open audio clip and load samples from the audio input stream.
//            clip.open(audioIn);
        System.out.println("stop bgm!");
        clip.stop();
    
    }
    
    void playBGM(){
        
            // Open audio clip and load samples from the audio input stream.
            
            clip.start();
        
    }
    
    void stopBGM(){
        clip.stop();
        clip.close();
    }
}
