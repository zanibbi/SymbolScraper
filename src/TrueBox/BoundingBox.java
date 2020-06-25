/******************************************************************************
* BoundingBox.java
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

import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.type1.Type1Font;
import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

//import org.apache.pdfbox.examples.util.DrawPrintTextLocations;

class BoundingBox extends PDFTextStripper {
//class BoundingBox extends DrawPrintTextLocations {
    int pageNum;
    PageStructure currentPage;
    int charNumber = 0;
    float prevBaseLineY=0;
    float prevBaseLineX=0;
    int lineId=0;
    int wordId=0;
    int charId=0;
    int mergeId=1;
    float lineStartX=0;
    float lineStartY=0;
    ArrayList<TextColor> colorList = new ArrayList<>();

    ArrayList<Words> WordList = new ArrayList<Words>();
    ArrayList<characterInfo> CharList = new ArrayList<characterInfo>();
    HashMap<Integer,compundCharacter> mergeMap= new HashMap<Integer,compundCharacter>();
    compundCharacter previousCharacter=null;
    ArrayList<characterInfo> radical =  new ArrayList<characterInfo>() ;

    public BoundingBox(PDDocument doc, int pagenum, PageStructure page) throws IOException {
//        super(doc, "filesname"); //drawprinttextlocations
        super(); //PDFTextStripper
        document = doc;
        this.pageNum = pagenum;
        this.currentPage = page;
        currentPage.pageCompundCharacters=mergeMap;
        setOperators();
    }
    
    public void setOperators(){
        addOperator(new SetStrokingColorSpace());
        addOperator(new SetNonStrokingColorSpace());
        addOperator(new SetStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceCMYKColor());
        addOperator(new SetNonStrokingDeviceRGBColor());
        addOperator(new SetStrokingDeviceRGBColor());
        addOperator(new SetNonStrokingDeviceGrayColor());
        addOperator(new SetStrokingDeviceGrayColor());
        addOperator(new SetStrokingColor());
        addOperator(new SetStrokingColorN());
        addOperator(new SetNonStrokingColor());
        addOperator(new SetNonStrokingColorN());
    }

    public String getGeometricInfo(int pagenum) throws IOException {
        BarDetection barD = new BarDetection(document.getPage(pagenum));
        currentPage.bars=barD.getAllBars();
        extract(pagenum);
        String transformation = getBoundingBox();
        includeBars();
        return transformation;
    }

    public void extract(int pageNum) throws IOException {
        this.setStartPage(pageNum + 1);
        this.setEndPage(pageNum + 1);
        this.pageNum = pageNum;
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        writeText(document, dummy);
        if(currentPage.pageCharacters.size()==0){
        	return;
        }
        Line prevLine = currentPage.Lines.get(currentPage.Lines.size()-1);
        prevLine.baseLine = new baseLine(lineStartX,lineStartY,prevBaseLineX,prevBaseLineY);
        //currentPage.Lines.add(new Line(lineId,prevLine.baseLine,WordList));
        dummy.close();
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        boolean singleWord=false;
        String currentword="";
        String word[] = string.split(getWordSeparator());
//        //debug
//        Utilities ut = new Utilities();
//        ut.debugWriteToPDF(string, textPositions, true);
        if(word.length==1){

            singleWord=true;
        }
        //mergeId++;
      TextPosition textLine = textPositions.get(0);
        if(textLine.getTextMatrix().getTranslateY()!= prevBaseLineY){
            if (prevBaseLineY==0){
                WordList =  new ArrayList<Words>();
                currentPage.Lines.add(new Line(lineId,null,WordList));
            }else{
                Line prevLine = currentPage.Lines.get(currentPage.Lines.size()-1);
                prevLine.baseLine = new baseLine(lineStartX,lineStartY,prevBaseLineX,prevBaseLineY);
                WordList =  new ArrayList<Words>();
                currentPage.Lines.add(new Line(lineId,null,WordList));
            }

            lineId+=1;
            prevBaseLineY = textLine.getTextMatrix().getTranslateY();
            lineStartY = textLine.getTextMatrix().getTranslateY();
            lineStartX = textLine.getTextMatrix().getTranslateX();

        }

        for (int i=0;i<textPositions.size();i++) {
            //System.out.println(text.getX());
            TextPosition text = textPositions.get(i);

            //String lineseparator = System.getProperty("line.separator");

            if(text.getUnicode().equals(getWordSeparator()) || text.getUnicode().equals("\uF020") ){
                WordList.add(new Words(wordId,CharList,currentword,false));
                wordId++;
                CharList= new ArrayList<characterInfo>();
                currentword="";
                continue;
            }

            int merge=0;
            String value = text.getUnicode();
			
			TextColor textColor=null;
            if(colorList.get(charId).unicode==value){
                textColor = colorList.get(charId);
            }else{
                //System.out.println("Color and text mismatch Text unicode: "+ value+" Color unicode :"+colorList.get(charId).unicode+" charid :"+charId);
            }

            characterInfo character = new characterInfo(charId,value,text,
                    null,null,null,merge,wordId,lineId-1,textColor);
            currentPage.pageCharacters.put(charId,character);
            CharList.add(character);
            charId++;
            currentword+=value;
            prevBaseLineX=text.getTextMatrix().getTranslateX()+text.getWidthDirAdj();


        }
        if(singleWord){
            WordList.add(new Words(wordId,CharList,word[0],false));
            wordId++;
            CharList= new ArrayList<characterInfo>();
        }


    }

	@Override
    protected void processTextPosition(TextPosition text)
    {
        super.processTextPosition(text);

        PDColor strokingColor = getGraphicsState().getStrokingColor();
        PDColor nonStrokingColor = getGraphicsState().getNonStrokingColor();
        String unicode = text.getUnicode();

        colorList.add(new TextColor(nonStrokingColor,unicode));
//        RenderingMode renderingMode = getGraphicsState().getTextState().getRenderingMode();
//        System.out.println("Unicode:            " + unicode);
//        System.out.println("Rendering mode:     " + renderingMode);
//        System.out.println("Stroking color:     " + strokingColor);
//        System.out.println("Non-Stroking color: " + nonStrokingColor);
//        System.out.println("Non-Stroking color: " + nonStrokingColor);
//        System.out.println();

        // See the PrintTextLocations for more attributes
    }

    public String getBoundingBox() throws IOException {
        HashMap<Integer, characterInfo> charList = currentPage.pageCharacters;

        Iterator iter = charList.entrySet().iterator();
        boolean first = true;
        String transformation = "";
        
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();

            characterInfo character = (characterInfo) pair.getValue();

            TextPosition text = character.charInfo;
        	
            // String originalInfo = "Initial Text: " + text.getUnicode() + "\n" +
            //                       "Original X, Y, Width Height (End): " + text.getEndX() + "," + text.getEndY() + "," + text.getWidth() + "," + text.getHeight() + "\n" + 
            //                       "Font Type: " + text.getFont() + "\n" +
            //                       "Font Size: " + text.getFontSize() + "\n" + 
            //                       "Text Rotation: " + text.getRotation() + "\n" +
            //                       "Page Width: " + text.getPageWidth() + "\n" + 
            //                       "Page Height: " + text.getPageHeight() + "\n" + 
            //                       "Adjusted X,Y Position For Directional Text: " + text.getXDirAdj() + "," + text.getYDirAdj() + "\n" + 
            //                       "Adjusted X,Y Position (0,0 upper sleft staritng point): " + text.getX() + text.getY() + "\n";  


            // starting information for lower left corner coords - shifted by offsets later
            float startX = text.getTextMatrix().getTranslateX();
            float startY = text.getTextMatrix().getTranslateY();

            currentPage.xmax = Math.max(startX, currentPage.xmax);
            currentPage.xmin = Math.min(startX, currentPage.xmin);
            currentPage.ymax = Math.max(startY + text.getHeight(), currentPage.ymax);
            currentPage.ymin = Math.min(startY + text.getHeight(), currentPage.ymin);
            
            float fontSize = text.getFontSizeInPt();
            Matrix scalingMatrix = text.getTextMatrix(); // this is the thing the fonts are scaled by!!

            PDFont font = text.getFont();
            drawGlyph glyph = null;

            // automatically determine the [likely] em square size - more accurate fine tuning per font later
            int emSquare = (int) (character.charInfo.getFont().getBoundingBox().getUpperRightX() - character.charInfo.getFont().getBoundingBox().getLowerLeftX());
            if (emSquare > 1550) { // TODO: this cutoff of 1550 works well it seems
                emSquare = 2048;
            } else {
                emSquare = 1000;
            }

            if (font instanceof PDTrueTypeFont){
//                System.out.println("TrueType Font");
                PDTrueTypeFont TTFfont = (PDTrueTypeFont) font;
                emSquare = TTFfont.getTrueTypeFont().getHeader().getUnitsPerEm();
                glyph = new drawGlyph(TTFfont.getPath(text.getCharacterCodes()[0]),
                        text.getCharacterCodes()[0], text.getUnicode(), fontSize, emSquare);
            }
            else if (font instanceof PDType1Font) {
//                System.out.println("Type1Font");
                PDType1Font Type1font = (PDType1Font) font;
                FontBoxFont FBFont = Type1font.getFontBoxFont();
                try {
                    emSquare = ((TrueTypeFont) FBFont).getUnitsPerEm();
                } catch (ClassCastException cce) {
                    // this emSquare setting works for some Type 1 fonts, others it doesn't
                    // for those it doesn't work for, it's OK, the above general check is accurate
                }

                try {
                    // TODO: this is for diacritics. Not sure if it's currently doing anything. Investigate later
                    GeneralPath diacriticpath = (GeneralPath) Type1font.getPath(Type1font.getGlyphList().codePointToName(text.getUnicode().codePointAt(1)).replace("cmb", "")).clone();
                    GeneralPath basepath = (GeneralPath) Type1font.getPath(Type1font.codeToName(text.getCharacterCodes()[0])).clone();
                    Rectangle bounds = diacriticpath.getBounds();
                    Rectangle bounds2 = basepath.getBounds();
                    Boolean descendingDiacritic = false;
                    if (bounds.y < bounds2.y) {
                        descendingDiacritic = true;
                    }
                    if (Character.isLowerCase(text.getUnicode().codePointAt(0)) || descendingDiacritic) { //lowercase or decending accent
                        AffineTransform transform = new AffineTransform();
                        basepath.append(diacriticpath.getPathIterator(transform), true);
                        glyph = new drawGlyph(basepath,
                                text.getCharacterCodes()[0], text.getUnicode(), fontSize, emSquare);

                    } else { // upper case with ascending accent
                        AffineTransform transform = new AffineTransform();
                        //                        float margin = PDfont.getFontDescriptor().getFontBoundingBox().getUpperRightY() - (bounds.y + bounds.height);
                        float margin = bounds.y - font.getFontDescriptor().getXHeight();
                        float newy = -bounds.y + font.getFontDescriptor().getAscent() + margin;
                        //                          float newy = -bounds.y + PDfont.getFontDescriptor().getAscent() + 90 + bounds.height/2;
                        transform.translate(0, newy);
                        basepath.append(diacriticpath.getPathIterator(transform), true);
                        glyph = new drawGlyph(basepath,
                                text.getCharacterCodes()[0], text.getUnicode(), fontSize, emSquare);

                    }
                } catch (Exception e) {
//                    System.out.println("No code point at 1.");
//                    System.out.println(e);
                    glyph = new drawGlyph(Type1font.getPath(Type1font.codeToName(text.getCharacterCodes()[0])),
                            text.getCharacterCodes()[0], text.getUnicode(), fontSize, emSquare);
                }

            }
            else if (font instanceof PDType1CFont) {
//                System.out.println("Type1CFont");
                PDType1CFont Type1Cfont = (PDType1CFont) font;
//                System.out.println(Type1Cfont.isEmbedded());
                if(true) {
                    glyph = new drawGlyph(Type1Cfont.getPath(Type1Cfont.codeToName(text.getCharacterCodes()[0])),
                            text.getCharacterCodes()[0], text.getUnicode(), fontSize, emSquare);
                } else {
                    glyph = new drawGlyph(Type1Cfont.getPath(Type1Cfont.codeToName(text.getCharacterCodes()[0])),
                            text.getCharacterCodes()[0], text.getUnicode(), fontSize, emSquare);
                }
            }
            else if (font instanceof PDType0Font) { //CID Fonts
//                System.out.println("Type0Font");
                PDType0Font Type0font = (PDType0Font) font;
                PDCIDFont descendant = Type0font.getDescendantFont();
                int emsize = 1000;

                if (descendant instanceof PDCIDFontType2) {
                    emsize = ((PDCIDFontType2) descendant).getTrueTypeFont().getHeader().getUnitsPerEm();
                } else if (descendant instanceof PDCIDFontType0){
                    emsize = 1000;
                }
                glyph = new drawGlyph(Type0font.getPath(text.getCharacterCodes()[0]),
                        text.getCharacterCodes()[0], text.getUnicode(), fontSize, emsize);
            }
            else if (font instanceof PDType3Font) {
                // TODO: an attempt at making Type 3 work. It still doesn't, unfortunately. Future work
                // currently makes tiny little boxes in the bottom left corners of characters
                PDType1Font maskFont = PDType1Font.TIMES_ROMAN;
                glyph = new drawGlyph(maskFont.getPath(maskFont.getName()),
                        text.getCharacterCodes()[0], text.getUnicode(), fontSize, emSquare);
            }else{
                //PDType1Font font1 = (PDType1Font) font;
                System.out.println("Unknown font type, skipping character at"+" Page:"+pageNum+" Label::" +text.getUnicode() + ". Font descriptor: " + font.getFontDescriptor());
                continue;
            }

            PDType1Font maskFont = PDType1Font.TIMES_ROMAN; //masking font for Type 3
            double width, height;
            try {
                glyph.coordinates();
                glyph.BoxCoord();

                character.glyph=glyph;

                width = glyph.adjustResolution(glyph.maxX, scalingMatrix.getScaleX()) - glyph.adjustResolution(glyph.minX, scalingMatrix.getScaleX());
                height = glyph.adjustResolution(glyph.maxY, scalingMatrix.getScaleY()) - glyph.adjustResolution(glyph.minY, scalingMatrix.getScaleY());
            }catch(Exception e){
                //TODO: this was to deal with Type 3 fonts, we might not need the try catch anymore
                width = (text.getWidth());
                height = character.charInfo.getHeight();  //scalingMatrix.getScaleY();
            }

            // updating starting point by shifting by x and y offsets
            // shift x coordinate
            double widthRatio = width / (glyph.maxX - glyph.minX);
            startX = startX + (float)(widthRatio * glyph.minX);

            // shift y coordinate
            double heightRatio = height / (glyph.maxY-glyph.minY);
            startY = startY + (float)(heightRatio * glyph.minY);

            character.boundingBox = new BBOX(startX, startY, (float) width, (float) height);


            // TODO: what is this for? Investigate later
            if(character.glyph != null && character.glyph.segmentCount > 1) { // check command line flag
                character.segmentBoxes = new ArrayList<BBOX>();
                for(int i = 0; i < character.glyph.segmentCount; i++){
                    startX = text.getTextMatrix().getTranslateX();
                    startY = text.getTextMatrix().getTranslateY();
                    width = glyph.adjustResolution(glyph.compMaxX.get(i),
                            fontSize) - glyph.adjustResolution(glyph.compMinX.get(i),
                            fontSize);
                    height =
                            glyph.adjustResolution(glyph.compMaxY.get(i),
                            fontSize) - glyph.adjustResolution(glyph.compMinY.get(i),
                                    fontSize);

//                     Update starting point (Y-axis)
                    heightRatio =
                            height / (glyph.compMaxY.get(i) - glyph.compMinY.get(i));
                    startY = startY + (float)(heightRatio * glyph.compMinY.get(i));

                    // Update starting point (X-axis)
                    //double widthRatio = width / (font.getWidthFromFont(text.getCharacterCodes()[0]));
                    widthRatio = heightRatio; // TODO: probably not a good idea
                    startX = startX + (float)(widthRatio * glyph.compMinX.get(i));

                    character.segmentBoxes.add(new BBOX(startX, startY,
                            (float) width, (float) height));
                }

            }


            if(character.value.equals('\u221A') || character.value.equals('\u23B7') || character.value.equals("√") ) {
                radical.add(character);
            }

            if(previousCharacter==null){
                BBOX tempBox = new BBOX(character.boundingBox.startX,character.boundingBox.startY,character.boundingBox.width,character.boundingBox.height);
                ArrayList<Integer> neighList = new ArrayList<Integer>();
                neighList.add(character.charId);
                previousCharacter= new compundCharacter(mergeId,character.value,neighList,tempBox,character.charInfo.getFontSize());
            }else {
                //if (notAlphabet(character.value) && notAlphabet(previousCharacter.value) && overlap(previousCharacter.boundingBox, character.boundingBox)) {
                if (overlap(previousCharacter.boundingBox, character.boundingBox)){

                    if (mergeMap.containsKey(mergeId)) {
                        compundCharacter comChar = mergeMap.get(mergeId);
                        ArrayList<Integer> neighList = comChar.charList;
                        boolean flag=false;
                        for (int id:neighList) {
                            characterInfo mergeChar = currentPage.pageCharacters.get(id);
                            if (intersect(mergeChar,character)) {
                                flag=true;
                                break;
                            }
                        }
                        if(flag){
                            character.mergeId = mergeId;
                            previousCharacter.value="unknown";
                            //ArrayList<Integer> neighList = comChar.charList;
                            neighList.add(character.charId);
                            BBOX combox = comChar.boundingBox;
                            float newStartX = findMin(combox.startX, startX);
                            float newStartY = findMin(combox.startY, startY);
                            float newEndX = findMax(combox.startX + combox.width, startX + (float) width);
                            float newEndY = findMax(combox.startY + combox.height, startY + (float) height);
                            combox.startX = newStartX;
                            combox.startY = newStartY;
                            combox.width = newEndX - newStartX;
                            combox.height = newEndY - newStartY;

                        }
                    } else{
                        ArrayList<Integer> neighList = previousCharacter.charList;
                        boolean flag=false;
                        for (int id:neighList) {
                            characterInfo mergeChar = currentPage.pageCharacters.get(id);

                            if (intersect(mergeChar, character)) {
                                flag=true;
                                break;
                            }
                        }
                        if(flag){
                            character.mergeId = mergeId;
                            previousCharacter.value="unknown";
                            for (Integer charid : neighList) {
                                characterInfo prev = charList.get(charid);
                                prev.mergeId = mergeId;
                            }
                            neighList.add(character.charId);
                            BBOX prevbox = previousCharacter.boundingBox;
                            float newStartX = findMin(prevbox.startX, startX);
                            float newStartY = findMin(prevbox.startY, startY);
                            float newEndX = findMax(prevbox.startX + prevbox.width, startX + (float) width);
                            float newEndY = findMax(prevbox.startY + prevbox.height, startY + (float) height);

                            prevbox.startX = newStartX;
                            prevbox.startY = newStartY;
                            prevbox.width = newEndX - newStartX;
                            prevbox.height = newEndY - newStartY;
                            mergeMap.put(mergeId, previousCharacter);

                        }
                    }

                }
                else {
                    mergeId++;
                    ArrayList<Integer> neighList = new ArrayList<Integer>();
                    neighList.add(character.charId);
                    BBOX tempBox = new BBOX(character.boundingBox.startX,character.boundingBox.startY,character.boundingBox.width,character.boundingBox.height);
                    previousCharacter= new compundCharacter(mergeId,character.value,neighList,tempBox,character.charInfo.getFontSize());
                }
            }
        }
        
        return transformation;
    }


    public float findMax(float val1,float val2){
        return (val1>val2)?val1:val2;
    }

    public float findMin(float val1,float val2){
        return (val1<val2)?val1:val2;
    }

    public boolean overlap(BBOX ch1,BBOX ch2){

        if (ch1.startX > (ch2.startX+ch2.width) || ch2.startX > (ch1.startX+ch1.width))
            return false;
        if ((ch1.startY+ch1.height) < (ch2.startY) || (ch2.startY+ch2.height) < (ch1.startY))
            return false;

        return true;
    }

    public boolean notAlphabet(String value){
        Pattern p = Pattern.compile("[^\\p{ASCII}]");
        boolean result = p.matcher(value).find();
        return  result;
    }

    public void includeBars(){
        //includeRadicalBars();

        for(characterInfo chID: radical){
            int lastChar = currentPage.pageCharacters.size();
            if(chID.mergeId!=0){
                //System.out.println(chID.mergeId);
                compundCharacter comChar = mergeMap.get(chID.mergeId);
                BBOX combox = comChar.boundingBox;
                BBOX ownbox = chID.boundingBox;
                for(int i=0;i<currentPage.bars.size();i++) {
                    Bars bar = currentPage.bars.get(i);

                    if(overlap(combox,bar.boundingBox) || overlap(ownbox,bar.boundingBox)){
                        ArrayList<Integer> neighList = comChar.charList;
                        BBOX tempBox = new BBOX(bar.boundingBox.startX,bar.boundingBox.startY,bar.boundingBox.width,bar.boundingBox.height);
                        characterInfo  newchar = new characterInfo(lastChar,
                                "square root bar(-)", null,tempBox,null,
                                null, chID.mergeId,chID.wordID,chID.lineID,
                                null);
                        currentPage.pageCharacters.put(lastChar,newchar);
                        neighList.add(newchar.charId);

                        float newStartX = findMin(combox.startX, bar.boundingBox.startX);
                        float newStartY = findMin(combox.startY, bar.boundingBox.startY);
                        float newEndX = findMax(combox.startX + combox.width, bar.boundingBox.startX +  bar.boundingBox.width);
                        float newEndY = findMax(combox.startY + combox.height, bar.boundingBox.startY +  bar.boundingBox.height);
                        combox.startX = newStartX;
                        combox.startY = newStartY;
                        combox.width = newEndX - newStartX;
                        combox.height = newEndY - newStartY;

                        ArrayList<Line> lineList = currentPage.Lines;
                        ArrayList<Words> wordList = lineList.get(chID.lineID).words;
                        for(Words word:wordList) {
                            if(word.wordId==chID.wordID) {
                                ArrayList<characterInfo> charList = word.characters;
                                charList.add(newchar);
                            }
                        }

                        currentPage.bars.remove(bar);
                        break;
                    }
                }
                //mergeId++;
                //mergeMap.put(mergeId,comChar);

            }else{
                mergeId++;
                BBOX tempBox = new BBOX(chID.boundingBox.startX,chID.boundingBox.startY,chID.boundingBox.width,chID.boundingBox.height);
                ArrayList<Integer> neighList = new ArrayList<Integer>();
                neighList.add(chID.charId);
                compundCharacter comchar= new compundCharacter(mergeId,chID.value,neighList,tempBox,chID.charInfo.getFontSize());
                chID.mergeId=mergeId;
                BBOX combox = comchar.boundingBox;
                for(int i=0;i<currentPage.bars.size();i++) {
                    Bars bar = currentPage.bars.get(i);
                    if (overlap(combox, bar.boundingBox)) {
                        BBOX barBox = new BBOX(bar.boundingBox.startX, bar.boundingBox.startY, bar.boundingBox.width, bar.boundingBox.height);
                        characterInfo newchar = new characterInfo(lastChar,
                                "square root bar(-)", null, barBox,null, null,
                                chID.mergeId,chID.wordID,chID.lineID,null);
                        currentPage.pageCharacters.put(lastChar, newchar);
                        neighList = comchar.charList;
                        neighList.add(newchar.charId);
                        float newStartX = findMin(combox.startX, bar.boundingBox.startX);
                        float newStartY = findMin(combox.startY, bar.boundingBox.startY);
                        float newEndX = findMax(combox.startX + combox.width, bar.boundingBox.startX + bar.boundingBox.width);
                        float newEndY = findMax(combox.startY + combox.height, bar.boundingBox.startY + bar.boundingBox.height);
                        combox.startX = newStartX;
                        combox.startY = newStartY;
                        combox.width = newEndX - newStartX;
                        combox.height = newEndY - newStartY;

                        ArrayList<Line> lineList = currentPage.Lines;
                        ArrayList<Words> wordList = lineList.get(chID.lineID).words;
                        for(Words word:wordList) {
                            if(word.wordId==chID.wordID) {
                                ArrayList<characterInfo> charList = word.characters;
                                int index=charList.indexOf(chID);
                                charList.add(index+1,newchar);
                            }
                        }
                        mergeMap.put(mergeId,comchar);
                        currentPage.bars.remove(bar);
                        break;
                    }
                }


            }
        }

        ArrayList<Line> allLines =  currentPage.Lines;
        ArrayList<Bars> allBars = currentPage.bars;
        System.out.println("All bars found on this page:\n" + currentPage.bars);
        // TODO: this for loop seems to be a good place for square root detection to go
        for(int i=0;i<allBars.size();i++) {
            int lastChar = currentPage.pageCharacters.size();
            int aboveLine = 0;
            int belowLine = 0;
            boolean flag=false;
            for (int j = 0; j < allLines.size(); j++) {
                try {
                    if ((allLines.get(j).baseLine.startY > allBars.get(i).boundingBox.startY)){ //&&
                        // TODO: commented out this and below print debuggers. Remove lines if no longer needed.
//                        System.out.println("Above line");
                        aboveLine = j;
                    } else if ((allLines.get(j).baseLine.startY <= allBars.get(i).boundingBox.startY)){// &&
//                        System.out.println("Below line");
                        //belowLine = j;
                        Line line = allLines.get(aboveLine);
                        ArrayList<Words> wordlist = line.words;
                        lastChar = currentPage.pageCharacters.size();
                        characterInfo newChar = new characterInfo(lastChar ,
                                "fraction(-)", null,
                                allBars.get(i).boundingBox,null,null, 0,
                                wordlist.size(),line.LineId,null);
                        ArrayList<characterInfo> charList = new ArrayList<characterInfo>();
                        charList.add(newChar);
                        Words newWord = new Words(wordlist.size(), charList,"/",false);
                        wordlist.add(newWord);
                        currentPage.pageCharacters.put(lastChar , newChar);
                        //System.out.println("Bar added into characters");
                        break;
                    }
                }catch (Exception e){

                    System.out.println(i+" "+j);
                    e.printStackTrace();
                }

            }
        }


    }

    public comCharPath getPath(characterInfo mergeChar, characterInfo character) throws IOException {
        float curroffsetX=0;
        float curroffsetY =0;
        float prevoffsetX=0;
        float prevoffsetY =0;
        float currfontFactor=0;
        float prevfontFactor=0;

        float mergeCharstartX=mergeChar.charInfo.getTextMatrix().getTranslateX();
        float mergeCharstartY=mergeChar.charInfo.getTextMatrix().getTranslateY();

        float characterstartX=character.charInfo.getTextMatrix().getTranslateX();
        float characterstartY=character.charInfo.getTextMatrix().getTranslateY();
		double heightRatio;
		if(character.boundingBox.height > mergeChar.boundingBox.height)
        	heightRatio = character.boundingBox.height/(character.glyph.maxY-character.glyph.minY);
        else
        	heightRatio = mergeChar.boundingBox.height/(mergeChar.glyph.maxY-mergeChar.glyph.minY);
        //double heightRatio =0.01;
        if (mergeChar.glyph.minY < 0) {
            double decentheight = heightRatio * mergeChar.glyph.minY;
            mergeCharstartY = mergeCharstartY + (float) decentheight;
        } else if (mergeChar.glyph.minY > 0) {
            double baseAccent = heightRatio * mergeChar.glyph.minY;
            mergeCharstartY = mergeCharstartY - (float) baseAccent;
        }
        // Update starting point (X-axis)
        //double widthRatio = width / (font.getWidthFromFont(text.getCharacterCodes()[0]));
        double widthRatio=heightRatio;
        if (mergeChar.glyph.minX < 0) {
            double leftMove = widthRatio * mergeChar.glyph.minX;
            mergeCharstartX = mergeCharstartX + (float) leftMove;
        } else if (mergeChar.glyph.minX > 0) {
            double rightMove = widthRatio * mergeChar.glyph.minX;
            mergeCharstartX = mergeCharstartX - (float) rightMove;
        }

/////////////////////
        if (character.glyph.minY < 0) {
            double decentheight = heightRatio * character.glyph.minY;
            characterstartY = characterstartY + (float) decentheight;
        } else if (character.glyph.minY > 0) {
            double baseAccent = heightRatio * character.glyph.minY;
            characterstartY = characterstartY - (float) baseAccent;
        }
        // Update starting point (X-axis)
        //double widthRatio = width / (font.getWidthFromFont(text.getCharacterCodes()[0]));

        if (character.glyph.minX < 0) {
            double leftMove = widthRatio * character.glyph.minX;
            characterstartX =characterstartX + (float) leftMove;
        } else if (mergeChar.glyph.minX > 0) {
            double rightMove = widthRatio * character.glyph.minX;
            characterstartX = characterstartX - (float) rightMove;
        }





        double currglyphheight = Math.abs(character.glyph.maxY - character.glyph.minY);
        double currglyphwidth = Math.abs(character.glyph.maxX - character.glyph.minX);

        //double ratio = mergeChar.boundingBox.height/(mergeChar.glyph.maxY-mergeChar.glyph.minY);
        double ratio = widthRatio;
        if(characterstartX > mergeCharstartX) {
            prevoffsetX = (mergeCharstartX-characterstartX) / (float) ratio;
            curroffsetX=0;

        }
        else{
            curroffsetX = ( characterstartX-mergeCharstartX) / (float) ratio;
            prevoffsetX=0;
        }
        if(characterstartY > mergeCharstartY) {
            curroffsetY = (characterstartY - mergeCharstartY) / (float) ratio;
            prevoffsetY=0;
        }
        else{
            prevoffsetY = (mergeCharstartY-characterstartY ) / (float) ratio;
            curroffsetY=0;
        }
        if(mergeChar.charInfo.getFontSize()> character.charInfo.getFontSize()) {
            currfontFactor = mergeChar.charInfo.getFontSize() / character.charInfo.getFontSize();
            prevfontFactor =1;
        }
        else{
            prevfontFactor = mergeChar.charInfo.getFontSize() / character.charInfo.getFontSize();
            currfontFactor =1;
        }
        GeneralPath mergeCharGlyphPath = getUpdatedPath(mergeChar.glyph,prevoffsetX,prevoffsetY,prevfontFactor);
        GeneralPath currGlyphPath = getUpdatedPath(character.glyph,curroffsetX,curroffsetY,currfontFactor);
        comCharPath comchar = new comCharPath(mergeCharGlyphPath,currGlyphPath);

        double imageheight = currglyphheight+ Math.abs(mergeChar.glyph.maxY - mergeChar.glyph.minY);
        double imagewidth = currglyphwidth+ Math.abs(mergeChar.glyph.maxX - mergeChar.glyph.minX);



        /*
        BufferedImage image = new BufferedImage((int)imagewidth +1000,(int)imageheight+1000, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphic = image.createGraphics();
        graphic.fill(mergeCharGlyphPath);
        graphic.fill(currGlyphPath);


        //Flip the image
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        //  System.out.println("Transform = "+ (int)(maxY.startY+maxY.height));
        tx.translate(0, -(int)(imageheight+1000));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = op.filter(image, null);



        File output = new File("output"+charNumber+".png");
        ImageIO.write(image, "png", output);
        charNumber++;
    */
        return comchar;
    }


    public GeneralPath getUpdatedPath(drawGlyph glyph,float offsetX, float offsetY, float fontFactor){
        //System.out.println("OffsetX:"+offsetX+" "+"OffsetY:"+offsetY);
        GeneralPath newPath = new GeneralPath();

        for(int i=0;i<glyph.op.size();i++) {
            switch (glyph.op.get(i)) {
                case PathIterator.SEG_CLOSE:
                    newPath.closePath();
                    break;

                case PathIterator.SEG_CUBICTO:
                    newPath.curveTo((glyph.allx1.get(i)+Math.abs(glyph.minX))*fontFactor+offsetX+500,
                            (glyph.ally1.get(i)+Math.abs(glyph.minY))*fontFactor+offsetY+500,
                            (glyph.allx2.get(i)+Math.abs(glyph.minX))*fontFactor+offsetX+500,
                            (glyph.ally2.get(i)+Math.abs(glyph.minY))*fontFactor+offsetY+500,
                            (glyph.allx3.get(i)+Math.abs(glyph.minX))*fontFactor+offsetX+500,
                            (glyph.ally3.get(i)+Math.abs(glyph.minY))*fontFactor+offsetY+500);
                    break;

                case PathIterator.SEG_LINETO:
                    newPath.lineTo((glyph.allx1.get(i)+Math.abs(glyph.minX))*fontFactor+offsetX+500,
                            (glyph.ally1.get(i)+Math.abs(glyph.minY))*fontFactor+offsetY+500);
                    break;

                case PathIterator.SEG_MOVETO:
                    newPath.moveTo((glyph.allx1.get(i)+Math.abs(glyph.minX))*fontFactor+offsetX+500,
                            (glyph.ally1.get(i)+Math.abs(glyph.minY))*fontFactor+offsetY+500);
                    break;

                case PathIterator.SEG_QUADTO:
                    newPath.quadTo((glyph.allx1.get(i)+Math.abs(glyph.minX))*fontFactor+offsetX+500,
                            (glyph.ally1.get(i)+Math.abs(glyph.minY))*fontFactor+offsetY+500,
                            (glyph.allx2.get(i)+Math.abs(glyph.minX))*fontFactor+offsetX+500,
                            (glyph.ally2.get(i)+Math.abs(glyph.minY))*fontFactor+offsetY+500);
                    break;
            }

        }

        return newPath;
    }




    public boolean intersect(characterInfo mergeChar, characterInfo character) throws IOException {
//        return true;
        comCharPath comchar = getPath(mergeChar,character);
        Area areaA = new Area(comchar.A);
        areaA.intersect(new Area(comchar.B));
        return !areaA.isEmpty();
    }

}

class comCharPath{
    Shape A;
    Shape B;

    comCharPath(Shape A,Shape B){
        this.A=A;
        this.B=B;
    }

}