package snack;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class Games {
	public static void main(String args[]) {//运行游戏主函数
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//设置显示风格为当前系统风格
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JFrame frame = new JFrame("贪吃蛇");		
		
		String src = "/photo/img.png";//设置蛇的图标
        ImageIcon icon = new ImageIcon(Games.class.getResource(src));
        frame.setIconImage(icon.getImage());
        
        Music music = new Music();
		
		Mypanel pan = new Mypanel();
		frame.add(pan);
		
		frame.setBounds(300,50,900,720);//在屏幕位置和大小
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frame.setVisible(true);
		
		music.start();
		while(!music.isAlive()) {
			music = new Music();//结束了再重新播放
			music.start();
		}
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				pan.close();//关闭窗口把数据库连接关闭
				System.exit(0);
			}
		});
		
	}
}
