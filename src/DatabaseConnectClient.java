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
import java.awt.BorderLayout;
import java.awt.Color;

// [FRONT END] [DRIVER]
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
    // parameter string represents title of application window.
    super("SQL Client App Version 1 - (GDF - CNT 4714 - Summer 2022 - Project 2)");
    
    /* initialize GUI components */
    // try-catch structure necessary because
    //  1. 'Connector/J' depedency could be missing:
    //    handled in the 'ClassNotFoundException' catch block
    //  2. Database connection invalid:
    //    handled in the 'SQLException' catch block
    try {

      /* instantiate a 'JTextArea' object */
      queryArea = new JTextArea(3, 100);
      
      // set text wrapping property.
      // passing 'true' as a parameter has text wrap at word boundary
      // passing 'false' has text wrap at character boundary
      queryArea.setWrapStyleWord(true);

      // set line wrapping property.
      // 'true' paramater has lines wrap if they exceed JTextArea width
      // 'false' paramter has lines never wrap
      queryArea.setLineWrap(true);

      // create 'Box' object to manage placement of graphical components
      Box box = Box.createHorizontalBox();
      
      // // place JTextArea object inside of Box object
      // box.add(queryArea);

      // instantiate a 'Color' object
      // parameters are (r, g, b) values
      Color verylightGrey = new Color(238, 238, 238);

      // change background color of window
      //    JFrame methods can be called statically since this class
      //    EXTENDS the JFrame class.
      //      in other words, the JVM knows JFrame methods are in reference
      //      to 'DatabaseConnectClient' since this code is inside the class's constructor
      //      and the class IS A JFrame object.
      getContentPane().setBackground(verylightGrey);

      // place GUI components on content pane
      add(queryArea, BorderLayout.NORTH);

      // set dimensions of JFrame window
      setSize(800, 400);

      // have the JFrame object create a window
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
      // DISPOSE of window when user quits application.
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
