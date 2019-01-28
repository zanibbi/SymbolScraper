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

public class Line {
	
	Long lineId;
	BoundingBox boundingBox;
	
	@Setter(AccessLevel.PUBLIC)
	List<CharData> characters;
}
