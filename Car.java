/**
 * This class is used to represent the parked car information.
 * 
 * @author Satrya Fajri Pratama
 * @version 1.0
 */
public class Car
{
	private String plateNo;
	private long entry;

	/**
	 * This constructor is used to instantiate a new <code>Car</code> object
	 * using the passed in parameter. This constructor will also initialize the
	 * value of <code>entry</code> instance variable to current UNIX timestamp.
	 * 
	 * @param plateNo
	 *            The plate number.
	 */
	public Car(String plateNo)
	{
		this.plateNo = plateNo;
		this.entry = System.currentTimeMillis();
	}

	/**
	 * This constructor is used to instantiate a new <code>Car</code> object
	 * using the passed in parameters.
	 * 
	 * @param plateNo
	 *            The plate number.
	 * @param entry
	 *            The entry time in UNIX timestamp format.
	 */
	public Car(String plateNo, long entry)
	{
		this.plateNo = plateNo;
		this.entry = entry;
	}

	/**
	 * This method is used to retrieve the plate number.
	 * 
	 * @return The plate number.
	 */
	public String getPlateNo()
	{
		return plateNo;
	}

	/**
	 * This method is used to retrieve the entry time in UNIX timestamp format
	 * (number of milliseconds after January 1, 1970).
	 * 
	 * @return The entry time in UNIX timestamp format.
	 */
	public long getEntry()
	{
		return entry;
	}
}