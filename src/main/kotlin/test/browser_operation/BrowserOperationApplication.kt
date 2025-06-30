package test.browser_operation

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import test.browser_operation.controller.AutomationController

@SpringBootApplication
class BrowserOperationApplication {

	@Bean
	fun automationRunner(
		automationController: AutomationController
	): CommandLineRunner {
		return CommandLineRunner {
			println("====자동화 테스트 시작====")
//			println(automationController.getGoogleTitleWithPlaywright())
//			println(automationController.getGoogleTitleWithPlaywrightUsingExistingChrome())
			automationController.loginToGoogle()
			println("====자동화 테스트 끝====")
		}
	}
}

fun main(args: Array<String>) {
	runApplication<BrowserOperationApplication>(*args)
}
