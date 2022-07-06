/*
 *  Name: Gregory Flynn
 *  Course: CNT 4714 Summer 2022
 *  Assignment title: Project 2 - A Two-tier Client-Server Application
 *  Date: July 4, 2022
 * 
 *  Class: DatabaseConnectClient.java
 */

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Properties;
import java.util.Vector;
import java.awt.Color;
import java.awt.Dimension;

// classTags: [FRONT END] [DRIVER] [CLIENT-SERVER]

// approximateTimeTakenToComplete: 100+ hours
//  history: github.com/AsThingsFallApart/Golden

// Create a 'DatabaseConnectClient' object that
//  1. graphically displays all pertinent information
//  2. provides buttons for executing actions
//  3. allows login as an arbitrary user
//  4. connects to a specific database via JDBC
//  5. issues mySQL-style commands
//  6. logs all client queries and updates
public class DatabaseConnectClient extends JFrame {

  /* class variables */
  private ResultSetTableModel tableModel;
  private JTable queryResultTable;
  private JTextArea queryArea;
  private JScrollPane tableScroller;

  private String propertiesFileName = null;
  private String databaseURL = null;
  private String databaseUser = null;
  private String databasePass = null;
  private boolean connectedToDatabase = false;
  private int numQueries;
  private int numUpdates;

  private MysqlDataSource dataSource;
  private Properties properties;
  private FileInputStream propertiesFileReader;
  private Connection userDatabaseLink;
  private Connection loggerDatabaseLink;
  private Statement statement;

  /* CONSTRUCTOR */
  public DatabaseConnectClient() {

    /* INITIALIZE 'JFrame' OBJECT */
    // evoke JFrame constructor and provide string.
    //  the constructor parameter string represents title of application.
    //  the JFrame object will be the "top-level component".
    //    that is to say, the JFrame object is the "root" in the "containment hierarchy".
    //      in english, all other components go onto the JFrame window.
    super("SQL Client App Version 1 - (GDF - CNT 4714 - Summer 2022 - Project 2)");
    
    // set dimensions of JFrame
    setSize( 1900, 1000 );

    /*
     * HANDLE LOGGING
     *   The client automatically makes a connection to the logging database
     *   as the root user.
     *   This connection is done with a special properties file ('logger.properties').
     *
     *   Keeping count of the number of queries and updates occurs
     *   whenever the user clicks the 'Execute SQL Command' button.
     *
     *   There is logic in the event handler that updates the database
     *   with incremented logging values everytime the button is clicked.
     *
     *   Commands that fail to execute are not logged.
     */
    
    propertiesFileName = "logger.properties";

    // read logger properties file.
    // make a connection to the logging database as root user.
    try {
      propertiesFileReader = new FileInputStream( propertiesFileName );
 
      properties = new Properties();
      properties.load( propertiesFileReader );
      
      dataSource = new MysqlDataSource();
      dataSource.setURL( properties.getProperty( "MYSQL_DB_URL" ) );
      dataSource.setUser( properties.getProperty( "MYSQL_DB_USERNAME" ) );
      dataSource.setPassword( properties.getProperty( "MYSQL_DB_PASSWORD" ) );

      loggerDatabaseLink = dataSource.getConnection();

      /*
       * if no exceptions are thrown by this point,
       * the logging connection is successful.
       */

      // reset logging database to 0 every time the client is started.
      //  'UPDATE' SQL commands are executed in Java with
      //  the '.executeUpdate(String update)' method from the 'Statement' class.

      // initialize the Statement (null -> something)
      statement = loggerDatabaseLink.createStatement();

      String resetNumQueries = "UPDATE operationscount set num_queries=0";
      String resetNumUpdate = "UPDATE operationscount set num_updates=0";

      // have the Statement object execute the update commands
      statement.executeUpdate( resetNumQueries );
      statement.executeUpdate( resetNumUpdate );

    }
    catch( FileNotFoundException fileNotFound ) {
      fileNotFound.printStackTrace();

      JOptionPane.showMessageDialog( null, "Logger properties file missing",
        "Logging disabled", JOptionPane.ERROR_MESSAGE );
    }
    catch( IOException ioException ) {
      ioException.printStackTrace();
    }
    catch( SQLException sqlException ) {
      sqlException.printStackTrace();
    }

    /* INITIALIZE GUI COMPONENTS */
    // try-catch structure necessary because
    //  1. 'Connector/J' depedency could be missing:
    //    handled in the 'ClassNotFoundException' catch block
    //  2. Database connection invalid:
    //    handled in the 'SQLException' catch block

    /* INSTANTIATE 'Color' OBJECTS */
    //  These Color objects are used to paint different components
    Color verylightGrey = new Color( 238, 238, 238 );
    Color limeGreen = new Color( 1, 255, 0 );

    // change background color of JFrame with methods
    // '.getContentPane()' from the 'JFrame' class and
    //    .getContentPane(): why?
    //      "In Java Swing, the layer that is used to hold objects is called the 'Content Pane'."
    //      The window that pops up when the program is run IS the content pane.
    //        Therefore, if modifying anything on this window, the content pane needs to be changed.
    // '.setBackground(Color)' from the 'Component' class. 
    //    JFrame methods can be called statically since this class
    //    EXTENDS the JFrame class.
    //      in other words, the JVM knows JFrame methods are in reference
    //      to 'DatabaseConnectClient' since this code is inside the class's constructor
    //      and the class IS A JFrame object.
    getContentPane().setBackground( verylightGrey );

    /* 
     * INSTANTIATE TOP-RIGHT OF JFRAME CONTENT PANE 
     *  The purpose of the top-right components is to input, clear, and execute
     *  SQL commands.
     *  includedComponenets
     *    1. 'clearSQLCommand'    (JButton) 
     *    2. 'executesSQLCommand' (JButton)
     *    3. 'queryAreaLabel'     (JLabel)
     *    4. 'queryArea'          (JTextArea)
     */

    /* INITIALIZE 'JButton' COMPONENTS */
    JButton clearSQLCommand = new JButton( "Clear SQL Command" );
    clearSQLCommand.setBackground( Color.WHITE );
    clearSQLCommand.setForeground( Color.RED );
    // clearSQLCommand.setAlignmentX( CENTER_ALIGNMENT );
    
    JButton executeSQLCommand = new JButton( "Execute SQL Command" );
    executeSQLCommand.setBackground( limeGreen );
    // executeSQLCommand.setAlignmentX( CENTER_ALIGNMENT );
    
    // Layout buttons left to right using a BoxLayout manager
    //  buttonBox organizes its components from left to right ("LINE_AXIS").
    Box buttonBox = new Box( BoxLayout.LINE_AXIS );
    
    // Components are organized in the order they are added:
    //  Adding 'clearSQLCommand' first ensures it is on the left.
    buttonBox.add( clearSQLCommand );
    buttonBox.add( Box.createRigidArea( new Dimension( 50, 0 ) ) );
    buttonBox.add( executeSQLCommand );
    // buttonBox.setAlignmentX( CENTER_ALIGNMENT );

    /* INSTANTIATE A 'JTextArea' OBJECT */
    //  the purpose of the JTextArea is to allow the user to pass MySQL queries to the client
    //  when connected to the database, the client then passes the query to the database (server).
    //    This is the "two-tier" behavior!
    queryArea = new JTextArea();

    // set text wrapping property.
    // passing 'true' as a parameter has text wrap at word boundary
    // passing 'false' has text wrap at character boundary
    queryArea.setWrapStyleWord( true );
    
    // set line wrapping property.
    // 'true' paramater has lines wrap if they exceed JTextArea width
    // 'false' paramter has lines never wrap
    // queryArea.setLineWrap( true );
    // queryArea.setAlignmentX( LEFT_ALIGNMENT );

    JLabel queryAreaLabel = new JLabel( "Enter An SQL Command" );
    queryAreaLabel.setForeground( Color.BLUE );
    // queryAreaLabel.setLabelFor( queryArea );
    // queryAreaLabel.setPreferredSize( new Dimension( 50, 50 ) );
    // queryAreaLabel.setAlignmentX( LEFT_ALIGNMENT );

    // align all SQL Command Area components in 'topRightBox'
    //  topRightBox componenets are aligned from top to bottom
    Box topRightBox = new Box( BoxLayout.PAGE_AXIS );
    topRightBox.add( queryAreaLabel );
    topRightBox.add( queryArea );
    topRightBox.add( buttonBox );

    /* INSTANTIATE TOP-LEFT OF JFRAME CONTENT PANE 
      *  The purpose of the top-left components is to log in to the database
      *  as any arbitrary user via a properties file.
      *  includedComponenets
      *    1. 'connectionDetailsLabel'     (JLabel) 
      *    2. 'propertiesFilePane'         (JTextPane)
      *    3. 'propertiesFileComboBox'     (JComboBox)
      *    4. 'usernamePane'               (JTextPane)
      *    5. 'usernameField'              (JTextField)
      *    6. 'passwordPane'               (JTextPane)
      *    7. 'passwordField'              (JPasswordField)
      *    8. 'connectToDatabaseButton'    (JButton)
      */
      
    JLabel connectionDetailsLabel = new JLabel( "Connection Details" );
    connectionDetailsLabel.setForeground( Color.BLUE );
    // connectionDetailsLabel.setAlignmentX( LEFT_ALIGNMENT );
    
    JTextPane propertiesFilePane = new JTextPane();
    propertiesFilePane.setText( "Properties File" );
    propertiesFilePane.setEditable( false );

    // hard-code 'String' objects to put into a vector
    String rootUserInfo = "root.properties";
    String clientUserInfo = "client.properties";

    // instantiate and populate a Vector of Strings
    //  'propertiesFileList' is passed to a JComboBox to provide options
    Vector propertiesFileList = new Vector();
    propertiesFileList.add( 0, rootUserInfo );
    propertiesFileList.add( 1, clientUserInfo );

    // System.out.println( propertiesFileList.size() );

    JComboBox propertiesFileComboBox = new JComboBox( propertiesFileList );

    // use a Box to align a pane with its respective input field
    Box propertiesFileBox = new Box( BoxLayout.LINE_AXIS );
    propertiesFileBox.add( propertiesFilePane );
    propertiesFileBox.add( propertiesFileComboBox );

    JTextPane usernamePane = new JTextPane();
    usernamePane.setText( "Username" );
    usernamePane.setEditable( false );

    JTextField usernameField = new JTextField();

    Box usernameBox = new Box( BoxLayout.LINE_AXIS );
    usernameBox.add( usernamePane );
    usernameBox.add( usernameField );

    JTextPane passwordPane = new JTextPane();
    passwordPane.setText( "Password" );
    passwordPane.setEditable( false );

    JPasswordField passwordField = new JPasswordField();

    Box passwordBox = new Box( BoxLayout.LINE_AXIS );
    passwordBox.add( passwordPane );
    passwordBox.add( passwordField );

    JButton connectToDatabaseButton = new JButton( "Connect to Database" );
    connectToDatabaseButton.setBackground( Color.BLUE );
    connectToDatabaseButton.setForeground( Color.YELLOW );
    // connectToDatabaseButton.setAlignmentX( LEFT_ALIGNMENT );

    Box topLeftBox = new Box( BoxLayout.PAGE_AXIS );
    topLeftBox.add( connectionDetailsLabel );
    topLeftBox.add( propertiesFileBox );
    topLeftBox.add( usernameBox );
    topLeftBox.add( passwordBox );
    topLeftBox.add( connectToDatabaseButton );

    /***** SPLIT JFRAME CONTENT INTO TWO CENTRAL BOXES *****/

    // 'northBox' is the top half of the JFrame content
    //  northBox organizes its content left to right
    Box northBox = new Box( BoxLayout.LINE_AXIS );

    northBox.add( topLeftBox );
    northBox.add( topRightBox );
    
    /* INSTANTIATE BOTTOM OF JFRAME CONTENT PANE
     * The purpose of the bottom components is to display query results.
     *  includedComponents:
     *    1. 'connectionStatusPane'         (JTextPane)
     *    2. 'SQLExecutionResultLabel'      (JLabel)
     *    3. 'queryResultTable'             (JScrollPane/JTable/ResultSetTableModel)
     *    4. 'clearResultButton'            (JButton)
     */
    
    JTextPane connectionStatusPane = new JTextPane();
    connectionStatusPane.setEditable( false );
    
    // change the text color of the JTextPane to red
    StyledDocument doc = connectionStatusPane.getStyledDocument();
    Style style = connectionStatusPane.addStyle( "red text", null );
    StyleConstants.setForeground( style, Color.RED );

    try {
      doc.insertString(doc.getLength(), "No Connection Now", style);
    }
    catch( BadLocationException e) {
      e.printStackTrace();
    }

    connectionStatusPane.setBackground( Color.BLACK );
    
    JLabel SQLExecutionResultLabel = new JLabel( "SQL Execution Result Window" );
    SQLExecutionResultLabel.setForeground( Color.BLUE );

    // create a place-holder area to display in JScrollPane before user enters their first query
    JTextPane placeholder = new JTextPane();
    placeholder.setEditable( false );

    /* INSTANTIATE A 'JScrollPane' OBJECT TO DISPLAY QUERY RESULTS */
    tableScroller = new JScrollPane( placeholder,
      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    tableScroller.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
    tableScroller.setPreferredSize( new Dimension( 500, 500 ) );

    JButton clearResultButton = new JButton( "Clear Result Window" );
    clearResultButton.setBackground( Color.YELLOW );
    
    // 'southBox' is the bottom half of the JFrame content
    //   southBox organizes its content top to bottom
    Box southBox = new Box( BoxLayout.PAGE_AXIS );
    southBox.add( connectionStatusPane );
    southBox.add( Box.createRigidArea( new Dimension(0, 30) ) );
    southBox.add( SQLExecutionResultLabel );
    southBox.add( tableScroller );
    southBox.add( clearResultButton );

    // add Box objects to JFrame.
    //  having the boxes managed by a BorderLayout manager allows them
    //  to take up as much space as the window is given.
    add( northBox, BorderLayout.NORTH );
    add( southBox, BorderLayout.SOUTH );

    /*
     * [EXECUTE]
     * ADD 'executeSQLCommand' BUTTON FUNCTIONALITY:
     *  1. Get text from JTextArea (ideally correct SQL syntax)
     *  2. use the '.setQuery' method from the 'ResultSetTableModel' class
     *     to execute query/update JTable
     */
    executeSQLCommand.addActionListener(
      new ActionListener() {

        // this ActionListener's 'actionPerformed' method
        // is defined as passing a query to the ResultSetTableModel.
        public void actionPerformed( ActionEvent buttonPressed ) {

          if( !connectedToDatabase ) {
            JOptionPane.showMessageDialog( null, "Not Connected to Database",
              "Database error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException( "Not Connected to Database" );
          }

          /*
           * BUSINESS LOGIC FOR LOGGING
           *  1. Determine SQL command type
           *  2. Increment numQueries or numUpdates
           *  3. Initialize a Statement object using "loggerDatabaseLink.createStatement()"
           *  4. Initialize String with SQL 'UPDATE' syntax and incremented logging value
           *  5. Update logging database using "statement.executeUpdate()"
           */

          // isolate first word in SQL command.
          int i = queryArea.getText().indexOf(' ');
          String SQLCommandType = (queryArea.getText().substring(0, i)).toUpperCase();

          System.out.println("SQL Command Type: " + SQLCommandType);

          switch(SQLCommandType) {
            case "SELECT":
              /* INSTANTIATE A 'JTable' OBJECT */
              try {
                System.out.println( "executeValidity:" + userDatabaseLink.isValid(1) );

                tableModel = new ResultSetTableModel( queryArea.getText(), userDatabaseLink );
                queryResultTable = new JTable( tableModel );
                queryResultTable.setGridColor( Color.BLACK );

                tableScroller.setViewportView( queryResultTable );
              }
              catch( ClassNotFoundException classNotFound) {
                JOptionPane.showMessageDialog( null, "MySQL driver not found",
                  "Driver not found", JOptionPane.ERROR_MESSAGE);

                // terminate application
                System.exit( 1 );
              }
              catch ( SQLException sqlException ) {
                JOptionPane.showMessageDialog( null, sqlException.getMessage(),
                  "Database error", JOptionPane.ERROR_MESSAGE);

                // ensure database connection is closed
                tableModel.disconnectFromDatabase();

                // terminate application
                System.exit( 1 );
              }

              // try to execute the user's query
              try {
                tableModel.setQuery( queryArea.getText() );

                // if code gets to here, query was successful.
                // log successful query.
                try {
                  statement = loggerDatabaseLink.createStatement();
  
                  numQueries++;
  
                  String incrementNumQueries = "UPDATE operationscount set num_queries=" + numQueries;
  
                  statement.executeUpdate( incrementNumQueries );
                }
                catch( SQLException sqlException ){
                  sqlException.printStackTrace();
  
                  JOptionPane.showMessageDialog(null, sqlException.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);                
                }
              }
              catch( SQLException sqlException ) {
                JOptionPane.showMessageDialog(null, sqlException.getMessage(),
                  "Database error", JOptionPane.ERROR_MESSAGE);
              }
              break;

            case "UPDATE":
              try {
                statement = userDatabaseLink.createStatement();

                statement.executeUpdate( queryArea.getText() );

                // if code is here, update is successful.
                // log successful update.
                try {
                  statement = loggerDatabaseLink.createStatement();
  
                  numUpdates++;
  
                  String incrementNumUpdates = "UPDATE operationscount set num_updates=" + numUpdates;
  
                  statement.executeUpdate( incrementNumUpdates );
                }
                catch( SQLException sqlException ){
                  sqlException.printStackTrace();
  
                  JOptionPane.showMessageDialog(null, sqlException.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);                
                }
              }
              catch( SQLException sqlException ) {
                sqlException.printStackTrace();

                JOptionPane.showMessageDialog(null, sqlException.getMessage(),
                  "Database error", JOptionPane.ERROR_MESSAGE);        
              }
              break;

            case "INSERT":
              try {
                statement = userDatabaseLink.createStatement();

                statement.executeUpdate( queryArea.getText() );
              }
              catch( SQLException sqlException ) {
                sqlException.printStackTrace();

                JOptionPane.showMessageDialog(null, sqlException.getMessage(),
                  "Database error", JOptionPane.ERROR_MESSAGE);        
              }
              break;

            case "DELETE":
              try {
                statement = userDatabaseLink.createStatement();

                statement.executeUpdate( queryArea.getText() );
              }
              catch( SQLException sqlException ) {
                sqlException.printStackTrace();

                JOptionPane.showMessageDialog(null, sqlException.getMessage(),
                  "Database error", JOptionPane.ERROR_MESSAGE);        
              }
              break;
          }

        }
      }
    );

    // add button functionality for 'clearSQLCommand'
    clearSQLCommand.addActionListener(
      new ActionListener() {

        // the 'clearSQLCommand' button clears all text
        // in the query area.
        public void actionPerformed( ActionEvent clearCommand ) {
          queryArea.setText( "" );
        }

      }
    );

    // add button functionality for 'clearResultButton'
    clearResultButton.addActionListener(
      new ActionListener() {

        // the purpose of 'clearResultButton' is to clear JTable.
        // Do this by replacing the JTable in the JScrollPane viewport
        // with a white, uneditable pane.
        public void actionPerformed( ActionEvent clearTable ) {
          tableScroller.setViewportView( placeholder );
        }
      }
    );

    /*
     * [CONNECT]
     * HANDLE LOGINS
     *  1. when the user selects an option from the JComboBox the following occurs:
     *    1a. reads the .properties file for 'MYSQL_DB_USERNAME' key
     *    1b. changes the text in 'usernameField' to the .properties corresponding value
     *    1c. reads the .properties file for 'MYSQL_DB_PASSWORD' key
     *    1d. changes the text in the 'passwordField' to the .properties correspondng value
     *    1e. reads the .properties file for 'MYSQL_DB_URL' key
     *    1f. stores the URL in 'databaseURL' internal variable
     *  2. when the user presses the 'connectToDatabaseButton' object the following occurs:
     *    2a. check if the URL is valid; if not, show an error dialog and cease
     *    2b. update the text in the 'connectionStatusPane' to display string in 'databaseURL'
     *    2c. run the '.establishConnection()' method; pass properties file name
     */

    // load properties from a properties file after selection
    propertiesFileComboBox.addActionListener(
      new ActionListener() {

        // the propertiesFileComboBox auto-populates the username and password fields on selection.
        public void actionPerformed( ActionEvent choose ) {

          properties = new Properties();
          propertiesFileName = propertiesFileComboBox.getSelectedItem().toString();
          System.out.println( "propFileName: " + propertiesFileName );

          try {
            propertiesFileReader = new FileInputStream( propertiesFileName );
            properties.load( propertiesFileReader );

            // record all values from properties file by referencing their corresponding keys.
            databaseURL = properties.getProperty( "MYSQL_DB_URL" );
            databaseUser = properties.getProperty( "MYSQL_DB_USERNAME" );
            databasePass = properties.getProperty( "MYSQL_DB_PASSWORD" );
          }
          catch( IOException e ) {
            e.printStackTrace();
          }
        }
      }
    );

    // CONNECT BUTTON 
    connectToDatabaseButton.addActionListener(
      new ActionListener() {

        boolean correctUsername = false;
        boolean correctPassword = false;

        public void actionPerformed( ActionEvent connect ) {
          // check if the the user/pass entered into the client MATCHES the properties file.
          if( usernameField.getText().equals( databaseUser ) ) {
            correctUsername = true;
          }

          String convertedPass = new String( passwordField.getPassword() );
          if( convertedPass.equals( databasePass )) {
            correctPassword = true;
          }

          if( correctUsername && correctPassword ) {
            // only connect if all required login information exists (not null).

            try {
              // make a 'Connection' object 'userDatabaseLink' to be used with execute SQL command button
              properties = new Properties();

              try {
                System.out.println( "propFileName: " + propertiesFileName );
                propertiesFileReader = new FileInputStream( propertiesFileName );
                properties.load( propertiesFileReader );

                dataSource = new MysqlDataSource();
                dataSource.setURL( properties.getProperty( "MYSQL_DB_URL" ) );
                dataSource.setUser( properties.getProperty( "MYSQL_DB_USERNAME" ) );
                dataSource.setPassword( properties.getProperty( "MYSQL_DB_PASSWORD" ) );

                // get connection to database
                userDatabaseLink = dataSource.getConnection();
                System.out.println( "validity: " + userDatabaseLink.isValid(10) );
              }
              catch( FileNotFoundException e) {
                e.printStackTrace();
              }
              catch( IOException ioException ) {
                ioException.printStackTrace();
              }

              // change the text color of the JTextPane to red
              StyledDocument doc = connectionStatusPane.getStyledDocument();
              Style style = connectionStatusPane.addStyle( "red text", null );
              StyleConstants.setForeground( style, Color.RED );
              String connectionStatusString = "Connected to " + databaseURL;

              try {
                doc.remove( 0, doc.getLength() );
                doc.insertString( doc.getLength(), connectionStatusString, style );
              }
              catch( BadLocationException e) {
                e.printStackTrace();
              }

              connectedToDatabase = true;
              System.out.println("connctedToDatabase: " + connectedToDatabase );
            }
            catch( NullPointerException nullPointer ) {
              nullPointer.printStackTrace();
            }
            catch( SQLException sqlException ) {
              JOptionPane.showMessageDialog( null, sqlException.getMessage(), 
                "Database error", JOptionPane.ERROR_MESSAGE );
               
              // ensure database connection is closed
              tableModel.disconnectFromDatabase();
              
              System.exit( 1 );   // terminate application
            }
          }
          else {
            JOptionPane.showMessageDialog( null, "Unknown login",
              "Login failed", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    );

    // have the JFrame object create its own process
    setVisible(true);

    // DISPOSE of JFrame when user clicks exits out of process
    // this is alternative to the default behavior of HIDING on close
    setDefaultCloseOperation( DISPOSE_ON_CLOSE );

    // TODO: investigate 'NullPointerException' generated from this event
    // ensure database connection is closed when user quits application
    //  implementation: in-line function
    addWindowListener( new WindowAdapter() {

      // disconnect from database and exit when window has closed
      public void windowClosed( WindowEvent event ) {
        tableModel.disconnectFromDatabase();

        System.exit( 0 );
      }

    });

  }
  
  public static void main(String args[]) {
    
    // create a 'DatabaseConnectClient' object which contains all graphical components.
    new DatabaseConnectClient();

    // System.out.println("Test");
  }
}
