package com.symbolScraper.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class Convert {

	public static void main(String[] args) throws InvalidPasswordException, IOException {
		
		String path = "/Users/parag/Workspace/GTDB-Dataset/GTDB-2/";
		
		Consumer<Path> generateImages = new Consumer<Path>() {
			
			@Override
			public void accept(Path t) {
				
				if(!t.toString().endsWith(".pdf")) {
					return;
				}
				
				try {
					System.out.println(t);
					PDDocument document = PDDocument.load(new File(t.toString()));
					PDFRenderer pdfRenderer = new PDFRenderer(document);
					
					String basename = FilenameUtils.getBaseName(t.toString());
					
					Path destDir = Paths.get(path, "images", basename);
					new File(destDir.toString()).mkdirs();

			    	for (int page = 0; page < document.getNumberOfPages(); ++page)
					{ 
					    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 600, ImageType.RGB);
						
					    File outputfile = new File(Paths.get(destDir.toString(), basename + "_" + (page+1)  + ".png").toString());
						ImageIO.write(bim, "png", outputfile);
						
					    // suffix in filename will be used as the file format
					    // ImageIOUtil.writeImage(bim, "png", destFile, );
					}
										
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println("Exception " + e);
				}
				
			}
		}; 
				
		try (Stream<Path> paths = Files.walk(Paths.get(path))) {
		    
 		    paths
		        .filter(Files::isRegularFile)
		        .forEach(generateImages);
		} 
				
	}
}
