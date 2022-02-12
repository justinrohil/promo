package io.test.promo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

	String promoCode;
	String skuIds;
	int quantity;
	double price;
}
