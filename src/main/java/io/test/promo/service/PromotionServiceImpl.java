package io.test.promo.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

	boolean isPromoApplied = false;

	ReaderService readService = null;

	public ReaderService getReadService() {
		if (readService == null)
			readService = new ReaderServiceImpl();
		return readService;
	}

	public boolean isPromoApplied() {
		return isPromoApplied;
	}

	public void setPromoApplied(boolean isPromoApplied) {
		this.isPromoApplied = isPromoApplied;
	}

	/**
	 * The main method for processing the promotion this will return the total after
	 * the promotion
	 */
	@Override
	public double process(String cartFile) {
		double total = 0d;
		Cart cart = new CartServiceImpl().readCart(cartFile);
		List<Promotion> promotionList = getPromotionList(PROMOTION_FILE);
		List<Sku> skuDetailList = getSkuDetails(SKU_DETAILS_FILE);
		Map<String, Double> skuPriceMap = getskuPriceMap(skuDetailList);
		List<Item> promoToBeApply = new ArrayList<Item>();

		List<Item> cartItems = cart.getItems();
		cartItems.forEach((t) -> {
			if (checkPromoCanApply(t.getItemId(), t.getQuantity(), promotionList, cartItems)) {
				promoToBeApply.add(t);
			}

		});

		if (promoToBeApply.size() == 0) {
			total = getTotalWithoutPromo(skuPriceMap, cartItems);
		}

		if (promoToBeApply.size() > 0) {
			total = handlePromotion(cartItems, promoToBeApply, promotionList, skuPriceMap);
		}

		return total;
	}

	/**
	 *  returns list of promotion after reading the promotion file.
	 */
	@Override
	public List<Promotion> getPromotionList(String path) {
		List<Promotion> promoList = new ArrayList<Promotion>();

		try {
			File file = getReadService().readFile(path);
			List<String> promoFile = Files.readAllLines(file.toPath());

			promoFile.forEach(t -> {
				if (!t.startsWith("--") && t.contains(";")) {
					String[] promoArray = t.split(";");
					Promotion promotion = new Promotion(promoArray[0], promoArray[1], Integer.parseInt(promoArray[2]),
							Double.parseDouble(promoArray[3]));
					promoList.add(promotion);
				}
			});

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		return promoList;
	}

	/**
	 * returns list of sku after reading the sku-details file
	 */
	@Override
	public List<Sku> getSkuDetails(String path) {
		List<Sku> skuList = new ArrayList<Sku>();

		try {
			File file = getReadService().readFile(path);
			List<String> skuFile = Files.readAllLines(file.toPath());

			skuFile.forEach(t -> {
				if (!t.startsWith("--") && t.contains(";")) {
					String[] skuArray = t.split(";");
					Sku sku = new Sku(skuArray[0], Double.parseDouble(skuArray[1]));
					skuList.add(sku);
				}
			});

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return skuList;
	}

	private double handlePromotion(List<Item> cartItems, List<Item> promoToBeApply, List<Promotion> promotionList,
			Map<String, Double> skuPriceMap) {
		double total = 0;
		Map<String, Boolean> doublePromoMap = new HashMap<>();
		for (Item cartItem : cartItems) {
			if (promoToBeApply.contains(cartItem) && !isPromoApplied()) {
				Promotion promo = promotionList.stream().filter(t -> t.getSkuIds().contains(cartItem.getItemId()))
						.findFirst().orElse(null);

				if (promo != null) {
					if (promo.getSkuIds().contains(",")) {
						total = handleMultiPromo(skuPriceMap, doublePromoMap, total, cartItem, promo);
					}
					total = handleSinglePromo(skuPriceMap, total, cartItem, promo);
				} else {
					total += (skuPriceMap.get(cartItem.getItemId()) * cartItem.getQuantity());
				}

			} else {
				total += (skuPriceMap.get(cartItem.getItemId()) * cartItem.getQuantity());
			}
		}

		return total;
	}

	/**
	 * Method will handle the promotion for multi sku values eg: C&D for 20
	 * 
	 * @param skuPriceMap
	 * @param doublePromoMap
	 * @param total
	 * @param cartItem
	 * @param promo
	 * @return
	 */
	private double handleMultiPromo(Map<String, Double> skuPriceMap, Map<String, Boolean> doublePromoMap, double total,
			Item cartItem, Promotion promo) {

		if (doublePromoMap.get(promo.getSkuIds()) == null) {
			// sets the value to true which means for the
			// other SKU ID in the promo can be skipped; Promo already applied
			doublePromoMap.put(promo.getSkuIds(), true);
			total += promo.getPrice();
		}

		return total;
	}

	/**
	 * Handle promo for single SKU promos
	 * 
	 * @param skuPriceMap
	 * @param total
	 * @param cartItem
	 * @param promo
	 * @return
	 */
	private double handleSinglePromo(Map<String, Double> skuPriceMap, double total, Item cartItem, Promotion promo) {
		int maxPromoApplyCount = findMaxPromoApplyCount(cartItem, promo);
		if (maxPromoApplyCount > 0) {
			total += getTotalWithSinglePromo(maxPromoApplyCount, cartItem, promo, skuPriceMap);
			setPromoApplied(true);
		}
		return total;
	}

	private double getTotalWithSinglePromo(int maxPromoApplyCount, Item cartItem, Promotion promo,
			Map<String, Double> skuPriceMap) {
		double total = 0;
		int balance = cartItem.getQuantity();
		while (maxPromoApplyCount > 0) {

			total += promo.getPrice();
			balance -= promo.getQuantity();
			maxPromoApplyCount--;
		}

		if (balance > 0) {
			total += (skuPriceMap.get(cartItem.getItemId()) * balance);
		}

		return total;
	}

	private int findMaxPromoApplyCount(Item cartItem, Promotion promo) {
		int count = 0;
		if (!promo.getSkuIds().contains(",") && promo.getSkuIds().contains(cartItem.getItemId())) {
			int cartCount = cartItem.getQuantity();
			int promoQuantityCount = promo.getQuantity();

			while (cartCount >= promoQuantityCount) {
				count++;
				cartCount -= promoQuantityCount;
			}
		}
		return count;
	}

	private Map<String, Double> getskuPriceMap(List<Sku> skuDetailList) {
		return skuDetailList.stream().collect(Collectors.toMap(Sku::getSkuId, Sku::getPrice));
	}

	private double getTotalWithoutPromo(Map<String, Double> skuPriceMap, List<Item> cartItems) {
		double totalAmount = 0;
		for (Item item : cartItems) {
			String sku = item.getItemId();
			double price = skuPriceMap.get(sku);
			totalAmount += (price * item.getQuantity());
		}
		return totalAmount;
	}

	/**
	 * Return true if a promo can be applied to the cart item
	 * @param itemId
	 * @param quantity
	 * @param promotionList
	 * @param cartItems
	 * @return
	 */
	private boolean checkPromoCanApply(String itemId, int quantity, List<Promotion> promotionList,
			List<Item> cartItems) {
		boolean promoCodeFound = false;
		for (Promotion t : promotionList) {
			String skuIds = t.getSkuIds();
			if (skuIds.contains(",")) {
				List<String> splitItem = Arrays.asList(skuIds.split(","));
				boolean check = true;
				for (String item : splitItem) {
					long count = cartItems.stream().filter(m -> m.getItemId().equals(item)).count();
					if (count == 0) {
						check = false;
						break;
					}
				}

				if (check) {
					promoCodeFound = true;
				}

			} else if (skuIds.contains(itemId) && quantity >= t.getQuantity()) {
				promoCodeFound = true;
			}
		}
		return promoCodeFound;
	}

}
