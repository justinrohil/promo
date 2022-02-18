package io.test.promo.service;

import java.io.File;
import java.net.URISyntaxException;

public interface ReaderService {
	
	File readFile(String fileName) throws URISyntaxException;

}
