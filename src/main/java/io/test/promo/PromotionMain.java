package io.test.promo;

import io.test.promo.service.PromotionService;
import io.test.promo.service.PromotionServiceImpl;

public class PromotionMain {
	
	private static final String TEST_PROMOTION_FILE = "test-cart";

	public static void main(String[] args) {
				
		PromotionService promotionService = new PromotionServiceImpl();

		System.out.println("Total : " + promotionService.process(TEST_PROMOTION_FILE));		

	}

}
