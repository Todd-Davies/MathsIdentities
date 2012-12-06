import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * A program to convert flashcards written in LATEX into printable .png files 
 * @author Todd Davies <todd434@gmail.com>
 * TODO: comment and clean the code
 */
public class Main {
	
	private final static int TILEWIDTH = 450;
	private final static int TILEHEIGHT = 150;
	private final static int COLUMNS = 3;	
	private static ArrayList<Question> questions = new ArrayList<Question>();
	private final static String URL = "github.com/Todd-Davies/MathsIdentities";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		questions = new ArrayList<Question>();
		
		try {
			questions = parseInputQuestions("questions.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int rows = ((questions.size()%COLUMNS)==0 ? (questions.size()/COLUMNS) : (questions.size()/COLUMNS)+1 );

		//Print the first side
		BufferedImage firstSide = new BufferedImage(TILEWIDTH*COLUMNS, TILEHEIGHT*rows, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics firstSideGraphics = firstSide.getGraphics();
		int row = 0;
		//Print first side: 
		for(int i=0;i<questions.size();i++) {
			BufferedImage tile = printTextToImage(questions.get(i).getSide1());
			firstSideGraphics.drawImage(tile, TILEWIDTH*(i%COLUMNS), TILEHEIGHT*row, null);
			if((i%COLUMNS)==2) {
				row++;
			}
		}
		
		BufferedImage secondSide = new BufferedImage(TILEWIDTH*COLUMNS, TILEHEIGHT*rows, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics secondSideGraphics = secondSide.getGraphics();
		//Print second side
		row = 0;
		for(int i=0;i<questions.size();i++) {
			BufferedImage tile = printTextToImage(questions.get(i).getSide2());
			//Swap the first and last columns
			int widthPos = COLUMNS - ((i%COLUMNS)+1);
			secondSideGraphics.drawImage(tile, TILEWIDTH*widthPos, TILEHEIGHT*row, null);
			if((i%COLUMNS)==2) {
				row++;
			}
		}
		
		try {			
			ImageIO.write(firstSide, "PNG", new File("side1.png"));
			ImageIO.write(secondSide, "PNG", new File("side2.png"));
		} catch (IOException e) {
			System.out.println("Failed to write images");
		}
		
		
		
	}

	private static ArrayList<Question> parseInputQuestions(String questionFile) throws Exception {
		ArrayList<Question> output = new ArrayList<Question>();
		BufferedReader br = new BufferedReader(new FileReader(questionFile));
	    try {
	        String line = br.readLine();
	        boolean question = true;
	        Question thisQuestion = new Question("","");
	        while (line != null) {
	        	if(line.subSequence(0, 1).equals("Q")) {
	        		question = true;
	        	} else {
	        		question = false;
	        	}
	        	if(question) {
	        		thisQuestion.setSide1(line.substring(2).trim());
	        	} else {
	        		thisQuestion.setSide2(line.substring(2).trim());
	        		output.add(thisQuestion);
	        		thisQuestion = new Question("","");
	        	}
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	    output.add(getCoverQuestion());
	    return output;
	}
	
	private static Question getCoverQuestion() {
		return new Question("{\\small \\copyright Todd Davies 2012}", "{\\tiny " + URL + "}");
	}

	private static BufferedImage printTextToImage(String s) {
		TeXFormula fomule = new TeXFormula(s);
		TeXIcon ti = fomule.createTeXIcon(
		TeXConstants.STYLE_DISPLAY, 40);
		int marginWidth = ((TILEWIDTH - ti.getIconWidth())/2);
		int marginHeight = ((TILEHEIGHT - ti.getIconHeight())/2);
		BufferedImage b = new BufferedImage(TILEWIDTH, TILEHEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = b.getGraphics();
		ti.paintIcon(new JLabel(), g, marginWidth, marginHeight);
		return b;
	}

}
