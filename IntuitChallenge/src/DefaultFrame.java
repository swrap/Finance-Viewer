import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.PopupMenu;

import javax.swing.JLabel;
import javax.swing.DropMode;

import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JRadioButton;

import org.jfree.chart.ChartPanel;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBox;

import java.awt.Toolkit;

import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * This massive file deals with the "V" and "C" part of the MVC pattern. It controls all the GUI
 * commands inputs and outputs. Keeps everything up to date and is the Observer part to the Models 
 * Observable pattern. This has a lot to it, but mostly is just style. Used WindowsBuilder for eclipse
 * for a lot of this.
 * 
 * @author Jesse Saran
 *
 */
public class DefaultFrame extends JFrame implements Observer
{
	public static final Color DEFUALT_BACKGROUND_COLOR = UIManager.getColor("Panel.background");
	private Model model;
	private JTextField categoryName;
	private JTree categoryTree;
	private JTree groupTree;
	private DefaultMutableTreeNode groupTop;
	private DefaultMutableTreeNode categoryTop;
	private JLabel totalSpent;
	private JLabel timeFrame;
	private JLabel avgDaily;
	private JLabel mostExpGroup;
	private JLabel leastExpGroup;
	private JLabel mostExpCategory;
	private JLabel leastExpCategory;
	private JLabel leastExpGroupObject;
	private JLabel mostExpGroupObject;
	private Charts pie;
	private JPanel chart;
	private JRadioButton pieButton;
	private JRadioButton histogramButton;
	private JRadioButton timeButton;
	private JCheckBox includeButton;
	private JMenuItem selGroupSep;
	private JMenuItem selGroupTog;
	private JMenuItem selCatSep;
	private AbstractButton selCatTog;
	private JPopupMenu popupMenu;
	
	/**
	 * Constructor for the JFrame. Sets all of its components up.
	 * Adds all the Listeners as well.
	 * 
	 * @param model Instance of the Model.
	 */
	public DefaultFrame(Model model)
	{
		super("Expense Analyzer");
		setIconImage(Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir") + "\\Logo.png"));
		this.model = model;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{50, 50, 50, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		categoryName = new JTextField();
		categoryName.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					addCategoryToJTree(categoryName.getText());
					categoryName.setText("");
				}
			}

			@Override
			public void keyTyped(KeyEvent e){}
			
		});
		GridBagConstraints gbc_categoryName = new GridBagConstraints();
		gbc_categoryName.fill = GridBagConstraints.HORIZONTAL;
		gbc_categoryName.insets = new Insets(0, 0, 5, 5);
		gbc_categoryName.gridx = 0;
		gbc_categoryName.gridy = 0;
		getContentPane().add(categoryName, gbc_categoryName);
		categoryName.setColumns(10);
		
		JButton addCategory = new JButton("Add Sorting Category");
		addCategory.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addCategoryToJTree(categoryName.getText());
				categoryName.setText("");
			}
		});
		GridBagConstraints gbc_addCategory = new GridBagConstraints();
		gbc_addCategory.fill = GridBagConstraints.HORIZONTAL;
		gbc_addCategory.insets = new Insets(0, 0, 5, 5);
		gbc_addCategory.gridx = 1;
		gbc_addCategory.gridy = 0;
		getContentPane().add(addCategory, gbc_addCategory);
		
		JButton deleteCategory = new JButton("Delete Selected Category");
		deleteCategory.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(categoryTree.getSelectionCount() > 0)
				{
					TreePath [] paths = categoryTree.getSelectionPaths();
			        for(int i = 0; i < paths.length; ++i)
			        {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
						if(node.getChildCount() == 0)
						{
							int n = JOptionPane.showConfirmDialog(DefaultFrame.this.getContentPane(),
								    "You are about to delete a category \"" + node.toString() +  "\"."
								    + "Click \"OK\" to continue.",
								    "Category Delete Confirmation",
								    JOptionPane.OK_CANCEL_OPTION);
							if(n == 0)
							{
								model.removeCategory(((Category)node.getUserObject()).getName());
								node.removeFromParent();
								((DefaultTreeModel)categoryTree.getModel()).reload();
							}
						}
						else
						{
							JOptionPane.showMessageDialog(DefaultFrame.this.getContentPane(), "Unable to delete Category: " + 
									node.getUserObject().toString() + ". Must remove all groups inside first."
									, "Error", JOptionPane.ERROR_MESSAGE);
						}
			        }
				}
			}
		});
		GridBagConstraints gbc_deleteCategory = new GridBagConstraints();
		gbc_deleteCategory.fill = GridBagConstraints.HORIZONTAL;
		gbc_deleteCategory.insets = new Insets(0, 0, 5, 5);
		gbc_deleteCategory.gridx = 2;
		gbc_deleteCategory.gridy = 0;
		getContentPane().add(deleteCategory, gbc_deleteCategory);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setResizeWeight(0.5);
		splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(15);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.gridheight = 2;
		gbc_splitPane.gridwidth = 3;
		gbc_splitPane.insets = new Insets(0, 0, 0, 5);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		getContentPane().add(splitPane, gbc_splitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		groupTop = new DefaultMutableTreeNode("Groups");
		groupTree = new JTree(groupTop);
		groupTree.setRootVisible(false);
		groupTree.setDragEnabled(true);
		groupTree.setDropMode(DropMode.ON);
		groupTree.setTransferHandler(new GroupDragger(model, this));
		groupTree.addFocusListener(new FocusListener()
		{

			@Override
			public void focusGained(FocusEvent e)
			{
				categoryTree.clearSelection();
			}

			@Override
			public void focusLost(FocusEvent e){}
			
		});
		groupTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		scrollPane.setViewportView(groupTree);
		
		JLabel lblnoCategoryList = new JLabel("\"No Category\" List");
		lblnoCategoryList.setHorizontalAlignment(SwingConstants.CENTER);
		scrollPane.setColumnHeaderView(lblnoCategoryList);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);
		
		categoryTop = new DefaultMutableTreeNode("Categories");
		categoryTree = new JTree(categoryTop);
		categoryTree.setRootVisible(false);
		categoryTree.setDragEnabled(true);
		categoryTree.setDropMode(DropMode.ON);
		categoryTree.setTransferHandler(new CategoryDragger(model,this));
		categoryTree.addFocusListener(new FocusListener()
		{

			@Override
			public void focusGained(FocusEvent e)
			{
				groupTree.clearSelection();
			}

			@Override
			public void focusLost(FocusEvent e){}
			
		});
		categoryTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		scrollPane_1.setViewportView(categoryTree);
		
		popupMenu = new JPopupMenu();
		addPopup(categoryTree, popupMenu);
		addPopup(groupTree, popupMenu);
		
		selCatTog = new JMenuItem("View Selected Categories on One Chart");
		selCatTog.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JMenuItem source = (JMenuItem) e.getSource();
				JTree tree = (JTree) ((JPopupMenu) source.getParent()).getInvoker();
				if(tree.getSelectionCount() > 0)
				{
					TreePath [] paths = tree.getSelectionPaths();
					ArrayList<Category> categories = new ArrayList<Category>();
					for(TreePath path : paths)
					{
						Category ob = (Category) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
						categories.add(ob);
					}
					Charts.showXYChartForCategoryTogether(categories, DefaultFrame.this);
				}
			}
		});
		popupMenu.add(selCatTog);
		
		selCatSep = new JMenuItem("View Selected Categories on Separte Charts");
		selCatSep.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JMenuItem source = (JMenuItem) e.getSource();
				JTree tree = (JTree) ((JPopupMenu) source.getParent()).getInvoker();
				if(tree.getSelectionCount() > 0)
				{
					TreePath [] paths = tree.getSelectionPaths();
					ArrayList<Category> categories = new ArrayList<Category>();
					for(TreePath path : paths)
					{
						Category ob = (Category) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
						categories.add(ob);
					}
					Charts.showXYChartForCategorySeparate(categories, DefaultFrame.this);
				}
			}
		});
		
		selGroupTog = new JMenuItem("View Selected Groups on One Chart");
		selGroupTog.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JMenuItem source = (JMenuItem) e.getSource();
				JTree tree = (JTree) ((JPopupMenu) source.getParent()).getInvoker();
				if(tree.getSelectionCount() > 0)
				{
					TreePath [] paths = tree.getSelectionPaths();
					ArrayList<Group> groups = new ArrayList<Group>();
					for(TreePath path : paths)
					{
						Group ob = (Group) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
						groups.add(ob);
					}
					Charts.showXYChartForGroupsTogether(groups, DefaultFrame.this);
				}
			}
		});
		
		selGroupSep = new JMenuItem("View Selected Groups on Separte Charts");
		selGroupSep.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JMenuItem source = (JMenuItem) e.getSource();
				JTree tree = (JTree) ((JPopupMenu) source.getParent()).getInvoker();
				if(tree.getSelectionCount() > 0)
				{
					TreePath [] paths = tree.getSelectionPaths();
					ArrayList<Group> groups = new ArrayList<Group>();
					for(TreePath path : paths)
					{
						Group ob = (Group) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
						groups.add(ob);
					}
					Charts.showXYChartForGroupsSeparate(groups, DefaultFrame.this);
				}
			}
		});
		
		JLabel lblcategoryList = new JLabel("\"Category\" List");
		lblcategoryList.setHorizontalAlignment(SwingConstants.CENTER);
		scrollPane_1.setColumnHeaderView(lblcategoryList);
		
		JPanel chartPanel = new JPanel();
		GridBagConstraints gbc_chartPanel = new GridBagConstraints();
		gbc_chartPanel.gridheight = 2;
		gbc_chartPanel.gridwidth = 3;
		gbc_chartPanel.insets = new Insets(0, 0, 5, 0);
		gbc_chartPanel.fill = GridBagConstraints.BOTH;
		gbc_chartPanel.gridx = 3;
		gbc_chartPanel.gridy = 0;
		getContentPane().add(chartPanel, gbc_chartPanel);
		chartPanel.setLayout(new BorderLayout(0, 0));
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		buttonPanel.add(panel_1);
		
		pieButton = new JRadioButton("Pie Chart");
		panel_1.add(pieButton);
		pieButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(pieButton.isSelected())
				{
					DefaultFrame.this.update(null,null);
				}
			}
		});
		buttonGroup.add(pieButton);
		
		pieButton.setSelected(true);
		histogramButton = new JRadioButton("Histogram");
		panel_1.add(histogramButton);
		histogramButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(histogramButton.isSelected())
				{
					DefaultFrame.this.update(null,null);
				}
			}
		});
		buttonGroup.add(histogramButton);
		timeButton = new JRadioButton("Time Chart");
		panel_1.add(timeButton);
		timeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(timeButton.isSelected())
				{
					DefaultFrame.this.update(null,null);
				}
			}
		});
		
		buttonGroup.add(timeButton);
		
		chartPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		buttonPanel.add(panel_2);
		
		includeButton = new JCheckBox("Include No Category");
		panel_2.add(includeButton);
		includeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DefaultFrame.this.update(null,null);
			}
		});
		includeButton.setSelected(true);
		
		chart = new JPanel();
		chart.setBorder(new LineBorder(new Color(0, 0, 0)));
		pie = new Charts(model);
		chartPanel.add(chart, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 3;
		gbc_panel.gridy = 2;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{150, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblTotalSpent = new JLabel("Total Spent:");
		GridBagConstraints gbc_lblTotalSpent = new GridBagConstraints();
		gbc_lblTotalSpent.anchor = GridBagConstraints.EAST;
		gbc_lblTotalSpent.fill = GridBagConstraints.VERTICAL;
		gbc_lblTotalSpent.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalSpent.gridx = 0;
		gbc_lblTotalSpent.gridy = 0;
		panel.add(lblTotalSpent, gbc_lblTotalSpent);
		
		totalSpent = new JLabel("");
		GridBagConstraints gbc_totalSpent = new GridBagConstraints();
		gbc_totalSpent.insets = new Insets(0, 0, 5, 0);
		gbc_totalSpent.gridx = 1;
		gbc_totalSpent.gridy = 0;
		panel.add(totalSpent, gbc_totalSpent);
		
		JLabel lblTimeFrame = new JLabel("Time Frame:");
		GridBagConstraints gbc_lblTimeFrame = new GridBagConstraints();
		gbc_lblTimeFrame.anchor = GridBagConstraints.EAST;
		gbc_lblTimeFrame.insets = new Insets(0, 0, 5, 5);
		gbc_lblTimeFrame.gridx = 0;
		gbc_lblTimeFrame.gridy = 1;
		panel.add(lblTimeFrame, gbc_lblTimeFrame);
		
		timeFrame = new JLabel("");
		GridBagConstraints gbc_timeFrame = new GridBagConstraints();
		gbc_timeFrame.insets = new Insets(0, 0, 5, 0);
		gbc_timeFrame.gridx = 1;
		gbc_timeFrame.gridy = 1;
		panel.add(timeFrame, gbc_timeFrame);
		
		JLabel lblAverageDaily = new JLabel("Average Daily:");
		GridBagConstraints gbc_lblAverageDaily = new GridBagConstraints();
		gbc_lblAverageDaily.anchor = GridBagConstraints.EAST;
		gbc_lblAverageDaily.insets = new Insets(0, 0, 5, 5);
		gbc_lblAverageDaily.gridx = 0;
		gbc_lblAverageDaily.gridy = 2;
		panel.add(lblAverageDaily, gbc_lblAverageDaily);
		
		avgDaily = new JLabel("");
		GridBagConstraints gbc_avgDaily = new GridBagConstraints();
		gbc_avgDaily.insets = new Insets(0, 0, 5, 0);
		gbc_avgDaily.gridx = 1;
		gbc_avgDaily.gridy = 2;
		panel.add(avgDaily, gbc_avgDaily);
		
		JLabel lblMostExpensiveGroup = new JLabel("Most Expensive Group:");
		GridBagConstraints gbc_lblMostExpensiveGroup = new GridBagConstraints();
		gbc_lblMostExpensiveGroup.anchor = GridBagConstraints.EAST;
		gbc_lblMostExpensiveGroup.insets = new Insets(0, 0, 5, 5);
		gbc_lblMostExpensiveGroup.gridx = 0;
		gbc_lblMostExpensiveGroup.gridy = 3;
		panel.add(lblMostExpensiveGroup, gbc_lblMostExpensiveGroup);
		
		mostExpGroup = new JLabel("");
		GridBagConstraints gbc_mostExpGroup = new GridBagConstraints();
		gbc_mostExpGroup.insets = new Insets(0, 0, 5, 0);
		gbc_mostExpGroup.gridx = 1;
		gbc_mostExpGroup.gridy = 3;
		panel.add(mostExpGroup, gbc_mostExpGroup);
		
		JLabel lblLeastExpensiveGroup = new JLabel("Least Expensive Group:");
		GridBagConstraints gbc_lblLeastExpensiveGroup = new GridBagConstraints();
		gbc_lblLeastExpensiveGroup.anchor = GridBagConstraints.EAST;
		gbc_lblLeastExpensiveGroup.insets = new Insets(0, 0, 5, 5);
		gbc_lblLeastExpensiveGroup.gridx = 0;
		gbc_lblLeastExpensiveGroup.gridy = 4;
		panel.add(lblLeastExpensiveGroup, gbc_lblLeastExpensiveGroup);
		
		leastExpGroup = new JLabel("");
		GridBagConstraints gbc_leastExpGroup = new GridBagConstraints();
		gbc_leastExpGroup.insets = new Insets(0, 0, 5, 0);
		gbc_leastExpGroup.gridx = 1;
		gbc_leastExpGroup.gridy = 4;
		panel.add(leastExpGroup, gbc_leastExpGroup);
		
		JLabel lblMostExpensivePurchase = new JLabel("Most Expensive Purchase:");
		GridBagConstraints gbc_lblMostExpensivePurchase = new GridBagConstraints();
		gbc_lblMostExpensivePurchase.anchor = GridBagConstraints.EAST;
		gbc_lblMostExpensivePurchase.insets = new Insets(0, 0, 5, 5);
		gbc_lblMostExpensivePurchase.gridx = 0;
		gbc_lblMostExpensivePurchase.gridy = 5;
		panel.add(lblMostExpensivePurchase, gbc_lblMostExpensivePurchase);
		
		mostExpGroupObject = new JLabel("");
		GridBagConstraints gbc_mostExpGroupObject = new GridBagConstraints();
		gbc_mostExpGroupObject.insets = new Insets(0, 0, 5, 0);
		gbc_mostExpGroupObject.gridx = 1;
		gbc_mostExpGroupObject.gridy = 5;
		panel.add(mostExpGroupObject, gbc_mostExpGroupObject);
		
		JLabel lblLeastExpensivePurchase = new JLabel("Least Expensive Purchase:");
		GridBagConstraints gbc_lblLeastExpensivePurchase = new GridBagConstraints();
		gbc_lblLeastExpensivePurchase.anchor = GridBagConstraints.EAST;
		gbc_lblLeastExpensivePurchase.insets = new Insets(0, 0, 5, 5);
		gbc_lblLeastExpensivePurchase.gridx = 0;
		gbc_lblLeastExpensivePurchase.gridy = 6;
		panel.add(lblLeastExpensivePurchase, gbc_lblLeastExpensivePurchase);
		
		leastExpGroupObject = new JLabel("");
		GridBagConstraints gbc_leastExpGroupObject = new GridBagConstraints();
		gbc_leastExpGroupObject.insets = new Insets(0, 0, 5, 0);
		gbc_leastExpGroupObject.gridx = 1;
		gbc_leastExpGroupObject.gridy = 6;
		panel.add(leastExpGroupObject, gbc_leastExpGroupObject);
		
		JLabel lblMostExpensiveCategory = new JLabel("Most Expensive Category:");
		GridBagConstraints gbc_lblMostExpensiveCategory = new GridBagConstraints();
		gbc_lblMostExpensiveCategory.anchor = GridBagConstraints.EAST;
		gbc_lblMostExpensiveCategory.insets = new Insets(0, 0, 5, 5);
		gbc_lblMostExpensiveCategory.gridx = 0;
		gbc_lblMostExpensiveCategory.gridy = 7;
		panel.add(lblMostExpensiveCategory, gbc_lblMostExpensiveCategory);
		
		mostExpCategory = new JLabel("");
		GridBagConstraints gbc_mostExpCategory = new GridBagConstraints();
		gbc_mostExpCategory.insets = new Insets(0, 0, 5, 0);
		gbc_mostExpCategory.gridx = 1;
		gbc_mostExpCategory.gridy = 7;
		panel.add(mostExpCategory, gbc_mostExpCategory);
		
		JLabel lblLeastExpensiveCategory = new JLabel("Least Expensive Category:");
		GridBagConstraints gbc_lblLeastExpensiveCategory = new GridBagConstraints();
		gbc_lblLeastExpensiveCategory.anchor = GridBagConstraints.EAST;
		gbc_lblLeastExpensiveCategory.insets = new Insets(0, 0, 0, 5);
		gbc_lblLeastExpensiveCategory.gridx = 0;
		gbc_lblLeastExpensiveCategory.gridy = 8;
		panel.add(lblLeastExpensiveCategory, gbc_lblLeastExpensiveCategory);
		
		leastExpCategory = new JLabel("");
		GridBagConstraints gbc_leastExpCategory = new GridBagConstraints();
		gbc_leastExpCategory.gridx = 1;
		gbc_leastExpCategory.gridy = 8;
		panel.add(leastExpCategory, gbc_leastExpCategory);
		
		this.setVisible(true);
		this.setSize(1500,700);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem menuOpenFile = new JMenuItem("Open File");
		menuOpenFile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(!FileStarter.fileReadFinished())
				{
					Thread thread = new Thread(new Runnable() {
					    public void run() {
					    	JFileChooser chooser = new JFileChooser();
							FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files","csv");
							chooser.setFileFilter(filter);
							int value = chooser.showOpenDialog(DefaultFrame.this);
							if(value == JFileChooser.APPROVE_OPTION)
							{
								File f = chooser.getSelectedFile();
								try {
									FileStarter.readInFile(f.getAbsolutePath(), model, DefaultFrame.this);
								} catch (FileNotFoundException e1) {
									JOptionPane.showMessageDialog(DefaultFrame.this.getContentPane(), "File Read in Error."
											, "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
					    }
					  });
					thread.start();
				}
				else
				{
					JOptionPane.showMessageDialog(DefaultFrame.this.getContentPane(), 
							"Implementation not finished. Must first restart program before\n"
							+ "loading in new file. Corrupts tree otherwise. Sorry:("
							, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnFile.add(menuOpenFile);
		
		JMenuItem menuExit = new JMenuItem("Exit");
		menuExit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		mnFile.add(menuExit);
		chart.add(new ChartPanel(pie.setUpPieChart(includeButton.isSelected())));
	}
	
	/**
	 * Adds a Category to the JTree on the right. Important
	 * adds a Category object based off the name looks it up
	 * with help from the Model.
	 * 
	 * @param name Category to add.
	 */
	private void addCategoryToJTree(String name)
	{
		if(name.length() > 0)
		{
			Category c = model.addCategory(name);
			if(c != null)
			{
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) categoryTop;
				DefaultMutableTreeNode temp = new DefaultMutableTreeNode(c);
				root.add(temp);
				((DefaultTreeModel)categoryTree.getModel()).reload();
			}
		}
	}
	
	/**
	 * Updates the JTree when the file is being read in and new Groups and Group objects
	 * are being added. Keeps them up to date. 
	 */
	private void updateGroupTree()
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTop;
		if(root.getChildCount() > 0)
		{
			DefaultMutableTreeNode groupSib = (DefaultMutableTreeNode) root.getChildAt(0);
			for(int a = 0; a <= groupSib.getSiblingCount(); ++a)
			{
				Group tempG = (Group)groupSib.getUserObject();
				if(groupSib.getChildCount() != tempG.getGroupObjectCount())
				{
					if(groupSib.getChildCount() > 0)
					{
						DefaultMutableTreeNode groupObjectSib = (DefaultMutableTreeNode) groupSib.getChildAt(0);
	
						ArrayList<GroupObject> treeGoList = new ArrayList<GroupObject>();
						for(int b = 0; b < groupObjectSib.getSiblingCount(); ++b)
						{
							treeGoList.add((GroupObject) groupObjectSib.getUserObject());
							if(groupObjectSib.getNextSibling() != null)
							{
								groupObjectSib = groupObjectSib.getNextSibling();
							}
						}
						
						ArrayList<GroupObject> missing = tempG.findMissing(treeGoList);
						for(GroupObject g : missing)
						{
							groupSib.add(new DefaultMutableTreeNode(g));
						}
					}
					else
					{
						ArrayList<GroupObject> missing = tempG.findMissing(new ArrayList<GroupObject>());
						for(GroupObject g : missing)
						{
							groupSib.add(new DefaultMutableTreeNode(g));
						}
					}
				}

				if(groupSib.getNextSibling() != null)
				{
					groupSib = groupSib.getNextSibling();
				}
			}
			((DefaultTreeModel)groupTree.getModel()).reload();
		}
	}

	/**
	 * Adds a Group to the JTree on the right.
	 * 
	 * @param g Group to add to JTree.
	 */
	private void addGroup(Group g)
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTop;
		root.add(new DefaultMutableTreeNode(g));
		((DefaultTreeModel)groupTree.getModel()).reload();
	}
	
	/**
	 * Used to keep this DefaultFrame up to date!
	 */
	@Override
	public void update(Observable o, Object arg)
	{
		update(arg);
	}
	
	/**
	 * Updates the Jframe.
	 * 
	 * @param arg Any new Groups to add.
	 */
	private void update(Object arg)
	{
		if(arg instanceof Group)
		{
			addGroup((Group)arg);
		}
		updateGroupTree();
		if(FileStarter.fileReadFinished())
		{
			updateChart();
			updateStats();
		}
		revalidate();
		repaint();
	}
	
	/**
	 * Used to update the chart on the DefaultFrame.
	 */
	private void updateChart()
	{
		chart.removeAll();
		if(pieButton.isSelected())
		{
			chart.add(new ChartPanel(pie.setUpPieChart(includeButton.isSelected()))
			{
		        @Override
		        public Dimension getPreferredSize()
		        {
		        	Dimension d = chart.getSize();
		        	d.setSize(d.getWidth() - 40, d.getHeight() - 10);
		            return d;
		        }
			});
		}
		else if(histogramButton.isSelected())
		{
			chart.add(new ChartPanel(pie.setUpHistogramChart(includeButton.isSelected()))
			{
		        @Override
		        public Dimension getPreferredSize()
		        {
		        	Dimension d = chart.getSize();
		        	d.setSize(d.getWidth() - 40, d.getHeight() - 10);
		            return d;
		        }
			});
		}
		else if(timeButton.isSelected())
		{
			chart.add(new ChartPanel(pie.setUpXYChart(includeButton.isSelected()))
			{
		        @Override
		        public Dimension getPreferredSize()
		        {
		        	Dimension d = chart.getSize();
		        	d.setSize(d.getWidth() - 40, d.getHeight() - 10);
		            return d;
		        }
			});
			
		}
	}
	
	/**
	 * Used to Update the Stats on this Default Frame. 
	 */
	private void updateStats()
	{
		DecimalFormat decimal = new DecimalFormat("####0.00");
		ArrayList<Group> groups = model.getAllGroups();
		
		//update total spent
		double total = 0;
		for(Group g : groups)
		{
			total += g.getTotalGroupAmount();
		}
		totalSpent.setText("$" + decimal.format(total));
		
		//update time frame
		Calendar [] c = groups.get(0).getStartEndDate();
		for(Group g : groups)
		{
			Calendar [] tempC = g.getStartEndDate();
			if(tempC[0].compareTo(c[0]) < 0)
			{
				c[0] = tempC[0];
			}
			if(tempC[1].compareTo(c[1]) > 0)
			{
				c[1] = tempC[1];
			}
		}
		timeFrame.setText("From: " + GroupObject.getDateToString(c[0], null) + " To: " + GroupObject.getDateToString(c[1], null));
		
		//update average daily
		long diff = c[1].getTime().getTime()-c[0].getTime().getTime();
		avgDaily.setText("$" + decimal.format(total/TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
		
	    //getting data for group and groupobjects
	    Group mostG = groups.get(0);
	    Group leastG = groups.get(0);
	    GroupObject mostGOb = groups.get(0).getGroupObject(0);
	    GroupObject leastGOb = groups.get(0).getGroupObject(0);
		for(Group g : groups)
		{
			if(g.getTotalGroupAmount() > mostG.getTotalGroupAmount())
			{
				mostG = g;
			}
			else if(g.getTotalGroupAmount() < leastG.getTotalGroupAmount())
			{
				leastG = g;
			}
			
			if(g.getMost().getAmount() > mostG.getMost().getAmount())
			{
				mostGOb = g.getMost();
			}
			else if(g.getLeast().getAmount() < leastG.getLeast().getAmount())
			{
				leastGOb = g.getLeast();
			}
		}
	    
		//update most expensive group
		mostExpGroup.setText(mostG.getName() + " $" + decimal.format(mostG.getTotalGroupAmount()));
		
		//update least expensive group
		leastExpGroup.setText(leastG.getName() + " $" + decimal.format(leastG.getTotalGroupAmount()));
		
		//update most expensive group object
		mostExpGroupObject.setText(mostGOb.getName() + " $" + decimal.format(mostGOb.getAmount()));

		//update least expensive group object
		leastExpGroupObject.setText(leastGOb.getName() + " $" + decimal.format(leastGOb.getAmount()));

		//update the high and low values for categories
		ArrayList<Category> categories = model.getAllCategories();
		String mostCStr = "-----";
		String leastCStr = "-----";
		if(categories.size() > 0)
		{
			ArrayList<Category> cat = model.getAllCategories();
			
			Collections.sort(cat, new CategoryAmountComparator());
			
			Category mostC = cat.get(0);
			Category leastC = cat.get(cat.size()-1);
			for(int i = cat.size()-1; i >= 0; --i)
			{
				if(i == 0)
				{
					leastC = null;
					break;
				}
				if(cat.get(i).getTotalAmount() != 0.0)
				{
					leastC = cat.get(i);
					break;
				}
			}
			
			if(mostC != null && mostC.getTotalAmount() != 0.0)
				mostCStr = mostC.getName() + " $" + decimal.format(mostC.getTotalAmount());

			if(leastC != null && leastC.getTotalAmount() != 0.0)
				leastCStr = leastC.getName() + " $" + decimal.format(leastC.getTotalAmount());
		}

		//update most expensive cat
		mostExpCategory.setText(mostCStr);
		
		//update least expensive cat
		leastExpCategory.setText(leastCStr);
	}
	
	/**
	 * Used for the right click PoppupMenu on the JTrees.
	 * 
	 * @param component Selected component.
	 * @param popup Popup.
	 */
	private void addPopup(Component component, final JPopupMenu popup)
	{
		component.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e){}
			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					JTree tree = (JTree) e.getComponent();
					if(tree.getSelectionCount() > 0)
					{
						int category = 0, group = 0;
						TreePath [] paths = tree.getSelectionPaths();
						for(TreePath path : paths)
						{
							Object ob = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
							if(ob instanceof Category)
							{
								++category;
							}
							else if(ob instanceof Group)
							{
								++group;
							}
							else
							{
								JOptionPane.showMessageDialog(DefaultFrame.this.getContentPane(), "Must either select"
										+ "group or category. You can NOT include a GroupObject (A purchase under a group\n"
										+ "of purchases)!"
										, "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						if(category  > 0 && group == 0)
						{
							popupMenu.removeAll();
							selCatTog.setText("View Selected Category");
							if(category > 1)
							{
								popupMenu.add(selCatSep);
								selCatTog.setText("View Selected Categories on One Chart");
							}
							popupMenu.add(selCatTog);
							showMenu(e);
						}
						else if(category == 0 && group > 0)
						{
							popupMenu.removeAll();
							if(group > 1)
							{
								popupMenu.add(selGroupSep);
								selGroupTog.setText("View Selected Groups on One Chart");
							}
							popupMenu.add(selGroupTog);
							showMenu(e);
						}
						else
						{
							JOptionPane.showMessageDialog(DefaultFrame.this.getContentPane(), "Must either select"
									+ "group or category. But can not select both types in order to see graph."
									, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
			private void showMenu(MouseEvent e)
			{
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
