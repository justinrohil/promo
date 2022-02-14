package io.test.promo.service;

import java.util.List;

import io.test.promo.model.Cart;
import io.test.promo.model.Promotion;
import io.test.promo.model.Sku;

public interface PromotionService {
	
	Cart readCart(String path);
	
	double process(Cart cart);
	
	List<Promotion> getPromotionList(String path);
	
	List<Sku> getSkuDetails(String path);

}
