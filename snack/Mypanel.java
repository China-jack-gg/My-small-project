//此蛇每次走25像素是计算好的
package snack;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.*;

class Point implements Serializable{//坐标
	int x;
	int y;
	public Point(int x,int y){
		this.x=x;
		this.y=y;
	}
}
public class Mypanel extends JPanel{//画板
	//显示一次设置一次位置，防止被挤出去
	private ArrayList<Point> point = new ArrayList<>();//蛇的位置
	private ArrayList<Point> Wpoint = new ArrayList<>();//墙的位置
	private Timer timer;//计时器
	private Point food;//事物坐标
	private Random random = new Random();
	private Myconnection con = new Myconnection();//创建数据库连接
	private String fx;//方向
	private boolean start=false;//判断游戏是否开始
	private boolean isstart=false;//判断现在游戏是否进行
	private boolean isfail=false;//刚刚开始肯定是未失败
	private int mode=1;//1是简单，2是普通，3是困难，默认1简单
	private int oldmode = -1;//防止读取存档后模式错乱
	private int seconds=300;//初始秒
	private int score=20;//初始分数
	public Mypanel() {
		iniv();
	}
	public void datainiv() {//数据初始化
		start = false;
		isstart = false;
		isfail = false;
		point.clear();//清空
		point.add(new Point(400,300));//刚刚开始只有1个头和一节身体
		point.add(new Point(375,300));
		fx = "R";
		food = new Point(0,0);//初始化一个不可能撞到的坐标
		Wpoint.clear();//清除墙壁，初始速度都一样增加游戏体验
		seconds=300;//初始化速度
		score = 20;//初始化分数
		timer.setDelay(seconds);
		if(oldmode!=-1) {
			mode = oldmode;//读取存档后用前面的难度
			oldmode = -1;
		}
		if(mode == 1) Wall1();//造墙
		if(mode == 2) Wall2();
		if(mode == 3) Wall3();
	}
	public void iniv() {//程序初始化
		readFile();//是否曾经有存档
		point.add(new Point(400,300));//刚刚开始只有1个头和一节身体
		point.add(new Point(375,300));
		fx = "R";
		food = new Point(0,0);//初始化一个不可能撞到的坐标
		Wall1();
		timer = new Timer(seconds, new ActionListener() {//线程类计时器，第一个参数是设置每多少时间执行一次此事件，第二个参数是此事件
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isstart) {//如果游戏开始且没有暂停就让蛇动起来
					ArrayList<Point> p = new ArrayList<Point>();//创建新集合来保存修改过后的蛇的位置
					Iterator<Point> it = point.iterator();
					Point head = it.next();
					Point body = new Point(head.x,head.y);//身体跟着头部的地方走
					if(fx.compareTo("U")==0) head.y=head.y-25;//头部走向
					else if(fx.compareTo("D")==0) head.y=head.y+25;
					else if(fx.compareTo("L")==0) head.x=head.x-25;
					else head.x=head.x+25;
					if(ispeng(head))isfail=true;//判断是否撞到了，撞到了下面加不加无所谓了
					if(!isfail) {
						boolean eating = false;
						if(head.x == food.x && head.y == food.y) {//吃到食物了
							if(mode == 1) {//根据模式选择最终速度和增加速度的速度
									seconds-=10;
									if(seconds<50)seconds=50;
							}else if(mode == 2) {
									seconds-=20;
									if(seconds<50)seconds=50;
							}else if(mode == 3) {
									seconds-=15;
									if(seconds<50)seconds=50;
							}
							timer.setDelay(seconds);
							eating = true;//表示吃到了食物
							if(seconds>250)score+=30;
							else if(seconds>200)score+=50;
							else if(seconds>100)score+=100;
							else score+=300;
							reflush();//刷新食物
						}
						p.add(head);
						p.add(body);//第一节身体的位置就是开始头部的位置
						while(it.hasNext()) {//更新身体位置		
							body = it.next();
							p.add(body);
						}
						if(!eating)p.remove(body);//如果未吃到食物删除最后一个加进去的元素
						point.clear();//释放集合
						point = p;//更新集合
					}
					repaint();
				}
				timer.start();//开始将事件运行一次 ，调用这个就可以如此反复运行
			}
		});
		timer.start();//运行起来
		
		this.setFocusable(true);//设置为true它才能让键盘的焦点聚集在这个pan里面
		this.addKeyListener(new KeyAdapter() {//匿名类获取键盘监听事件
			@Override
			public void keyPressed(KeyEvent e) {//如果按键会怎么样
				if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {//按空格键开始
					if(isfail) {
						if(e.getKeyCode() == KeyEvent.VK_SPACE) {
							if(isstart) {//失败按了空格
								Data.lab.setVisible(true);
								Data.text.setVisible(true);
								Data.text.requestFocus();//获得焦点
							} else isstart = !isstart;
						}
						if(e.getKeyCode() == KeyEvent.VK_ENTER)
						{
							if(!Data.text.isVisible())datainiv();//数据更新
							else isstart = !isstart;
						}
					}
					if(start) {//游戏开始
						if(!isfail) {
							if(isstart) {//游戏正在进行按的空格
								showcont();//显示暂停界面
							} else {
								hidecont();//隐藏暂停界面
							}
						}
					} 
					if(!Data.instruction.isVisible()&&!Data.Rs.isVisible()&&!Data.simple.isVisible()&&!Data.fone.isVisible())
						isstart = !isstart;//判断现在的游戏是暂停还是继续
				}
				else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {//三个界面按钮不可能有俩组以上出现
					if(isfail) {
						if(!Data.text.isVisible()) {//显示数据界面
							datainiv();//清楚数据
							showstart();
							repaint();
						} else {
							Data.lab.setVisible(false);
							Data.text.setVisible(false);
							isstart = !isstart;
						}
					}
					else if(Data.start.isVisible()) {//在主界面按esc键退出游戏
						con.close();//关闭数据库连接
						System.exit(0);
					}
					else if(Data.cont.isVisible()) {//如果是显示继续界面
						hidecont();//隐藏暂停界面
						isstart = !isstart;
						repaint();
					} 
					else if(Data.fone.isVisible()) {//是存档按钮
						hidefile();//隐藏存档按钮
						if(start) showcont();//游戏开始肯定是暂停界面
						else showstart();//游戏没开始就是继续游戏界面
					}
					else if(Data.simple.isVisible()) {//选择简单困难界面
						hidesimple();//隐藏困难程度按钮
						showstart();//显示主界面按钮
					}else if(Data.instruction.isVisible()){
						Data.instruction.setVisible(false);
						showstart();//显示主界面
					}else if(Data.Rs.isVisible()) {
						Data.Rs.setVisible(false);
						Data.Rc.setVisible(false);
						Data.Rd.setVisible(false);
						showstart();//显示主界面
					}
					else
					{//都没显示就是游戏正在运行
						isstart = !isstart;
						showcont();//显示暂停界面
					}
				}
				else if(isstart) {//游戏开始了小蛇的头才可以动
					if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
						Iterator<Point> it=point.iterator();
						Point head = it.next();//头位置
						Point body = it.next();//后面那个位置
						if(!(head.x == body.x && head.y-25 == body.y)) {//如果回头就不能
							fx="U";//不能回头
						}
					}
					else if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
						Iterator<Point> it=point.iterator();
						Point head = it.next();//头位置
						Point body = it.next();//后面那个位置
						if(!(head.x == body.x && head.y+25 == body.y)) {//如果回头就不能
							fx="D";//不能回头
						}
					}
					else if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
						Iterator<Point> it=point.iterator();
						Point head = it.next();//头位置
						Point body = it.next();//后面那个位置
						if(!(head.x-25 == body.x && head.y == body.y)) {//如果回头就不能
							fx="L";//不能回头
						}
					}
					else if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
						Iterator<Point> it=point.iterator();
						Point head = it.next();//头位置
						Point body = it.next();//后面那个位置
						if(!(head.x+25 == body.x && head.y == body.y)) {//如果回头就不能
							fx="R";//不能回头
						}
					}
				}
				repaint();//刷新界面，重新加载paintComponent类
			}
		});
		
		this.addMouseListener(new MouseAdapter() {//点击界面时获得焦点
			@Override
			public void mouseClicked(MouseEvent e) {
				e.getComponent().requestFocus();
			}
		});
		
		this.add(Data.start);//添加主界面按钮
		this.add(Data.difficulty);
		this.add(Data.file);
		this.add(Data.rank);
		this.add(Data.speak);
		this.add(Data.exit);
		Data.start.setFocusable(false);//设置之后不会获取焦点
		Data.difficulty.setFocusable(false);//设置之后不会获取焦点
		Data.file.setFocusable(false);//设置之后不会获取焦点
		Data.rank.setFocusable(false);//设置之后不会获取焦点
		Data.speak.setFocusable(false);//设置之后不会获取焦点
		Data.exit.setFocusable(false);//设置之后不会获取焦点
		
		this.add(Data.simple);//添加模式按钮
		this.add(Data.common);
		this.add(Data.difficult);
		Data.simple.setVisible(false);
		Data.common.setVisible(false);
		Data.difficult.setVisible(false);
		Data.simple.setFocusable(false);//设置之后不会获取焦点
		Data.common.setFocusable(false);//设置之后不会获取焦点
		Data.difficult.setFocusable(false);//设置之后不会获取焦点
		
		this.add(Data.cont);//添加暂停按钮
		this.add(Data.save);
		this.add(Data.retu);
		Data.cont.setVisible(false);
		Data.save.setVisible(false);
		Data.retu.setVisible(false);
		Data.cont.setFocusable(false);//设置之后不会获取焦点
		Data.save.setFocusable(false);//设置之后不会获取焦点
		Data.retu.setFocusable(false);//设置之后不会获取焦点
		
		this.add(Data.fone);//添加存档按钮
		this.add(Data.ftwo);
		this.add(Data.fthree);
		Data.fone.setVisible(false);
		Data.ftwo.setVisible(false);
		Data.fthree.setVisible(false);
		Data.fone.setFocusable(false);//设置之后不会获取焦点
		Data.ftwo.setFocusable(false);//设置之后不会获取焦点
		Data.fthree.setFocusable(false);//设置之后不会获取焦点
		
		Data.instruction.setForeground(Color.blue);//设置说明书的格式
		Data.instruction.setFocusable(false);
		Data.instruction.setFont(new Font("楷体",Font.BOLD,30));
		Data.instruction.setBackground(Color.black);
		Data.instruction.setEditable(false);
		this.add(Data.instruction);//添加说明
		Data.instruction.setVisible(false);
		
		Data.Rs.setForeground(Color.blue);//设置简单模式排行格式
		Data.Rs.setFocusable(false);
		Data.Rs.setFont(new Font("楷体",Font.BOLD,20));
		Data.Rs.setBackground(Color.black);
		Data.Rs.setEditable(false);
		this.add(Data.Rs);//添加说明
		Data.Rs.setVisible(false);

		Data.Rc.setForeground(Color.blue);//设置普通模式排行格式
		Data.Rc.setFocusable(false);
		Data.Rc.setFont(new Font("楷体",Font.BOLD,20));
		Data.Rc.setBackground(Color.black);
		Data.Rc.setEditable(false);
		this.add(Data.Rc);//添加说明
		Data.Rc.setVisible(false);
		
		Data.Rd.setForeground(Color.blue);//设置困难模式排行格式
		Data.Rd.setFocusable(false);
		Data.Rd.setFont(new Font("楷体",Font.BOLD,20));
		Data.Rd.setBackground(Color.black);
		Data.Rd.setEditable(false);
		this.add(Data.Rd);//添加说明
		Data.Rd.setVisible(false);
		
		Data.text.setFont(new Font("楷体",Font.BOLD,20));
		Data.text.setHorizontalAlignment(JTextField.CENTER);
		Data.text.setVisible(false);
		this.add(Data.text);//用户名输入框
		
		Data.lab.setForeground(Color.white);
		Data.lab.setFont(new Font("楷体",Font.BOLD,30));
		Data.lab.setHorizontalAlignment(JLabel.CENTER);
		Data.lab.setVisible(false);
		this.add(Data.lab);
		
		Data.start.addActionListener(new ActionListener() {//开始按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				datainiv();//数据清除
				isstart = true;
				hidestart();//显示主界面
			}
		});
		Data.difficulty.addActionListener(new ActionListener() {//困难程度按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				hidestart();//隐藏主界面
				showsimple();//显示困难程度按钮
			}
		});
		Data.simple.addActionListener(new ActionListener() {//简单模式按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				hidesimple();//隐藏困难程度按钮
				showstart();//显示主界面
				mode=1;
				datainiv();//初始化
			}
		});
		Data.common.addActionListener(new ActionListener() {//普通模式按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				hidesimple();//隐藏困难程度
				showstart();//显示主界面
				mode=2;//设置模式
				datainiv();//初始化
			}
		});
		Data.difficult.addActionListener(new ActionListener() {//困难模式按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				hidesimple();//隐藏困难程度按钮
				showstart();//显示主界面
				mode=3;//设置模式
				datainiv();//初始化
			}
		});
		Data.file.addActionListener(new ActionListener() {//读取存档按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				hidestart();
				showfile();
			}
		});
		Data.fone.addActionListener(new ActionListener() {//存档一
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Data.fone.getText().compareTo("空存档") !=0 ) {
					if(start) {//游戏进行是存档，覆盖存档
						int select=JOptionPane.showConfirmDialog(null,"你想要覆盖掉该存档吗？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION)save(1);//存档
						hidefile();//界面操作
						showcont();
					}else {
						int select=JOptionPane.showConfirmDialog(null,"是否读取存档？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION) {
							hidefile();
							read(1);//读取存档
						}
					}
				} else {
					if(start) {//游戏进行是存档
						save(1);//存档
						JOptionPane.showMessageDialog(null, "存档成功", "贪吃蛇", JOptionPane.INFORMATION_MESSAGE);//提示成功信息
						Data.fone.setText("存档一");
						hidefile();
						showcont();
					}
				}
			}
		});
		Data.fone.addMouseListener(new MouseAdapter() {//鼠标右击按钮删除存档一
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.isMetaDown()) {//判断是否鼠标右击
					if(Data.fone.getText().compareTo("空存档") !=0 ) {//不是空存档才能删除
						int select=JOptionPane.showConfirmDialog(null,"你想要删除掉该存档吗？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION) {
							File fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data1.txt");
							fdata.delete();//将原文件删除
							try {
								fdata.createNewFile();//再创建新文件
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							Data.fone.setText("空存档");//变成空存档
						}
					}else {//是空档报错
						JOptionPane.showMessageDialog(null, "空存档无法删除", "贪吃蛇", JOptionPane.ERROR_MESSAGE);//报错信息
					}
				}
			}
		});
		Data.ftwo.addActionListener(new ActionListener() {//存档二
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Data.ftwo.getText().compareTo("空存档") !=0 ) {
					if(start) {//游戏进行是存档，覆盖存档
						int select=JOptionPane.showConfirmDialog(null,"你想要覆盖掉该存档吗？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION)save(2);//存档
						hidefile();//界面操作
						showcont();
					}else {
						int select=JOptionPane.showConfirmDialog(null,"是否读取存档？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION) {
							hidefile();
							read(2);//读取存档
						}
					}
				} else {
					if(start) {//游戏进行是存档
						save(2);//存档
						JOptionPane.showMessageDialog(null, "存档成功", "贪吃蛇", JOptionPane.INFORMATION_MESSAGE);//提示成功信息
						Data.ftwo.setText("存档二");
						hidefile();
						showcont();
					}
				}
			}
		});
		Data.ftwo.addMouseListener(new MouseAdapter() {//鼠标右击按钮删除存档二
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.isMetaDown()) {//判断是否鼠标右击
					if(Data.ftwo.getText().compareTo("空存档") !=0 ) {//不是空存档才能删除
						int select=JOptionPane.showConfirmDialog(null,"你想要删除掉该存档吗？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION) {
							File fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data2.txt");
							fdata.delete();//将原文件删除
							try {
								fdata.createNewFile();//再创建新文件
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							Data.ftwo.setText("空存档");//变成空存档
						}
					}else {//是空档报错
						JOptionPane.showMessageDialog(null, "空存档无法删除", "贪吃蛇", JOptionPane.ERROR_MESSAGE);//报错信息
					}
				}
			}
		});
		Data.fthree.addActionListener(new ActionListener() {//存档三
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Data.fthree.getText().compareTo("空存档") !=0 ) {
					if(start) {//游戏进行是存档，覆盖存档
						int select=JOptionPane.showConfirmDialog(null,"你想要覆盖掉该存档吗？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION)save(3);//存档
						hidefile();//界面操作
						showcont();
					}else {
						int select=JOptionPane.showConfirmDialog(null,"是否读取存档？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION) {
							hidefile();
							read(3);//读取存档
						}
					}
				} else {
					if(start) {//游戏进行是存档
						save(3);//存档
						JOptionPane.showMessageDialog(null, "存档成功", "贪吃蛇", JOptionPane.INFORMATION_MESSAGE);//提示成功信息
						Data.fthree.setText("存档三");
						hidefile();
						showcont();
					}
				}
			}
		});
		Data.fthree.addMouseListener(new MouseAdapter() {//鼠标右击按钮删除存档
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.isMetaDown()) {//判断是否鼠标右击
					if(Data.fthree.getText().compareTo("空存档") !=0 ) {//不是空存档才能删除
						int select=JOptionPane.showConfirmDialog(null,"你想要删除掉该存档吗？", "贪吃蛇", JOptionPane.YES_NO_OPTION);
						if(select == JOptionPane.YES_OPTION) {
							File fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data3.txt");
							fdata.delete();//将原文件删除
							try {
								fdata.createNewFile();//再创建新文件
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							Data.fthree.setText("空存档");//变成空存档
						}
					}else {//是空档报错
						JOptionPane.showMessageDialog(null, "空存档无法删除", "贪吃蛇", JOptionPane.ERROR_MESSAGE);//报错信息
					}
				}
			}
		});
		Data.rank.addActionListener(new ActionListener() {//排行按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				hidestart();//隐藏主界面
				con.rank();//排序
				Data.Rs.setVisible(true);
				Data.Rc.setVisible(true);
				Data.Rd.setVisible(true);
			}
		});
		Data.speak.addActionListener(new ActionListener() {//说明按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				hidestart();//隐藏主界面
				Data.instruction.setVisible(true);
			}
		});
		Data.exit.addActionListener(new ActionListener() {//退出按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				con.close();//关闭数据库连接
				System.exit(0);//退出系统
			}
		});
		Data.cont.addActionListener(new ActionListener() {//继续游戏按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				isstart = !isstart;
				hidecont();//隐藏暂停界面
			}
		});
		Data.save.addActionListener(new ActionListener() {//存档
			@Override
			public void actionPerformed(ActionEvent e) {
				hidecont();//隐藏暂停界面
				showfile();//显示存档界面
			}
		});
		Data.retu.addActionListener(new ActionListener() {//返回上一层按钮
			@Override
			public void actionPerformed(ActionEvent e) {
				hidecont();//隐藏暂停界面
				showstart();//显示主界面
				datainiv();//重置数据
				repaint();//刷新界面
			}
		});
		Data.text.addKeyListener(new KeyAdapter() {//用户记录保存框
			@Override
			public void keyPressed(KeyEvent e) {//按键作用
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					con.submit(mode,Data.text.getText(),score);//提交分数和账户和模式
					Data.text.setText("");//重置
					Data.lab.setVisible(false);
					Data.text.setVisible(false);//保存之后就隐藏
					datainiv();//清除数据
					showstart();
				}else if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
					Data.text.setText("");//数据清除
					Data.lab.setVisible(false);
					Data.text.setVisible(false);
					isstart = !isstart;
					repaint();//刷新画面
				}
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g) {//此函数会自动调用绘画屏幕
		//Graphics是类似于画笔的类
		super.paintComponent(g);//清屏
		this.setBackground(Color.white);
		
		Data.header.paintIcon(this,g,25,11);//用画笔绘制头部的说明图，第一个是本面板，第二哥是画笔，后面俩是坐标
		g.fillRect(25,75,850,600);//绘制矩形，前面是左上角坐标，后面是右下角坐标
		g.setColor(Color.black);//设置画笔颜色
		g.setFont(new Font("楷体",Font.BOLD,25));//设置添加字体
		g.drawString("欢迎来到贪吃蛇小游戏！！！",300,50);
		
		if(!isfail) {//没结束才打印
			if(start)move(g);//打印小蛇和墙壁
			if(start) {//如果游戏开始了
				if(!isstart) setLocations();//游戏暂停就设置按钮位置，不然位置会被挤的错乱
				Data.food.paintIcon(this,g,food.x,food.y);//游戏开始了就需要一直打印食物
				g.setColor(Color.black);//设置画笔颜色
				g.setFont(new Font("楷体",Font.BOLD,25));//设置添加字体
				g.drawString("长度：" + point.size() + " 得分：" + score,625,50);
				g.drawString("速度："+ (300-seconds+10),50,50);
			}
			else {
				if(isstart) {//第一次按空格键
					start=true;//游戏已经开始
					reflush();//游戏开始后才刷新食物的地方
					Data.food.paintIcon(this,g,food.x,food.y);//打印食物
					g.setColor(Color.black);//设置画笔颜色
					g.setFont(new Font("楷体",Font.BOLD,25));//设置添加字体
					g.drawString("长度：" + point.size() + " 得分：" + score,625,50);
					g.drawString("速度："+ (300-seconds+10),50,50);
					hidestart();//隐藏主界面
				}else {//还未按空格键
					setLocations();//第一次进来设置所有按键的位置，一开始主界面的按钮就是显示的
				}
			}
		}
		else {//已经失败了
			if(isstart) {
				g.setColor(Color.red);//设置画笔颜色
				g.setFont(new Font("楷体",Font.BOLD,40));//设置添加字体
				g.drawString("你已死亡！最终长度：" + point.size() + " 最终得分：" + score,100,300);
				String s;
				if(mode == 1)s="简单";
				else if(mode == 2)s="普通";
				else s="困难";
				g.drawString("游戏难度："+s,300,350);
				g.drawString("按空格键保存记录，按ESC键回到主界面...",50,400);
			}else {
				setLocations();
			}
		}
	}
	public void reflush() {//刷新食物，坐标不能是蛇身体，不能出界，不能重复，不能在墙里面
		int x=0,y=0;
		while(true) {//符合规定才能出去
			boolean is = false;//判断是否是蛇身体或者是头的部分
			x = 25 + 25*random.nextInt(34);//数据都是经过计算好的，不会出界
			y = 75 + 25*random.nextInt(24);
			Iterator<Point> it = point.iterator();
			if(food.x == x && food.y == y)is=true;//不能和上次一样
			if(!is) {
				while(it.hasNext()) {//蛇身体
					Point body = it.next();
					if(body.x == x && body.y == y) {
						is=true;//证明有就不用在找了
						break;
					}
				}
			}
			if(!is) {
				Iterator<Point> its = Wpoint.iterator();//墙
				while(its.hasNext()) {
					Point body = its.next();
					if(body.x == x && body.y == y) {
						is=true;//在墙里面
						break;
					}
				}
			}
			if(!is)break;//都符合就退出循环
		}
		food.x=x;//更新新数据
		food.y=y;
	}
	public boolean ispeng(Point head) {//判断有没有撞到边界，撞到自己，撞到墙壁
		if(fx.compareTo("U")==0) { if(head.y<75)return true;}
		else if(fx.compareTo("D")==0) {if(head.y>650)return true;}
		else if(fx.compareTo("L")==0) {if(head.x<25)return true;}
		else if(head.x>850) {return true;}
		
		Iterator<Point> it = point.iterator();//判断撞到自己没有
		it.next();//去掉第一个，防第一次直接终止程序
		while(it.hasNext()) {
			Point body = it.next();
			if(body.x == head.x && body.y == head.y)return true;//撞到了
		}
		
		Iterator<Point> its = Wpoint.iterator();//判断撞到墙壁没有
		while(its.hasNext()) {
			Point body = its.next();
			if(body.x == head.x && body.y == head.y)return true;//撞到了
		}
		return false;//出来了说明没有撞到
	}
	public void move(Graphics g) {
		Iterator<Point> it = point.iterator();//打印小蛇
		Point head = it.next();
		if(fx.compareTo("U")==0)Data.up.paintIcon(this,g,head.x,head.y);
		else if(fx.compareTo("D")==0)Data.down.paintIcon(this,g,head.x,head.y);
		else if(fx.compareTo("L")==0)Data.left.paintIcon(this,g,head.x,head.y);
		else Data.right.paintIcon(this,g,head.x,head.y);//根据头部的方向打印头部
		while(it.hasNext()) {
			Point body = it.next();
			Data.body.paintIcon(this,g,body.x,body.y);
		}
		
		Iterator<Point> its = Wpoint.iterator();//打印墙壁
		while(its.hasNext()) {
			Point body = its.next();
			Data.wall.paintIcon(this,g,body.x,body.y);
		}
	}
	public void setLocations() {
		Data.cont.setLocation(400,180);//设置暂停时按钮位置
		Data.save.setLocation(400,280);
		Data.retu.setLocation(400,380);
		
		Data.start.setLocation(400,80);//设置按钮显示，只有刚刚进入游戏才需要
		Data.difficulty.setLocation(400,180);
		Data.file.setLocation(400,280);
		Data.rank.setLocation(400,380);
		Data.speak.setLocation(400,480);
		Data.exit.setLocation(400,580);
		
		Data.simple.setLocation(400,180);//设置困难程度按钮位置
		Data.common.setLocation(400,280);
		Data.difficult.setLocation(400,380);
		
		Data.fone.setLocation(400,180);//设置存档按钮位置
		Data.ftwo.setLocation(400,280);
		Data.fthree.setLocation(400,380);
		
		Data.instruction.setBounds(25,75,850,600);//说明书的位置
		
		Data.Rs.setBounds(50,75,225,600);//排行位置
		Data.Rc.setBounds(350,75,225,600);
		Data.Rd.setBounds(650,75,225,600);
		
		Data.lab.setBounds(200,300,300,50);//记录账户记录
		Data.text.setBounds(500,300,300,50);
	}
	public void showstart() {//展示第一组按钮
		//setLocations();
		Data.start.setVisible(true);
		Data.difficulty.setVisible(true);
		Data.file.setVisible(true);
		Data.rank.setVisible(true);
		Data.speak.setVisible(true);
		Data.exit.setVisible(true);
	}
	public void hidestart() {//隐藏第一组按钮
		Data.start.setVisible(false);
		Data.difficulty.setVisible(false);
		Data.file.setVisible(false);
		Data.rank.setVisible(false);
		Data.speak.setVisible(false);
		Data.exit.setVisible(false);
	}
	public void showsimple() {//展示第二组按钮
		//setLocations();
		Data.simple.setVisible(true);
		Data.common.setVisible(true);
		Data.difficult.setVisible(true);
	}
	public void hidesimple() {//隐藏第二组按钮
		Data.simple.setVisible(false);
		Data.common.setVisible(false);
		Data.difficult.setVisible(false);
	}
	public void showcont() {//展示第三组按钮
		//setLocations();
		Data.cont.setVisible(true);
		Data.save.setVisible(true);
		Data.retu.setVisible(true);
	}
	public void hidecont() {//隐藏第三组按钮
		Data.cont.setVisible(false);
		Data.save.setVisible(false);
		Data.retu.setVisible(false);
	}
	public void showfile() {//展示第四组按钮
		//setLocations();
		Data.fone.setVisible(true);
		Data.ftwo.setVisible(true);
		Data.fthree.setVisible(true);
	}
	public void hidefile() {//隐藏第四组按钮
		Data.fone.setVisible(false);
		Data.ftwo.setVisible(false);
		Data.fthree.setVisible(false);
	}
	public void Wall1() {//第一次的简单模式墙壁
		for(int i=25;i<25+25*3;i+=25)
			for(int j=75;j<=650;j+=25) 
				Wpoint.add(new Point(i,j)); 
		for(int i=850;i>850-25*3;i-=25)
			for(int j=75;j<=650;j+=25) 
				Wpoint.add(new Point(i,j)); 
	}
	public void Wall2() {//第二次的普通模式墙壁
		Wall1();
		for(int i=75;i<75+25*3;i+=25)
			for(int j=100;j<850;j+=25) 
				Wpoint.add(new Point(j,i));
		for(int i=650;i>650-25*3;i-=25)
			for(int j=100;j<850;j+=25) 
				Wpoint.add(new Point(j,i));
		for(int i=200;i<200+25*4;i+=25)
			for(int j=250;j<250+25*4;j+=25)
				Wpoint.add(new Point(i,j));
		for(int i=200;i<200+25*4;i+=25)
			for(int j=400;j<400+25*4;j+=25)
				Wpoint.add(new Point(i,j));
		for(int i=600;i<600+25*4;i+=25)
			for(int j=250;j<250+25*4;j+=25)
				Wpoint.add(new Point(i,j));
		for(int i=600;i<600+25*4;i+=25)
			for(int j=400;j<400+25*4;j+=25)
				Wpoint.add(new Point(i,j));
	}
	public void Wall3() {///第三次的困难模式墙壁
		Wall2();
		for(int i=175;i<175+25*2;i+=25)Wpoint.add(new Point(200,i));
		for(int i=175;i<175+25*2;i+=25)Wpoint.add(new Point(275,i));
		for(int i=175;i<175+25*2;i+=25)Wpoint.add(new Point(600,i));
		for(int i=175;i<175+25*2;i+=25)Wpoint.add(new Point(675,i));
		for(int i=525;i<525+25*2;i+=25)Wpoint.add(new Point(200,i));
		for(int i=525;i<525+25*2;i+=25)Wpoint.add(new Point(275,i));
		for(int i=525;i<525+25*2;i+=25)Wpoint.add(new Point(600,i));
		for(int i=525;i<525+25*2;i+=25)Wpoint.add(new Point(675,i));
		
		for(int i=125;i<125+25*2;i+=25)Wpoint.add(new Point(i,250));
		for(int i=125;i<125+25*2;i+=25)Wpoint.add(new Point(i,325));
		for(int i=125;i<125+25*2;i+=25)Wpoint.add(new Point(i,400));
		for(int i=125;i<125+25*2;i+=25)Wpoint.add(new Point(i,475));
		for(int i=725;i<725+25*2;i+=25)Wpoint.add(new Point(i,250));
		for(int i=725;i<725+25*2;i+=25)Wpoint.add(new Point(i,325));
		for(int i=725;i<725+25*2;i+=25)Wpoint.add(new Point(i,400));
		for(int i=725;i<725+25*2;i+=25)Wpoint.add(new Point(i,475));
		
		for(int i=325;i<350+25*9;i+=25)Wpoint.add(new Point(i,250));
		for(int i=325;i<350+25*9;i+=25)Wpoint.add(new Point(i,325));
		for(int i=325;i<350+25*9;i+=25)Wpoint.add(new Point(i,400));
		for(int i=325;i<350+25*9;i+=25)Wpoint.add(new Point(i,475));
	}
	public void read(int m) {
		File fdata = null;
		File fsnake = null;
		if(m==1) {
			fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data1.txt");
			fsnake = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\snake1.txt");
		}
		if(m==2) {
			fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data2.txt");
			fsnake = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\snake2.txt");
		}
		if(m==3) {
			fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data3.txt");
			fsnake = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\snake3.txt");
		}
		//读取的肯定是游戏进行中的数据
		start = true;
		isstart = true;
		isfail = false;
		point.clear();//原来的蛇清空
		Wpoint.clear();//清除墙壁
		try {//读取数据
			FileReader file = new FileReader(fdata);
			char ch = (char) file.read();//读取方向
			String s = new String(ch+"");
			fx = s;
			seconds = file.read();//读取速度
			score = file.read();//读取分数
			oldmode = mode;//保存开始的难度
			mode = file.read();//读取模式
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {//存储坐标
			FileInputStream file = new FileInputStream(fsnake);
			ObjectInputStream fileobj = new ObjectInputStream(file);
			try {
				food = (Point) fileobj.readObject();//读取食物
				point = (ArrayList<Point>) fileobj.readObject();//读取蛇身体
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//读取食物坐标
			file.close();
			fileobj.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timer.setDelay(seconds);//设置速度
		if(mode == 1) Wall1();//造墙
		if(mode == 2) Wall2();
		if(mode == 3) Wall3();
		repaint();//刷新
	}
	public void save(int m) {
		File fdata = null;
		File fsnake = null;
		if(m==1) {
			fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data1.txt");
			fsnake = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\snake1.txt");
		}
		if(m==2) {
			fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data2.txt");
			fsnake = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\snake2.txt");
		}
		if(m==3) {
			fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data3.txt");
			fsnake = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\snake3.txt");
		}
		try {//存储数据
			FileWriter file = new FileWriter(fdata);
			char ch = fx.charAt(0);
			file.write(ch);//存储方向
			file.write(seconds);//存储速度
			file.write(score);//存储分数
			file.write(mode);//存储模式
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {//存储坐标
			FileOutputStream file = new FileOutputStream(fsnake);
			ObjectOutputStream fileobj = new ObjectOutputStream(file);
			fileobj.writeObject(food);//存储食物坐标
			fileobj.writeObject(point);//存储蛇身体坐标
			file.close();
			fileobj.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void readFile() {//判断是否有存档
		File fdata = null;
		fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data1.txt");//数据区有东西就代表存档了
		if(fdata.length()>0)Data.fone.setText("存档一");
		
		fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data2.txt");
		if(fdata.length()>0)Data.ftwo.setText("存档二");
		
		fdata = new File("C:\\Users\\86173\\Desktop\\java程序\\贪吃蛇\\贪吃蛇代码和数据保存\\data3.txt");
		if(fdata.length()>0)Data.fthree.setText("存档三");
	}
	public void close() {
		con.close();
	}
}
