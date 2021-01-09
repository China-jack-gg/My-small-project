package snack;
import javax.swing.*;

public class Data {//数据存放类
	//存放需要的图片数据，ImageIcon：图片
	public static ImageIcon header = new ImageIcon(Data.class.getResource("/photo/header.png"));//说明图片
	public static ImageIcon up = new ImageIcon(Data.class.getResource("/photo/up.png"));//头部向上图片
	public static ImageIcon down = new ImageIcon(Data.class.getResource("/photo/down.png"));//头部向下图片
	public static ImageIcon left = new ImageIcon(Data.class.getResource("/photo/left.png"));//头部向左图片
	public static ImageIcon right = new ImageIcon(Data.class.getResource("/photo/right.png"));//头部向右图片
	public static ImageIcon body = new ImageIcon(Data.class.getResource("/photo/body.png"));//蛇身体图片
	public static ImageIcon food = new ImageIcon(Data.class.getResource("/photo/food.png"));//蛇食物图片
	public static ImageIcon wall = new ImageIcon(Data.class.getResource("/photo/wall.png"));//墙图片
	public static Mybutton start = new Mybutton("开始游戏");
	public static Mybutton difficulty = new Mybutton("游戏难度");
	public static Mybutton rank = new Mybutton("得分排行");
	public static Mybutton speak = new Mybutton("游戏说明");
	public static Mybutton exit = new Mybutton("退出游戏");
	
	public static Mybutton simple = new Mybutton("简单");
	public static Mybutton common = new Mybutton("普通");
	public static Mybutton difficult = new Mybutton("困难");
	
	public static Mybutton cont = new Mybutton("继续游戏");
	public static Mybutton save = new Mybutton("存档");
	public static Mybutton retu = new Mybutton("返回系统");
	
	public static Mybutton file = new Mybutton("读取存档");
	
	public static Mybutton fone = new Mybutton("空存档");
	public static Mybutton ftwo = new Mybutton("空存档");
	public static Mybutton fthree = new Mybutton("空存档");
	
	public static JTextArea instruction = new JTextArea("    尊敬的用户，您好！欢迎来到贪吃蛇小游戏！\n"
												+ "    规则：\n"
												+ "        主界面按空格或者点击开始游戏开始进行游戏\n"
												+ "        主界面按exit或者关闭游戏对话框就退出游戏\n"
												+ "        游戏难度按钮可以设置本游戏的难度\n"
												+ "        游戏排行可以查看本游戏的各难度排行榜\n"
												+ "        不能撞墙，撞自己和撞边界哦\n"
												+ "	用户名只能在6个字符以内，超过的部分无效\n"
												+ "	如果用户名为空会自己保存失败哦\n"
												+ " 	输入用户名后回车即可保存\n"
												+ " 	鼠标左键存档读取，鼠标右键存档删除\n"
												+ " 	操作：控制小蛇上下左右\n"
												+ " 	上：w 或者 ↓ \n"
												+ "        下：S 或者 ↑ \n"
												+ "        左：A 或者 ← \n"
												+ "        右：D 或者 →");//说明书
	public static JTextArea Rs = new JTextArea();//简单模式排行榜
	public static JTextArea Rc = new JTextArea();//普通模式排行榜
	public static JTextArea Rd = new JTextArea();//困难模式排行榜
	
	public static JLabel lab = new JLabel("请输入你的用户名：");
	public static JTextField text = new JTextField(10);//用户名输入框
}
