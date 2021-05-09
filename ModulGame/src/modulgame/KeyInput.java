/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modulgame.Game.STATE;

/**
 *
 * @author Fauzan
 */
public class KeyInput extends KeyAdapter{
    
    private Handler handler;
    Game game;
    
    public KeyInput(Handler handler, Game game){
        this.game = game;
        this.handler = handler;
    }
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        
        if(game.gameState == STATE.Game){
            for(int i = 0;i<handler.object.size();i++){
                GameObject tempObject = handler.object.get(i);

                if(tempObject.getId() == ID.Player){
                    if(key == KeyEvent.VK_W){
                        tempObject.setVel_y(-5);
                    }

                    if(key == KeyEvent.VK_S){
                        tempObject.setVel_y(+5);
                    }

                    if(key == KeyEvent.VK_A){
                        tempObject.setVel_x(-5);
                    }

                    if(key == KeyEvent.VK_D){
                        tempObject.setVel_x(+5);
                    }
                }
            }
            
        }
        
        if(game.gameState == STATE.GameOver){
            if(key == KeyEvent.VK_SPACE){
                
                
                int score = game.getScore();
                String username = game.getUsername();
                int duration = game.getDuration();
                int total_score = score + duration;
                
                new dbConnection().pushData(username, score, duration, total_score);
//                java.sql.Statement stmt = game.getStm();
                
                 // query simpan
//                String sql = "INSERT INTO db_gamepbo (Username, Score) VALUE('%s', '%d')";
//                sql = String.format(sql, username, score);
//
//                try {
//                    // simpan data
//                    stmt.execute(sql);
//                } catch (SQLException ex) {
//                    Logger.getLogger(KeyInput.class.getName()).log(Level.SEVERE, null, ex);
//                }
                new Menu().setVisible(true);
//                game.playSound("/anthem.wav");
                game.close();
            }
        }
        if(key == KeyEvent.VK_ESCAPE){
            System.exit(1);
        }   
    }
    
    
    
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        
        for(int i = 0;i<handler.object.size();i++){
            GameObject tempObject = handler.object.get(i);
            
            if(tempObject.getId() == ID.Player){
                if(key == KeyEvent.VK_W){
                    tempObject.setVel_y(0);
                }
                
                if(key == KeyEvent.VK_S){
                    tempObject.setVel_y(0);
                }
                
                if(key == KeyEvent.VK_A){
                    tempObject.setVel_x(0);
                }
                
                if(key == KeyEvent.VK_D){
                    tempObject.setVel_x(0);
                }
            }
        }
    }
}
