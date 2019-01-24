package com.MathScraper.TrueBox;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


enum type {
    Adjusted,Normal
}

class drawGlyph extends Applet {

    GeneralPath glyphPath;
    ArrayList<Double> x1 = new ArrayList<Double>();
    ArrayList<Double> x2 = new ArrayList<Double>();
    ArrayList<Double> x3 = new ArrayList<Double>();
    ArrayList<Double> y1 = new ArrayList<Double>();
    ArrayList<Double> y2 = new ArrayList<Double>();
    ArrayList<Double> y3 = new ArrayList<Double>();

    ArrayList<Double> allx1 = new ArrayList<Double>();
    ArrayList<Double> allx2 = new ArrayList<Double>();
    ArrayList<Double> allx3 = new ArrayList<Double>();
    ArrayList<Double> ally1 = new ArrayList<Double>();
    ArrayList<Double> ally2 = new ArrayList<Double>();
    ArrayList<Double> ally3 = new ArrayList<Double>();

    ArrayList<Integer> op = new ArrayList<Integer>();
    double minX;
    double minY;
    double maxX;
    double maxY;

    float fontSize;

    int EmSqaure=1000;
    int ch;
    String unicode="";
    drawGlyph(GeneralPath glyphPath, int ch,String unicode,float fontSize, int EmSqaure){
        this.glyphPath=glyphPath;
        this.ch=ch;
        this.unicode=unicode;
        this.EmSqaure=EmSqaure;
        this.fontSize=fontSize;

    }

    public void draw(type t){
        coordinates();

        BoxCoord();

        if(t == type.Adjusted)
            adjustCoordResolution();
        else
            adjustCoord();

    }
    public void paint(Graphics g) {
        //Create Graphics2D object, cast g as a Graphics2D
        Graphics2D g2d = (Graphics2D) g;
        g2d.draw(glyphPath);
    }

    public double adjustResolution(double coord,float fontSize){
        float pixel_size = fontSize * 72 /72;
        return (coord * pixel_size)/EmSqaure;
    }
    public void adjustCoordResolution(){

        GeneralPath newPath = new GeneralPath();
        for(int i=0;i<op.size();i++) {

            switch (op.get(i)) {
                case PathIterator.SEG_CLOSE:
                    newPath.closePath();
                    break;

                case PathIterator.SEG_CUBICTO:
                    newPath.curveTo(adjustResolution(allx1.get(i)+Math.abs(minX),this.fontSize),
                            adjustResolution(ally1.get(i)+Math.abs(minY),this.fontSize),
                            adjustResolution(allx2.get(i)+Math.abs(minX),this.fontSize),
                            adjustResolution(ally2.get(i)+Math.abs(minY),this.fontSize),
                            adjustResolution(allx3.get(i)+Math.abs(minX),this.fontSize),
                            adjustResolution(ally3.get(i)+Math.abs(minY),this.fontSize));
                    break;

                case PathIterator.SEG_LINETO:
                    newPath.lineTo(adjustResolution(allx1.get(i)+Math.abs(minX),this.fontSize),
                            adjustResolution(ally1.get(i)+Math.abs(minY),this.fontSize));
                    break;

                case PathIterator.SEG_MOVETO:
                    newPath.moveTo(adjustResolution(allx1.get(i)+Math.abs(minX),this.fontSize),
                            adjustResolution(ally1.get(i)+Math.abs(minY),this.fontSize));
                    break;

                case PathIterator.SEG_QUADTO:
                    newPath.quadTo(adjustResolution(allx1.get(i)+Math.abs(minX),this.fontSize),
                            adjustResolution(ally1.get(i)+Math.abs(minY),this.fontSize),
                            adjustResolution(allx2.get(i)+Math.abs(minX),this.fontSize),
                            adjustResolution(ally2.get(i)+Math.abs(minY),this.fontSize));
                    break;

            }

        }
        drawGlyph temp = new drawGlyph(newPath,ch,unicode,fontSize,EmSqaure);
        temp.saveAsImage();




    }



    public void adjustCoord(){
        GeneralPath newPath = new GeneralPath();
        for(int i=0;i<op.size();i++) {
            switch (op.get(i)) {
                case PathIterator.SEG_CLOSE:
                    newPath.closePath();
                    break;

                case PathIterator.SEG_CUBICTO:
                    newPath.curveTo(allx1.get(i)+Math.abs(minX),
                            ally1.get(i)+Math.abs(minY),
                            allx2.get(i)+Math.abs(minX),
                            ally2.get(i)+Math.abs(minY),
                            allx3.get(i)+Math.abs(minX),
                            ally3.get(i)+Math.abs(minY));
                    break;

                case PathIterator.SEG_LINETO:
                    newPath.lineTo(allx1.get(i)+Math.abs(minX),
                            ally1.get(i)+Math.abs(minY));
                    break;

                case PathIterator.SEG_MOVETO:
                    newPath.moveTo(allx1.get(i)+Math.abs(minX),
                            ally1.get(i)+Math.abs(minY));
                    break;

                case PathIterator.SEG_QUADTO:
                    newPath.quadTo(allx1.get(i)+Math.abs(minX),
                            ally1.get(i)+Math.abs(minY),
                            allx2.get(i)+Math.abs(minX),
                            ally2.get(i)+Math.abs(minY));
                    break;

            }

        }
        drawGlyph temp = new drawGlyph(newPath,ch,unicode,fontSize,EmSqaure);
        temp.saveAsImage();




    }


    public void saveAsImage(){

        BufferedImage image = new BufferedImage(5000, 5000, BufferedImage.TYPE_INT_BGR);

        try {
            Graphics2D graphic = image.createGraphics();
            File output = new File("output"+ch+unicode+".png");
            paint(graphic);  // actual drawing on your image
            ImageIO.write(image, "png", output);
        } catch( Exception log
                ) {
            System.out.println(log);
        }


    }

    public void coordinates(){

        PathIterator iter = glyphPath.getPathIterator(null);
        //PathIterator copyIter = iter.clone();


        double coords[] = new double[6];
        double sumX = 0;
        double sumY = 0;
        int numPoints = 0;

        //System.out.println("Coords:: "+ch);
        while(!iter.isDone()){
            //iter.next();
            int s = iter.currentSegment(coords);

            int seg[]={PathIterator.SEG_CLOSE,
                    PathIterator.SEG_CUBICTO,
                    PathIterator.SEG_LINETO,
                    PathIterator.SEG_MOVETO,
                    PathIterator.SEG_QUADTO,
                    PathIterator.WIND_EVEN_ODD,
                    PathIterator.WIND_NON_ZERO};

//            for(int seg_code:seg){
//                if(seg_code==s){
//                    System.out.println("Seg Code::"+seg_code);
//                    break;
//                }
//            }
//            for(double c:coords){
//
//                System.out.print(c+" ");
//
//            }
            op.add(s);
            switch (s) {
                case PathIterator.SEG_CLOSE:
                    break;

                case PathIterator.SEG_CUBICTO:
                    x1.add(coords[0]);
                    y1.add(coords[1]);
                    x2.add(coords[2]);
                    y2.add(coords[3]);
                    x3.add(coords[4]);
                    y3.add(coords[5]);
                    break;

                case PathIterator.SEG_LINETO:
                    x1.add(coords[0]);
                    y1.add(coords[1]);
                    break;

                case PathIterator.SEG_MOVETO:
                    x1.add(coords[0]);
                    y1.add(coords[1]);
                    break;

                case PathIterator.SEG_QUADTO:
                    x1.add(coords[0]);
                    y1.add(coords[1]);
                    x2.add(coords[2]);
                    y2.add(coords[3]);
                    break;

            }




            allx1.add(coords[0]);
            ally1.add(coords[1]);
            allx2.add(coords[2]);
            ally2.add(coords[3]);
            allx3.add(coords[4]);
            ally3.add(coords[5]);

            //           System.out.println();
            iter.next();
        }



    }

    public void BoxCoord() {

        ArrayList<Double> XList = new ArrayList<Double>();
        ArrayList<Double> YList = new ArrayList<Double>();
        if(x1.size()!=0) {
            double minX1 = Collections.min(x1);
            double maxX1 = Collections.max(x1);
            XList.add(minX1);
            XList.add(maxX1);
        }
        if(x2.size()!=0) {
            double minX2 = Collections.min(x2);
            double maxX2 = Collections.max(x2);
            XList.add(minX2);
            XList.add(maxX2);
        }
        if(x3.size()!=0) {
            double minX3 = Collections.min(x3);
            double maxX3 = Collections.max(x3);
            XList.add(minX3);
            XList.add(maxX3);
        }
        if(y1.size()!=0) {
            double minY1 = Collections.min(y1);
            double maxY1 = Collections.max(y1);
            YList.add(minY1);
            YList.add(maxY1);
        }
        if(y2.size()!=0) {
            double minY2 = Collections.min(y2);
            double maxY2 = Collections.max(y2);
            YList.add(minY2);
            YList.add(maxY2);
        }
        if(y3.size()!=0) {
            double minY3 = Collections.min(y3);
            double maxY3 = Collections.max(y3);
            YList.add(minY3);
            YList.add(maxY3);
        }


        minX = Collections.min(XList);
        maxX = Collections.max(XList);
        minY = Collections.min(YList);
        maxY = Collections.max(YList);

    }

}
