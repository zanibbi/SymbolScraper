/******************************************************************************
* Main.java
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

package TrueBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

public class Main {

    static String Inpfilename;
    static String OutDir="";
    static OsCheck.OSType ostype;
    static  String fileseparator=null;
    static boolean displayFlag = false;
    static boolean printFlag = false;
    static boolean boundingboxFlag = false;
    static boolean trainingFlag = false;
    static boolean filterFlag= false;
    static boolean batchFlag=false;
    static boolean inpDirFlag=false;

    public static void main(String args[]) throws IOException, JDOMException, SAXException, ParserConfigurationException, InterruptedException {

        ostype = OsCheck.getOperatingSystemType();
        switch (ostype) {
            case Windows:
                fileseparator = "\\\\";
                break;
            case MacOS:
                fileseparator = "/";
                break;
            case Linux:
                fileseparator = "/";
                break;
            case Other:
                System.out.println("Unidentified OS output will be generated in working environment");
                break;
        }

        String inpfile = "";
        String outfile = "";

        int argLength = args.length;
        File file;
        File dir;
        String op;
        String op1;

        switch (argLength) {
            case 1:
                inpfile = args[0];

                file = new File(inpfile);
                if (file.exists()) {
                    break;
                } else {
                    System.out.println("Invalid input file");
                    System.exit(0);
                }
                System.out.println("Invalid input parameter");
                System.out.println("*************Usage*************");
                System.out.println("Please follow the below instructions ");
                System.out.println("java Main <mode> [op1] [op2] <input dir/file path> [output directory]");
                System.out.println("mode indicates running mode for the program");
                System.out.println("\tsingle mode is default (i.e. only require input file name)");
                System.out.println("\t-batch - indicates batch mode (takes input file directory and output file directory)");
                System.out.println("op1 optional parameter to write pdf with bounding box");
                System.out.println("\t-b optional parameter to create PDFs with bounding box");
                System.out.println("\t-f optional parameter to create PDFs with bounding box");
                System.out.println("op2 is an option parameter for displaying output on the console  or writing onto a file");
                System.out.println("\t-d to display");
                System.out.println("\t-p to print");
                System.out.println("input dir/file path- input directory or PDF file path");
                System.out.println("output directory - Directory where output files will be created");
                System.exit(0);

            case 2:
                op = args[0];
                setFlags(op);

                inpfile = args[1];
                file = new File(inpfile);
                if (file.exists()) {
                    break;
                } else {
                    System.out.println("Invalid input file");
                    System.exit(0);
                }

                break;
            case 3:
                if(args[0].equals("-batch")){
                    batchFlag=true;
                    inpfile = args[1];
                    outfile =args[2];
                    file = new File(inpfile);

                    if(file.isDirectory()) {
                        System.out.println("Input Type : Directory");
                        inpDirFlag=true;
                    }else if (file.exists()) {
                       System.out.println("Input Type : File");
                    }  else{
                        System.out.println("Invalid input file");
                        System.exit(0);
                    }

                    dir =  new File(outfile);

                    if(!dir.isDirectory()){
                        System.out.println("Directory Not Found ... trying to to create new directory -"+ outfile);
                        try {
                            dir.mkdir();

                        }catch(Exception  e){
                            System.out.println("Error occurred while creating directory...Please specify correct output directory path");
                            System.exit(1);
                        }
                    }
                }else {
                    op = args[0];   //first optional parameter
                    setFlags(op);

                    op1 =args[1];
                    setFlags(op1);  //second optional parameter

                    inpfile = args[2];
                    file = new File(inpfile);
                    if (file.exists()) {
                        break;
                    } else {
                        System.out.println("Invalid input file");
                        System.out.println("Invalid input parameter");
                        System.out.println("*************Usage*************");
                        System.out.println("Please follow the instructions ");
                        System.out.println("java Main <mode> [op1] [op2] <input dir/file path> [output directory]");
                        System.out.println("mode indicates running mode for the program");
                        System.out.println("\tsingle mode is default (i.e. only require input file name)");
                        System.out.println("\t-batch - indicates batch mode (takes input file directory/file and output file directory)");
                        System.out.println("op1 optional parameter to write pdf with bounding box");
                        System.out.println("\t-b optional parameter to create PDFs with bounding box");
                        System.out.println("\t-f optional parameter to create PDFs with bounding box");
                        System.out.println("op2 is an option parameter for displaying output on the console  or writing onto a file");
                        System.out.println("\t-d to display");
                        System.out.println("\t-p to print");
                        System.out.println("input dir/file path- input directory or PDF file path");
                        System.out.println("output directory - Directory for output files (required parameter in batch mode)");
                        System.exit(0);
                    }
                }
                break;
            case 4:
                if(args[0].equals("-batch")){
                    batchFlag=true;

                    op = args[1];
                    setFlags(op);
                    inpfile = args[2];
                    outfile =args[3];
                    file = new File(inpfile);
                    if(file.isDirectory()) {
                        System.out.println("Input Type : Directory");
                        inpDirFlag=true;
                    }else if (file.exists()) {
                        System.out.println("Input Type : File");
                    }  else{
                        System.out.println("Invalid input file");
                        System.exit(0);
                    }


                    dir =  new File(outfile);

                    if(!dir.isDirectory()){
                        System.out.println("Directory Not Found ... trying to to create new directory -"+ outfile);
                        try {
                            dir.mkdir();
                        }catch(Exception  e){
                            System.out.println("Error occurred while creating directory...Please specify correct output directory path");
                            System.exit(1);
                        }
                    }
                }
                else{
                    System.out.println("Invalid input file");
                    System.out.println("*************Usage*************");
                    System.out.println("Please follow the instructions ");
                    System.out.println("java Main <mode> [op1] [op2] <input dir/file path> [output directory]");
                    System.out.println("mode indicates running mode for the program");
                    System.out.println("\tsingle mode is default (i.e. only require input file name)");
                    System.out.println("\t-batch - indicates batch mode (takes input file directory and output file directory)");
                    System.out.println("op1 optional parameter to write pdf with bounding box");
                    System.out.println("\t-b optional parameter to create PDFs with bounding box");
                    System.out.println("\t-f optional parameter to create PDFs with bounding box");
                    System.out.println("op2 is an option parameter for displaying output on the console  or writing onto a file");
                    System.out.println("\t-d to display");
                    System.out.println("\t-p to print");
                    System.out.println("input dir/file path- input directory or PDF file path");
                    System.out.println("output directory - Directory for output files (required parameter in batch mode)");
                    System.exit(0);
                }
                break;

            case 5:
                if(args[0].equals("-batch")){
                    batchFlag=true;

                    op = args[1];
                    setFlags(op);


                    op1 = args[2];
                    setFlags(op1);

                    inpfile = args[3];
                    outfile =args[4];
                    file = new File(inpfile);

                    if(file.isDirectory()) {
                        System.out.println("Input Type : Directory");
                        inpDirFlag=true;
                    }else if (file.exists()) {
                        System.out.println("Input Type : File");
                    }  else{
                        System.out.println("Invalid input file");
                        System.exit(0);
                    }

                        dir =  new File(outfile);

                    if(!dir.isDirectory()){
                        System.out.println("Directory Not Found ... trying to to create new directory -"+ outfile);
                        try {
                            dir.mkdir();
                        }catch(Exception  e){
                            System.out.println("Error occurred while creating directory...Please specify correct output directory path");
                            System.exit(1);
                        }
                    }
                }
                else{
                    System.out.println("Invalid input file");
                    System.out.println("*************Usage*************");
                    System.out.println("Please follow the instructions ");
                    System.out.println("java Main <mode> [op1] [op2] <input dir/file path> [output directory]");
                    System.out.println("mode indicates running mode for the program");
                    System.out.println("\tsingle mode is default (i.e. only require input file name)");
                    System.out.println("\t-batch - indicates batch mode (takes input file directory and output file directory)");
                    System.out.println("op1 optional parameter to write pdf with bounding box");
                    System.out.println("\t-b optional parameter to create PDFs with bounding box");
                    System.out.println("\t-f optional parameter to create PDFs with bounding box");
                    System.out.println("op2 is an option parameter for displaying output on the console  or writing onto a file");
                    System.out.println("\t-d to display");
                    System.out.println("\t-p to print");
                    System.out.println("input dir/file path- input directory or PDF file path");
                    System.out.println("output directory - Directory for output files (required parameter in batch mode)");
                    System.exit(0);
                }
                break;

        }

        if(batchFlag){
            OutDir= outfile;

            if(inpDirFlag){
                System.out.println("Batch operation started for Directory "+inpfile);
                File folder = new File(inpfile);
                File[] listOfFiles = folder.listFiles();

                for(int i = 0; i < listOfFiles.length; i++) {
                    File dirFile = listOfFiles[i];
                    if (dirFile.isFile() && dirFile.getName().endsWith(".pdf")) {
                        Inpfilename = dirFile.getName().split("\\.")[0];
                        System.out.println("\n\nFile :\t"+dirFile.getName());
                        String inFile = inpfile+fileseparator+dirFile.getName(); 
                        mathextract(inFile);
                    }
                }

                //System.exit(0);
            }else{
                String[] filearray = inpfile.split(fileseparator);
                Inpfilename = filearray[filearray.length - 1].split("\\.")[0];
                mathextract(inpfile);
            }

        }
        else {
            String[] filearray = inpfile.split(fileseparator);
            Inpfilename = filearray[filearray.length - 1].split("\\.")[0];

            for (int iter = 0; iter < filearray.length - 1; iter++) {
                OutDir += filearray[iter] + fileseparator;
            }
            OutDir += "Output";
            mathextract(inpfile);
        }
    }

    public static void writeTransformationsToFile(String filename, List<PageStructure> allPages) throws IOException {
    	
    	String basename = FilenameUtils.getBaseName(filename);
		String path	= FilenameUtils.getFullPath(filename);
		
		Path destFile = Paths.get(path, basename + ".md");
		
    	BufferedWriter writer = new BufferedWriter(new FileWriter(destFile.toString()));
        
    	for(PageStructure pageStructure: allPages) {
        	
    		//writer.append(pageStructure.meta.getTransformation());
    		writer.append(pageStructure.xmin + "," + pageStructure.ymax);
    		writer.append("\n");
        }
    	
    	writer.close();
    }

    public static void mathextract(String filename) throws IOException, JDOMException, SAXException, ParserConfigurationException, InterruptedException {
	        
    	try {
            long starttime = System.currentTimeMillis();
            
            System.out.println("Parsing File:");
            System.out.println(filename);
	        //Load File
	        File file = new File(filename);
	        FileInputStream inpStream = new FileInputStream(file);
	        PDDocument documnet = PDDocument.load(inpStream);
	
	        //Readfile
	        read reader = new read(documnet);
	        ArrayList<PageStructure> allPages = reader.readPdf();
        	
	        writeTransformationsToFile(filename, allPages);
	        
	        //DisplayPDF
	        if (displayFlag) {
	            DisplayPDF display = new DisplayPDF(allPages);
	            display.displayPDF();
	            //System.out.println(display.builder.toString());
	
	        }
	        //Print flag
	        if (printFlag) {
	            long timetaken = System.currentTimeMillis()-starttime;
	            
	            String pageMetrics = "<Document>\n<runtime>"+timetaken+"</runtime>\n";
	            pageMetrics+= "<pagemetrics>\n";
	            for(int i=0;i<allPages.size();i++){
            		
	            	System.out.println(allPages.get(i).xmin + ", " + allPages.get(i).ymax + ", " + allPages.get(i).xmax + ", " + allPages.get(i).ymin);
	            	
//	                pageMetrics+="\t<page>\n";
//	                pageMetrics+="\t\t<no>"+i+"</no>\n";
//	                pageMetrics+="\t\t<lines>"+allPages.get(i).meta.linecount+"</lines>\n";
//	                pageMetrics+="\t\t<words>"+allPages.get(i).meta.wordcount+"</words>\n";
//	                pageMetrics+="\t\t<characters>"+allPages.get(i).meta.charactercount+"</characters>\n";
//	                pageMetrics+="\t</page>\n";
//	
	            }
	            pageMetrics+="</pagemetrics>\n";
	
	
	            DisplayPDF display = new DisplayPDF(allPages);
	            display.displayPDF();
	            //System.out.println(display.xmlFormat);
	            try {
	                File outDirectory = new File(OutDir);
	                if (!outDirectory.isDirectory()) {
	                    outDirectory.mkdir();
	                }
	                //File newFile = new File(OutDir + fileseparator + Inpfilename + ".xml");
	                OutputStream out = new FileOutputStream(OutDir + fileseparator + Inpfilename + ".xml");
	                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
	                //PrintWriter writer = new PrintWriter(newFile);
	                writer.write(pageMetrics+display.builder.toString()+"</Document>");
	                writer.flush();
	                writer.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	
	        //drawBoundingBox
	        if (boundingboxFlag) {
	            drawBBOX box = new drawBBOX(documnet, allPages);
	            box.drawPDF(doctype.Normal);
	            System.out.println();
	            System.out.println("Output files generated...@ " + OutDir);
	
	        }
	
	        //Filter text
	        if(filterFlag){
	            //Load Dictionary
	
	            File dicfile = new File("Dictionary");
	            HashMap<String, Integer> wordDictionary =  new HashMap<String,Integer>();
	            wordDictionary = loadDictionary(dicfile);
	            FilterText filterText = new FilterText(wordDictionary);
	            HashMap<Integer, characterInfo> filtercharList;
	            drawBBOX box = new drawBBOX(documnet, allPages);
	            for (int pageIter = 0; pageIter < allPages.size(); pageIter++) {
	                filtercharList = filterPage(filterText, pageIter, allPages);
	                box.drawPageBBOX(pageIter, filtercharList,allPages.get(pageIter).pageCompundCharacters, doctype.Filtered);
	            }
	            System.out.println();
	            System.out.println("Output files generated....");
	        }
	
	
	        if (trainingFlag) {
	            Process p = Runtime.getRuntime().exec("python C:\\Users\\ritvi\\PycharmProjects\\CapstoneProject\\MathScrapper\\main.py");
	            while (p.isAlive()) {
	                continue;
	            }
	            File gtfile = new File("C:\\Users\\ritvi\\PycharmProjects\\CapstoneProject\\MATHXML");
	            TrainingGTParser gtParser = new TrainingGTParser(gtfile);
	            ArrayList<GT> gtList = gtParser.readGT();
	
	            FilterText wordFiltering = new FilterText(allPages);
	
	            wordFiltering.isMath();
	            /*
	            for (GT g : gtList) {
	                System.out.println("Page:" + g.pageid);
	                for (int i : g.charid)
	                    System.out.println(("\tChar id:" + i));
	            }
	            */
	        }
	
	
	        long endtime = System.currentTimeMillis()-starttime;
	        System.out.println("Time taken ="+endtime+"ms");
    	}
    	catch(Exception e) {
            e.printStackTrace();
    		System.err.println("Exception occurred for file: "+Inpfilename );
    	}
    }


    public static HashMap<String, Integer> loadDictionary(File file) throws FileNotFoundException {
        HashMap<String, Integer> wordDictionary =  new HashMap<String,Integer>();
        Scanner scan = new Scanner(new FileReader(file));
        while(scan.hasNext()){
            String word =scan.nextLine();
            word=word.trim().toLowerCase();
            wordDictionary.put(word,0);
        }

        return wordDictionary;
    }

    public static HashMap<Integer,characterInfo> filterPage(FilterText filterText,int page, ArrayList<PageStructure> allPages){
        HashMap<Integer,Words> filtered= filterText.filter(allPages.get(page));
        HashMap<Integer,characterInfo> charList =filterText.getCharacterList(filtered);
        return charList;
    }


    public static void setFlags(String op){
        switch(op){
            case "-b":
                boundingboxFlag = true;
                break;
            case "-d":
                displayFlag = true;
                break;
            case "-p":
                printFlag = true;
                break;
            case "-f":
                filterFlag = true;
                break;

            default:
                System.out.println("Invalid input file");
                System.out.println("*************Usage*************");
                System.out.println("Please follow the instructions ");
                System.out.println("java Main <mode> [op1] [op2] <input dir/file path> [output directory]");
                System.out.println("mode indicates running mode for the program");
                System.out.println("\tsingle mode is default (i.e. only require input file name)");
                System.out.println("\t-batch - indicates batch mode (takes input file directory and output file directory)");
                System.out.println("op1 optional parameter to write pdf with bounding box");
                System.out.println("\t-b optional parameter to create PDFs with bounding box");
                System.out.println("\t-f optional parameter to create PDFs with bounding box");
                System.out.println("op2 is an option parameter for displaying output on the console  or writing onto a file");
                System.out.println("\t-d to display");
                System.out.println("\t-p to print");
                System.out.println("input dir/file path- input directory or PDF file path");
                System.out.println("output directory - Directory for output files (required parameter in batch mode)");
                System.exit(0);
        }

    }



}


