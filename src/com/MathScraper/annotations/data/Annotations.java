package com.MathScraper.annotations.data;

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

public class Annotations {
	
	Heading heading;
	
	@Setter(AccessLevel.PUBLIC)
	List<Sheet> sheets;
}
