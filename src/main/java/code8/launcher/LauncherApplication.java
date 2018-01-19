package code8.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LauncherApplication {

	public static void main(String[] args) {
		SpringApplication.run(LauncherApplication.class, args);
	}
}
