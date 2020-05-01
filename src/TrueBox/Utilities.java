package TrueBox;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.type1.Type1Font;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;


class Utilities {

    public Utilities() throws IOException{
        //do nothing
    }

    // for use inside of writeString function
    public void debugWriteToPDF(String string, List<TextPosition> textPositions, boolean output) throws IOException{
        for(int i=0; i < textPositions.size(); i++){
            System.out.println(textPositions.get(i));
        }
        System.out.println(textPositions);

        //get the text object from text positions
        TextPosition dummytext = textPositions.get(0);

        //create blank document and page
        PDDocument dummydoc = new PDDocument();
        PDPage dummypage = new PDPage();
        dummydoc.addPage(dummypage);

        //choose a font to write with
//        PDFont dummyfont = textPositions.get(0).getFont();
        PDType1Font Type1font = (PDType1Font) textPositions.get(0).getFont();
//        PDFont dummyfont = PDType0Font.load(document, PDDocument.class.getClassLoader().getResourceAsStream("https://ctan.org/tex-archive/fonts/cm/ps-type1/bakoma/ttf/cmr10.ttf"));
//        PDFont dummyfont = PDType0Font.load(document, this.getClass().getResourceAsStream("/cmr10.ttf"));
        File fontfile = new File("/home/jdiehl/IdeaProjects/cmunrm.ttf");
//        File fontfile = new File("/home/jdiehl/IdeaProjects/cmr10.ttf");
        PDType0Font dummyfont = PDType0Font.load(dummydoc, fontfile);
//        PDType1Font dummyfont = PDType1Font.TIMES_ROMAN;
        //java.lang.IllegalArgumentException: U+0308 ('dieresiscmb') is not available
        //in this font HODZXD+CMR10 (generic: HODZXD+CMR10) encoding: built-in (Type 1)
        //... U+00CB ('Edieresis') ..

        //attempt to use the merge diacritic function to create one character from two codepoints, makes no impact
        TextPosition baseText = new TextPosition(dummytext.getRotation(), dummytext.getPageWidth(),
                dummytext.getPageHeight(), dummytext.getTextMatrix(), dummytext.getEndX(), dummytext.getEndY(), dummytext.getHeight(),
                dummytext.getIndividualWidths()[0], dummytext.getWidthOfSpace(), Type1font.getGlyphList().toUnicode(Type1font.getGlyphList().codePointToName(dummytext.getCharacterCodes()[0])),
                dummytext.getCharacterCodes(), dummyfont, dummytext.getFontSize(), (int) dummytext.getFontSizeInPt());
        try {
            TextPosition diacriticText = new TextPosition(dummytext.getRotation(), dummytext.getPageWidth(),
                    dummytext.getPageHeight(), dummytext.getTextMatrix(), dummytext.getEndX(), dummytext.getEndY(), dummytext.getHeight(),
                    dummytext.getIndividualWidths()[0], dummytext.getWidthOfSpace(), Type1font.getGlyphList().toUnicode(Type1font.getGlyphList().codePointToName(dummytext.getUnicode().codePointAt(1))),
                    dummytext.getCharacterCodes(), dummyfont, dummytext.getFontSize(), (int) dummytext.getFontSizeInPt());
//            System.out.println(diacritictext.isDiacritic());
            baseText.mergeDiacritic(diacriticText);
        } catch (Exception e)
        {
            //do nothing
        }

        //attempt to set font descriptor settings
//        PDFontDescriptor dummydesc = dummyfont.getFontDescriptor();
//        dummydesc.setNonSymbolic(true);
//        dummydesc.setSymbolic(true);
//        dummyfont.setFontDescriptor(dummydesc);
//        dummyfont.setFontEncoding(WinAnsiEncoding.INSTANCE);
//        byte[] encodedStr = dummyfont.encode(string);
//        System.out.println(document.getPage(0).getCOSObject());

        // encoding manager is deprecated
//        Encoding e = EncodingManager.INSTANCE.getEncoding(COSName.WIN_ANSI_ENCODING);
//        String encodedChar = String.valueOf(Character.toChars(e.getCode(e.getNameFromCharacter(string))));

        //append raw commands is deprecated
//        byte[] commands = "(x) Tj ".getBytes();
//        commands[1] = (byte) 128;
//        dummystream.appendRawCommands(commands);
//        dummystream.showText(Character.toString((char)textPositions.get(0).getCharacterCodes()[0]));

        //create a new stream using font
        PDPageContentStream dummystream = new PDPageContentStream(dummydoc, dummypage);
        dummystream.beginText();
        dummystream.setFont(dummyfont, textPositions.get(0).getFontSizeInPt());
        dummystream.newLineAtOffset(100,100);
        //write character
        dummystream.showText(baseText.getUnicode());
        dummystream.endText();
        dummystream.close();
        //save file
        if(output == true) {
            if (baseText.getUnicode().charAt(0) == 'A') {
                dummydoc.save("/home/jdiehl/IdeaProjects/testsave.pdf");
            }
            // check the size of the page contents
            PDRectangle dummybox = dummypage.getBBox();
            System.out.println("dummybox:" + dummybox.toString());
        }
        dummydoc.close();


    }


    public void debugGraphicEngine()throws IOException{
//        PDFGraphicsStreamEngine dummystream = new PDFGraphicsStreamEngine(document.getPage(0));
//        dummystream.beginText();
//        dummystream.showText(encodedStr);
//        dummystream.endText();
//        dummydoc.close();
    }


    public void debugTextType1Stats(PDFont font, TextPosition text)throws IOException{
        //get fonts
        PDType1Font Type1font = (PDType1Font) font;
        FontBoxFont FBFont = Type1font.getFontBoxFont();
        Type1Font T1Font = (Type1Font) FBFont;

        //debug out
        System.out.println(Type1font.getEncoding());
        System.out.println(Type1font.getGlyphList());
//        System.out.println(T1Font.getType1CharString(Type1font.codeToName(text.getCharacterCodes()[0])));
//        System.out.println(T1Font.getType1CharString("dieresis"));
//        System.out.println(T1Font.getType1CharString("ring"));
        System.out.println(text.getCharacterCodes()[0]);
        System.out.println(font.getHeight(text.getCharacterCodes()[0]));
        System.out.println(font.getWidthFromFont(text.getCharacterCodes()[0]));
        System.out.println(text.getHeight());
        System.out.println(text.getTextMatrix());
        System.out.println(T1Font.getSubrsArray());
//                    System.out.println( "\\u" + Integer.toHexString(text.getUnicode() | 0x10000).substring(1) );
        System.out.println(text.getUnicode().charAt(0));
        System.out.println(GlyphList.getAdobeGlyphList().sequenceToName(text.getUnicode()));
        System.out.println(Character.getName(text.getUnicode().charAt(0)));
        System.out.println(font.getFontDescriptor().getCharSet());
        System.out.println(T1Font.getCharStringsDict());
        System.out.println(font.getFontDescriptor().getFontBoundingBox());
        System.out.println(text.getIndividualWidths());
        System.out.println(font.getFontDescriptor().getAscent());
        System.out.println(font.getFontDescriptor().getCapHeight());
        System.out.println(font.getFontDescriptor().getDescent());
        System.out.println(Normalizer.normalize(text.getUnicode(), Form.NFC));
        System.out.println(font.getDisplacement(text.getUnicode().codePointAt(0)));
        System.out.println(text.getYScale());
        System.out.println(text.getHeight());
        System.out.println(text.getX());
        try {
            System.out.println(Type1font.getGlyphList().codePointToName(text.getUnicode().codePointAt(1)));
            System.out.println(font.getHeight(text.getUnicode().codePointAt(1)));
            System.out.println(font.getWidthFromFont(text.getUnicode().codePointAt(1)));
            System.out.println(T1Font.getEncoding().getName(text.getUnicode().codePointAt(1)));
        }
        catch (Exception e) {
            //do nothing, there is no code point at 1
        }
    }

}