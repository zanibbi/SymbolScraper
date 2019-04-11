# SymbolScraper

An Apache [PDFBox](https://pdfbox.apache.org) extension for extracting symbol labels and bounding boxes from born-digital PDF files. Implemented in Java.

**Copyright (c) 2018, 2019 Ritvik Joshi, Parag Mali, Puneeth Kukkadapu, Mahshad Mahdavi, and Richard Zanibbi**   

Developed at the [Document and Pattern Recognition Laboratory](https://www.cs.rit.edu/~dprl/index.html)  
(Rochester Institute of Technology, USA)

## Installation

**Dependencies:** Make sure that both Maven (3.6.0 for us - other versions may work fine) and Java 1.8 are installed on your system. 

From the current directory, issue ``make``. This will run the Maven build system, download dependencies, etc., compile source files and generate .jar files in ``./target``. Maven will also install the resulting .jar files. 

The ``pom.xml`` file can be modified to change the Maven build parameters. 


## Usage

Run using the following:
	
	java Main <mode> [op1] [op2] <input dir/file> [output dir]
        
or:

	java -jar SymbolScraper.jar <mode> [op1] [op2] <input dir/file> [output dir]
	
Example: processing PDF files in a directory, writing XML symbol data to a separate output directory:
 
	java -jar SymbolScraper.jar -batch -p input_dir output_dir 

## Parameters

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


