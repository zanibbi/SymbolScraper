import java.io.FileNotFoundException;
import java.io.IOException;

import com.symbolScraper.annotations.AnnotationReader;

public class Test {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		Character c = 't';
		Character d = c;
		
		//test(c);
		
		//System.out.println(c);
		c = null;
		
		AnnotationReader reader = new AnnotationReader();
		
		System.out.println(reader.read("/Users/parag/Workspace/GTDB-Dataset/GTDB-1/AIF_1970_493_498.csv"));
		//System.out.println(reader.read("/Users/parag/eclipse-workspace/MathScraper/src/testData.csv"));
		
	}
	
	static void test(Character c) {
		
	}
}
