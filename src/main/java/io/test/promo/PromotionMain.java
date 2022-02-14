package io.test.promo;

import io.test.promo.model.Cart;
import io.test.promo.service.PromotionService;

public class PromotionMain {
	
	static PromotionService promotionService;
	private static final String SKU_DETAILS_FILE = "src/test/resources/sku-details.txt";
	private static final String PROMOTION_FILE = "src/main/resources/promotion.txt";
	private static final String TEST_PROMOTION_FILE = "test-cart";

	public static void main(String[] args) {
		
		promotionService = new PromotionService();
		
		Cart cart = promotionService.readCart(TEST_PROMOTION_FILE);
		System.out.println("Total : " + promotionService.process(cart));
		

	}

}
