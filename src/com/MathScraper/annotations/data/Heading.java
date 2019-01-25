package com.MathScraper.annotations.data;

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

public class Heading {
	
	String heading;
	
}
