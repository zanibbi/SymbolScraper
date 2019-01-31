/*
 * @Author: Parag Mali
 * 
 * This file annotates the pdf files from the following 
 * datatset given the ground truth:
 * 
 * https://github.com/uchidalab/GTDB-Dataset 
 * 
 * It can be used to visualize the ground truth annotations
 */

package com.symbolScraper.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.symbolScraper.annotations.data.Annotations;
import com.symbolScraper.annotations.data.BoundingBox;
import com.symbolScraper.annotations.data.CharData;
import com.symbolScraper.annotations.data.Image;
import com.symbolScraper.annotations.data.Line;
import com.symbolScraper.annotations.data.Sheet;
import com.symbolScraper.annotations.data.Text;
import com.symbolScraper.annotations.data.TextMode;
import com.symbolScraper.constants.Constants;

import opennlp.tools.parser.Cons;

public class Annotate {
	
	public void drawBoundingBoxForImage(PDDocument document, String ouputFile, Annotations annotations) 
			throws IOException {
			
		if(annotations == null || 
		   annotations.getSheets() == null ||
		   annotations.getSheets().size() < 1) {
			
			System.out.println("No annotations found!");
			return;
		}
		
		int sheetCount = 0;
		
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		PDDocument annotatedDoc = new PDDocument();
		
		for(Sheet sheet: annotations.getSheets()) {

			BufferedImage bufferedImage = 
				pdfRenderer.renderImageWithDPI(sheetCount, Constants.SCAN_DPI, ImageType.RGB);
			
			PDPage annotatedPage = null;
				        	        
	        PDRectangle mediaBox = 
					new PDRectangle(bufferedImage.getMinX(), 
          					        bufferedImage.getMinY(),
					        			bufferedImage.getWidth(),
					     			bufferedImage.getHeight());
				
			annotatedPage = new PDPage(mediaBox);
			
			Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
			graphics.setStroke(new BasicStroke(Constants.STROKE_SIZE));
			
			annotateTextAreas(sheet.getTextAreas(), graphics);
			annotateImageAreas(sheet.getImageAreas(), graphics);
			
			PDImageXObject pdImageXObject = 
					JPEGFactory.createFromImage(annotatedDoc, bufferedImage);
				
			PDPageContentStream contents = 
					new PDPageContentStream(annotatedDoc, annotatedPage);
			
			contents.drawImage(pdImageXObject, Constants.X_ORIGIN, Constants.Y_ORIGIN);
			
			contents.close();
			
			annotatedDoc.addPage(annotatedPage);
			sheetCount = sheetCount + 1;
		}
		
		File file = new File(ouputFile);
		annotatedDoc.save(file);
		annotatedDoc.close();
	}
	
	private void annotateImageAreas(List<Image> imageAreas, Graphics2D graphics) {
		
		if(imageAreas == null) {
			return;
		}
		
		graphics.setColor(Color.MAGENTA);
		
		for(Image image: imageAreas) {
			
	        BoundingBox boundingBox = image.getBoundingBox();
			drawBoundingBox(graphics, boundingBox);
		}
	}

	private void annotateTextAreas(List<Text> textAreas, Graphics2D graphics) {

		if(textAreas == null) {
			return;
		}
		
		for(Text text: textAreas) {
			
	        BoundingBox boundingBox = text.getBoundingBox();
			annotateLines(text.getLines(), graphics);
			
			graphics.setColor(Constants.TRASPARENT_RED);

			drawBoundingBox(graphics, boundingBox);
		}
	}

	private void annotateLines(List<Line> lines, Graphics2D graphics) {
		
		if(lines == null) {
			return;
		}
				
		for(Line line: lines) {
			
	        BoundingBox boundingBox = line.getBoundingBox();
			annotateCharacters(line.getCharacters(), graphics);
			
			graphics.setColor(Constants.TRASPARENT_BLUE);
			drawBoundingBox(graphics, boundingBox);
		}
		
	}

	private void annotateCharacters(List<CharData> characters, Graphics2D graphics) {
		
		if(characters == null) {
			return;
		}
		
		for(CharData character: characters) {
			
	        BoundingBox boundingBox = character.getBoundingBox();
	        
	        if(character.getTextMode() == TextMode.MATH_SYMBOL) {
	        		graphics.setColor(Constants.TRASPARENT_YELLOW);
	        } else {
	        		graphics.setColor(Constants.TRASPARENT_GREEN);
	        }
	        
	        fillBoundingBox(graphics, boundingBox);			
	        drawBoundingBox(graphics, boundingBox);
		}
	}

	private void fillBoundingBox(Graphics2D graphics, BoundingBox boundingBox) {
		
		graphics.fillRect(
			boundingBox.getLeft().intValue(), 
			boundingBox.getTop().intValue(),
			boundingBox.getRight().intValue() - boundingBox.getLeft().intValue(),
			boundingBox.getBottom().intValue() - boundingBox.getTop().intValue());
	
	}
	
	private void drawBoundingBox(Graphics2D graphics, BoundingBox boundingBox) {
		
		graphics.drawRect(
			boundingBox.getLeft().intValue(), 
			boundingBox.getTop().intValue(),
			boundingBox.getRight().intValue() - boundingBox.getLeft().intValue(),
			boundingBox.getBottom().intValue() - boundingBox.getTop().intValue());
	
	}

//	public void drawBoundingBox(PDDocument document, Annotations annotations) 
//	throws IOException {
//	
//	if(annotations == null || annotations.getSheets() == null || annotations.getSheets().size() < 1) {
//		System.out.println("No annotations found!");
//		return;
//	}
//	
//	PDDocument editedDoc = new PDDocument();
//	
//	int sheetCount = 0;
//	
//	PDPageContentStream[] pageContentStreams = 
//			new PDPageContentStream[annotations.getSheets().size()];
//	
//	for(Sheet sheet: annotations.getSheets()) {
//
//		List<Text> textAreas = sheet.getTextAreas();
//		
//		for(Text text: textAreas) {
//
//			PDPage page = document.getPage(sheetCount);
//	        editedDoc.addPage(page);
//	        
//		        PDRectangle box = page.getBBox();
//	        
//	        System.out.println(box);
//	        pageContentStreams[sheetCount] = 
//	        		new PDPageContentStream(
//        				editedDoc, page, 
//        				PDPageContentStream.AppendMode.APPEND, true
//    				);
//	        
//	        BoundingBox boundingBox = text.getBoundingBox();
//	        
//	        pageContentStreams[sheetCount].addRect(
//	        		boundingBox.getLeft()/10, 
//	        		boundingBox.getBottom()/10, 
//	        		(boundingBox.getRight() - boundingBox.getLeft())/10,
//	        		(boundingBox.getBottom() - boundingBox.getTop())/10);
//
//	        pageContentStreams[sheetCount].setLineWidth((float)1);
//            pageContentStreams[sheetCount].setStrokingColor(Color.PINK);
//            pageContentStreams[sheetCount].stroke();
//            pageContentStreams[sheetCount].close();
//		}
//		sheetCount = sheetCount + 1;
//		//TODO
//		break;
//	}
//	
//	File file = new File(ouputFile);
//	document.save(file);
//	document.close();
//}
	
	
}
