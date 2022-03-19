package it.openly.projects.million4ukraine.m4urest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class M4uRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(M4uRestApplication.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(1);	// do NOT increase this without making the TileComposerService thread-safe or changing the application's architecture
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("ProcessingExecutor-");
		executor.initialize();
		return executor;
	}
}
