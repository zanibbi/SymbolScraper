/*
 * @Author: Parag Mali
 * 
 * This file reads the annotation data for GTDB dataset
 * https://github.com/uchidalab/GTDB-Dataset 
 */

package com.MathScraper.annotations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.MathScraper.annotations.data.Annotations;
import com.MathScraper.annotations.data.BoundingBox;
import com.MathScraper.annotations.data.CharData;
import com.MathScraper.annotations.data.Heading;
import com.MathScraper.annotations.data.Image;
import com.MathScraper.annotations.data.Line;
import com.MathScraper.annotations.data.LinkLabel;
import com.MathScraper.annotations.data.Sheet;
import com.MathScraper.annotations.data.Text;
import com.MathScraper.annotations.data.TextMode;
import com.MathScraper.constants.Constants;

public class AnnotationReader {
	
	public Annotations read(String filePath) throws FileNotFoundException, IOException {
		
		Heading heading = null;
	
		List<Sheet> sheets = new ArrayList<>();
	    List<Text> texts = new ArrayList<>();
		List<Image> images = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		List<CharData> chars = new ArrayList<>();
		
		int count = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    String line = null;
		    
		    line = br.readLine();
		    
		    // first line of the annotation file must be the format version
		    // For example, 
		    // Infty GT-Data Format Ver.1.1
		    if(!StringUtils.isEmpty(line)) {
		    		heading = new Heading(line);
	    		}
			
	    		Sheet currentSheet = null;
	    		Text currentText = null;
	    		Image currentImage = null;
	    		Line currentLine = null;
	    		CharData currentChar = null;

		    while ((line = br.readLine()) != null) {
		    		
		    		count = count + 1;
		    		
		    		if(count % 100 == 0) {
		    			System.out.println("Processing line " + count);
		    		}
		    	
		    		// process the line.
		    		String[] entries = StringUtils.split(line, Constants.CSV_SEPARATOR);
		    		
		    		switch(entries[0]) {
		    		
			    		case "Sheet":

			    			if(currentSheet != null) {
				    		
			    				processLine(currentLine, lines, chars);
				    			processText(currentText, lines, texts);
				    			processImage(currentImage, images);
				    			
				    			currentSheet.setImageAreas(images);
				    			currentSheet.setTextAreas(texts);
			    				
				    			sheets.add(currentSheet);
				    			
				    			chars = new ArrayList<>();
				    			currentLine = null;

				    			lines = new ArrayList<>();
				    			currentText = null;
				    			
				    			currentImage = null;

				    			texts = new ArrayList<>();
				    			images = new ArrayList<>();
			    			}
			    			
			    			currentSheet = handleSheet(entries);
			    			break;
		    			
			    		case "Text":
			    			
			    			processLine(currentLine, lines, chars);
			    			processText(currentText, lines, texts);
			    			processImage(currentImage, images);
			    			
			    			currentImage = null;

			    			lines = new ArrayList<>();
			    			currentText = null;

			    			chars = new ArrayList<>();
			    			currentLine = null;

			    			currentText = handleText(entries);
			    			break;
			    			
			    		case "Image":
			    			
			    			processLine(currentLine, lines, chars);
			    			processText(currentText, lines, texts);
			    			processImage(currentImage, images);
			    			
			    			lines = new ArrayList<>();
			    			currentText = null;

			    			chars = new ArrayList<>();
			    			currentLine = null;

			    			currentImage = handleImage(entries);
			    			break;
			    			
			    		case "Line":
			    			
			    			processLine(currentLine, lines, chars);
			    			chars = new ArrayList<>();
			    			currentLine = null;

			    			currentLine = handleLine(entries);
			    			break;
			    			
		    			case "Chardata":
		    				currentChar = handleCharData(entries);
		    				chars.add(currentChar);
		    				break;		    			
		    			
		    			default:
		    				System.out.println("This type of annotation is not supported!");
		    		}
		    }
		    
		    if(currentSheet != null) {
	    		
				processLine(currentLine, lines, chars);
	    			processText(currentText, lines, texts);
	    			processImage(currentImage, images);
	    			
	    			currentSheet.setImageAreas(images);
	    			currentSheet.setTextAreas(texts);
	    			
	    			sheets.add(currentSheet);
			}
		    
		}
		
		Annotations annotations = new Annotations(heading, sheets);
		
		return annotations;
	}
	
	
	private void processLine(Line currentLine, List<Line> lines, List<CharData> chars) {
		
		if(currentLine != null) {
			
			currentLine.setCharacters(chars);
			lines.add(currentLine);
		}
		
	}
	
	private void processImage(Image currentImage, List<Image> images) {
		
		if(currentImage != null) {
			
			images.add(currentImage);
		}
		
	}
	
	private void processText(Text currentText, 
							List<Line> lines, List<Text> texts) {
		
		if(currentText != null) {
			
			currentText.setLines(lines);
			texts.add(currentText);
		}
		
	}
	
	/*
	 * Records starting with Chardata denote the characters or symbols. 
	 * 1:Chardata, 2:Character ID, 3-6:Coordinates of the bounding box (left, top, right, bottom),
	 * 7:Text Mode (0:Ordinary text, 1:Math symbol), 8:Link label, 9:ID of parent character, 10:OCR code
	 */
	private CharData handleCharData(String[] entries) {
		
		BoundingBox boundingBox = new BoundingBox(Double.parseDouble(entries[2]),
												 Double.parseDouble(entries[3]),
												 Double.parseDouble(entries[4]),
												 Double.parseDouble(entries[5]));
		
		TextMode textMode = null;
		
		if(entries[6].equals("0")) {
			textMode = TextMode.ORDINARY_TEXT;
		} else {
			textMode = TextMode.MATH_SYMBOL;
		}
		
		LinkLabel linkLabel = LinkLabel.NONE;
		
		/*
		  	0: horizontal
			1: right-superscript
			2: right-subscript
			3: left-superscript
			4: left-subscript
			5: upper
			6: lower
			-1: the first character in expressions or ordinary texts
		 */
		
		switch(entries[7]) {
			
			case "0":
				linkLabel = linkLabel.HORIZONTAL;
				break;
			case "1":
				linkLabel = linkLabel.RIGHT_SUP;
				break;
			case "2":
				linkLabel = linkLabel.RIGHT_SUB;
				break;
			case "3":
				linkLabel = linkLabel.LEFT_SUP;
				break;
			case "4":
				linkLabel = linkLabel.LEFT_SUB;
				break;
			case "5":
				linkLabel = linkLabel.UPPER;
				break;
			case "6":
				linkLabel = linkLabel.LOWER;
				break;
			case "-1":
				linkLabel = linkLabel.NONE;
				break;
			default:
				System.out.println("This type of link label is not supported!");
		}
		
		
		CharData charData = new CharData(Long.parseLong(entries[1]), //charId
										boundingBox, textMode, linkLabel, 
										Long.parseLong(entries[8]),
										entries[9]);
		
		return charData;
	}

	/*
	 * Records starting with Line denote the text line. 1:Line, 
	 * 2:Line ID, 3-6:Coordinates of the bounding box (left, top, right, bottom)
	 */
	private Line handleLine(String[] entries) {
		
		BoundingBox boundingBox = new BoundingBox(Double.parseDouble(entries[2]),
				 Double.parseDouble(entries[3]),
				 Double.parseDouble(entries[4]),
				 Double.parseDouble(entries[5]));

		return new Line(Long.parseLong(entries[1]), boundingBox, null/*chars*/);
	}

	/*
	 * Records stating with Text or Image are corresponding to one document layout component. 
	 * 1:Text or Image, 2:Component ID, 
	 * 3-6:Coordinates of the bounding box (left, top, right, bottom)
	 */
	private Image handleImage(String[] entries) {
		
		BoundingBox boundingBox = new BoundingBox(Double.parseDouble(entries[2]),
				 Double.parseDouble(entries[3]),
				 Double.parseDouble(entries[4]),
				 Double.parseDouble(entries[5]));

		return new Image(Long.parseLong(entries[1]), boundingBox);

	}
	
	/*
	 * Records stating with Text or Image are corresponding to one document layout component. 
	 * 1:Text or Image, 2:Component ID, 
	 * 3-6:Coordinates of the bounding box (left, top, right, bottom)
	 */
	private Text handleText(String[] entries) {
		
		BoundingBox boundingBox = new BoundingBox(Double.parseDouble(entries[2]),
				 Double.parseDouble(entries[3]),
				 Double.parseDouble(entries[4]),
				 Double.parseDouble(entries[5]));

		return new Text(Long.parseLong(entries[1]), boundingBox, null/*lines*/);
	}

	/*
	 * Records (lines) starting with Sheet denote the beginning of a new page. Each field (column) 
	 * in Sheet record denotes 
	 * 1:Sheet, 2:Page ID, 3:Filename of the page image, 4:always -1.
	 */
	private Sheet handleSheet(String[] entries) {
		
		return new Sheet(Long.parseLong(entries[1]), entries[2], null/*textAreas*/, null/*imageAreas*/);
	}
	
//	private void processSheet(Sheet currentSheet, Text currentText, Image currentImage, Line currentLine, 
//    CharData currentChar, List<Sheet> sheets, List<Text> texts, List<Image> images, 
//    List<Line> lines, List<CharData> chars) {
//
//if(currentSheet != null) {
//
//if(currentChar != null) {
//
//chars.add(currentChar);
//
//currentLine.setCharacters(chars);
//lines.add(currentLine);
//
//currentText.setLines(lines);
//texts.add(currentText);
//
//currentSheet.setTextAreas(texts);
//
//} else if(currentLine != null) {
//
//lines.add(currentLine);
//
//currentText.setLines(lines);
//texts.add(currentText);
//
//currentSheet.setTextAreas(texts);
//
//} else if(currentText != null) {
//
//texts.add(currentText);
//currentSheet.setTextAreas(texts);
//}
//
//// images are treated separately
//if(currentImage != null) {
//images.add(currentImage);
//currentSheet.setImageAreas(images);
//}
//
//
//sheets.add(currentSheet);
//
//currentSheet = null;
//currentText = null;
//currentImage = null;
//currentLine = null;
//currentChar = null;
//}
//}
	
}
