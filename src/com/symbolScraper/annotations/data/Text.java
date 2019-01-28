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

public class Text {
	
	Long componentId;
	BoundingBox boundingBox;
	
	@Setter(AccessLevel.PUBLIC)
	List<Line> lines;
}
