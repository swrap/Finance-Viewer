import java.util.Comparator;

/**
 * This class is used as another means to compare amounts of
 * Categories as a means to sort them.
 * 
 * @author Jesse Saran
 *
 */
public class CategoryAmountComparator implements Comparator
{
	/**
	 * Comparing the amounts of a Categories groups.
	 * 
	 * return If e2 > e1 value is positive. Else value is negative.
	 */
	@Override
	public int compare(Object e1, Object e2)
	{
		return (int)(((Category)e2).getTotalAmount() - ((Category)e1).getTotalAmount());
	}
}
