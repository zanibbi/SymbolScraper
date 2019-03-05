package com.symbolScraper.annotations.data;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Sheet {
	
	// This class stores annotations per page or sheet
	
	Long pageId;
	String fileName;
	
	@Setter(AccessLevel.PUBLIC)
	BoundingBox boundingBox;
	
	@Setter(AccessLevel.PUBLIC)
	List<Text> textAreas;
	
	@Setter(AccessLevel.PUBLIC)
	List<Image> imageAreas;
	
	@Setter(AccessLevel.PUBLIC)
	List<MathData> mathAreas;
}
