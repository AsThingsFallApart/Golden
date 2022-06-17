/*
 *  Name: Gregory Flynn
 *  Course: CNT 4714 Summer 2022
 *  Assignment title: Project 2 - A Two-tier Client-Server Application
 *  Date: June 26, 2022
 * 
 *  Class: SimpleJDBC_PropertiesFile.java
 */

// Very basic JDBC example showing:
//  1. loading of JDBC driver
//  2. establishing connection
//  3. creating a statement
//  4. executing a simple SQL query
//  5. and displaying the results.

// [NEW STYLE]: This example uses a properties file to hold connection information.
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import com.mysql.cj.jdbc.MysqlDataSource;

public class SimpleJDBC_PropertiesFile {

  public static void main(String[] args) throws SQLException, ClassNotFoundException {

	  // instantiate a 'Properties' instance
    Properties properties = new Properties();

    // instantiate a 'FileInputStream' instance
    FileInputStream filein = null;

    // instantiate a 'MysqlDataSource' instance
    MysqlDataSource dataSource = null;

    /*read a properties file */
    try {
      // invoke class 'FileInputStream''s constructor with the 'new' keyword.
      // FileInputStream's constructor will be able to access the properties file since
      // the properties file is in the project directory (./) and
      // the FileInputStream instance is passed the name of the file.
      // the new FileInputStream instance is assigned to the variable 'filein' already created.

      // instantiation and initialization can not happen in the same line since
      // FileInputStream could be given a null reference, a FileNotFoundException would be thrown, and
      // the rest of the program would fail to execute.
      // by placing initialization in a try/catch block, it is very clear what the error is if an error occurs.
      // additionally, the rest of the program gets to continue.
    	filein = new FileInputStream("db.properties");
      
      // origin: the '.load(FileInputStream obj)' method is a member of the 'java.util.Properties' class
      // typing: .load(InputStream obj) takes in an 'InputStream' object as a parameter
      // (FileInputStream is not an 'InputStream' object,
      // but FileInputStream EXTENDS InputStream so - by inheritance - FileInputStream IS A InputStream
      // purpose: .load(InputStream obj) reads bytes from the file scanned by the 'FileInputStream' object.
    	properties.load(filein);

      // create new 'MysqlDataSource' instance
      // assign new instance to variable 'dataSource' which is type 'MysqlDataSource'.
      // the MysqlDataSource has to be initialized here because
      // it loads data from the properties file
      // and the path to the properties file is unknown until runtime
    	dataSource = new MysqlDataSource();

      // use methods on the MysqlDataSource instance to speficically reference keys from the properties file
      // and load their corresponding values.
      // additionally, the methods assign the corresponding values to internal,
      // private variables
    	dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
    	dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
    	dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));	 	
    }
    catch (IOException e) {
    	e.printStackTrace();
    }

    /* if all has gone right by this point, the variable 'dataSource'
     * has all the correct credentials and references to locate and log in to
     * the database.
     */

    System.out.println("Output from SimpleJDBC_PropertiesFile: Using a properties file to hold connection details.");
	  java.util.Date date = new java.util.Date();
	  System.out.println(date);
    System.out.println(); 
	
    //establish a connection to the dataSource - the database
    // NEW: 'Connection' instance
    // a Connection object uses a MysqlDataSource object to enter the database.
    // the Connection object is used by 'Statement' objects to
    // pass queries to the database.
	  Connection connection = dataSource.getConnection();
    System.out.println("Database connected");
    DatabaseMetaData dbMetaData = connection.getMetaData();
	  System.out.println("JDBC Driver name " + dbMetaData.getDriverName() );
	  System.out.println("JDBC Driver version " + dbMetaData.getDriverVersion());
	  System.out.println("Driver Major version " + dbMetaData.getDriverMajorVersion());
	  System.out.println("Driver Minor version " + dbMetaData.getDriverMinorVersion() );
	  System.out.println();

    // Create a statement
    // statements are objects in Java that act as
    // queries passed via command line, for example.
	  Statement statement = connection.createStatement();
	
    // Execute a statement
    // the mySQL query syntax is passed as a parameter to the
    // .executeQuery(String sql) method run on a Statement instance.
    // the .executeQuery(String sql) method returns a 'ResultSet' object.
    // thus, the method's return value is assigned to a variable typed 'ResultSet'.
    ResultSet resultSet = statement.executeQuery("select bikename,cost,mileage from bikes");

    // Iterate through the result set and print the returned results
    System.out.println("Results of the Query: . . . . . . . . . . . . . . . . . . . . . . . . . . . . .\n");
    while (resultSet.next())
      System.out.println(resultSet.getString("bikename") + "         \t" +
        resultSet.getString("cost") + "         \t" + resultSet.getString("mileage"));

		//the following print statement works exactly the same  
      //System.out.println(resultSet.getString(1) + "   connection      \t" +
      //  resultSet.getString(2) + "         \t" + resultSet.getString(3));
    System.out.println();
    System.out.println();

    /* notice HOW the values generated from the query are printed:
     * a 'ResultSet' object can be thought of as a 2D array.
     * Each row is a set of values.
     * The values also relate to a set of columns.
     * How many columns depends on the syntax of the query.
     * The ResultSet object is capable of referencing the values in rows
     * by simply passing the name of the column the value belongs to.
     * Alternatively, the column's number (starting at 1) and auto-incrementing
     * with columns following the same order of the query can be used.
     */

    // Close the connection
    // in other words, log out of the databaseS
    connection.close();
  }
}
