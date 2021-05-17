package org.mark.demo.safe;

import lombok.Data;

/**
 * @FileName Apple
 * @Description TODO
 * @Author markt
 * @Date 2020-06-09
 * @Version 1.0
 */
@Data
public class Apple {

	String color;

	public Apple(String color) {
		this.color = color;
	}

}
