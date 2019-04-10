# symbolScraper

An Apache [PDFBox](https://pdfbox.apache.org) extension for extracting symbol labels and bounding boxes from born-digital PDF files. Implemented in Java.

**Copyright (c) 2018, 2019 Ritvik Joshi, Parag Mali, Mahshad Mahdavi, and Richard Zanibbi**   

Developed at the [Document and Pattern Recognition Laboratory](https://www.cs.rit.edu/~dprl/index.html)  
(Rochester Institute of Technology, USA)

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
	[op1] write pdf with bounding boxes added 
		-b optional parameter to create PDFs with bounding box
		-f optional parameter to create PDFs with bounding box
	[op2] display symbol data in XML format 
		-d display on standard output
		-p print to file
	<input dir/file path> - input directory *or* PDF file path
	<output dir> - output directory (required in batch mode)


