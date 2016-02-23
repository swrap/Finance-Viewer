import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Class used to create charts as well as JFrames of data from the model.
 * This class uses the JFreeChart .jar file!!!!!!!!!!!!!!!!!!!! Thanks
 * to them for the awesome charts!
 * 
 * @author Jesse Saran
 *
 */
public class Charts
{
	//default for the "No Category" group
	private static Color NO_CATEGORY_COLOR = Color.yellow;
	//instance of the main model
	private Model model;
	
	/**
	 * Instance of the model used for updating JFrame Content.
	 * 
	 * @param model
	 */
	public Charts(Model model)
	{
		this.model = model;
	}
	
	/**
	 * Will set up a PieChart of the Category data. Sums up all amounts.
	 * 
	 * @param includeNoCat Whether or not to include the "No Category" chart.
	 * @return Returns a pie chart of the given data.
	 */
	public JFreeChart setUpPieChart(boolean includeNoCat)
	{
	    DefaultPieDataset dataset = new DefaultPieDataset();
	    
	    if(includeNoCat)
	    {
	    	double total = 0;
		    for(Group g : model.getAllGroups())
		    {
		    	if(!g.isInCategory())
		    		total += g.getTotalGroupAmount();
		    }
		    dataset.setValue("No Category", total);
	    }
	    
	    for(Category c : model.getAllCategories())
	    {
	    	dataset.setValue(c.getName(), c.getTotalAmount());
	    }
		    
		JFreeChart pieChart = ChartFactory.createPieChart("No Category and Categories", dataset);
		PiePlot plot = (PiePlot) pieChart.getPlot();
		
		plot.setSectionPaint("No Category", NO_CATEGORY_COLOR);
		
	    for(Category c : model.getAllCategories())
	    {
	    	plot.setSectionPaint(c.getName(), c.getColor());
	    }
	    
	    StandardPieToolTipGenerator gen = new StandardPieToolTipGenerator("{0} ${1} ({2})",
	    		new DecimalFormat("#.##"), new DecimalFormat("0%"));
		plot.setToolTipGenerator(gen);
		plot.setBackgroundPaint(DefaultFrame.DEFUALT_BACKGROUND_COLOR);
		return pieChart;
	}
	
	/**
	 * Will set up a Histogram of the Category data. Sums up all amounts.
	 * 
	 * @param includeNoCat Whether or not to include the "No Category" chart.
	 * @return Returns a histogram chart of the given data.
	 */
	public JFreeChart setUpHistogramChart(boolean includeNoCat)
	{
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		String series = "No Category";
		String cat = "All Data";
		
		if(includeNoCat)
		{
		    double total = 0;
		    for(Group g : model.getAllGroups())
		    {
		    	if(!g.isInCategory())
		    		total += g.getTotalGroupAmount();
		    }
		    data.addValue(total, series, cat);
		}
	    		
	    int count = 2;
	    for(Category c : model.getAllCategories())
	    {
		    data.addValue(c.getTotalAmount(), c.getName(), cat);
		    count++;
	    }
	    JFreeChart chart = ChartFactory.createBarChart(
	    		"\"No Category\" and Category data",
	    		"Category","$",data,PlotOrientation.VERTICAL,
	    		true, true, false);
	    CategoryPlot p = chart.getCategoryPlot();
	    BarRenderer r = (BarRenderer) p.getRenderer();
	    StandardCategoryToolTipGenerator gen = new StandardCategoryToolTipGenerator("{0}, {1}, {3}",
	    		new DecimalFormat("#.##"));
	    r.setBaseToolTipGenerator(gen);
	    return chart;
	}
	
	/**
	 * Will set up a XYChart of the Category data. Sums up all amounts.
	 * 
	 * @param includeNoCat Whether or not to include the "No Category" chart.
	 * @return Returns a xy chart of the given data.
	 */
	public JFreeChart setUpXYChart(boolean includeNoCat)
	{
		ArrayList<TimeSeries> arrT = new ArrayList<TimeSeries>();
		
		if(includeNoCat)
		{
			TimeSeries ser = new TimeSeries("No Category");
		    for(Group g : model.getAllGroups())
		    {
		    	if(!g.isInCategory())
		    	{
			    	for(GroupObject go : g.getGroupObjects())
			    	{
			    		TimeSeriesDataItem item = ser.getDataItem(new Day(go.getDate().getTime()));
			    		if(item == null)
			    		{
			    			ser.add(new Day(go.getDate().getTime()), go.getAmount());
			    		}
			    		else
			    		{
			    			ser.addOrUpdate(item.getPeriod(), item.getValue().doubleValue() + go.getAmount());
			    		}
			    	}
		    	}
		    }
	    	arrT.add(ser);
		}
		
		for(Category c : model.getAllCategories())
		{
	    	TimeSeries ser = new TimeSeries(c.getName());
		    for(Group g : c.getAllGroups())
		    {
		    	for(GroupObject go : g.getGroupObjects())
		    	{
		    		TimeSeriesDataItem item = ser.getDataItem(new Day(go.getDate().getTime()));
		    		if(item == null)
		    		{
		    			ser.add(new Day(go.getDate().getTime()), go.getAmount());
		    		}
		    		else
		    		{
		    			ser.addOrUpdate(item.getPeriod(), item.getValue().doubleValue() + go.getAmount());
		    		}
		    	}
		    }
	    	arrT.add(ser);
		}
	    
	    TimeSeriesCollection data = new TimeSeriesCollection();
	    for(TimeSeries ts : arrT)
	    {
		    data.addSeries(ts);
	    }
	    
	    JFreeChart chart = ChartFactory.createTimeSeriesChart("Time", "Time", "Amount", data);
	    
	    XYPlot plot = (XYPlot) chart.getPlot();
	    XYItemRenderer ren = plot.getRenderer();
	    StandardXYToolTipGenerator tooltip = new StandardXYToolTipGenerator("{0} {1} {2}",
	    		new SimpleDateFormat("yyyy-MM-dd"),new DecimalFormat("#.##"));
	    ren.setBaseToolTipGenerator(tooltip);
	    
	    return chart;
	}
	
	/**
	 * Creates one graph of XYChart of chart data. Puts all Categories into one JFrame.
	 * 
	 * @param cat Categories that will be in the chart.
	 * @param f Frame that the chart is related to.
	 */
	public static void showXYChartForCategoryTogether(ArrayList<Category> cat, DefaultFrame f)
	{
		ArrayList<TimeSeries> arrT = new ArrayList<TimeSeries>();

		for(Category c : cat)
		{
	    	TimeSeries ser = new TimeSeries(c.getName());
		    for(Group g : c.getAllGroups())
		    {
		    	for(GroupObject go : g.getGroupObjects())
		    	{
		    		TimeSeriesDataItem item = ser.getDataItem(new Day(go.getDate().getTime()));
		    		if(item == null)
		    		{
		    			ser.add(new Day(go.getDate().getTime()), go.getAmount());
		    		}
		    		else
		    		{
		    			ser.addOrUpdate(item.getPeriod(), item.getValue().doubleValue() + go.getAmount());
		    		}
		    	}
		    }
	    	arrT.add(ser);
		}
	    
	    TimeSeriesCollection data = new TimeSeriesCollection();
	    for(TimeSeries ts : arrT)
	    {
		    data.addSeries(ts);
	    }
	    
	    JFreeChart chart = ChartFactory.createTimeSeriesChart("Time", "Time", "Amount", data);
	    
	    XYPlot plot = (XYPlot) chart.getPlot();
	    XYItemRenderer ren = plot.getRenderer();
	    StandardXYToolTipGenerator tooltip = new StandardXYToolTipGenerator("{0} {1} {2}",
	    		new SimpleDateFormat("yyyy-MM-dd"),new DecimalFormat("#.##"));
	    ren.setBaseToolTipGenerator(tooltip);
		plot.setBackgroundPaint(DefaultFrame.DEFUALT_BACKGROUND_COLOR);
		
		JFrame frame = new JFrame("Multiple Categories XY Plot");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(f);
		
		frame.getContentPane().add(new ChartPanel(chart));
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir") + "\\Logo.png"));
		frame.setVisible(true);
	}
	
	/**
	 * Creates multiple graphs of XYChart of chart data. Puts Categories into separate JFrames.
	 * 
	 * @param cat Categories that will make up the separate JFrames.
	 * @param f Frame that the chart is related to.
	 */
	public static void showXYChartForCategorySeparate(ArrayList<Category> cat, DefaultFrame f)
	{
		for(Category c : cat)
		{
			ArrayList<TimeSeries> arrT = new ArrayList<TimeSeries>();


	    	TimeSeries ser = new TimeSeries(c.getName());
		    for(Group g : c.getAllGroups())
		    {
		    	for(GroupObject go : g.getGroupObjects())
		    	{
		    		TimeSeriesDataItem item = ser.getDataItem(new Day(go.getDate().getTime()));
		    		if(item == null)
		    		{
		    			ser.add(new Day(go.getDate().getTime()), go.getAmount());
		    		}
		    		else
		    		{
		    			ser.addOrUpdate(item.getPeriod(), item.getValue().doubleValue() + go.getAmount());
		    		}
		    	}
		    }
	    	arrT.add(ser);
	    
		    TimeSeriesCollection data = new TimeSeriesCollection();
		    for(TimeSeries ts : arrT)
		    {
			    data.addSeries(ts);
		    }
		    
		    JFreeChart chart = ChartFactory.createTimeSeriesChart("Time", "Time", "Amount", data);
		    
		    XYPlot plot = (XYPlot) chart.getPlot();
		    XYItemRenderer ren = plot.getRenderer();
		    StandardXYToolTipGenerator tooltip = new StandardXYToolTipGenerator("{0} {1} {2}",
		    		new SimpleDateFormat("yyyy-MM-dd"),new DecimalFormat("#.##"));
		    ren.setBaseToolTipGenerator(tooltip);
			plot.setBackgroundPaint(DefaultFrame.DEFUALT_BACKGROUND_COLOR);
			
			JFrame frame = new JFrame("Category: " + c.getName() + " XY Plot");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setSize(300, 300);
			frame.setLocationRelativeTo(f);
			
			frame.getContentPane().add(new ChartPanel(chart));
			frame.setIconImage(Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir") + "\\Logo.png"));
			frame.setVisible(true);
		}
	}
	
	/**
	 * Creates one graph of XYChart of chart data. Puts all Groups into one JFrame.
	 * 
	 * @param groups Groups that will be in the JFrame.
	 * @param f Frame that the chart is related to.
	 */
	public static void showXYChartForGroupsTogether(ArrayList<Group> groups, DefaultFrame f)
	{
		ArrayList<TimeSeries> arrT = new ArrayList<TimeSeries>();

	    for(Group g : groups)
	    {
	    	TimeSeries ser = new TimeSeries(g.getName());
	    	for(GroupObject go : g.getGroupObjects())
	    	{
	    		TimeSeriesDataItem item = ser.getDataItem(new Day(go.getDate().getTime()));
	    		if(item == null)
	    		{
	    			ser.add(new Day(go.getDate().getTime()), go.getAmount());
	    		}
	    		else
	    		{
	    			ser.addOrUpdate(item.getPeriod(), item.getValue().doubleValue() + go.getAmount());
	    		}
	    	}
	    	arrT.add(ser);
	    }
	    
	    TimeSeriesCollection data = new TimeSeriesCollection();
	    for(TimeSeries ts : arrT)
	    {
		    data.addSeries(ts);
	    }
	    
	    JFreeChart chart = ChartFactory.createTimeSeriesChart("Time", "Time", "Amount", data);
	    
	    XYPlot plot = (XYPlot) chart.getPlot();
	    XYItemRenderer ren = plot.getRenderer();
	    StandardXYToolTipGenerator tooltip = new StandardXYToolTipGenerator("{0} {1} {2}",
	    		new SimpleDateFormat("yyyy-MM-dd"),new DecimalFormat("#.##"));
	    ren.setBaseToolTipGenerator(tooltip);
		plot.setBackgroundPaint(DefaultFrame.DEFUALT_BACKGROUND_COLOR);
		
		JFrame frame = new JFrame("Multiple Groups XY Plot");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(f);
		
		frame.getContentPane().add(new ChartPanel(chart));
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir") + "\\Logo.png"));
		frame.setVisible(true);
	}
	
	/**
	 * Creates multiple graphs of XYChart of chart data. Puts Groups into separate JFrames.
	 * 
	 * @param cat Categories that will make up the separate JFrames.
	 * @param f Frame that the chart is related to.
	 */
	public static void showXYChartForGroupsSeparate(ArrayList<Group> groups, DefaultFrame f)
	{
		for(Group g : groups)
	    {
			ArrayList<TimeSeries> arrT = new ArrayList<TimeSeries>();
			
	    	TimeSeries ser = new TimeSeries(g.getName());
	    	for(GroupObject go : g.getGroupObjects())
	    	{
	    		TimeSeriesDataItem item = ser.getDataItem(new Day(go.getDate().getTime()));
	    		if(item == null)
	    		{
	    			ser.add(new Day(go.getDate().getTime()), go.getAmount());
	    		}
	    		else
	    		{
	    			ser.addOrUpdate(item.getPeriod(), item.getValue().doubleValue() + go.getAmount());
	    		}
	    	}
	    	arrT.add(ser);
		    
		    TimeSeriesCollection data = new TimeSeriesCollection();
		    for(TimeSeries ts : arrT)
		    {
			    data.addSeries(ts);
		    }
		    
		    JFreeChart chart = ChartFactory.createTimeSeriesChart("Time", "Time", "Amount", data);
		    
		    XYPlot plot = (XYPlot) chart.getPlot();
		    XYItemRenderer ren = plot.getRenderer();
		    StandardXYToolTipGenerator tooltip = new StandardXYToolTipGenerator("{0} {1} {2}",
		    		new SimpleDateFormat("yyyy-MM-dd"),new DecimalFormat("#.##"));
		    ren.setBaseToolTipGenerator(tooltip);
			plot.setBackgroundPaint(DefaultFrame.DEFUALT_BACKGROUND_COLOR);
			
			JFrame frame = new JFrame("Group: " + g.getName() + " XY Plot");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setSize(300, 300);
			frame.setLocationRelativeTo(f);
			
			frame.getContentPane().add(new ChartPanel(chart));
			frame.setIconImage(Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir") + "\\Logo.png"));
			frame.setVisible(true);
	    }
	}
}