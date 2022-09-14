package com.dm.earth.m24;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Point24 {
	public static final String MODID = "modid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final String PREFIX = "answer";
	public static final int FINALE = 24;

	public static Identifier asIdentifier(String id) {
		return new Identifier(MODID, id);
	}

	private static int number1 = 0;
	private static int number2 = 0;
	private static int number3 = 0;
	private static int number4 = 0;

	private static int getRandomInt() {
		Random rand = new Random();
		return rand.nextInt(1, 13);
	}

	public static void genNumbers() {
		number1 = getRandomInt();
		number2 = getRandomInt();
		number3 = getRandomInt();
		number4 = getRandomInt();
	}

	public static int getNumber(Args24 arg) {
		return switch (arg) {
			case A -> number1;
			case B -> number2;
			case C -> number3;
			case D -> number4;
		};
	}
}
