/******************************************************************************
* drawBBOX.java
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import static TrueBox.Main.Inpfilename;
import static TrueBox.Main.OutDir;
import static TrueBox.Main.fileseparator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

enum doctype{
    Normal,Filtered
}

public class drawBBOX {
    PDDocument document;
    ArrayList<PageStructure> allPages;

    drawBBOX(PDDocument doc, ArrayList<PageStructure> allPages){
        this.allPages=allPages;
        this.document=doc;
    }

    public void draw(int pagenum,doctype t) throws IOException {
       // drawPageBBOX(pagenum,allPages.get(pagenum).pageCharacters,allPages.get(pagenum).bars,t);
        drawPageBBOX(pagenum,allPages.get(pagenum).pageCharacters,allPages.get(pagenum).pageCompundCharacters,t);
    }

    public void drawPDF(doctype t) throws IOException {
        Logger logFile = new Logger("symbolscraper-log.csv");
        List<String[]> logData = new ArrayList<String[]>();
        logData.add(new String[] {"Page Number", "Parse Succeeded", "Parse Failed"});
        for(int i=0;i<allPages.size();i++){
//            if (i > 30) {
//                break; // TODO: used on laptop with too little memory
//            }
            try {
            draw(i,t);
                logData.add(new String[] {String.valueOf(i + 1), "1", "0"});
            } catch (IOException e) {
                logData.add(new String[] {String.valueOf(i + 1), "0", "1"});
                e.printStackTrace();
            }
        }
        logFile.writeBatchLog(logData);
    }

    //public void drawPageBBOX(int pagenum,HashMap<Integer, characterInfo> charList,ArrayList<Bars> bars, doctype t) throws IOException {
    public void drawPageBBOX(int pagenum,HashMap<Integer, characterInfo> charList,HashMap<Integer, compundCharacter> comChar, doctype t) throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = document.getPage(pagenum);
        doc.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);

        Iterator iter = charList.entrySet().iterator();

        // temporary box around the entire page for visualizing where the page is
        contentStream.addRect(page.getBBox().getLowerLeftX(), page.getBBox().getLowerLeftY(), page.getBBox().getWidth(), page.getBBox().getHeight());
        contentStream.setStrokingColor(Color.magenta);
        contentStream.stroke();

        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            characterInfo character = (characterInfo) pair.getValue();

            BBOX box = character.boundingBox;
            if(character.charInfo==null){
                contentStream.addRect(box.startX, box.startY, box.width, box.height);
                contentStream.setLineWidth((float)0.2);
                contentStream.setStrokingColor(Color.PINK);
                contentStream.stroke();
            }else {
                //character.charInfo.getTextMatrix().setValue(0, 2, 15);
                try {
                    int rotation = 0; //character.charInfo.getRotation();  // TODO: this is the PAGE'S rotation, not text.
                    if (rotation != 0) {  // TODO: for rotation to work, we need to find the ACTUAL rotation.
                        // we need to draw each side manually for the rotated character/bbox
                        // lines are drawn from lower left corner, anticlockwise back to it
                        
                        // first, start at lower left corner
                        contentStream.moveTo(box.startX, box.startY);
                        
                        // find new lower right corner and draw the line
                        double lowerRightX = box.width * Math.cos(rotation);
                        double lowerRightY = box.width * Math.sin(rotation);
                        contentStream.lineTo((float) lowerRightX, (float) lowerRightY);
                        
                        // find new upper right corner and draw the line
                        double upperRightX = lowerRightX - box.height * Math.sin(rotation);
                        double upperRightY = lowerRightY + box.height * Math.cos(rotation);
                        contentStream.lineTo((float) upperRightX, (float) upperRightY);

                        // find new upper left corner and draw the line
                        double upperLeftX = box.startX - box.height * Math.sin(rotation);
                        double upperLeftY = box.height * Math.cos(rotation);
                        contentStream.lineTo((float) upperLeftX, (float) upperLeftY);

                        // final stroke
                        contentStream.lineTo(box.startX, box.startY);
                        
                    } else {
                        contentStream.addRect(box.startX, box.startY, box.width, box.height);
                    }
                    //contentStream.addRect(character.charInfo.getTextMatrix().getTranslateX(), character.charInfo.getTextMatrix().getTranslateY(), character.charInfo.getWidth(), character.charInfo.getHeight());
                    if (t == doctype.Filtered) {
                        contentStream.setNonStrokingColor(Color.WHITE);
                        contentStream.fill();
                    } else if (t == doctype.Normal) {
                        contentStream.setLineWidth((float) 0.2);
                        contentStream.setStrokingColor(Color.GREEN);
                        contentStream.stroke();
                    } else {
                        System.out.println("Invalid doctype input for character::" + character.value);
                    }
                } catch (NullPointerException npe) {
                    // TODO
                }
            }
        }
        //for(Bars bar: bars )
        //for(Bars bar: comChar ){
        Iterator iter2 = comChar.entrySet().iterator();

        while(iter2.hasNext()){
            Map.Entry pair =(Map.Entry) iter2.next();

            compundCharacter com = (compundCharacter) pair.getValue();
            BBOX box = com.boundingBox;

            contentStream.addRect(box.startX, box.startY, box.width, box.height);
            if(t==doctype.Filtered) {
                contentStream.setNonStrokingColor(Color.WHITE);
                contentStream.fill();
            }
            else if(t==doctype.Normal){
                contentStream.setStrokingColor(Color.RED);
                contentStream.stroke();
            }
            else{
                System.out.println("Invalid doctype input");
            }
        }

        contentStream.closeAndStroke();
        contentStream.close();
        File outDirectory = new File(OutDir);
        if(!outDirectory.isDirectory()){
            outDirectory.mkdir();
        }
        String outfilename = OutDir+fileseparator +Inpfilename+"_"+ (t==doctype.Normal?"normal":"filtered")+ pagenum + ".pdf";
        File file = new File(outfilename);
        doc.save(file);
        doc.close();

    }

}
