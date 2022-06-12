// Very basic JDBC example showing loading of JDBC driver, establishing connection
// creating a statement, executing a simple SQL query, and displaying the results.
// This example uses a properties file to hold connection information.
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import com.mysql.cj.jdbc.MysqlDataSource;

public class SimpleJDBC_PropertiesFile {
  public static void main(String[] args)
    throws SQLException, ClassNotFoundException {
	//using a properties file  
    Properties properties = new Properties();
    FileInputStream filein = null;
    MysqlDataSource dataSource = null;
    //read a properties file
    try {
    	filein = new FileInputStream("db.properties");
    	properties.load(filein);
    	dataSource = new MysqlDataSource();
    	dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
    	dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
    	dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));	 	
    } catch (IOException e) {
    	e.printStackTrace();
    }
    System.out.println("Output from SimpleJDBC_PropertiesFile:   Using a properties file to hold connection details.");
	java.util.Date date = new java.util.Date();
	System.out.println(date);  System.out.println(); 
	
	
    //establish a connection to the dataSource - the database
	Connection connection = dataSource.getConnection();
    System.out.println("Database connected");
    DatabaseMetaData dbMetaData = connection.getMetaData();
	System.out.println("JDBC Driver name " + dbMetaData.getDriverName() );
	System.out.println("JDBC Driver version " + dbMetaData.getDriverVersion());
	System.out.println("Driver Major version " +dbMetaData.getDriverMajorVersion());
	System.out.println("Driver Minor version " +dbMetaData.getDriverMinorVersion() );
	System.out.println();

    // Create a statement
	Statement statement = connection.createStatement();
	
    // Execute a statement
    ResultSet resultSet = statement.executeQuery ("select bikename,cost,mileage from bikes");

    // Iterate through the result set and print the returned results
    System.out.println("Results of the Query: . . . . . . . . . . . . . . . . . . . . . . . . . . . . .\n");
    while (resultSet.next())
      System.out.println(resultSet.getString("bikename") + "         \t" +
        resultSet.getString("cost") + "         \t" + resultSet.getString("mileage"));
		//the following print statement works exactly the same  
      //System.out.println(resultSet.getString(1) + "         \t" +
      //  resultSet.getString(2) + "         \t" + resultSet.getString(3));
    System.out.println();  System.out.println();
    // Close the connection
    connection.close();
  }
}
