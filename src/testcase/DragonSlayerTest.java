package testcase;

import huawei.DragonSlayerImpl;
import huawei.Map;
import huawei.MyElement;
import huawei.exam.Area;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

/**
 *
 */
public class DragonSlayerTest {
    @Test
    public void main() {
        File file = new File("D:\\work\\Github\\DragonSlayer\\testcase.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            JFrame jframe=new JFrame();
            Scanner scanner=new Scanner(System.in);
            DragonSlayerImpl test_map=new DragonSlayerImpl();
            String order="start";
            //while(order.matches("ends")==false){
            while(order!=null){
                try {
                    order=br.readLine();
                    if(order==null){
                        test_map.query(150);
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                }else if(parts[0].matches("r")){
                    test_map.reset();
                }

            }
            jframe.setVisible (false);
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jframe=drawMap(test_map.map);
            jframe.setVisible (true);
            scanner.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static JFrame drawMap(Map map){
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
                if(map.table[i][j].element== MyElement.HERO){
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
                }else if(map.table[i][j].element==MyElement.HERO_TORNADO){
                    label[i][j].setBackground (Color.DARK_GRAY);
                }else if(map.table[i][j].element==MyElement.HERO_TORNADO_PORTAL_EXIT){
                    label[i][j].setBackground (Color.DARK_GRAY);
                }

            }
        }
        for ( int i = 0; i < label.length; i++ )
        {
            for ( int j = 0; j < label[i].length; j++ )
            {
                map_draw.add (label[15-i][j]);
            }
        }
        jframe.add (map_draw, BorderLayout.CENTER);
        jframe.setBounds (10, 10, 650, 650);
        jframe.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        return jframe;

    }
}
