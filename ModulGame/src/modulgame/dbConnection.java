/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Fauzan
 */
public class dbConnection {
    public static Connection con;
    public static Statement stm;
    
    public void connect(){//untuk membuka koneksi ke database
        try {
            String url ="jdbc:mysql://localhost/db_gamepbo";
            String user="root";
            String pass="";
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,user,pass);
            stm = con.createStatement();
            System.out.println("koneksi berhasil;");
        } catch (Exception e) {
            System.err.println("koneksi gagal" +e.getMessage());
        }
    }
    
    public DefaultTableModel readTable(){
        
        DefaultTableModel dataTabel = null;
        try{
            Object[] column = {"No", "Username", "Score", "Duration", "Total Score"};
            connect();
            dataTabel = new DefaultTableModel(null, column){
                // Defining the type of column on your JTable. I wish sort my second column as a numeric (1,2,11), not String (1,11,2). For that I defined the second class as Integer.
                Class[] types = { Integer.class, String.class, Integer.class, Integer.class, Integer.class };
                boolean[] canEdit = new boolean [] {
                    false, false, false
                };

                    // You must add this Override in order to works sorting by numeric.
                @Override
                public Class getColumnClass(int columnIndex) {
                        return this.types[columnIndex];
                }       

                     // This override is just for avoid editing the content of my JTable. 
                @Override
                public boolean isCellEditable(int row, int column) {
                        return false;
                }
            };
            String sql = "Select * from highscore";
            ResultSet res = stm.executeQuery(sql);
            
            int no = 1;
            while(res.next()){
                Object[] hasil = new Object[5];
                hasil[0] = no;
                hasil[1] = res.getString("Username");
                hasil[2] = res.getInt("Score");
                hasil[3] = res.getInt("Duration");
                hasil[4] = res.getInt("Total_Score");
                no++;
                dataTabel.addRow(hasil);
            }
        }catch(Exception e){
            System.err.println("Read gagal " +e.getMessage());
        }
        
        
        return dataTabel;
    }
    
    public Statement getStm(){
        return stm;
    }
    
    public void pushData(String username, int score, int duration, int total_score){
        connect();
        int i;
        boolean success = true;
        String sql = "INSERT INTO highscore (Username, Score, Duration, Total_Score) VALUE('%s', '%d', '%d', '%d')";
                sql = String.format(sql, username, score, duration, total_score);
        DefaultTableModel tabel = this.readTable();
        for(i=0; i<tabel.getRowCount(); i++){
//            System.out.println(i);
            if(tabel.getValueAt(i, 1).toString().equals(username) ){
                if((int)tabel.getValueAt(i, 4)<total_score){
                    sql = "UPDATE highscore SET Score=('%d'), Duration=('%d'), Total_Score=('%d') WHERE Username=('%s')";
                    sql = String.format(sql, score, duration, total_score, username);
                    break;
                }
                else{
                    success=false;
                }
              
            }
        }
        if(success){
            try {
                // simpan data
                stm.execute(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
}
