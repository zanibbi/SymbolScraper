/******************************************************************************
* Convert.java
*
* Copyright (c) 2018, 2019
* Ritvik Joshi, Parag Mali, Puneeth Kukkadapu, Mahshad Mahdavi, and 
* Richard Zanibbi
*
* Document and Pattern Recognition Laboratory
* Rochester Institute of Technology, USA
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/

package test;

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
		
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();

		System.out.println("Number of documents " + directoryListing.length);
		
		if (directoryListing != null) {
			
			for (File t : directoryListing) {

				if(!t.toString().endsWith(".pdf")) {
					return;
				}
				
				System.out.println(t);
				PDDocument document = PDDocument.load(new File(t.toString()));
				PDFRenderer pdfRenderer = new PDFRenderer(document);
				
				String basename = FilenameUtils.getBaseName(t.toString());
				
				Path destDir = Paths.get(path, "images", basename);
				File k = new File(destDir.toString());
				k.mkdirs();
				
		    	for (int page = 0; page < document.getNumberOfPages(); ++page)
				{ 
				    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 600, ImageType.RGB);
					
				    File outputfile = new File(Paths.get(destDir.toString(), basename + "_" + (page+1)  + ".png").toString());
					ImageIO.write(bim, "png", outputfile);
				}
		    	
			}
		}		
	}
}
