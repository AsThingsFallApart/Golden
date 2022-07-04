/*
 *  Name: Gregory Flynn
 *  Course: CNT 4714 Summer 2022
 *  Assignment title: Project 2 - A Two-tier Client-Server Application
 *  Date: July 4, 2022
 * 
 *  Class: DatabaseConnectClient.java
 */

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
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

// classTags: [FRONT END] [DRIVER] [CLIENT-SERVER]

// Create a 'DatabaseConnectClient' object that
//  1. graphically displays all pertinent information
//  2. provides buttons for executing actions
//  3. allows login as an arbitrary user
//  4. connects to a specific database via JDBC
//  5. issues mySQL-style commands
//  6. logs all client queries and updates
public class DatabaseConnectClient extends JFrame {

  /* class variables */
  static final String DEFAULT_QUERY = "SELECT * FROM racewinners";
  private ResultSetTableModel tableModel;
  private JTable queryResultTable;
  private JTextArea queryArea;
  private String databaseURL;
  private String databaseUser;
  private String databasePass;
  
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
    setSize( 2400, 1200 );

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
    
    JButton executeSQLCommand = new JButton( "Execute SQL Command" );
    executeSQLCommand.setBackground( limeGreen );
    
    // Layout buttons left to right using a BoxLayout manager
    //  buttonBox organizes its components from left to right ("LINE_AXIS").
    Box buttonBox = new Box( BoxLayout.LINE_AXIS );
    
    // Components are organized in the order they are added:
    //  Adding 'clearSQLCommand' first ensures it is on the left.
    buttonBox.add( clearSQLCommand );
    buttonBox.add( executeSQLCommand );

    /* INSTANTIATE A 'JTextArea' OBJECT */
    //  the purpose of the JTextArea is to allow the user to pass MySQL queries to the client
    //  when connected to the database, the client then passes the query to the database (server).
    //    This is the "two-tier" behavior!
    queryArea = new JTextArea( DEFAULT_QUERY );

    // set text wrapping property.
    // passing 'true' as a parameter has text wrap at word boundary
    // passing 'false' has text wrap at character boundary
    queryArea.setWrapStyleWord( true );
    
    // set line wrapping property.
    // 'true' paramater has lines wrap if they exceed JTextArea width
    // 'false' paramter has lines never wrap
    // queryArea.setLineWrap( true );

    JLabel queryAreaLabel = new JLabel( "Enter An SQL Command" );

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
    connectToDatabaseButton.setBackground(Color.BLUE);
    connectToDatabaseButton.setForeground(Color.YELLOW);

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
    connectionStatusPane.setText( "No Connection Now" );
    connectionStatusPane.setEditable( false );
    
    JLabel SQLExecutionResultLabel = new JLabel( "SQL Execution Result Window" );

    /* INSTANTIATE A 'JTable' OBJECT */
    //  Must be done in a try-catch block since the 'ResultSetTableModel' class
    //  specifies some errors that might be thrown.
    try {
      tableModel = new ResultSetTableModel( DEFAULT_QUERY );
      queryResultTable = new JTable(tableModel);
      queryResultTable.setGridColor(Color.BLACK);
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

    JButton clearResultButton = new JButton( "Clear Result Window" );
    
    // 'southBox' is the bottom half of the JFrame content
    //   southBox organizes its content top to bottom
    Box southBox = new Box( BoxLayout.PAGE_AXIS );
    southBox.add( connectionStatusPane );
    southBox.add( SQLExecutionResultLabel );
    southBox.add( new JScrollPane( queryResultTable ) );
    southBox.add( clearResultButton );

    // add Box objects to JFrame.
    //  having the boxes managed by a BorderLayout manager allows them
    //  to take up as much space as the window is given.
    add( northBox, BorderLayout.NORTH );
    add( southBox, BorderLayout.SOUTH );

    /*
    * ADD 'executeSQLCommand' BUTTON FUNCTIONALITY:
    *  1. Get text from JTextArea (ideally correct SQL syntax)
    *  2. use the '.setQuery' method from the 'ResultSetTableModel' class
    *     to execute query
    *  3. Update JTable
    */
    executeSQLCommand.addActionListener(
      new ActionListener() {
        // this ActionListener's 'actionPerformed' method
        // is defined as passing a query to the ResultSetTableModel.
        public void actionPerformed( ActionEvent buttonPressed ) {

          // try to execute the user's query
          try {
            tableModel.setQuery( queryArea.getText() );
          }
          catch( SQLException sqlException ) {
            JOptionPane.showMessageDialog(null, sqlException.getMessage(),
              "Database error", JOptionPane.ERROR_MESSAGE);
            
            // // try to recover from invalid user query
            // // by executing default query
            // try {
            //   tableModel.setQuery( DEFAULT_QUERY );
            //   queryArea.setText( DEFAULT_QUERY );
            // }
            // catch( SQLException sqlException2 ) {
            //   JOptionPane.showMessageDialog(null, sqlException2.getMessage(),
            //   "Database error", JOptionPane.ERROR_MESSAGE);

            //   // ensure database connection is closed
            //   tableModel.disconnectFromDatabase();

            //   // terminate application
            //   System.exit( 1 );
            // }
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
        public void actionPerformed( ActionEvent clearTable ) {
          tableModel.setRowCount(0);
          queryResultTable.setVisible( false );
        }
      }
    );

    /*
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
     *    
     */

    propertiesFileComboBox.addActionListener(
      new ActionListener() {

        // the propertiesFileComboBox auto-populates the username and password fields on selection.
        public void actionPerformed( ActionEvent connect ) {

          FileInputStream propFile = null;
          Properties properties = new Properties();
          String propertiesFileName = propertiesFileComboBox.getSelectedItem().toString();

          try {
            propFile = new FileInputStream( propertiesFileName );
            properties.load( propFile );

            // record all values from properties file by referencing their corresponding keys.
            databaseURL = properties.getProperty( "MYSQL_DB_URL" );
            databaseUser = properties.getProperty( "MYSQL_DB_USERNAME" );
            databasePass = properties.getProperty( "MYSQL_DB_PASSWORD" );

            // graphically display properties file info
            usernameField.setText( databaseUser );
            passwordField.setText( databasePass );
          }
          catch( IOException e ) {
            e.printStackTrace();
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
