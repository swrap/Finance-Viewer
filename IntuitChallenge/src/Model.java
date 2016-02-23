import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * Model that keeps track of all data. Groups, Categories, you name it, it is in here.
 * Well besides of course the GUI stuff because that is not part of the MVC pattern.
 * That would be just foolish.
 * 
 * @author Jesse Saran
 *
 */
public class Model extends Observable
{
	//hashmap of all the groups, key is the name of the group
	private HashMap<String,Group> groups = new HashMap<String,Group>();
	//hashmap of all of the categories, key is the name of the Category
	private HashMap<String,Category> categories = new HashMap<String,Category>();

	/**
	 * Constructor. Just used to create an instance.
	 */
	public Model()
	{
		
	}
	
	/**
	 * Add the group object to the Group.
	 * 
	 * @param gname Name of the Group to add the GroupObject to.
	 * @param go GroupObject to add.
	 */
	public void addGroupObject(String gname, GroupObject go)
	{
		Group tempG = groups.get(gname);
		tempG.addGroupObject(go);
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Adds Group if Group does not already exist. Does NOT replace
	 * existing Groups.
	 * 
	 * @param name Name of Group to add.
	 * @return Group if successfully added or null if group already exists.
	 */
	public Group addGroup(String name)
	{
		Group g = new Group(name);

		if(!groups.containsKey(name))
		{
			groups.put(name, g);
			this.setChanged();
			this.notifyObservers(g);
			return g;
		}
		return null;
	}
	
	/**
	 * Returns the Group of the given name.
	 * 
	 * @param name Name of Group to return.
	 * @return The Group.
	 */
	public Group getGroup(String name)
	{
		return groups.get(name);
	}
	
	/**
	 * Returns an ArrayList of all the Groups.
	 * 
	 * @return List of all the Groups.
	 */
	public ArrayList<Group> getAllGroups()
	{
		return new ArrayList<Group>(groups.values());
	}
	
	/**
	 * The total amount of groups.
	 * 
	 * @return Total amount of groups.
	 */
	public int getTotalGroupSize()
	{
		return groups.size();
	}
	
	/**
	 * Names of the group.
	 * 
	 * @return All names of the groups.
	 */
	public Set<String> getGroupNames()
	{
		return groups.keySet();
	}
	
	/**
	 * Returns an ArrayList of all the Categories.
	 * 
	 * @return List of all the Categories.
	 */
	public ArrayList<Category> getAllCategories()
	{
		return new ArrayList<Category>(categories.values());
	}
	
	/**
	 * Adds a Group to a Category.
	 * 
	 * @param c Category to add Group to.
	 * @param g Group that is being added.
	 */
	public void addToCategory(Category c, Group g)
	{
		categories.get(c.getName()).addGroup(g);
	}
	
	/**
	 * Adds Category if Category does not already exist. Does NOT replace
	 * existing Category.
	 * 
	 * @param name Name of Category to add.
	 * @return Category if successfully added or null if group already exists.
	 */
	public Category addCategory(String name)
	{
		Category cat = new Category(name);
		
		if(!categories.containsKey(name))
		{
			categories.put(name, cat);
			this.setChanged();
			this.notifyObservers();
			return cat;
		}
		return null;
	}
	
	/**
	 * Gets the total amount of Categories.
	 * 
	 * @return Size of the categories.
	 */
	public int getTotalCategorySize()
	{
		return categories.size();
	}
	
	/**
	 * Returns a Category with the given name.
	 * 
	 * @param name Category name.
	 * @return Category.
	 */
	private Category getCategory(String name)
	{
		return categories.get(name);
	}
	
	/**
	 * Adds a Group to the give Category. Should always use this method to add.
	 * 
	 * @param catName Name of the Category.
	 * @param groupName Name of the Group.
	 */
	public void addGroupToCategory(String catName, String groupName)
	{
		Group g = groups.get(groupName);
		g.toggleCategory(true);
		categories.get(catName).addGroup(g);
	}
	
	/**
	 * Removes a Group to the give Category. Should always use this method to remove.
	 * 
	 * @param catName Name of the Category.
	 * @param groupName Name of the Group.
	 */
	public void removeGroupFromCategory(String catName, String groupName)
	{
		Group g = groups.get(groupName);
		g.toggleCategory(false);
		categories.get(catName).removeGroup(g);
	}
	
	/**
	 * Removes a Category.
	 * 
	 * @param name Name of Category.
	 * @return Category that was removed.
	 */
	public Category removeCategory(String name)
	{
		return categories.remove(name);
	}
}
