package com.symbolScraper.TrueBox;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

class BarDetection extends PDFGraphicsStreamEngine {

    private final GeneralPath linePath = new GeneralPath();
    private int clipWindingRule = -1;
    PDPage page;
    PDPageContentStream contentStream;
    private ArrayList<Bars> allBars = new ArrayList<Bars>();

    /**
     * Constructor.
     *
     * @param page
     */
    protected BarDetection(PDPage page) {
        super(page);
        this.page=page;
    }


    public ArrayList<Bars> getAllBars() throws IOException {
        processPage(page);
        //filterBars();
        return allBars;
    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException
    {
        //System.out.println("appendRectangle");
        // to ensure that the path is created in the right direction, we have to create
        // it by combining single lines instead of creating a simple rectangle
        //System.out.println("This Rectangle");
        linePath.moveTo((float) p0.getX(), (float) p0.getY());
        linePath.lineTo((float) p1.getX(), (float) p1.getY());
        linePath.lineTo((float) p2.getX(), (float) p2.getY());
        linePath.lineTo((float) p3.getX(), (float) p3.getY());

        // close the subpath instead of adding the last line so that a possible set line
        // cap style isn't taken into account at the "beginning" of the rectangle
        linePath.closePath();
    }

    @Override
    public void drawImage(PDImage pdi) throws IOException
    {
    }

    @Override
    public void clip(int windingRule) throws IOException
    {
        // the clipping path will not be updated until the succeeding painting operator is called
        clipWindingRule = windingRule;

    }

    @Override
    public void moveTo(float x, float y) throws IOException
    {
        linePath.moveTo(x, y);
       // System.out.println("moveTo");
    }

    @Override
    public void lineTo(float x, float y) throws IOException
    {
        linePath.lineTo(x, y);
        //System.out.println("lineTo");
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException
    {
        linePath.curveTo(x1, y1, x2, y2, x3, y3);
        //System.out.println("curveTo");
    }

    @Override
    public Point2D getCurrentPoint() throws IOException
    {
        return linePath.getCurrentPoint();
    }

    @Override
    public void closePath() throws IOException
    {

        linePath.closePath();
    }

    @Override
    public void endPath() throws IOException
    {
        //System.out.println("This is end Path");
        if (clipWindingRule != -1)
        {
            linePath.setWindingRule(clipWindingRule);
            getGraphicsState().intersectClippingPath(linePath);
            clipWindingRule = -1;
        }
        linePath.reset();

    }

    @Override
    public void strokePath() throws IOException
    {
        // do stuff
        //System.out.println(linePath.getBounds2D().getMinX()+" "+linePath.getBounds2D().getMaxX()+" "+linePath.getBounds2D().getMinY()+" "+linePath.getBounds2D().getMaxY());

        drawGlyph glyph = new drawGlyph(linePath,0,"Bars",0,0);
        glyph.coordinates();
        glyph.BoxCoord();
        //glyph.saveAsImage();
        allBars.add(new Bars(new BBOX((float)glyph.minX,(float)glyph.minY,(float)(glyph.maxX-glyph.minX),(float)(glyph.maxY-glyph.minY))));
        /*
        allBars.add(new Bars(new BBOX((float)linePath.getBounds2D().getMinX(),(float)linePath.getBounds2D().getMinY(),
                (float)(linePath.getBounds2D().getMaxX()-linePath.getBounds2D().getMinX()),
                (float)(linePath.getBounds2D().getMaxY()-linePath.getBounds2D().getMinY()))));
        */

        linePath.reset();
    }

    @Override
    public void fillPath(int windingRule) throws IOException
    {
        //System.out.println("This is Fill Path");
        drawGlyph glyph = new drawGlyph(linePath,0,"Bars",0,0);
        glyph.coordinates();
        glyph.BoxCoord();
        allBars.add(new Bars(new BBOX((float)glyph.minX,(float)glyph.minY,(float)(glyph.maxX-glyph.minX),(float)(glyph.maxY-glyph.minY))));
        //System.out.println(linePath.getBounds2D());

        linePath.reset();
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException
    {
        System.out.println("This is Stroke and Fill");
        linePath.reset();
    }

    @Override
    public void shadingFill(COSName cosn) throws IOException
    {
        System.out.println("This is Shading and Fill");
    }


    public void filterBars(){
        HashSet<Bars> toRemove = new HashSet<Bars>();

        for(int i=0;i<allBars.size()-1;i++){
            Bars currentBar = allBars.get(i);
            if(currentBar.boundingBox.height>=20 || currentBar.boundingBox.width>=200){
                toRemove.add(currentBar);
            }
            for(int j=i+1;j<allBars.size();j++){
                Bars nextBars = allBars.get(j);
                if(Math.abs(Math.floor(currentBar.boundingBox.startX)-Math.floor(nextBars.boundingBox.startX)) <=0.2  && Math.abs(Math.floor(currentBar.boundingBox.startY)-Math.floor(nextBars.boundingBox.startY)) <=0.2){
                    toRemove.add(currentBar);
                    toRemove.add(nextBars);
                }
            }
        }
        for(Bars bar:toRemove){
            allBars.remove(bar);
        }
    }
}

