package com.symbolScraper.TrueBox;

class DrawSymbol {

    drawGlyph obj;
    float startX;
    float startY;
    double height;
    double width;
    double fontSize;
    public DrawSymbol(drawGlyph obj,float startX,	float startY,double fs) {
        // TODO Auto-generated constructor stub
        this.obj = obj;
        this.startX = startX;
        this.startY = startY;
        this.fontSize = fs;
    }

}