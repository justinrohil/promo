package io.test.promo.service;

import java.io.File;

import io.test.promo.model.Cart;

public interface Reader {
	
	Cart read(File file);

}
