/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rohit
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class DatabaseUtility 

{
    
     private final  String MYSQL_URL  ;
     final  String DB_URL;
     private Connection sqlConnection,dbConnection;
     private Statement  statement;
     private final String dbCreateSQL;
     private final String USER_NAME;
     private final String PASSWORD;
     private final String TABLE_USER_DETAILS;
     private final String dbDeleteTable;
     private DatabaseMetaData dbmd;
    
     public DatabaseUtility ()

        {
        
            
            MYSQL_URL = "jdbc:mysql://localhost:3306";
            DB_URL = MYSQL_URL +"/FoodDetails";
            //initialise MySql usename and password 
            USER_NAME ="root";
            PASSWORD = "Summer2017";
            statement = null;
            //sql query to create database.
            dbCreateSQL = "CREATE DATABASE FoodDetails";
            dbDeleteTable = "DROP TABLE USERDETAILS";
             //sql queries to create Tables
            TABLE_USER_DETAILS = "CREATE TABLE USERDETAILS"+
                                                                           "(UserId INTEGER not NULL AUTO_INCREMENT," +
                                                                              "Username VARCHAR(30)," +
                                                                              "password VARCHAR(30),"  +                                                
                                                                              "PRIMARY KEY (UserId) )";
        }
     
     public boolean DBtables() throws SQLException
             
        {

                boolean dbExists = false,tblUserDetails = false;
                        
           String databaseName ="";
           //Register MySql database driver
          try {
                  Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                    System.out.println("Where is your MySQL JDBC Driver?");
                    e.printStackTrace();
                    return false;
            }
            //System.out.println("MySQL JDBC Driver Registered!"); 
            //connect to MySql ;
          try {
                 sqlConnection = DriverManager.getConnection(MYSQL_URL, USER_NAME, PASSWORD);
                 statement = sqlConnection.createStatement();
                 //System.out.println("SQL connected \n");
          } catch (SQLException e) {
                    System.out.println("Connection Failed! Check output console");
                    e.printStackTrace();
                    return false;
            }
          //chack whether the databse exists.
            {
                 //get the list of databases
                 ResultSet dbData = sqlConnection.getMetaData().getCatalogs();

                 //iterate each catalog in the ResultSet 
                 while (dbData.next()) {
                 // Get the database name, which is at position 1
                          databaseName = dbData.getString(1);  
                          if (databaseName.equalsIgnoreCase("fooddetails"));
                             dbExists = true;
                 }
                 if (! dbExists)  //if database doesn't exist create database executing the query.
                 {
                    statement.executeUpdate(dbCreateSQL);
                 }
                 if (sqlConnection != null)
                    sqlConnection.close();  //close the existing connection to connect to MySql
                 //connect to  database
                 dbConnection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
                 
                 statement = dbConnection.createStatement();
                 
                 dbmd= dbConnection.getMetaData();
                  // loop through the list of tables if the tables are already created
                 ResultSet rs = dbmd.getTables(null, null, "%", null);
                 while (rs.next()) 
                 
                 {
                     
                         if((rs.getString(3).equalsIgnoreCase("userdetails")))
                             
          
                           tblUserDetails = true;
                 }
                 
                 //if any of the tables doesn't exist create table executing the query
                 if (!tblUserDetails){
                    statement.executeUpdate(TABLE_USER_DETAILS);                                   
                 }
                    return true;                  
           } 
         
        }
        //insert values into the Authors tables using user entry
        public void insertUser(String userName, String password) 
        {
          PreparedStatement addUser;  
          try{
               if (dbConnection  == null)
                  dbConnection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD); 
                  addUser  = dbConnection.prepareStatement("INSERT INTO USERDETAILS " +
                                                                   "(Username, password)" +
                                                                    "VALUES (?, ?)");
                  addUser.setString(1, userName);
                  addUser.setString(2, password);
                  addUser.executeUpdate();
                                                   
                  
            }
            catch(SQLException e) {
                    System.out.println("Connection Failed! Check output console");
                                       System.out.println("SQLException: " + e.getMessage());
                                       System.out.println("SQLState: " + e.getSQLState());
                    e.printStackTrace();
                    return;
	}
    }
        
        public int getUserID(String Username) throws SQLException
          
           
            {   
                int userID = 0;
                final String SEARCH_USERID_QRY = "SELECT UserID FROM USERDETAILS"
                                                                     + " WHERE Username = ?";
                
                try
                    
                    {
                        PreparedStatement getUserID;
                
                        if (dbConnection  == null)
                    dbConnection = DriverManager.getConnection (DB_URL, USER_NAME, PASSWORD); 
                    Statement selectionstate = dbConnection.createStatement();
                    getUserID   = dbConnection.prepareStatement( SEARCH_USERID_QRY);
                    getUserID.setString(1, Username);
                    ResultSet dbData = getUserID.executeQuery();
                    ResultSetMetaData metaData  = dbData.getMetaData();
                    int numberOfColumns = metaData.getColumnCount();
                    //columns start from 1 , not from 0;
                    //display table header
                    
                    System.out.println();
                    //display table content
                    while(dbData.next())
                    {
                        for (int i =1; i <= numberOfColumns; i++) 
                        {
                        userID = Integer.valueOf(dbData.getObject(i).toString());
                        }
                    }
                           }
                
                 catch (SQLException e) {
		System.out.println("Connection Failed! Check output console");
                                   System.out.println("SQLException: " + e.getMessage());
                                   System.out.println("SQLState: " + e.getSQLState());
		e.printStackTrace();
		
        }
                
        return userID;

            
        }
        public String getPassword(String UserID) throws SQLException
          
           
            {
                String password = null;
                final String SEARCH_PASSWORD_QRY = "SELECT password FROM USERDETAILS"
                                                                     + " WHERE UserID= ?";
          
                try
                    
                    {
                        PreparedStatement getUserID;
                
                        if (dbConnection  == null)
                    dbConnection = DriverManager.getConnection (DB_URL, USER_NAME, PASSWORD); 
                    Statement selectionstate = dbConnection.createStatement();
                    getUserID   = dbConnection.prepareStatement( SEARCH_PASSWORD_QRY);
                    getUserID.setString(1, UserID);
                    ResultSet dbData = getUserID.executeQuery();
                    ResultSetMetaData metaData  = dbData.getMetaData();
                    int numberOfColumns = metaData.getColumnCount();
                    //display table content
                    while(dbData.next())
                    {
                        for (int i =1; i <= numberOfColumns; i++) 
                            password =  dbData.getObject(i).toString();
                    }
                           }
                
                 catch (SQLException e) {
		System.out.println("Connection Failed! Check output console");
                                   System.out.println("SQLException: " + e.getMessage());
                                   System.out.println("SQLState: " + e.getSQLState());
		e.printStackTrace();
		
        }
        
                return password;
}
        public String getUsername(String UserID) throws SQLException
          
           
            {
                String Username = null;
              
                final String SEARCH_USERNAME_QRY = "SELECT Username FROM USERDETAILS"
                                                                     + " WHERE UserID= ?";
                
                try
                    
                    {
                        PreparedStatement getUserID;
                
                        if (dbConnection  == null)
                    dbConnection = DriverManager.getConnection (DB_URL, USER_NAME, PASSWORD); 
                    Statement selectionstate = dbConnection.createStatement();
                    getUserID   = dbConnection.prepareStatement( SEARCH_USERNAME_QRY);
                    getUserID.setString(1, UserID);
                    ResultSet dbData = getUserID.executeQuery();
                    ResultSetMetaData metaData  = dbData.getMetaData();
                    int numberOfColumns = metaData.getColumnCount();
                    //display table content
                    while(dbData.next())
                    {
                        for (int i =1; i <= numberOfColumns; i++) 
                            //System.out.printf("%-8s\t",dbData.getObject(i));
                            Username =  dbData.getObject(i).toString();
                    }
                           }
                
                 catch (SQLException e) {
		System.out.println("Connection Failed! Check output console");
                                   System.out.println("SQLException: " + e.getMessage());
                                   System.out.println("SQLState: " + e.getSQLState());
		e.printStackTrace();
		
        }
        
                return Username;
}
        
        public void deleteTable() throws SQLException
        {
          statement.executeUpdate(dbDeleteTable); 
        }
        
}
     
    

