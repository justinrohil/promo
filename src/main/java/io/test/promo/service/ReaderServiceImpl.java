package io.test.promo.service;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class ReaderServiceImpl implements ReaderService {

	@Override
	public File readFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file not found! " + fileName);
		}

		return new File(resource.toURI());
	}

}
