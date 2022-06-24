import javax.swing.Box;
import javax.swing.JFrame;

/*
 *  Name: Gregory Flynn
 *  Course: CNT 4714 Summer 2022
 *  Assignment title: Project 2 - A Two-tier Client-Server Application
 *  Date: July 1, 2022
 * 
 *  Class: DatabaseConnectClient.java
 */

// [FRONT END] [DRIVER]
// Create a 'DatabaseConnectClient' object that
//  1. graphically displays all pertinent information
//  2. provides buttons for executing actions
//  3. allows login as an arbitrary user
//  4. connects to a specific database via JDBC
//  5. issues mySQL-style commands
//  6. logs all client queries and updates
public class DatabaseConnectClient extends JFrame{
  
  /* CONSTRUCTOR */
  public DatabaseConnectClient() {
    
    // evoke JFrame constructor and provide string.
    // parameter string represents title of application window.
    super("SQL Client App Version 1 - (GDF - CNT 4714 - Summer 2022 - Project 2)");

    // create 'Box' object to manage placement of graphical components
    Box box = Box.createHorizontalBox();

    // place GUI components on content pane
    add(box);

    // set dimensions of JFrame object
    setSize(600, 300);

    // have the JFrame object create a window
    setVisible(true);

    // dispose of window when user quits application.
    // this is alternative to the default behavior of HIDING on close
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    // try {

    // }
    // catch(ClassNotFoundException classNotFound) {

    // }

  }

  public static void main(String args[]) {

    // create a 'DatabaseConnectClient' object which contains all graphical components.
    new DatabaseConnectClient();

    System.out.println("Test");
  }
}
