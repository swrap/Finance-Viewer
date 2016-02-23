import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JComponent;

/**
 * This is a TransferHandler class that is used by the Group JTree
 * to handle DND events.
 * 
 * @author Jesse Saran
 *
 */
public class GroupDragger extends TransferHandler
{
	//accepts only DefaultTreeNodes
	private DataFlavor def = new DataFlavor(DefaultMutableTreeNode.class, "DTreeNode");
	//the flavors accepted in an array
	private DataFlavor [] flavors = {def};
	//instance of the model
	private Model mainModel;
	//instance of the frame
	private DefaultFrame df;
	
	/**
	 * Constructor to create a Group Dragger.
	 * 
	 * @param mainModel Instance of the Model.
	 * @param df Instance of the DefualtFrame
	 */
	public GroupDragger(Model mainModel, DefaultFrame df)
	{
		this.mainModel = mainModel;
		this.df = df;
	}
	
	/**
	 * Imports the DefaultMutableTreeNodes that were selected if Groups.
	 */
	@Override
	public boolean importData(TransferSupport support)
	{
		ArrayList <DefaultMutableTreeNode> nodes = null;
		
		Transferable t = support.getTransferable();
		try {
			nodes = (ArrayList <DefaultMutableTreeNode>) t.getTransferData(def);
		} catch (UnsupportedFlavorException | IOException e) {

			e.printStackTrace();
		}
		
		JTree.DropLocation d = (javax.swing.JTree.DropLocation) support.getDropLocation();
		int childIndex = d.getChildIndex();
		
		TreePath path = d.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((JTree) support.getComponent()).getModel().getRoot();
		childIndex = root.getIndex(node);
		
		JTree tree = (JTree) support.getComponent();

		node = (DefaultMutableTreeNode) root;
		
		for(int i = 0; i < nodes.size(); ++i)
		{
			if(nodes.get(i).getUserObject() instanceof Group)
			{
				Group group = mainModel.getGroup(((Group) nodes.get(i).getUserObject()).getName());
				node.insert(nodes.get(i), childIndex);
			}
		}
		return true;
	}
	
	/**
	 * Will only import Group. NOT GropObjects.
	 */
	@Override
	public boolean canImport(TransferSupport support)
	{
		support.setShowDropLocation(true);
	    if (!support.isDataFlavorSupported(def) || !support.isDrop())
	    {
	        return false;
	    }
	    
	    //checks that dropping on category
	    JTree.DropLocation dropLocation = (JTree.DropLocation)support.getDropLocation();
	    TreePath dropPath = dropLocation.getPath();
	    if(dropPath == null || !(((DefaultMutableTreeNode) dropPath.getLastPathComponent()).getUserObject() instanceof Group))
	    {
	    	return false;
	    }
	    
	    //checks that transferables have a group and then group objects
		ArrayList <DefaultMutableTreeNode> nodes = null;
		Transferable t = support.getTransferable();
		try {
			nodes = (ArrayList <DefaultMutableTreeNode>) t.getTransferData(def);
		} catch (UnsupportedFlavorException | IOException e) {

			e.printStackTrace();
		}

		for(int i = 0; i < nodes.size(); ++i)
		{
			DefaultMutableTreeNode node = nodes.get(i);
			if(!(node.getUserObject() instanceof Group))			
			{
				return false;
			}
		}
	    return true;
	}

	/**
	 * Will create a Transferable Array of Group DefaultMutableTreeNodes.
	 */
	@Override
	protected Transferable createTransferable(JComponent c) 
	{
		JTree tree = (JTree)c;
        TreePath[] paths = tree.getSelectionPaths();
        ArrayList<DefaultMutableTreeNode> list = new ArrayList<DefaultMutableTreeNode>();
        
        for(int i = 0; i < paths.length; ++i)
        {
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
        	list.add(node);
        }
        return new GroupTransferable(list);
	}
	
	/**
	 * Deletes the nodes of the exported information.
	 */
	@Override
	protected void exportDone(JComponent source, Transferable data, int action)
	{
		if(action == MOVE)
		{
			JTree tree = (JTree)source;
			TreePath [] paths = tree.getSelectionPaths();
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			for(TreePath temp : paths)
			{
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
				if(root.getIndex((TreeNode) temp.getLastPathComponent()) < model.getChildCount(root))
				{
					root.remove(root.getIndex((TreeNode) (temp.getLastPathComponent())));
				}
			}
			((DefaultTreeModel)tree.getModel()).reload();
			df.update(null,null);
		}
	}
	
	/**
	 * Sets action as MOVE only.
	 */
	@Override
	public int getSourceActions(JComponent c)
	{
		return MOVE;
	}
	
	/**
	 * Class used for creating Transferable DataFlavor.
	 * 
	 * @author Jesse Saran
	 *
	 */
	public class GroupTransferable implements Transferable
	{
		//transferable list of groups "DefaultMutableTreeNodes" to be sent
		ArrayList<DefaultMutableTreeNode> groups;
		
		/**
		 * Constructor for the groups for the internal data of the Transferable
		 * data.
		 * 
		 * @param groups ArrayList<Group> to send.
		 */
		public GroupTransferable(ArrayList<DefaultMutableTreeNode> groups)
		{
			this.groups = groups;
		}
		
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
		{
			if(!isDataFlavorSupported(flavor))
			{
				throw new UnsupportedFlavorException(flavor);
			}
			return groups;
		}
		
		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			return flavors;
		}
		
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			return def.equals(flavor);
		}
	}
}
