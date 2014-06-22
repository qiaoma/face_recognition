
package edu.calstatela.cs560.qiao.hw4;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class DrawingCanvas extends JPanel{

	private static final long serialVersionUID = 1L;
	
	BufferedImage image;
	
	public DrawingCanvas(int[][] pixels, int rows, int cols){
	
		image = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_GRAY);
		int pixel = 0;
		
		for (int r = 0; r < rows; r++){
			int col = 0;
   	   		for (int c = 0; c < cols; c++){   	   			
   	   			if(c+11 < cols){
   	   				pixel = pixels[r][c+11];
   	   			}
   	   			else{
   	   				pixel = pixels[r][col];
   	   				col++;
   	   			}
   	   			image.setRGB(c, r, ((255<<24) | (pixel << 16) | (pixel << 8) | pixel));	
   	   		}
		}
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null); 
	}
}
