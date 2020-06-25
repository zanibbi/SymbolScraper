/******************************************************************************
* drawGlyph.java
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

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

enum type {
    Adjusted,Normal
}

class drawGlyph {

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

    HashMap<Integer, ArrayList<Double>> compx1 = new HashMap<>();
    HashMap<Integer, ArrayList<Double>> compx2 = new HashMap<>();
    HashMap<Integer, ArrayList<Double>> compx3 = new HashMap<>();
    HashMap<Integer, ArrayList<Double>> compy1 = new HashMap<>();
    HashMap<Integer, ArrayList<Double>> compy2 = new HashMap<>();
    HashMap<Integer, ArrayList<Double>> compy3 = new HashMap<>();

    ArrayList<Double> compMinX = new ArrayList<Double>();
    ArrayList<Double> compMaxX = new ArrayList<Double>();
    ArrayList<Double> compMinY = new ArrayList<Double>();
    ArrayList<Double> compMaxY = new ArrayList<Double>();

    int segmentCount;

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
        this.segmentCount = 0;
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
        float pixel_size = fontSize * 72 /72; // TODO: why multiply and then divide?
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
            op.add(s);
            switch (s) {
                case PathIterator.SEG_CLOSE:
                    segmentCount += 1;
                    //System.out.println(segmentCount);

                    compx1.put(segmentCount, x1);
                    compx2.put(segmentCount, x2);
                    compx3.put(segmentCount, x3);
                    compy1.put(segmentCount, y1);
                    compy2.put(segmentCount, y2);
                    compy3.put(segmentCount, y3);

                    x1 = new ArrayList<Double>();
                    x2 = new ArrayList<Double>();
                    x3 = new ArrayList<Double>();
                    y1 = new ArrayList<Double>();
                    y2 = new ArrayList<Double>();
                    y3 = new ArrayList<Double>();
                    //System.out.println("Segment close " + segmentCount);
                    break;

                case PathIterator.SEG_CUBICTO:
                    x1.add(coords[0]);
                    y1.add(coords[1]);
                    x2.add(coords[2]);
                    y2.add(coords[3]);
                    x3.add(coords[4]);
                    y3.add(coords[5]);
                    //System.out.println("cubic " + segmentCount);
                    break;

                case PathIterator.SEG_LINETO:
                    x1.add(coords[0]);
                    y1.add(coords[1]);
                    //System.out.println("line " + segmentCount);
                    break;

                case PathIterator.SEG_MOVETO:
                    x1.add(coords[0]);
                    y1.add(coords[1]);
                    //System.out.println("move " + segmentCount);
                    break;

                case PathIterator.SEG_QUADTO:
                    x1.add(coords[0]);
                    y1.add(coords[1]);
                    x2.add(coords[2]);
                    y2.add(coords[3]);
                    //System.out.println("quad " + segmentCount);
                    break;
            }

            // adds the coordinate data to the lists for lookup later
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
        for(int i=1; i <= segmentCount; i++) {
            ArrayList<Double> XList = new ArrayList<Double>();
            ArrayList<Double> YList = new ArrayList<Double>();

            ArrayList<Double> x1c = compx1.get(i);
            if (x1c.size() != 0) {
                double minX1 = Collections.min(x1c);
                double maxX1 = Collections.max(x1c);
                XList.add(minX1);
                XList.add(maxX1);
            }
            ArrayList<Double> x2c =compx2.get(i);
            if (x2c.size() != 0) {
                double minX2 = Collections.min(x2c);
                double maxX2 = Collections.max(x2c);
                XList.add(minX2);
                XList.add(maxX2);
            }
            ArrayList<Double> x3c = compx3.get(i);
            if (x3c.size() != 0) {
                double minX3 = Collections.min(x3c);
                double maxX3 = Collections.max(x3c);
                XList.add(minX3);
                XList.add(maxX3);
            }
            ArrayList<Double> y1c =compy1.get(i);
            if (y1c.size() != 0) {
                double minY1 = Collections.min(y1c);
                double maxY1 = Collections.max(y1c);
                YList.add(minY1);
                YList.add(maxY1);
            }
            ArrayList<Double> y2c = compy2.get(i);
            if (y2c.size() != 0) {
                double minY2 = Collections.min(y2c);
                double maxY2 = Collections.max(y2c);
                YList.add(minY2);
                YList.add(maxY2);
            }
            ArrayList<Double> y3c = compy3.get(i);
            if (y3c.size() != 0) {
                double minY3 = Collections.min(y3c);
                double maxY3 = Collections.max(y3c);
                YList.add(minY3);
                YList.add(maxY3);
            }


            compMinX.add(Collections.min(XList));
            compMaxX.add(Collections.max(XList));
            compMinY.add(Collections.min(YList));
            compMaxY.add(Collections.max(YList));
        }

        try {
            minX = Collections.min(compMinX);
            maxX = Collections.max(compMaxX);
            minY = Collections.min(compMinY);
            maxY = Collections.max(compMaxY);
        }
        catch(Exception e){
            minX = 0;
            maxX = 0;
            minY = 0;
            maxY = 0;
        }

        ArrayList<Integer> removalArr = new ArrayList<Integer>();
        //check for encompassing boxes
        for (int i = 0;  i < segmentCount; i++){
            double minx1 = compMinX.get(i);
            double maxx1 = compMaxX.get(i);
            double miny1 = compMinY.get(i);
            double maxy1 = compMaxY.get(i);
            for (int j = 0;  j < segmentCount; j++){
                double minx2 = compMinX.get(j);
                double maxx2 = compMaxX.get(j);
                double miny2 = compMinY.get(j);
                double maxy2 = compMaxY.get(j);
                if(minx1 > minx2 && miny1 > miny2 &&
                   maxx1< maxx2 && maxy1 < maxy2){
                    // one box is inside two box
                    compMinX.set(i, 0.0);
                    compMaxX.set(i, 0.0);
                    compMinY.set(i, 0.0);
                    compMaxY.set(i, 0.0);
                    removalArr.add(i);
                    j = segmentCount;
                }
            }
        }

        //remove interrior boxes from composited lists
        for (int counter = removalArr.size()-1; counter >= 0; counter--) {
            int removeIndex = removalArr.get(counter);
            compMinX.remove(removeIndex);
            compMaxX.remove(removeIndex);
            compMinY.remove(removeIndex);
            compMaxY.remove(removeIndex);
        }

        this.segmentCount = compMinX.size();
    }

}
