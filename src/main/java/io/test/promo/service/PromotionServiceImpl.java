package io.test.promo.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.test.promo.model.Cart;
import io.test.promo.model.Item;
import io.test.promo.model.Promotion;
import io.test.promo.model.Sku;

public class PromotionServiceImpl implements PromotionService {

	private static final String SKU_DETAILS_FILE = "sku-details";
	private static final String PROMOTION_FILE = "promotion";
	
	@Override
	public double process(Cart cart) {
		double total = 0d;
		List<Promotion> promotionList = getPromotionList(PROMOTION_FILE);
		List<Sku> skuDetailList = getSkuDetails(SKU_DETAILS_FILE);
		Map<String, Double> skuPriceMap = getskuPriceMap(skuDetailList);
		List<Item> promoToBeApply = new ArrayList<Item>();
		
		List<Item> cartItems = cart.getItems();
//		cartItems.forEach((t) -> {
//			if(checkPromoCanApply(t.getItemId(),promotionList)) {
//				promoToBeApply.add(t);
//			}
//			
//		});
		
		if(promoToBeApply.size() == 0) {
			total = getTotalWithoutPromo(skuPriceMap,cartItems);
		}
		
		
		return total;
	}

	
	
	private Map<String, Double> getskuPriceMap(List<Sku> skuDetailList) {		
		return skuDetailList.stream().collect(Collectors.toMap(Sku::getSkuId, Sku::getPrice));
	}



	private double getTotalWithoutPromo(Map<String, Double> skuPriceMap, List<Item> cartItems) {
		double totalAmount = 0;
		for (Item item : cartItems) {
			String sku = item.getItemId();
			double price = skuPriceMap.get(sku);
			totalAmount += (price*item.getQuantity());
		}
		return totalAmount;
	}



	private boolean checkPromoCanApply(String itemId, List<Promotion> promotionList) {
		boolean promoCodeFound = false;		
		for(Promotion t : promotionList) {
			String skuIds = t.getSkuIds();
			if(skuIds.contains(",")) {
				List<String> split = Arrays.asList(skuIds.split(","));
				if(split.contains(itemId)) {
					promoCodeFound = true;
				}				
			}else if(skuIds.contains(itemId)){
					promoCodeFound = true;
				}
			}
		return promoCodeFound;
		}




	private File readFile(String fileName) throws URISyntaxException {		
		    ClassLoader classLoader = getClass().getClassLoader();		
		    URL resource = classLoader.getResource(fileName);
		    if (resource == null) {
		        throw new IllegalArgumentException("file not found! " + fileName);
		    }
	
		    return new File(resource.toURI());
	}
	
	@Override
	public Cart readCart(String path) {		
		Cart cart = new Cart();
		List<Item> items = new ArrayList<>();
		
		try {
			File file = readFile(path);
			List<String> cartFile = Files.readAllLines(file.toPath());
			
			cartFile.forEach(t -> {
				if (!t.startsWith("--") && t.contains(";")) {
					String[] splittedString = t.split(";");
					Item item = new Item(splittedString[0], Integer.parseInt(splittedString[1]));
					items.add(item);
				}
			});
			if (items.size() > 0) {
				cart.setItems(items);
			}

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		return cart;
	}



	@Override
	public List<Promotion> getPromotionList(String path) {
		List<Promotion> promoList = new ArrayList<Promotion>();
		
		try {
			File file = readFile(path);
			List<String> promoFile = Files.readAllLines(file.toPath());
			
			promoFile.forEach(t -> {
				if (!t.startsWith("--") && t.contains(";")) {
					String[] promoArray = t.split(";");
					Promotion promotion = new Promotion(promoArray[0],promoArray[1],Integer.parseInt(promoArray[2]),Double.parseDouble(promoArray[3]));
					promoList.add(promotion);
				}
			});

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		
		return promoList;
	}



	@Override
	public List<Sku> getSkuDetails(String path) {
		List<Sku> skuList = new ArrayList<Sku>();
		
		try {
			File file = readFile(path);
			List<String> skuFile = Files.readAllLines(file.toPath());
			
			skuFile.forEach(t -> {
				if (!t.startsWith("--") && t.contains(";")) {
					String[] skuArray = t.split(";");
					Sku sku = new Sku(skuArray[0],Double.parseDouble(skuArray[1]));
					skuList.add(sku);
				}
			});

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return skuList;
	}



}
