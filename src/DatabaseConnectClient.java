/*
 *  Name: Gregory Flynn
 *  Course: CNT 4714 Summer 2022
 *  Assignment title: Project 2 - A Two-tier Client-Server Application
 *  Date: July 1, 2022
 * 
 *  Class: DatabaseConnectClient.java
 */

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;

// classTags: [FRONT END] [DRIVER] [CLIENT-SERVER]

// Create a 'DatabaseConnectClient' object that
//  1. graphically displays all pertinent information
//  2. provides buttons for executing actions
//  3. allows login as an arbitrary user
//  4. connects to a specific database via JDBC
//  5. issues mySQL-style commands
//  6. logs all client queries and updates
public class DatabaseConnectClient extends JFrame {

  /* class variables that represent GUI components */
  private JTextArea queryArea;
  
  /* CONSTRUCTOR */
  public DatabaseConnectClient() {
    
    // evoke JFrame constructor and provide string.
    //  the constructor parameter string represents title of application.
    super("SQL Client App Version 1 - (GDF - CNT 4714 - Summer 2022 - Project 2)");
    
    /* initialize GUI components */
    // try-catch structure necessary because
    //  1. 'Connector/J' depedency could be missing:
    //    handled in the 'ClassNotFoundException' catch block
    //  2. Database connection invalid:
    //    handled in the 'SQLException' catch block
    try {

      /* INSTANTIATE A 'JTextArea' OBJECT */
      //  the purpose of the JTextArea is to allow the user to pass MySQL queries to the client
      //  when connected to the database, the client then passes the query to the database
      //    This is the "two-tier" behavior!
      queryArea = new JTextArea();
      queryArea.setBounds(10, 10, 500, 500);
      
      // set text wrapping property.
      // passing 'true' as a parameter has text wrap at word boundary
      // passing 'false' has text wrap at character boundary
      queryArea.setWrapStyleWord(true);

      // set line wrapping property.
      // 'true' paramater has lines wrap if they exceed JTextArea width
      // 'false' paramter has lines never wrap
      queryArea.setLineWrap(true);

      /* INSTANTIATE 'JButton' OBJECTS */
      // the purpose of the JButton object is 
      //  1. submitting queries
      //  2. clearing a JTextArea object
      //  3. connecting to database
      //  4. clearing a "result window"
      JButton clearQueryResults = new JButton("Clear Result Window");
      clearQueryResults.setBackground(Color.YELLOW);
      
      /* INSTANTIATE A 'Color' OBJECT */
      // the purpose of a Color object is to specify background color of client
      // parameters of the Color object are (r, g, b) values
      Color verylightGrey = new Color(238, 238, 238);

      // change background color of JFrame with methods
      // '.getContentPane()' from the 'JFrame' class and
      // '.setBackground(Color)' from the 'Component' class. 
      //    JFrame methods can be called statically since this class
      //    EXTENDS the JFrame class.
      //      in other words, the JVM knows JFrame methods are in reference
      //      to 'DatabaseConnectClient' since this code is inside the class's constructor
      //      and the class IS A JFrame object.
      getContentPane().setBackground(verylightGrey);

      /* INSTANTIATE 'Box' OBJECTS */
      // the purpose of Box objects is to organize GUI elements
      Box topBox = Box.createHorizontalBox();
      Box botBox = Box.createHorizontalBox();

      // add 'queryArea' to 'topBox' container
      topBox.add(queryArea);

      // add 'clearQueryResults' button to 'botBox' container
      botBox.add(clearQueryResults);

      // add Box objects to JFrame.
      //  component alignment in the JFrame is realized through 
      //  BorderLayout objects passed as parameters.
      add(topBox, BorderLayout.NORTH);
      add(botBox, BorderLayout.SOUTH);

      // set dimensions of JFrame
      setSize(1200, 600);

      // suppress automatic layout
      //  causes no components to load!
      //    in other words, invalidates component hierarchy
      // setLayout(null);

      // have the JFrame object create a seperate process
      setVisible(true);
    }
    // catch(ClassNotFoundException classNotFound) {
      
    //   // instantiate and intitalize a 'JOptionPane' object
    //   // to deliver an error pop-up.
    //   JOptionPane.showMessageDialog(null, "MySQL driver not found",
    //     "Driver not found", JOptionPane.ERROR_MESSAGE);

    //   // terminate program
    //   //  the parameter "1" indicates an abnormal program termination
    //   System.exit(1);
    // }
    finally {
      // DISPOSE of JFrame when user clicks exits out of process
      // this is alternative to the default behavior of HIDING on close
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

  }
    
  public static void main(String args[]) {

    // create a 'DatabaseConnectClient' object which contains all graphical components.
    new DatabaseConnectClient();

    // System.out.println("Test");
  }
}
