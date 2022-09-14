package com.dm.earth.m24;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;
import net.objecthunter.exp4j.shuntingyard.ShuntingYard;
import net.objecthunter.exp4j.tokenizer.NumberToken;
import net.objecthunter.exp4j.tokenizer.Token;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegistry implements CommandRegistrationCallback {
	private static String genNumsStr() {
		return "[" + Point24.getNumber(Args24.A) + "] " + "[" + Point24.getNumber(Args24.B) + "] " + "[" + Point24.getNumber(Args24.C) + "] " + "[" + Point24.getNumber(Args24.D) + "]";
	}

	private static void genNumbers(ServerCommandSource src, boolean broadcast24) throws CommandSyntaxException {
		Point24.genNumbers();
		String output = "Please use " + genNumsStr() + " to construct a equation that results 24. Please use '" + Point24.PREFIX + " ' as verification at the start of the equation.";
		if (broadcast24)
			src.getServer().getPlayerManager().broadcastChatMessage(new LiteralText("<" + src.getPlayer().getEntityName() + "> " + "24 Points"), MessageType.CHAT, src.getPlayer().getUuid());
		src.getServer().getPlayerManager().broadcastChatMessage(new LiteralText(output), MessageType.SYSTEM, src.getPlayer().getUuid());
	}

	@Override
	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean integrated, boolean dedicated) {
		dispatcher.register(literal("m24")
				.then(literal("new")
						.requires(arg -> arg.hasPermissionLevel(1))
						.executes(crx -> {
							ServerCommandSource src = crx.getSource();
							genNumbers(src, true);
							return 1;
						})
				)
				.then(literal(Point24.PREFIX).then(argument("equation", StringArgumentType.greedyString())
						.requires(arg -> arg.hasPermissionLevel(0))
						.executes(crx -> {
							if (Point24.getNumber(Args24.A) == 0) return 0;
							ServerCommandSource src = crx.getSource();
							String equation = StringArgumentType.getString(crx, "equation")
									.replace('（', '(')
									.replace('）', ')')
									.replace('x', '*')
									.replace('×', '*')
									.replace('÷', '/')
									.replace('－', '-')
									.replace('＋', '+')
									.replace('！', '!')
									.replace('＜', '<')
									.replace('＞', '>');

							src.getServer().getPlayerManager().broadcastChatMessage(new LiteralText("<" + src.getPlayer().getEntityName() + "> " + Point24.PREFIX + " " + equation), MessageType.CHAT, src.getPlayer().getUuid());

							if (equation.contains("%")) {
								src.getServer().getPlayerManager().broadcastChatMessage(new LiteralText("% Operators are disabled."), MessageType.SYSTEM, src.getPlayer().getUuid());
								return 1;
							}
							Expression expression = new ExpressionBuilder(equation)
									.operator(ExpressionOperators.OPERATORS)
									.implicitMultiplication(false)
									.build();

							Map<String, Operator> operatorMap = new HashMap<>();

							for (Operator operator : ExpressionOperators.OPERATORS) {
								operatorMap.put(operator.getSymbol(), operator);
							}

							Token[] tokens = ShuntingYard.convertToRPN(
									equation,
									null,
									operatorMap,
									null,
									false
							);
							List<Integer> nums = new ArrayList<>();
							{
								int i = 0;
								for (Args24 arg : Args24.values()) {
									nums.add(i, Point24.getNumber(arg));
									i++;
								}
							}

							boolean usedAll = true;

							for (Token token : tokens) {
								if (token.getType() == (int) Token.TOKEN_NUMBER) {
									double value = ((NumberToken) token).getValue();
									int i = 0;
									while (i < nums.size()) {
										if ((double) nums.get(i) == value && nums.get(i) != 0) break;
										else ++i;
									}
									if (i < nums.size()) nums.remove(i);
									else usedAll = false;
								} else if (token.getType() == (int) Token.TOKEN_FUNCTION) {
									src.getServer().getPlayerManager().broadcastChatMessage(new LiteralText("Math functions are disabled."), MessageType.SYSTEM, src.getPlayer().getUuid());
									return 1;
								}
							}

							if (!nums.isEmpty()) usedAll = false;

							double result = expression.evaluate();
							if (!usedAll) {
								src.getServer().getPlayerManager().broadcastChatMessage(new LiteralText("The result is " + result + ", please use the numbers provided by the game!"), MessageType.SYSTEM, src.getPlayer().getUuid());
								return 1;
							}
							if (result == Point24.FINALE) {
								src.getServer().getPlayerManager().broadcastChatMessage(new LiteralText("Congratulations!"), MessageType.SYSTEM, src.getPlayer().getUuid());
								genNumbers(src, false);
							} else {
								src.getServer().getPlayerManager().broadcastChatMessage(new LiteralText("The result is " + result + ", please make it be 24!"), MessageType.SYSTEM, src.getPlayer().getUuid());
							}
							return 1;
						})
				))
		);
	}
}
