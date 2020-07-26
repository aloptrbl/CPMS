import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import java.text.SimpleDateFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class is used to represent the parking information.
 *
 * @author Satrya Fajri Pratama
 * @version 1.0
 */
public class Parking {
  // JDBC driver name and database URL
  static final String db_url = "jdbc:mysql://localhost:3306/parking_system";
  static final String db_username = "root";
  static final String db_password = "";

  private static final int LENGTH = 10;
  private Car[] parkedCars = new Car[LENGTH];
  private List<Integer> parkedList = new ArrayList<Integer>();

  public Parking() {
    try {
      Connection connection = (Connection) DriverManager.getConnection(
        db_url,
        db_username,
        db_password
      );

      PreparedStatement st = connection.prepareStatement(
        "Select PlotNo from Parking WHERE Duration is NULL"
      );

      ResultSet rs = st.executeQuery();
      while (rs.next()) {
        //Retrieve by column name
        int PlotNo = rs.getInt("PlotNo");
        parkedList.add(PlotNo);
      }
      System.out.println(parkedList);

      //STEP 6: Clean-up environment
      rs.close();
      st.close();
      connection.close();
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }
  }

  public List<Integer> getParkedList()
  {
	  return parkedList;
  }

  public boolean getPlateNoAvailable(String plateNo)
  {
	try {
		Connection connection = (Connection) DriverManager.getConnection(
		  db_url,
		  db_username,
		  db_password
		);
  
		PreparedStatement st = connection.prepareStatement(
		  "Select PlotNo from Parking WHERE PlateNo = ?"
		);
		st.setString(1, plateNo);
		ResultSet rs = st.executeQuery();
		if(rs.next()) {
		  //Retrieve by column name
		  return true;
		}
		//STEP 6: Clean-up environment
		rs.close();
		st.close();
		connection.close();
	  } catch (SQLException sqlException) {
		sqlException.printStackTrace();
	  }
	  return false; 
  }

  public boolean exitCar(int plotNo)
  {
	double price = 0;
	String entry = null;
	try {
		Connection connection = (Connection) DriverManager.getConnection(
		  db_url,
		  db_username,
		  db_password
		);
  
		PreparedStatement stx = connection.prepareStatement(
		  "Select Entry from Parking WHERE plotNo = ?"
		);
		stx.setInt(1, plotNo);
		ResultSet rsx = stx.executeQuery();
		if(rsx.next()) {
		 entry = rsx.getString("Entry");
		 
		}
		//STEP 6: Clean-up environment
	} catch (SQLException sqlException) {
		sqlException.printStackTrace();
	  }

  try {
	Connection connection = (Connection) DriverManager.getConnection(
		db_url,
		db_username,
		db_password
	  );
		PreparedStatement st = connection.prepareStatement(
		  "UPDATE Parking SET Duration = ?, Fee = ? WHERE PlotNo = ?"
		);
		try {
		String date = entry;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = format.parse(date);
		long millis = d.getTime();
		price = getPrice(millis);
		double duration = Math.ceil((System.currentTimeMillis() - millis) / 3_600_000d);
		st.setDouble(1, duration);
		st.setDouble(2, price);
		st.setInt(3, plotNo);
	}
	catch (Exception  e)
	{
		System.out.println("error"+e);
	}
		int rs = st.executeUpdate();
		if (rs != 0) {
		  //Retrieve by column name
		  System.out.println("success");
		  return true;

		}
  
		//STEP 6: Clean-up environment
		st.close();
		connection.close();
	  } catch (SQLException sqlException) {
		sqlException.printStackTrace();
	  }
	  return false;
  }
  /**
   * This method assigns the {@link Car} object passed in the parameter to the
   * <code>parkedCars</code> instance variable using the plot value passed in
   * the parameter as the array index.
   *
   * @param plot
   *            The plot number.
   * @param car
   *            The car object to be stored.
   */
  public void parkCar(int plot, Car car) {
    Date date = new java.util.Date(car.getEntry());
    SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    sdf.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
	String formattedDate = sdf.format(date);
	
    try {
      Connection connection = (Connection) DriverManager.getConnection(
        db_url,
        db_username,
        db_password
      );

      PreparedStatement st = connection.prepareStatement(
        "INSERT INTO Parking(PlotNo, PlateNo, Entry) VALUES(?, ?, ?)"
      );

      st.setInt(1, plot);
      st.setString(2, car.getPlateNo());
      st.setString(3, formattedDate);
      int rs = st.executeUpdate();
      if (rs != 0) {
        //Retrieve by column name
        System.out.println("success");
      }

      //STEP 6: Clean-up environment
      st.close();
      connection.close();
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }
  }

  /**
   * This overloaded method which receives the plot value passed in the
   * parameter will remove the {@link Car} object on that plot if the
   * {@link Car} object is exist. Thus, this method will calculate the parking
   * fee, and will use this fee as the return value. Otherwise, this method
   * will return 0 if there is no {@link Car} object stored in the specified
   * plot.
   *
   * @param plot
   *            The plot number.
   * @return The parking fee or 0 if there is no {@link Car} object stored in
   *         the specified plot.
   */
//   public double exitCar(int plot) {
//     double price = 0;
//     Car parkedCar = parkedCars[plot];

//     if (parkedCar != null) {
//       parkedCars[plot] = null;
//       price = getPrice(parkedCar.getEntry());
//     }

//     return price;
//   }

  /**
   * This overloaded method which receives the plate number will iterate
   * through all elements of <code>parkedCars</code> array to search for the
   * {@link Car} object that matches the specified plate number using the
   * <code>plateNo</code> value passed in the parameter (refer to
   * {@link String#equalsIgnoreCase(String)} method in Java API). If it is
   * found, the {@link Car} object will be removed from the
   * <code>parkedCars</code> array and calculate the parking fee, and will use
   * this fee as the return value. Otherwise, this method will return 0 if
   * there is no {@link Car} object with the specified plate number.
   *
   * @param plateNo
   *            The plate number.
   * @return The parking fee or 0 if there is no {@link Car} object with the
   *         specified plate number.
   */
  public double exitCar(String plateNo) {
    double price = 0;

    for (int i = 0; i < LENGTH; i++) {
      Car parkedCar = parkedCars[i];

      if (
        parkedCar != null && parkedCar.getPlateNo().equalsIgnoreCase(plateNo)
      ) {
        parkedCars[i] = null;
        price = getPrice(parkedCar.getEntry());

        break;
      }
    }

    return price;
  }

  /**
   * This method will print the list of parked cars and their plot. However,
   * this method is deprecated and is not recommmended to be used.
   */
  @Deprecated
  public void displayCars() {
    System.out.println("+-------+---------------+");
    System.out.println("| Plot  |   Plate No.   |");
    System.out.println("+-------+---------------+");

    for (int i = 0; i < LENGTH; i++) {
      Car parkedCar = parkedCars[i];

      if (parkedCar != null) System.out.println(
        "| " + (i + 1) + "\t| " + parkedCar.getPlateNo() + "\t|"
      );
    }

    System.out.println("+-------+---------------+");
  }

  /**
   * This method returns the array of plate numbers from the elements of
   * <code>parkedCars</code> array.
   *
   * @return The array of plate numbers.
   */
  public String[] getPlateNos() {
    int count = 0;

    for (Car parkedCar : parkedCars) if (parkedCar != null) count++;

    int index = 0;
    String[] plateNos = new String[count];

    for (Car parkedCar : parkedCars) if (parkedCar != null) plateNos[index++] =
      parkedCar.getPlateNo();

    return plateNos;
  }

  /**
   * This method will return the first available parking plot, or will return
   * -1 if there are no available plots.
   *
   * @return The first available parking plot, or -1 if there are no available
   *         plots.
   */
  public int getAvailablePlot() {
    int index = -1;

    for (int i = 0; i < LENGTH; i++) {
      if (parkedCars[i] == null) {
        index = i;
        break;
      }
    }

    return index;
  }

  /**
   * This method will return <code>true</code> if there are no {@link Car}
   * objects stored in <code>parkedCars</code> array, or <code>false</code> if
   * there is at least one {@link Car} object stored in the array.
   *
   * @return The empty status.
   */
  public boolean isEmpty() {
    boolean empty = true;

    for (Car parkedCar : parkedCars) {
      if (parkedCar != null) {
        empty = false;
        break;
      }
    }

    return empty;
  }

  /**
   * This method receives the value of start in the parameter, calculates the
   * duration, which is the rounded-up differences in hour between current
   * time and the start time (refer to {@link Math#ceil()} method from Java
   * API), and will return the parking fee based on the duration.
   *
   * @param start
   *            The start time in UNIX timestamp format.
   * @return
   */
  private double getPrice(long start) {
    double price = 3, duration = Math.ceil(
      (System.currentTimeMillis() - start) / 3_600_000d
    );

    if (duration > 2) price += duration - 2;

    return price;
  }
}
