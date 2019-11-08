/******************************************************************************
* read.java
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
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class read extends PDFTextStripper{

    ArrayList<PageStructure> allPages = new ArrayList<PageStructure>();


    /**
     * Instantiate a new PDFTextStripper object.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public read(PDDocument doc) throws IOException {
        super();
        document =doc;

    }


    public ArrayList<PageStructure> readPdf() throws IOException {

        int numPages = document.getNumberOfPages();

        for(int i=0;i<numPages;i++){
            PageStructure currentPage = readText(i);
            allPages.add(currentPage);
        }

        return allPages;

    }

    public PageStructure readText(int pagenum) throws IOException {
        PageStructure page = new PageStructure(pagenum,new ArrayList<Line>(),null,
                new HashMap<Integer, characterInfo>(),
                new HashMap<Integer, compundCharacter>(),null);
        BoundingBox BBox = new BoundingBox(document,pagenum,page);
        String transformation = BBox.getGeometricInfo(pagenum);
        metadata meta = new metadata(BBox.lineId,BBox.wordId,BBox.charId, transformation);
        page.meta=meta;
        // System.out.println("PageNume::"+BBox.pageNum+" Lines::"+BBox.lineId+" Words::"+  BBox.wordId+" Characters::"+BBox.charId);
        return page;
    }

//    public void readText(int pagenum){
//        PDDocument doc = new PDDocument();
//        doc.addPage(document.getPage(pagenum));
//        try {
//            PDFTextStripper textStripper = new PDFTextStripper();
//            String allText=textStripper.getText(doc).replaceAll("\\.",".sep;");
//
//            String allSentence[] = allText.split("sep;");
//
//            PageStructure page = new PageStructure(pagenum,new ArrayList<Sentence>(),new HashMap<Integer,characterInfo>());
//            int charNumber=0;
//            for(int sentenceIter=0;sentenceIter<allSentence.length;sentenceIter++){
//                String words[] = allSentence[sentenceIter].split("\\s+");
//                Sentence sentence = new Sentence(sentenceIter,new ArrayList<Words>());
//                for(int wordIter=0;wordIter<words.length;wordIter++){
//                    Words word = new Words(wordIter,new ArrayList<characterInfo> ());
//                    char characters[] = words[wordIter].toCharArray();
//                    for(int charIter=0;charIter<characters.length;charIter++){
//                        if(characters[charIter]!='\n') {
//                            charNumber++;
//                            characterInfo character = new characterInfo(charNumber, characters[charIter], null,null);
//                            word.characters.add(character);
//                            page.pageCharacters.put(charNumber,character);
//                        }
//                    }
//                    sentence.words.add(word);
//                }
//
//                page.sentences.add(sentence);
//            }
//            allPages.add(page);
//
//        }
//        catch (Exception e){
//            System.out.println("readText");
//            e.printStackTrace();
//        }
//
//    }



}
