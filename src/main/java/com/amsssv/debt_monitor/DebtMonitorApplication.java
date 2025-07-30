package com.amsssv.debt_monitor;

import com.amsssv.debt_monitor.bot.Bot;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class DebtMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DebtMonitorApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(Bot bot) {
		return args -> {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(bot);
		};
	}
}
