import java.awt.Color;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Group used to keep data for the program. Is Seralizable because is used
 * in Transferable.
 * 
 * @author Jesse Saran
 *
 */
public class Group implements Serializable
{
	//groupname
	private String groupName = "";
	//list of GroupObjects contained
	private ArrayList<GroupObject> go = new ArrayList<GroupObject>();
	//Whether or not in a Category
	private boolean isInCategory = false;
	//most and least GroupObjects
	private GroupObject most, least;
	//color used on GUI Charts
	private Color color = new Color(
			(int)(Math.random()*255),
			(int)(Math.random()*255),
			(int)(Math.random()*255));
	
	/**
	 * Constructor for new Group.
	 * 
	 * @param name Name of the Group.
	 */
	public Group(String name)
	{
		this.groupName = name;
	}
	
	/**
	 * Color associated for the group.
	 * 
	 * @return Color.
	 */
	public Color getColor()
	{
		return color;
	}
	
	/**
	 * Adds GroupObject as well as sets its
	 * most and least values for the Group.
	 * 
	 * @param g GroupObject to add.
	 */
	public void addGroupObject(GroupObject g)
	{
		go.add(g);
		if(most == null)
		{
			most = g;
			least = g;
		}
		
		if(g.getAmount() > most.getAmount())
		{
			most = g;
		}
		else if(g.getAmount() < least.getAmount())
		{
			least = g;
		}
	}
	
	/**
	 * ArrayList of all the GroupObjects associated with this Group.
	 * 
	 * @return All this Groups GroupObjects.
	 */
	public ArrayList<GroupObject> getGroupObjects()
	{
		return go;
	}
	
	/**
	 * Name of this Group.
	 * 
	 * @return Name of this Group.
	 */
	public String getName()
	{
		return groupName;
	}
	
	/**
	 * All the names of this Groups, GroupObjects.
	 * 
	 * @return An array of the GroupObjects names.
	 */
	public String [] getGroupObjectsNames()
	{
		String [] temp = new String[go.size()];
		for(int i = 0; i < temp.length; ++i)
		{
			temp[i] = go.get(i).getName();
		}
		return temp;
	}
	
	/**
	 * Removes all this GroupsObjects.
	 */
	public void removeAllObjects()
	{
		go.clear();
	}
	
	/**
	 * The String representation. Name of the group followed
	 * by the total amount of this group.
	 */
	@Override
	public String toString()
	{
		DecimalFormat decimal = new DecimalFormat("#.##");
		return groupName + ": $" + decimal.format(getTotalGroupAmount());
	}
	
	/**
	 * Set the state of the given Group.
	 * 
	 * @param bool True if in Category. False else.
	 */
	public void toggleCategory(boolean bool)
	{
		this.isInCategory = bool;
	}
	
	/**
	 * Get the value of this Group whether or not in a Category.
	 * 
	 * @return True if in Category. False else.
	 */
	public boolean isInCategory()
	{
		return this.isInCategory;
	}

	/**
	 * Total GroupObject count in this Group.
	 * 
	 * @return Amount of GroupObjects.
	 */
	public int getGroupObjectCount()
	{
		return go.size();
	}
	
	/**
	 * Most valuable GroupObject in this Group.
	 * 
	 * @return GroupObject with highest Value.
	 */
	public GroupObject getMost()
	{
		return most;
	}
	
	/**
	 * Least valuable GroupObject in this Group.
	 * 
	 * @return GroupObject with least Value.
	 */
	public GroupObject getLeast()
	{
		return least;
	}
	
	/**
	 * Return the missing GroupObjects when comparing to another group.
	 * 
	 * @param g List of other group objects.
	 * @return List of missing objects.
	 */
	public ArrayList<GroupObject> findMissing(ArrayList<GroupObject> g)
	{
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList<GroupObject> temp = new ArrayList<GroupObject>();
		for(GroupObject tempG : go)
		{
			if(!g.contains(tempG))
			{
				temp.add(tempG);
			}
		}
		return temp;
	}
	
	/**
	 * Returns the total amount of this Groups, GroupObjects combined.
	 * 
	 * @return Total amount.
	 */
	public double getTotalGroupAmount()
	{
		double total = 0;
		for(GroupObject g : go)
		{
			total += g.getAmount();
		}
		
		return total;
	}
	
	/**
	 * Returns the start and end date of this Groups, GroupObjects.
	 * 
	 * @return Calendar start and end date.
	 */
	public Calendar [] getStartEndDate()
	{
		Calendar [] c = {go.get(0).getDate(), go.get(0).getDate()};
		
		for(GroupObject g : go)
		{
			if(g.getDate().compareTo(c[0]) < 0)
			{
				c[0] = g.getDate();
			}
			else if(g.getDate().compareTo(c[1]) > 0)
			{
				c[1] = g.getDate();
			}
		}
		return c;
	}
	
	/**
	 * Returns the given GroupObject at the given index.
	 * 
	 * @param index Index of GroupObject
	 * @return GivenGroup.
	 */
	public GroupObject getGroupObject(int index)
	{
		return go.get(index);
	}
}
