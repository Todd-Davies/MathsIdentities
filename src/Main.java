import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * A program to convert flashcards written in LATEX into printable .png files 
 * @author Todd Davies <todd434@gmail.com>
 */
public class Main {
	
	//Variables
	private final static int TILEWIDTH = 530;
	private final static int TILEHEIGHT = 150;
	private final static int TILEMARGIN = 10;
	private final static Color TILEMARGINCOLOR = Color.BLACK;
	private final static int COLUMNS = 3;	
	private final static String URL = "github.com/Todd-Davies/MathsIdentities";
	private final static String COPYRIGHT_NOTICE = "Todd Davies 2013";

	public static void main(String[] args) {
		ArrayList<Question> questions = new ArrayList<Question>();
		
		//Try reading the question file
		try {
			questions = parseInputQuestions("questions.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Stop if there's no questions
		if(questions.size()==0) {
			System.out.println("No questions found");
			return;
		}
		
		//Add the cover to the beginning of the question list
		questions.add(0, getCoverQuestion().setCornerImage("github-logo.png"));
		
		//Find out how many rows we need for our questions
		int rows = ((questions.size()%COLUMNS)==0 ? ((questions.size()<4) ? 1 : questions.size()/COLUMNS) : (questions.size()/COLUMNS)+1 );

		//Print the first side
		BufferedImage firstSide = new BufferedImage(TILEWIDTH*COLUMNS, TILEHEIGHT*rows, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics firstSideGraphics = firstSide.getGraphics();
		int row = 0;
		//Print first side: 
		for(int i=0;i<questions.size();i++) {
			Question q = questions.get(i);
			BufferedImage tile = printTextToImage(q.getSide1());
			//If we've got a logo, then write it to the image
			if(questions.get(i).getCornerImage()!=null) {
				try {
					Image img = null;
					img = ImageIO.read(new File(q.getCornerImage()));
					tile.getGraphics().drawImage(img, TILEWIDTH-TILEMARGIN-1-img.getWidth(null), TILEHEIGHT-TILEMARGIN-1-img.getHeight(null), null);
				} catch (IOException e) {
					System.out.println("Failed to draw logo image");
				}
			}
			//Draw the tile to the side
			firstSideGraphics.drawImage(tile, TILEWIDTH*(i%COLUMNS), TILEHEIGHT*row, null);
			//If we've reached the end of the row, then increment the row count
			if((i%COLUMNS)==(COLUMNS-1)) {
				row++;
			}
		}
		
		BufferedImage secondSide = new BufferedImage(TILEWIDTH*COLUMNS, TILEHEIGHT*rows, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics secondSideGraphics = secondSide.getGraphics();
		//Print second side
		row = 0;
		for(int i=0;i<questions.size();i++) {
			Question q = questions.get(i);
			BufferedImage tile = printTextToImage(q.getSide2());
			//Swap the first and last columns
			int widthPos = COLUMNS - ((i%COLUMNS)+1);
			//If we've got a logo, then write it to the image
			if(questions.get(i).getCornerImage()!=null) {
				try {
					Image img = null;
					img = ImageIO.read(new File(q.getCornerImage()));
					tile.getGraphics().drawImage(img, TILEWIDTH-TILEMARGIN-1-img.getWidth(null), TILEHEIGHT-TILEMARGIN-1-img.getHeight(null), null);
				} catch (IOException e) {
					System.out.println("Failed to draw logo image");
				}
			}
			//Draw the tile to the side
			secondSideGraphics.drawImage(tile, TILEWIDTH*widthPos, TILEHEIGHT*row, null);
			//If we've reached the end of the row, then increment the row count
			if((i%COLUMNS)==(COLUMNS-1)) {
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

	/**
	 * Reads the question file and parses it into questions
	 * @param questionFile the path of a text file containing questions
	 * @return an ArrayList of Questions
	 * @throws Exception when the file can't be read usually
	 */
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
	    return output;
	}
	
	/**
	 * Gets the 'cover question' for the set
	 */
	private static Question getCoverQuestion() {
		return new Question("{\\small \\copyright " + COPYRIGHT_NOTICE + "}", "{\\tiny " + URL + "}");
	}

	/**
	 * Prints the LATEX string into an image
	 * @param s the LATEX formatted string
	 * @return a BufferedImage of the question
	 */
	private static BufferedImage printTextToImage(String s) {
		TeXFormula fomule = new TeXFormula(s);
		TeXIcon ti = fomule.createTeXIcon(
		TeXConstants.STYLE_DISPLAY, 40);
		int marginWidth = ((TILEWIDTH - ti.getIconWidth())/2);
		int marginHeight = ((TILEHEIGHT - ti.getIconHeight())/2);
		BufferedImage b = new BufferedImage(TILEWIDTH, TILEHEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = b.getGraphics();
		g.setColor(TILEMARGINCOLOR);
		g.drawRect(TILEMARGIN/2, TILEMARGIN/2, TILEWIDTH-TILEMARGIN, TILEHEIGHT-TILEMARGIN);
		ti.paintIcon(new JLabel(), g, marginWidth, marginHeight);
		return b;
	}

}
