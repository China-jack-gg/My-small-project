package snack;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
/**
 * 制作一个圆形按钮时，需要做两件事：
 * 第一件事是重载一个适合的绘图方法以画出一个图形
 * 第二件事是设置一些事件使得只有当单击图形按钮的范围中的时候才会做出响应*/
public class Mybutton extends JButton {//制作需要的图形按钮
	public Mybutton(String label) {
		super(label);
		// 这些声明把按钮扩展为一个圆而不是一个椭圆。
		Dimension size = getPreferredSize();//获取按钮的最佳大小
		size.width = size.height = 90;//调整按钮的大小,使之变成一个方形
		setPreferredSize(size);
		setBackground(Color.green);//设置背景颜色为绿色
		
		// 这个调用使JButton不画背景，而允许我们画一个圆的背景。
		setContentAreaFilled(false);
	}
	// 画圆的背景和标签
	protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
			  //getModel方法返回鼠标的模型ButtonModel
			 //如果鼠标按下按钮，则buttonModel的armed属性为真
			// 按下后的颜色
			g.setColor(Color.red);
		} else {
			g.setColor(getBackground());
		}
		g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
		// 这个调用会画一个标签和焦点矩形。
		super.paintComponent(g);
	}

	// 用简单的弧画按钮的边界。
	protected void paintBorder(Graphics g) {
		g.setColor(getForeground());
		g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
	}

	// 侦测点击事件
	Shape shape;

	public boolean contains(int x, int y) {
		// 如果按钮改变大小，产生一个新的形状对象。
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1);
		}
		return shape.contains(x, y);
	}
}