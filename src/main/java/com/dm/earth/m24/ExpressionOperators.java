package com.dm.earth.m24;

import net.objecthunter.exp4j.operator.Operator;

import java.util.List;

public class ExpressionOperators {
	public static List<Operator> OPERATORS = List.of(
			new Operator(">>", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
				@Override
				public double apply(double... args) {
					return ((int) args[0]) >> ((int) args[1]);
				}
			},
			new Operator("<<", 2, true, Operator.PRECEDENCE_ADDITION - 1) {
				@Override
				public double apply(double... args) {
					return ((int) args[0]) << ((int) args[1]);
				}
			},
			new Operator("&", 2, true, Operator.PRECEDENCE_ADDITION - 2) {
				@Override
				public double apply(double... args) {
					return ((int) args[0]) & ((int) args[1]);
				}
			},
			new Operator("^", 2, true, Operator.PRECEDENCE_ADDITION - 3) {
				@Override
				public double apply(double... args) {
					return ((int) args[0]) ^ ((int) args[1]);
				}
			},
			new Operator("|", 2, true, Operator.PRECEDENCE_ADDITION - 4) {
				@Override
				public double apply(double... args) {
					return ((int) args[0]) | ((int) args[1]);
				}
			}
	);
}
