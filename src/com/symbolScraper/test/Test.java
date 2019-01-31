package com.symbolScraper.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.symbolScraper.annotations.Annotate;
import com.symbolScraper.annotations.AnnotationReader;
import com.symbolScraper.annotations.data.Annotations;

public class Test {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		AnnotationReader reader = new AnnotationReader();
		
		Annotations annotations = reader.read("/Users/parag/Workspace/GTDB-Dataset/GTDB-2/Borcherds86.csv");
		//Annotations annotations = reader.read("/Users/parag/Workspace/GTDB-Dataset/GTDB-1/AIF_1970_493_498.csv");
		//System.out.println(reader.read("/Users/parag/Workspace/GTDB-Dataset/GTDB-1/AIF_1970_493_498.csv"));
		//System.out.println(reader.read("/Users/parag/eclipse-workspace/MathScraper/src/testData.csv"));
		
		Annotate visualizations = new Annotate();
		
		//Load File
		//File file = new File("/Users/parag/Workspace/math_detection/GTDB-2 papers/Alford94.pdf");
		File file = new File("/Users/parag/Downloads/Borcherds86.pdf");
        FileInputStream inpStream = new FileInputStream(file);
        PDDocument document = PDDocument.load(inpStream);
        
        String outputFile = "/Users/parag/Downloads/swiggy2.pdf"; 
		//visualizations.drawBoundingBox(document, annotations);
		visualizations.drawBoundingBoxForImage(document, outputFile, annotations);

	}
	
}
