import java.awt.Color;
import java.util.ArrayList;
/**
 * Class used by to keep track of groups in specific categories. All used to make
 * further comparisons easier for JTree comparisons.
 * 
 * @author Jesse Saran
 *
 */
public class Category
{
	//groups in category
	private ArrayList<Group> groups = new ArrayList<Group>();
	//name of category
	private String name;
	//color for category in pictures
	private Color color = new Color(
			(int)(Math.random()*255),
			(int)(Math.random()*255),
			(int)(Math.random()*255));
	
	/**
	 * Constructor for Category that sets up its name.
	 * 
	 * @param name Name of the Category.
	 */
	public Category(String name)
	{
		this.name = name;
	}
	
	/**
	 * The color of the group for graphs.
	 * 
	 * @return Color of the group.
	 */
	public Color getColor()
	{
		return color;
	}
	
	/**
	 * Adding group to the category. Must be done through Model.
	 * 
	 * @param g Group to add.
	 */
	protected void addGroup(Group g)
	{
		if(!groups.contains(g))
		{
			groups.add(g);
		}
	}
	
	/**
	 * Removing group to the category. Must be done through Model.
	 * 
	 * @param g Group to remove.
	 */
	protected void removeGroup(Group g)
	{
		groups.remove(g);
	}
	
	/**
	 * The name of the Category.
	 * 
	 * @return Name of the category.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * The amount of Groups in the Category.
	 * 
	 * @return Number of groups in the Category.
	 */
	public int getGroupCount()
	{
		return groups.size();
	}
	
	/**
	 * Used for comparisons and returns the name.
	 * 
	 * @return The name of the Category.
	 */
	public String toString()
	{
		return name;
	}
	
	/**
	 * Returns the total amount of the contained groups amounts.
	 * 
	 * @return The total amount of the groups inside.
	 */
	public double getTotalAmount()
	{
		double total = 0.0;
		for(Group g : groups)
		{
			total += g.getTotalGroupAmount();
		}
		return total;
	}
	
	/**
	 * Returns all the groups.
	 * 
	 * @return All the groups.
	 */
	protected ArrayList<Group> getAllGroups()
	{
		return groups;
	}
}
