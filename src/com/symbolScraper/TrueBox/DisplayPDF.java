package com.symbolScraper.TrueBox;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;


class DisplayPDF {
    String xmlFormat="";
    StringBuilder builder = new StringBuilder();
    ArrayList<PageStructure> allPages;
    DisplayPDF(ArrayList<PageStructure> allPages){
        this.allPages=allPages;
    }
    public void displayPage(int pagenum){

        PageStructure page = allPages.get(pagenum);
        int prevMergeId=0;
        String compoundIndent="";
        for(int lineIter=0;lineIter<page.Lines.size();lineIter++){

            Line line = page.Lines.get(lineIter);
            /*
            xmlFormat+="\t<Line id=\""+line.LineId+"\" BBOX="+line.baseLine.startX+" "+
                    line.baseLine.startY+" "+
                    line.baseLine.endX+" "+
                    line.baseLine.endY+" "+">\n";
            */
            xmlFormat="\t<Line id=\""+line.LineId+"\" BBOX=\""+line.baseLine.startX+" "+
                    line.baseLine.startY+" "+
                    line.baseLine.endX+" "+
                    line.baseLine.endY+"\""+">\n";
            builder.append(xmlFormat);
            for(int wordIter=0;wordIter<line.words.size();wordIter++){
                Words word = line.words.get(wordIter);
                //xmlFormat+="\t\t<Word id=\""+word.wordId+"\">\n";
                xmlFormat="\t\t<Word id=\""+word.wordId+"\">\n";
                builder.append(xmlFormat);
                boolean compoundOpen=false;
                for(int charIter=0;charIter<word.characters.size();charIter++){
                    int mergeID=word.characters.get(charIter).mergeId;
                    if(mergeID!=0 && prevMergeId!=mergeID){
                        /*
                        xmlFormat+="\t\t\t<CompoundChar id=\""+page.pageCompundCharacters.get(mergeID).charId+"\"" +
                                "BBOX=\""+page.pageCompundCharacters.get(mergeID).boundingBox.startX+" "+
                                +page.pageCompundCharacters.get(mergeID).boundingBox.startY+" "+
                                +page.pageCompundCharacters.get(mergeID).boundingBox.width+" "+
                                +page.pageCompundCharacters.get(mergeID).boundingBox.height+"\" "+
                                "Label=\""+page.pageCompundCharacters.get(mergeID).value+"\">\n";
                          */
                        if(prevMergeId!=0){
                            xmlFormat="\t\t\t</CompoundChar>\n";
                            builder.append(xmlFormat);
                        }
                        //System.out.println(mergeID);
                        xmlFormat="\t\t\t<CompoundChar id=\""+page.pageCompundCharacters.get(mergeID).charId+"\" " +
                                "BBOX=\""+page.pageCompundCharacters.get(mergeID).boundingBox.startX+" "+
                                +page.pageCompundCharacters.get(mergeID).boundingBox.startY+" "+
                                +page.pageCompundCharacters.get(mergeID).boundingBox.width+" "+
                                +page.pageCompundCharacters.get(mergeID).boundingBox.height+"\" "+
                                "Label=\""+page.pageCompundCharacters.get(mergeID).value+"\">\n";
                        builder.append(xmlFormat);
                        prevMergeId=mergeID;
                                compoundIndent="\t";
                                compoundOpen=true;

                    }
                    if(mergeID==0 && compoundOpen){
                        compoundOpen=false;
                        //xmlFormat+="\t\t\t</CompoundChar>\n";
                        xmlFormat="\t\t\t</CompoundChar>\n";
                        builder.append(xmlFormat);
                        prevMergeId=0;
                        compoundIndent="";
                    }
                   /*
                    xmlFormat+=compoundIndent+"\t\t\t<Char id=\""+word.characters.get(charIter).charId+"\" mergeId=\""+word.characters.get(charIter).mergeId+"\" "+
                            "BBOX=\""+word.characters.get(charIter).boundingBox.startX+" "+
                            +word.characters.get(charIter).boundingBox.startY+" "+
                            +word.characters.get(charIter).boundingBox.width+" "+
                            +word.characters.get(charIter).boundingBox.height+"\">"+
                            word.characters.get(charIter).value;
                    //System.out.print(word.characters.get(charIter).value);
                    xmlFormat+="</Char>\n";
                    */
                   TextColor textColor =  word.characters.get(charIter).textcolor;
                   String color;
                    if(textColor==null){
                        color="RGB=\"unknown\"";
                    }else {
                      color=colorString(textColor.color);
                    }
                    xmlFormat=compoundIndent+"\t\t\t<Char id=\""+word.characters.get(charIter).charId+"\" mergeId=\""+word.characters.get(charIter).mergeId+"\" "+
                            "BBOX=\""+word.characters.get(charIter).boundingBox.startX+" "+
                            word.characters.get(charIter).boundingBox.startY+" "+
                            word.characters.get(charIter).boundingBox.width+" "+
                            word.characters.get(charIter).boundingBox.height+"\" "+
                            color+">"+
                            word.characters.get(charIter).value;
                    //System.out.print(word.characters.get(charIter).value);
                    builder.append(xmlFormat);
                    xmlFormat="</Char>\n";
                    builder.append(xmlFormat);
                }
                if(prevMergeId!=0){
                    //xmlFormat+="\t\t\t</CompoundChar>\n";
                    xmlFormat="\t\t\t</CompoundChar>\n";
                    builder.append(xmlFormat);
                    prevMergeId=0;
                    compoundIndent="";
                }
                //xmlFormat+="\t\t</Word>\n";
                xmlFormat="\t\t</Word>\n";
                builder.append(xmlFormat);
                //System.out.print(" ");
            }
            //xmlFormat+="\t</Line>\n";
            xmlFormat="\t</Line>\n";
            builder.append(xmlFormat);
            //System.out.print("\n");
        }
        //System.out.println("***************************************\n***************************************\n");
    }

    public void displayPDF(){
        for(int i=0;i<allPages.size();i++){
            //xmlFormat+="<Page id=\""+i+"\">\n";
            xmlFormat="<Page id=\""+i+"\">\n";
            builder.append(xmlFormat);
            displayPage(i);
            //xmlFormat+="<\\Page>";
            xmlFormat="</Page>\n";
            builder.append(xmlFormat);
        }
    }

    public String colorString(PDColor color){
        return "RGB = \"" + Arrays.toString(color.getComponents())+"\"";
    }

}

