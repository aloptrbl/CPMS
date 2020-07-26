import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.sql.Connection;
import java.util.Vector;
import java.util.ArrayList;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarParkFrame extends JFrame implements ActionListener {
  private JPanel mainPanel, viewTransactionPanel;
  private JFrame secondFrame = new JFrame(getTitle());
  private static final long serialVersionUID = 1L;
  private Parking parking = new Parking();
  private JButton[] btns = new JButton[10];
  private JButton btnViewTransactions = new JButton("View transactions");
  private double total;
  static JTable table;
  ArrayList columnNames = new ArrayList();
  ArrayList data = new ArrayList();

  public CarParkFrame() {
    super("Car Park Management System");
    mainPanel = new JPanel(); // main panel
    mainPanel.setLayout(new GridLayout(2, 5));
    mainPanel.setBackground(Color.GRAY);

    btnViewTransactions.addActionListener(this);
    for (int x = 0; x < btns.length; x++) {
      final int i = x;
      btns[x] = new JButton((x + 1) + "");
      boolean parked = parking.getParkedList().contains(i);
      if(parked)
      {
        Color on = Color.RED;
        btns[x].setBackground(on);
      }
      else
      {
        Color on = Color.GREEN;
        btns[x].setBackground(on);
      }
      
      
      btns[x].setOpaque(true);
      btns[x].addActionListener(
          new ActionListener() {

            public void actionPerformed(ActionEvent e) {
              AbstractButton button = (AbstractButton) e.getSource();
              Color color = button.getBackground();

              if (color == Color.GREEN) {
                String plateNo = JOptionPane.showInputDialog(
                  "Please enter the plate number:"
				);
				if(plateNo.isEmpty())
				{
					JOptionPane.showMessageDialog(null, "Plate number is required.");
				}
				else if(plateNo.length() > 15)
				{
					JOptionPane.showMessageDialog(null, "Plate number is too long, only 15 characters are allowed.");
				}
                else if (!plateNo.isEmpty()) {
                  if(parking.getPlateNoAvailable(plateNo))
                  {
                    JOptionPane.showMessageDialog(null, plateNo + " is already inside the parking.");
                  }
                  else
                  {
                  plateNo = plateNo.toUpperCase();
                  Car car = new Car(plateNo);
                  parking.parkCar(i, car);
                  JOptionPane.showMessageDialog(
                    null,
                    "The car " + plateNo + " is parked in plot " + (i + 1) + "."
                  );
                  btns[i].setBackground(Color.RED);
                  }
				}
				
              } else {
                parking.exitCar(i);
                btns[i].setBackground(Color.GREEN);
              }
            }
          }
        );
      mainPanel.add(btns[x]);
    }

    viewTransactionPanel = new JPanel();
    btnViewTransactions.setPreferredSize(new Dimension(40, 40));
    viewTransactionPanel.add(btnViewTransactions);
    this.add(mainPanel);
    this.add(btnViewTransactions);
    this.setSize(450, 200);
    this.setLocationRelativeTo(null);
    this.setLayout(new GridLayout(2, 5));
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setVisible(true);
    
    //this.add(btnViewTransactions);
    try {

      String url = "jdbc:mysql://localhost:3306/parking_system";
      String userid = "root";
      String password = "";
      String sql = "SELECT * FROM Parking";
  
      Connection connection = DriverManager.getConnection(url, userid, password);
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      ResultSetMetaData md = rs.getMetaData();
      int columns = md.getColumnCount();
      for (int i = 1; i <= columns; i++) {
        columnNames.add(md.getColumnName(i));
      }
      while (rs.next()) {
        ArrayList row = new ArrayList(columns);
        for (int i = 1; i <= columns; i++) {
          row.add(rs.getObject(i));
        }
        data.add(row);
      }

      Vector columnNamesVector = new Vector();
    Vector dataVector = new Vector();
    for (int i = 0; i < data.size(); i++) {
      ArrayList subArray = (ArrayList) data.get(i);
      Vector subVector = new Vector();
      for (int j = 0; j < subArray.size(); j++) {
        subVector.add(subArray.get(j));
      }
      dataVector.add(subVector);
    }
    for (int i = 0; i < columnNames.size(); i++)
      columnNamesVector.add(columnNames.get(i));
    JTable table = new JTable(dataVector, columnNamesVector) {
      public Class getColumnClass(int column) {
        for (int row = 0; row < getRowCount(); row++) {
          Object o = getValueAt(row, column);
          if (o != null) {
            return o.getClass();
          }
        }
        return Object.class;
      }
    };
    JScrollPane scrollPane = new JScrollPane(table);
    secondFrame.add(scrollPane);
    JPanel buttonPanel = new JPanel();
    secondFrame.add(buttonPanel, BorderLayout.SOUTH);
      
  } catch (SQLException sqlException) {
    sqlException.printStackTrace();
  }
  
  }


  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == btnViewTransactions) 
    if(data.isEmpty())
    {
      JOptionPane.showMessageDialog(null, "There are no transactions available.");
    }
    else
    {
      secondFrame.setVisible(true);
      secondFrame.setSize(550, 500);
      secondFrame.setLocationRelativeTo(null);
      secondFrame.setLayout(new GridLayout(2, 5));
    }
  }

  public static void main(String[] args) throws Exception {
    new CarParkFrame();
  }
}
