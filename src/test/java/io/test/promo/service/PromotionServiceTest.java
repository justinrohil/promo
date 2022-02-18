package io.test.promo.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PromotionServiceTest {

	private static final String TEST_FILE_MULTI = "test2";
	private static final String TEST_FILE_COMBO = "test1";
	private static final String TEST_FILE_SINGLE_COMBO = "test3";

	PromotionService promotionService = null;

	@BeforeEach
	void init() {
		promotionService = new PromotionServiceImpl();
	}

	@Test
	@DisplayName("Checks cart contains multi promotions")
	void processTest1() {
		double expectedTotal = 360;
		assertTrue(promotionService.process(TEST_FILE_MULTI) == expectedTotal);
	}

	@Test
	@DisplayName("Checks cart contains combo promotion")
	void processTest2() {
		double expectedTotal = 110;
		assertTrue(promotionService.process(TEST_FILE_COMBO) == expectedTotal);
	}

	@Test
	@DisplayName("Checks cart contains multi promotions")
	void processTest3() {
		double expectedTotal = 30;
		assertTrue(promotionService.process(TEST_FILE_SINGLE_COMBO) == expectedTotal);
	}

}
