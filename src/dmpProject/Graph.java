package dmpProject;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class Graph extends ApplicationFrame {
	
	
	public Graph(String chartTitle, int value[], String type){
		super( "Histogram" );      
		
		if(type.equals("vertical")){
		
			JFreeChart barChart = ChartFactory.createBarChart(
					chartTitle,           
					"bin",            
					"freq",            
					createDataset(value),          
					PlotOrientation.VERTICAL,           
					true, true, false);
	
			barChart.setBackgroundPaint(Color.white);
	
			CategoryPlot plot = barChart.getCategoryPlot();
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
	
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setDrawBarOutline(false);
			
			final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray);
			for(int i=0; i<256; i++)
			renderer.setSeriesPaint(i, gp0);
			
			barChart.removeLegend();
			
			ChartPanel chartPanel = new ChartPanel( barChart );        
			chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
			setContentPane( chartPanel ); 
		}
		
		else if(type.equals("horizontal")){
			JFreeChart barChart = ChartFactory.createBarChart(
					chartTitle,           
					"bin",            
					"freq",            
					createDataset(value),          
					PlotOrientation.HORIZONTAL,           
					true, true, false);
	
			barChart.setBackgroundPaint(Color.white);
	
			CategoryPlot plot = barChart.getCategoryPlot();
			plot.setBackgroundPaint(Color.lightGray);
			plot.setDomainGridlinePaint(Color.white);
			plot.setRangeGridlinePaint(Color.white);
	
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setDrawBarOutline(false);
			
			final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray);
			for(int i=0; i<256; i++)
			renderer.setSeriesPaint(i, gp0);
			
			barChart.removeLegend();
			
			ChartPanel chartPanel = new ChartPanel( barChart );        
			chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
			setContentPane( chartPanel ); 
		}
	}

    static public class ProbabilityDistributor {
        float center;
        float power;
        int leftMargin;
        int rightMargin;
        public ProbabilityDistributor(float center, float power, int leftMargin, int rightMargin) {
            this.center = center;
            this.power = power;
            this.leftMargin = Math.max(1,leftMargin);
            this.rightMargin = Math.max(1,rightMargin);
        }
        
        private float distributionFunction(float value, float positionPercentage) {
            return value * (1 - this.power * Math.abs(positionPercentage - this.center) );
        }
        
        public Vector<Integer> distribute(Vector<Integer> peaks) {
            Vector<Integer> distributedPeaks = new Vector<Integer>();
            for (int i=0; i<peaks.size(); i++) {
                if (i < leftMargin || i > peaks.size() - rightMargin) {
                    distributedPeaks.add((int) 0f);
                } else {
                    distributedPeaks.add((int) distributionFunction(peaks.elementAt(i),
                            ((float)i/peaks.size())
                            )
                            );
                }
            }
            
            return distributedPeaks;
        }
    }
    
	private CategoryDataset createDataset( int value[]) {
		
		
		final DefaultCategoryDataset dataset = 
				new DefaultCategoryDataset( );  
	
		for(int i=0; i<value.length; i++) {			
			dataset.addValue(value[i],String.valueOf(i),"");
		}
		
		return dataset; 
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
