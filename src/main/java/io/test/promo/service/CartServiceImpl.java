package io.test.promo.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.test.promo.model.Cart;
import io.test.promo.model.Item;

public class CartServiceImpl implements CartService {

	@Override
	public Cart readCart(String path) {
		Cart cart = new Cart();
		List<Item> items = new ArrayList<>();
		ReaderService readService = new ReaderServiceImpl();

		try {
			File file = readService.readFile(path);
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
	
	

}
