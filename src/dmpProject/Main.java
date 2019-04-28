package dmpProject;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jfree.ui.RefineryUtilities;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Plot;
import ij.gui.ProfilePlot;
import ij.io.Opener;
import ij.plugin.Profiler;
import ij.process.ImageProcessor;
import ij.Prefs;

public class Main {
		
	static int Yb0, Yb1, diff;	///Yb0=left, Yb1=right
	static double peakFootConstantRel = 0.4; //0.55
	static double peakDiffMultiplicationConstant = 0;	
	static Vector<Peak> peakList;

	public static void main(String[] args) throws IOException {	
		
		String imageInput = "";
		for(int k = 1; k <= 79; k++){ //loop for every photo in folder
		
			Yb0 = 0 ;
			Yb1 = 0;
			diff = 0;
			peakList = new Vector<Peak>();
			
		if(k < 10)	
			imageInput = "C:\\Users\\User\\Desktop\\snapshots\\test_00" + k + ".jpg";
		else
			imageInput = "C:\\Users\\User\\Desktop\\snapshots\\test_0" + k + ".jpg";
		
		processImage(imageInput);//preprocess input
		String imageFilePath = "C:/Users/USER/Desktop/snapshots/sobelout.jpg";
			
		BufferedImage buff = ImageIO.read(new File(imageFilePath));
		BufferedImage originalImage = ImageIO.read(new File(imageInput));
		ImagePlus imp = IJ.openImage(imageFilePath);
		IJ.run(imp, "Rotate 90 Degrees Left", ""); //rotate picture to get vertical projection
		imp.setRoi(0,0,imp.getWidth(),imp.getHeight());
		ProfilePlot pp = new ProfilePlot(imp); //vertical projection
//		pp.createWindow();//graph of vertical projection
//		imp.show();

		//find peak index
		int maxIndex = 0;
		
		double[] profileData = pp.getProfile(); 
		
		
		for(int j = 0; j < 3; j++){
			double py = 0; //y value of peak
			
			for(int i = 0; i < profileData.length; i++){
				if(allowedInterval(peakList, i)){
					if(profileData[i] >= py){
						py = profileData[i];
						maxIndex = i;
				}
	//			System.out.println(profileData[i]);
				}
			}
			System.out.println(j + "--maxIndex: "+ maxIndex + " max py: " + py);
			
			//find Yb0
			int index=maxIndex;
	        for (int i=maxIndex; i>=0; i--) {
	            index = i;
	            if (profileData[index] < peakFootConstantRel*profileData[maxIndex] ) break;
	        }
	      Yb0 =  Math.max(0,index);
	 
			      
			 //find Yb1
	        for (int i=maxIndex; i<profileData.length; i++) {
	            index = i;
	            if (profileData[index] < peakFootConstantRel*profileData[maxIndex] ) break;
	        }
	       Yb1 = Math.min(profileData.length, index);
	       
	       diff = Yb1-Yb0;
	       
	       Yb0 -= peakDiffMultiplicationConstant * diff;   /*CONSTANT*/
           Yb1 += peakDiffMultiplicationConstant * diff;       
	      
           if(Yb0 >= 0 && Yb1 <= profileData.length){
        	   
	           Peak peak = new Peak(maxIndex, Yb0, Yb1);
	           peakList.add(peak);
	           System.out.println("L: " + Yb0 + " R: " + Yb1 + "\n");
	           imp.setRoi(Yb0,0,diff,imp.getHeight());
		          	
		   		BufferedImage band = originalImage.getSubimage(0, Yb0, buff.getWidth(), diff);
		   		File outputfile = new File("");
		   	if(diff>1)	{
		   		outputfile = new File("C:/Users/USER/Desktop/snapshots/output/" + k + "-band"  + j + ".jpg");
		   		ImageIO.write(band, "jpg", outputfile);
		   	}
		   		System.out.println("diff: " + diff);
		   		
//		   		if(diff > 1)
//		   			showImage(band, "Band ");
		   }
		}
		}
	}
	
	private static boolean allowedInterval(Vector<Peak> peaks, int i) {
		// TODO Auto-generated method stub
		 for (Peak peak : peaks)
	            if (peak.getLeft() <= i && i <= peak.getRight()) return false;
	        return true;
	}

	public static void showImage(BufferedImage img, String title) throws IOException{

		ImageIcon icon = new ImageIcon(img);
		
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		int width = img.getWidth();
		int height = img.getHeight();
		frame.setSize(width, 100);
		System.out.println("size: " + img.getWidth() + " "+ img.getHeight());
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		frame.setTitle(title);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public static void processImage(String imageFile) throws IOException{
        String filename = imageFile;
        File file = new File(filename);
        BufferedImage image = ImageIO.read(file);
        int x = image.getWidth();
        int y = image.getHeight();

        int maxGval = 0;
        int[][] edgeColors = new int[x][y];
        int maxGradient = -1;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {

                int val00 = getGrayScale(image.getRGB(i - 1, j - 1));
                int val01 = getGrayScale(image.getRGB(i - 1, j));
                int val02 = getGrayScale(image.getRGB(i - 1, j + 1));

                int val10 = getGrayScale(image.getRGB(i, j - 1));
                int val11 = getGrayScale(image.getRGB(i, j));
                int val12 = getGrayScale(image.getRGB(i, j + 1));

                int val20 = getGrayScale(image.getRGB(i + 1, j - 1));
                int val21 = getGrayScale(image.getRGB(i + 1, j));
                int val22 = getGrayScale(image.getRGB(i + 1, j + 1));

                int gy =  ((-1 * val00) + (-2 * val01) + (-1 * val02))
                        + ((0 * val10) + (0 * val11) + (0 * val12))
                        + ((1 * val20) + (2 * val21) + (1 * val22));

                double gval = Math.sqrt((gy * gy));
                int g = (int) gval;

                if(maxGradient < g) {
                    maxGradient = g;
                }
                edgeColors[i][j] = g;
            }
        }

        double scale = 255.0 / maxGradient;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {
                int edgeColor = edgeColors[i][j];
                edgeColor = (int)(edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                image.setRGB(i, j, edgeColor);
            }
        }
        
        
         //Sobel Operator + Grey Scale
         JFrame editorFrame = new JFrame("Sobel Operator + Grey Scale");
	     editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	 
	     ImageIcon imageIcon = new ImageIcon( image);
	     JLabel jLabel = new JLabel();
	     jLabel.setIcon(imageIcon);
	     editorFrame.getContentPane().add(jLabel, BorderLayout.CENTER);

	     editorFrame.pack();
	     editorFrame.setLocationRelativeTo(null);
//	     editorFrame.setVisible(true);
       
         File outputfile = new File("C:/Users/USER/Desktop/snapshots/sobelout.jpg");
         ImageIO.write(image, "jpg", outputfile);
	}
	
    public static int  getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        int gray = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
        return gray;
   }
   

}
