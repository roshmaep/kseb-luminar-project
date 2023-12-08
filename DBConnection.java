package com.kseb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	
	final String driver="com.mysql.cj.jdbc.Driver";
	final String url="jdbc:mysql://localhost:3306/ksebdb";
	final String user="root";
	final String password="mercury@2023";
	Connection con=null;
	
	
	public Connection getConnection(){
		
		try{
			
			Class.forName(driver);
			con=DriverManager.getConnection(url,user,password);
			
			
		}catch(ClassNotFoundException cfne){
			
			cfne.printStackTrace();
		}catch(SQLException sql){
			
			sql.printStackTrace();
		}
		return con;
	}
}
