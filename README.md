# SymbolScraper

An Apache [PDFBox](https://pdfbox.apache.org) extension for extracting symbol labels and bounding boxes from born-digital PDF files. Implemented in Java.

**Copyright (c) 2018, 2019 Ritvik Joshi, Parag Mali, Puneeth Kukkadapu, Mahshad Mahdavi, Jessica Diehl and Richard Zanibbi**   

Developed at the [Document and Pattern Recognition Laboratory](https://www.cs.rit.edu/~dprl/index.html)  
(Rochester Institute of Technology, USA)

## Installation

**Dependencies:** Make sure that both Maven (3.6.0 for us - other versions may work fine) and Java 1.8 are installed on your system. Perl is also used to generate the bash scripts used to run the program more easily.

**Operating System:** While this code should work on Windows systems as well, we have only tested on Linux and MacOS X so far.

From the main directory, issue ``make``. This will run the Maven build system, download dependencies, etc., compile source files and generate .jar files in ``./target``. Finally, a bash script ``bin/sscraper`` is generated, so that the program can be easily used in different directories.

The ``pom.xml`` file can be modified to change the Maven build parameters. 

## Bash Script

The ``sscraper`` bash script for processing directories of PDF files can be invoked using:

	sscraper [-b] inDir outDir
	
where inDir and outDir are directories containing PDFs to process and place XML output files respectively. Use the '-b' flag to also generate copies of each PDF with bounding boxes in the output directory. 

To process a single file, in this first version you will need to use Java directly (see below).

**Note:** Add the bin/ directory to your execution path (the PATH variable in bash shell) in order to use the command anywhere on your system.

## Java Usage

From Java, SymbolScraper is invoked using these parameters:
	
	java -jar target/SymbolScraper-0.1.0-jar-with-dependencies.jar <mode> [op1] [op2] <input dir/file> [output dir]
	
Parameters:

	<mode>: single file input (default)
		-batch for input directory containing PDF files 
			*requires an output directory* ([output dir])
	[op1] write modified pdf files  
		-b add bounding boxes in pdf file output
		-f filter english words (from a fixed dictionary) in pdf output file
	[op2] display symbol data in XML format 
		-d display on standard output
		-p print to file
	<input dir/file path> - input directory *or* PDF file path
	<output dir> - output directory (required in batch mode)
	
An example of processing a single file, writing an XML file to inputDir/Output:

	java -jar target/SymbolScraper-0.1.0-jar-with-dependencies.jar -p inputDir/file.pdf 

## Questions

Please direct questions or concerns to Richard Zanibbi [rxzvcs@rit.edu](rxzvcs@rit.edu), the director of the Document and Pattern Recognition Lab.

