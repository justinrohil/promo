package io.test.promo.service;

import java.util.List;

import io.test.promo.model.Promotion;
import io.test.promo.model.Sku;

public interface PromotionService {
	
	double process(String testPromotionFile);
	
	List<Promotion> getPromotionList(String path);
	
	List<Sku> getSkuDetails(String path);

}
