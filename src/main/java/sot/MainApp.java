package sot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableScheduling
public class MainApp {

	public static void main(String[] args) {
		SpringApplication.run(MainApp.class, args);

	}

	@Bean
	public Executor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}
	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}

}
