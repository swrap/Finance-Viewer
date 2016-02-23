import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * This class is used to read in files and serves as the starting point from the program.
 * 
 * @author Jesse Saran
 *
 */
public class FileStarter
{
	//instance of the main model
	private static Model model;
	//used for groups no in the regex
	public static final String UNKNOWN = "No Name";
	//whether or not the file has been successfully read
	private static boolean fileReadDone = false;
	//total line length as well as the current line
	private static int count = 0, end = 0;
	
	/**
	 * Reads in the designated file as well as blocks user input till file is read.
	 * 
	 * @param filename Name of the file to read in.
	 * @param model Instance of the mode.
	 * @param frame Instance of the frame. Used for the JDialog Box.
	 * @throws FileNotFoundException Thrown if the file is not found.
	 */
	public static void readInFile(String filename, Model model, DefaultFrame frame) throws FileNotFoundException
	{
		fileReadDone = false;
		Scanner scan = new Scanner(new File(filename));
		count = 0;
		
		while(scan.hasNextLine())
		{
			scan.nextLine();
			count++;
		}
		
		int end = count;
		
		JDialog dialog = new JDialog(frame,true);
		dialog.getContentPane().setLayout(new GridLayout(2, 0, 0, 0));
		
		JLabel label = new JLabel("Loading File Please Wait: 0% done.");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		dialog.getContentPane().add(label);
		
		JProgressBar progressBar = new JProgressBar(0, end);
		dialog.getContentPane().add(progressBar);
		
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.setSize(300,200);
		dialog.setLocationRelativeTo(frame);
		dialog.setAlwaysOnTop(true);
		Thread th = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				dialog.setVisible(true);
			}	
		});
		th.start();
		
		count = 0;
		scan = new Scanner(new File(filename));
		while(scan.hasNextLine())
		{
			String line = scan.nextLine();
			
			//only does after reading last group in
			if(scan.hasNextLine() == false)
			{
				fileReadDone = true;
			}
			
			String [] temp = line.split(",");
			
			String groupName = makeGroupName(temp[1]);
			
			GroupObject go = new GroupObject(temp[0], temp[1], Double.parseDouble(temp[2]));
			Group tempG = model.addGroup(groupName);
			
			model.addGroupObject(groupName, go);
			count++;
			
			progressBar.setValue(count);
			DecimalFormat decimal = new DecimalFormat("####0.00");
			label.setText("Loading File Please Wait: " + decimal.format((double)(count)/((double)end)*100)+ "% done.");
		}
		scan.close();
		dialog.setVisible(false);
		dialog.dispose();
	}

	/**
	 * Used by the read in file to give the group names.
	 * 
	 * @param full The starting name.
	 * @return What the final name for the group will be.
	 */
	private static String makeGroupName(String full)
	{
		//regular expression can start with numbers but not end
		//needed to include 3 sisters
		Pattern p = Pattern.compile("^(\\d*)(\\s*)(\\D*)");
		Matcher m = p.matcher(full);
		if(m.find())
		{
			full = m.group();
			if(full.length() < 5)
			{
				System.err.println("Has No Name setting to UNKNOWN");
				full = FileStarter.UNKNOWN;
			}
		}
		return full;
	}

	/**
	 * Whether or not the file was successfully read.
	 * 
	 * @return State of the file reading.
	 */
	public static boolean fileReadFinished()
	{
		return fileReadDone;
	}
	
	/**
	 * Main for the program. Sets the model. Adds the observers to the model and starts
	 * the frame. Basically gets everything going :)
	 * 
	 * @param args Not used.
	 */
	public static void main(String [] args)
	{
		Model m = new Model();
		DefaultFrame f = new DefaultFrame(m);
		m.addObserver(f);
	}
}
