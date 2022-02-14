package io.test.promo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.test.promo.model.Cart;
import io.test.promo.model.Item;

public class CartReader implements Reader {

	@Override
	public Cart read(File file) {
		Cart cart = new Cart();
		List<Item> items = new ArrayList<>();

		try {
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

		} catch (IOException e) {
			e.printStackTrace();
		}

		return cart;
	}

	

}
