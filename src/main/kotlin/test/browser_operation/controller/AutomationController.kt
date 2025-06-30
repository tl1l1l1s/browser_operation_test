package test.browser_operation.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import test.browser_operation.service.PlaywrightService

@RestController
@RequestMapping("/automation")
class AutomationController(private val playwrightService: PlaywrightService) {

    @GetMapping("/p1")
    fun getGoogleTitleWithPlaywright(): String {
        return "Playwright으로 가져온 구글 타이틀 : ${playwrightService.getGoogleTitle()}"
    }

    @GetMapping("/p2")
    fun getGoogleTitleWithPlaywrightUsingExistingChrome(): String {
        return " Playwright으로 크롬에서 가져온 구글 타이틀 : ${playwrightService.getTitleFromExistingChrome()}"
    }

    @GetMapping("/p3")
    fun loginToGoogle() {
        playwrightService.navigate("https://www.google.com")
        playwrightService.login("id", "pw")
    }
}