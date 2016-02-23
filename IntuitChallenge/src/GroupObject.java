import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * GroupObjects that keep the CSV file data. Must be Serializable
 * because sent through Transferable.
 * 
 * @author Jesse Saran
 *
 */
public class GroupObject implements Serializable
{
	//date of GroupObject
	private Calendar date;
	//name of GroupObject
	private String name;
	//amount of GroupObject
	private double amount;
	
	/**
	 * Constructor used to get values of GroupObject.
	 * 
	 * @param dateS Date of the transaction.
	 * @param name Name of the transaction.
	 * @param amount Amount of the transaction.
	 */
	public GroupObject(String dateS, String name, double amount)
	{
		String [] arrD = dateS.split("\\D+");
		this.date = new GregorianCalendar(Integer.parseInt(arrD[0]),
				Integer.parseInt(arrD[1]), Integer.parseInt(arrD[2]));
		this.name = name;
		this.amount = amount;
	}
	
	/**
	 * The Date of the Transaction.
	 * 
	 * @return Date.
	 */
	public Calendar getDate()
	{
		return date;
	}
	
	/**
	 * Returns the date in the given format. Or has "yyyy-MM-dd" as default.
	 * 
	 * @param givenDate Calendar date to turn to String.
	 * @param format Format to turn to.
	 * @return The String representation.
	 */
	public static String getDateToString(Calendar givenDate, String format)
	{
		if(format == null)
		{
			format = "yyyy-MM-dd";
		}
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(givenDate.getTime());
	}
	
	/**
	 * Returns this GroupObjects date as a String. Returns the date in the given format.
	 *  Or has "yyyy-MM-dd" as default.
	 * 
	 * @param format Format to turn to.
	 * @return The String representation.
	 */
	public String getDateString(String format)
	{
		if(format == null)
		{
			format = "yyyy-MM-dd";
		}
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(date.getTime());
	}
	
	/**
	 * Name of this GroupObject.
	 * 
	 * @return Name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Amount of this GroupObject.
	 * 
	 * @return Amount.
	 */
	public double getAmount()
	{
		return amount;
	}
	
	/**
	 * Date of this GroupObject followed by the Amount.
	 */
	@Override
	public String toString()
	{
		return getDateString(null) + " " + name + " $" + amount;
	}
}
