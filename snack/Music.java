package snack;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class Music extends Thread {
	@Override
	public void run() {
		String filename="C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\背景音乐\\晴天.mp3";//文件路径
        try { 
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(filename));
            Player player = new Player(buffer);//加载
            player.play();//开始播放
        } catch (Exception e) {
            System.out.println(e);
        }
	}
}