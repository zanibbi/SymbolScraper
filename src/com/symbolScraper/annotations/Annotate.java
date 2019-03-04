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
import java.io.BufferedReader;
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

public class Annotate {
	
	
	public void drawBoundingBoxForImage(
		PDDocument document, String ouputFile, 
		Annotations annotations, BufferedReader transformationsReader, boolean useTransforms
	) throws IOException {
			
		if(annotations == null || 
		   annotations.getSheets() == null ||
		   annotations.getSheets().size() < 1) {
			
			System.out.println("No annotations found!");
			return;
		}
		
		int sheetCount = 0;
		
		double[][] transformations = new double[document.getPages().getCount()][2];
		String line;
		int count = 0;
		
		if(useTransforms) {
			while((line = transformationsReader.readLine()) != null) {
			    
				String[] coords = line.split(",");
				transformations[count][0] = Double.parseDouble(coords[0]);
				transformations[count][1] = Double.parseDouble(coords[1]);
			    
			    count = count + 1;
			}
		}
		
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		PDDocument annotatedDoc = new PDDocument();
		
		for(Sheet sheet: annotations.getSheets()) {

			BufferedImage bufferedImage = 
				pdfRenderer.renderImageWithDPI(sheetCount, Constants.SCAN_DPI, ImageType.RGB);
			
			PDPage page = document.getPage(sheetCount);
			
			//System.out.println(page.getBleedBox());
			//System.out.println(page.getArtBox());
			//System.out.println(page.getCropBox());
			System.out.println(page.getMediaBox());

			//BufferedImage bufferedImage = ImageIO.read(new File("/Users/parag/Downloads/GTDB-2_images/Alford94/1.png"));
			
//			Icon icon = new ImageIcon(bufferedImage);
//	        JLabel label = new JLabel(icon);
//
//	        final JFrame f = new JFrame("ImageIconExample");
//	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	        f.getContentPane().add(label);
//	        f.pack();
//	        SwingUtilities.invokeLater(new Runnable(){
//	            public void run() {
//	                f.setLocationRelativeTo(null);
//	                f.setVisible(true);
//	            }
//	        });
			PDPage annotatedPage = null;
	        	        			
	        double widthRatio = bufferedImage.getWidth() / page.getMediaBox().getWidth();
     		double heightRatio = bufferedImage.getHeight() / page.getMediaBox().getHeight(); 
    		
     		CharData firstChar = sheet.getTextAreas().get(0).getLines().get(0).getCharacters().get(0);
     		
     		int offsetX = (int)Math.round(widthRatio * transformations[sheetCount][0] - firstChar.getBoundingBox().getLeft()); 
    		int offsetY = (int)Math.round(heightRatio * (page.getMediaBox().getHeight() - transformations[sheetCount][1]) - firstChar.getBoundingBox().getBottom());
	        
    		System.out.println("Offsets:(x, y) --> " + offsetX + " , " + offsetY);
    		
	        PDRectangle mediaBox = 
					new PDRectangle(bufferedImage.getMinX(), 
          					        bufferedImage.getMinY(),
				        			bufferedImage.getWidth(),
					     			bufferedImage.getHeight());

	        annotatedPage = new PDPage(mediaBox);
    		
			Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
			graphics.setStroke(new BasicStroke(Constants.STROKE_SIZE));
			
			annotateTextAreas(sheet.getTextAreas(), graphics, offsetX, offsetY, useTransforms);
			//annotateImageAreas(sheet.getImageAreas(), graphics, offsetX, offsetY, useTransforms);
			
			PDImageXObject pdImageXObject = 
					JPEGFactory.createFromImage(annotatedDoc, bufferedImage);
			
			PDPageContentStream contents = 
					new PDPageContentStream(annotatedDoc, annotatedPage);
			
			//File outputfile = new File("/Users/parag/Documents/image.png");
			//ImageIO.write(bufferedImage, "png", outputfile);
			contents.drawImage(pdImageXObject, Constants.X_ORIGIN, Constants.Y_ORIGIN);
			
			contents.close();
			
			annotatedDoc.addPage(annotatedPage);
			sheetCount = sheetCount + 1;
			
			//TODO
			//break;
		}
		
		File file = new File(ouputFile);
		annotatedDoc.save(file);
		annotatedDoc.close();
	}
	
	private void annotateImageAreas(
		List<Image> imageAreas, Graphics2D graphics, int offsetX, int offsetY) {
		
		if(imageAreas == null) {
			return;
		}
		
		graphics.setColor(Color.MAGENTA);
		
		for(Image image: imageAreas) {
			
	        BoundingBox boundingBox = image.getBoundingBox();
			//drawBoundingBox(graphics, boundingBox);
		}
	}

	private void annotateTextAreas(
		List<Text> textAreas, Graphics2D graphics, int offsetX, int offsetY, boolean useTransforms) {

		if(textAreas == null) {
			return;
		}
		
		for(Text text: textAreas) {
			
	        BoundingBox boundingBox = text.getBoundingBox();
			annotateLines(text.getLines(), graphics, offsetX, offsetY, useTransforms);
			
			graphics.setColor(Constants.TRASPARENT_RED);

			//drawBoundingBox(graphics, boundingBox);
		}
	}

	private void annotateLines(List<Line> lines, Graphics2D graphics, int offsetX, int offsetY, boolean useTransforms) {
		
		if(lines == null) {
			return;
		}
				
		for(Line line: lines) {
			
	        BoundingBox boundingBox = line.getBoundingBox();
			annotateCharacters(line.getCharacters(), graphics, boundingBox, offsetX, offsetY, useTransforms);
			
			graphics.setColor(Constants.TRASPARENT_BLUE);
			//drawBoundingBox(graphics, boundingBox);
		}
		
	}

	private void annotateCharacters(
		List<CharData> characters, Graphics2D graphics, BoundingBox lineBB, 
		int offsetX, int offsetY, boolean useTransforms) {
		
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
	        drawBoundingBox(graphics, boundingBox, lineBB, offsetX, offsetY, useTransforms);
		}
	}

	private void fillBoundingBox(Graphics2D graphics, BoundingBox boundingBox) {
		
//		graphics.fillRect(
//			boundingBox.getLeft().intValue(), 
//			boundingBox.getTop().intValue(),
//			boundingBox.getRight().intValue() - boundingBox.getLeft().intValue(),
//			boundingBox.getBottom().intValue() - boundingBox.getTop().intValue());
//	
	}
	
	private void drawBoundingBox(Graphics2D graphics, BoundingBox boundingBox, BoundingBox lineBB, int offsetX, int offsetY, boolean useTransforms) {
		
		if(!useTransforms) {
			offsetX = 0;
			offsetY = 0;
		}
		
		graphics.drawRect(
			boundingBox.getLeft().intValue() + offsetX, 
			boundingBox.getTop().intValue() + offsetY,
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
