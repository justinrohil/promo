package io.test.promo;

import io.test.promo.model.Cart;
import io.test.promo.service.PromotionService;
import io.test.promo.service.PromotionServiceImpl;

public class PromotionMain {
	
	static PromotionService promotionService;
	private static final String TEST_PROMOTION_FILE = "test-cart";

	public static void main(String[] args) {
		
		promotionService = new PromotionServiceImpl();
		
		Cart cart = promotionService.readCart(TEST_PROMOTION_FILE);
		System.out.println("Total : " + promotionService.process(cart));
		

	}

}
