package snack;

import java.sql.*;
public class Myconnection {//利用数据库连接实现数据存储和排名
	static String Driver="com.mysql.cj.jdbc.Driver";//引擎
	static String Url="jdbc:mysql://localhost:3306/test?useSSL=true&serverTimezone=UTC&characterEncoding=UTF-8";//数据库连接
	static String Root="root";
	static String Password="121022gui";
	static Connection con = null;
	public Myconnection() {//创建类即是创建数据库链接
		try {
			Class.forName(Driver);
			con = DriverManager.getConnection(Url,Root,Password);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close() {//关闭链接
		try {
			if(con!=null)con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void rank() {//排行
		String sql = "select * from simple order by score desc";
		Statement st = null;
		try {
			st = con.createStatement();
			ResultSet set = st.executeQuery(sql);//查询简单模式
			int i=1;//最多只读取二十个，也就是最多输出前二十
			String s="       简单模式\n 排名   得分   用户名\n";
			while(set.next()&&i<=20) {
				s=s+"  "+i;
				if(i<10)s=s+"     ";
				else s=s+"    ";
				s=s+set.getString(2);
				if(set.getString(2).length()==2)s=s+"    ";
				else if(set.getString(2).length()==3)s=s+"   ";
				else if(set.getString(2).length()==4)s=s+"  ";
				else s=s+" ";
				s=s+set.getString(1)+"\n";//存入得分和用户
				i++;
			}
			Data.Rs.setText(s);
			
			sql = "select * from common order by score desc";
			set = st.executeQuery(sql);//查询普通模式
			i=1;//最多只读取二十个，也就是最多输出前二十
			s="       普通模式\n 排名   得分   用户名\n";
			while(set.next()&&i<=20) {
				s=s+"  "+i;
				if(i<10)s=s+"     ";
				else s=s+"    ";
				s=s+set.getString(2);
				if(set.getString(2).length()==2)s=s+"    ";
				else if(set.getString(2).length()==3)s=s+"   ";
				else if(set.getString(2).length()==4)s=s+"  ";
				else s=s+" ";
				s=s+set.getString(1)+"\n";//存入得分和用户
				i++;
			}
			Data.Rc.setText(s);
			
			sql = "select * from difficult order by score desc";
			set = st.executeQuery(sql);//查询困难模式
			i=1;//最多只读取二十个，也就是最多输出前二十
			s="       困难模式\n 排名   得分   用户名\n";
			while(set.next()&&i<=20) {
				s=s+"  "+i;
				if(i<10)s=s+"     ";
				else s=s+"    ";
				s=s+set.getString(2);
				if(set.getString(2).length()==2)s=s+"    ";
				else if(set.getString(2).length()==3)s=s+"   ";
				else if(set.getString(2).length()==4)s=s+"  ";
				else s=s+" ";
				s=s+set.getString(1)+"\n";//存入得分和用户
				i++;
			}
			Data.Rd.setText(s);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(st!=null)
				try {
					st.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	public void submit(int mode,String account,int score) {//存储数据
		String m;
		if(mode == 1)m="simple";
		else if(mode == 2)m="common";
		else m="difficult";
		account=account.replace(" ","");//去掉空格
		if(account.compareTo("")!=0) {//不能全为空格
			if(account.length()>6)account = account.substring(0,6);//只能存六个字符以内
			String sql = "insert into " + m + "(account,score) values('" + account + "'," + score +")";
			Statement st = null;
			try {
				st=con.createStatement();
				st.executeUpdate(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
					try {
						if(st!=null)st.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		
	}
}
