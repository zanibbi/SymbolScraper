package com.symbolScraper.TrueBox;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.text.TextPosition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

class PageStructure{
    int pageId;
    ArrayList<Line> Lines;
    ArrayList<Bars> bars;
    HashMap<Integer,characterInfo> pageCharacters;
    HashMap<Integer,compundCharacter> pageCompundCharacters;
    metadata meta;
    public float xmax = Integer.MIN_VALUE, xmin = Integer.MAX_VALUE, ymax = Integer.MIN_VALUE, ymin = Integer.MAX_VALUE;
    
    PageStructure(int pgId, ArrayList<Line> sent,ArrayList<Bars> bars, HashMap<Integer,characterInfo> pageChar,HashMap<Integer,compundCharacter> comChar, metadata meta){
        this.pageId=pgId;
        this.Lines=sent;
        this.bars=bars;
        this.pageCharacters= pageChar;
        this.pageCompundCharacters=comChar;
        this.meta =meta;
    }

}

class Line{
    int LineId;
    ArrayList<Words> words;
    baseLine baseLine;
    Line(int sentid, baseLine baseline, ArrayList<Words> words){
        this.LineId=sentid;
        this.words=words;
        this.baseLine=baseline;
    }

}

class Words{
    int wordId;
    String wordString;
    boolean NonMath;
    ArrayList<characterInfo> characters;
    Words(int wordid, ArrayList<characterInfo> charInfo,String wordString, boolean NonMath ){
        this.wordId=wordid;
        this.characters =charInfo;
        this.wordString=wordString;
        this.NonMath=NonMath;

    }

}

class Bars{
    BBOX boundingBox;
    Bars(BBOX boundingBox){
        this.boundingBox=boundingBox;
    }
}

class characterInfo{

    int charId;
    String value;
    BBOX boundingBox;
    int mergeId;
    int wordID;
    int lineID;
    org.apache.pdfbox.text.TextPosition charInfo;
    drawGlyph glyph;
    TextColor textcolor;
    
    characterInfo(int charId,String value,TextPosition text,BBOX bbox,drawGlyph glyph, int mergeId,int wordID,int lineID,TextColor textcolor){
        this.charId=charId;
        this.value=value;
        this.charInfo = text;
        this.boundingBox=bbox;
        this.mergeId = mergeId;
        this.wordID=wordID;
        this.lineID=lineID;
        this.glyph=glyph;
        this.textcolor = textcolor;
    }

}


class compundCharacter{
    int charId;
    String value;
    BBOX boundingBox;

    float font;

    ArrayList<Integer> charList;

    compundCharacter(int mergeId,String value,ArrayList<Integer> charList,BBOX bbox,float font){
        this.charId=mergeId;
        this.value=value;
        this.boundingBox=bbox;
        this.charList=charList;
        this.font=font;
    }

}


class BBOX{
    float startX;
    float startY;
    float width;
    float height;

    BBOX(float x,float y, float w,float h){
        this.startX=x;
        this.startY=y;
        this.width=w;
        this.height=h;
    }

}

class baseLine{
    float startX;
    float startY;
    float endX;
    float endY;
    baseLine(float x,float y, float x2, float y2){
        this.startX=x;
        this.startY=y;
        this.endX=x2;
        this.endY=y2;
    }
}

@Getter
@Setter
@AllArgsConstructor
@ToString
class metadata{
    int linecount;
    int wordcount;
    int charactercount;
    String transformation;
    
    metadata(int linecount,int wordcount, int charactercount){
        this.linecount=linecount;
        this.wordcount=wordcount;
        this.charactercount=charactercount;
        this.transformation = null;
    }
}

class TextColor{
    PDColor color;
    String unicode;

    TextColor(PDColor color, String unicode){
        this.color=color;
        this.unicode=unicode;
    }
}
