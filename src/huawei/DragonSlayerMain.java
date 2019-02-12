package huawei;

package huawei;

import huawei.exam.Area;
import huawei.exam.ExamCmd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.huawei.exam.Command;
import com.huawei.exam.ExamSocketServer;

/**
 * 主执行类
 * 
 * 考生不得修改，亦无须关注
 */
public class DragonSlayerMain
{
    public static void main(String[] args)
    {
        /**
         * 启动Socket服务侦听5555端口，从Socket获取命令，会丢给Command类的command函数执行
         * Command类的command函数已经实现了从Socket接收到字符串后的解析与分发 考生只需要实现DragonSlayerImpl类的各命令接口即可。
         
        
        Command cmd = new ExamCmd(new DragonSlayerImpl());
        ExamSocketServer ess = new ExamSocketServer(cmd);
        ess.start();*/
    	
    	Scanner scanner=new Scanner(System.in);
    	DragonSlayerImpl test_map=new DragonSlayerImpl();
    	String order="start";
    	while(order.matches("ends")==false){
    		order=scanner.nextLine();
    		String[] parts=order.split(" ");   		
    		if(parts[0].matches("sf")){
    			int x,y,t;
    			x=Integer.valueOf(parts[1]);
    			y=Integer.valueOf(parts[2]);
    			t=Integer.valueOf(parts[3]);
    			test_map.setFire(new Area(x,y),t);
    		}else if(parts[0].matches("st")){
    			int x,y,t;
    			x=Integer.valueOf(parts[1]);
    			y=Integer.valueOf(parts[2]);
    			t=Integer.valueOf(parts[3]);
    			test_map.setTornado(new Area(x,y),t);
    		}else if(parts[0].matches("sp")){
    			int x1,y1,x2,y2,t;
    			x1=Integer.valueOf(parts[1]);
    			y1=Integer.valueOf(parts[2]);
    			x2=Integer.valueOf(parts[3]);
    			y2=Integer.valueOf(parts[4]);
    			t=Integer.valueOf(parts[5]);
    			test_map.setPortal(new Area(x1,y1), new Area(x2,y2), t);
    		}else if(parts[0].matches("q")){
    			int t;
    			t=Integer.valueOf(parts[1]);
    			test_map.query(t);
    		}
    		drawMap(test_map.map);
    	}
    	
    }
    
    public static void drawMap(Map map){
    	JFrame jframe=new JFrame();
        GridLayout grid = new GridLayout (16, 16);
        JPanel map_draw = new JPanel ();
        map_draw.setLayout (grid);
        Label[][] label = new Label[16][16];
        for ( int i = 0; i < label.length; i++ )
        {
            for ( int j = 0; j < label[i].length; j++ )
            {
                label[i][j] = new Label ();
                if (( i + j ) % 2 == 0){
                    label[i][j].setBackground (Color.white);
                }else{
                    label[i][j].setBackground (Color.black);
                }
                if(map.table[i][j].element==MyElement.HERO){
                	label[i][j].setBackground (Color.green);
                }else if(map.table[i][j].element==MyElement.DRAGON){
                	label[i][j].setBackground (Color.cyan);
                }else if(map.table[i][j].element==MyElement.FIRE){
                	label[i][j].setBackground (Color.red);
                }else if(map.table[i][j].element==MyElement.TORNADO){
                	label[i][j].setBackground (Color.blue);
                }else if(map.table[i][j].element==MyElement.PORTAL_ENTRANCE){
                	label[i][j].setBackground (Color.orange);
                }else if(map.table[i][j].element==MyElement.PORTAL_EXIT){
                	label[i][j].setBackground (Color.yellow);
                }else if(map.table[i][j].element==MyElement.HERO_PORTAL_EXIT){
                	label[i][j].setBackground (Color.pink);
                }else if(map.table[i][j].element==MyElement.DRAGON_PORTAL_EXIT){
                	label[i][j].setBackground (Color.GRAY);
                }else if(map.table[i][j].element==MyElement.FIRE_PORTAL_EXIT){
                	label[i][j].setBackground (Color.magenta);
                }else if(map.table[i][j].element==MyElement.TORNADO_PORTAL_EXIT){
                	label[i][j].setBackground (Color.LIGHT_GRAY);
                }
                map_draw.add (label[i][j]);
            }
        }
        jframe.add (map_draw, BorderLayout.CENTER);
        jframe.setBounds (10, 10, 650, 650);
        jframe.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        jframe.setVisible (true);
    }
}