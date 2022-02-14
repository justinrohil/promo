package io.test.promo.service;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import io.test.promo.model.Cart;

public class PromotionService {

	public double process(Cart cart) {
		
		return 0d;
	}

	public Cart readCart(String testPromotionFile) {
		Reader reader = new CartReader();
		File file = null;
		try {
			file = readFile(testPromotionFile);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return reader.read(file);
	}
	
	
	private File readFile(String fileName) throws URISyntaxException {		
		    ClassLoader classLoader = getClass().getClassLoader();		
		    URL resource = classLoader.getResource(fileName);
		    if (resource == null) {
		        throw new IllegalArgumentException("file not found! " + fileName);
		    }
	
		    return new File(resource.toURI());
	}


}
