package test.browser_operation.service

import com.microsoft.playwright.Browser
import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import org.springframework.stereotype.Service
import com.microsoft.playwright.Playwright
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import java.util.concurrent.TimeoutException

@Service
class PlaywrightService {

    private lateinit var playwright: Playwright
    private lateinit var browser: Browser
    private lateinit var page: Page

    /**
     * 애플리케이션 실행 시 playwright 생성, 창 유지를 위함
     */
    @PostConstruct
    fun initialize() {
        playwright = Playwright.create()
        browser = playwright.chromium().launch(
            com.microsoft.playwright.BrowserType.LaunchOptions()
                .setHeadless(false) // 화면이 눈에 보이게 하기 위해서 설정
                .setSlowMo(50.0) // 화면 이동, 조작이 눈에 보이게 실행하기 위해 슬로우 모션 적용
                .setChannel("chrome")
        )
        page = browser.newPage() // 페이지 실행
    }

    /**
     * 애플리케이션 종료 전 close 실행
     */
    @PreDestroy
    fun cleanup() {
        playwright.close()
    }

    /**
     * 여러 선택자 후보 중 화면에 보이는 첫 번째 요소를 찾아 반환하는 함수
     * @param selectors 시도할 선택자 리스트
     * @return 찾은 Page.Locator 또는 null
     */
    private fun findVisibleElement(selectors: List<String>): Locator? {
        for (selector in selectors) {
            val locator = page.locator(selector)
            if (locator.count() > 0 && locator.isVisible()) { // 화면에 보이는 요소만을 사용
                println("'${selector}' 선택자로 요소를 찾았습니다.")
                return locator
            }
        }
        return null
    }

    /**
     * url을 기반으로 접속
     * @param url 접속할 주소 (https:// 부터 포함 필요)
     */
    fun navigate(url: String) {
        page.navigate(url)
        return
    }

    /**
     * google에 접속해 타이틀 반환
     */
    fun getGoogleTitle(): String {
        navigate("https://google.com")
        return page.title()
    }

    /**
     * 로그인 진행
     * @param id 아이디
     * @param pw 비밀번호
     */
    fun login(id: String, pw: String) {
        // 테스트를 위해 유저 네임, 로그인, 비밀번호, 다음 진행 버튼 셀렉터는 상수로 작성
        val usernameSelectors = listOf(
            "input[name='username']",
            "input[name='email']",
            "input[name='id']",
            "input[name='userid']",
            "input#username",
            "input#email",
            "input[placeholder*='아이디']",
            "input[placeholder*='이메일']",
            "input[placeholder*='email' i]",
            "input[type='email']"
        )

        var usernameInput = findVisibleElement(usernameSelectors)
        if (usernameInput == null) { // 아이디 칸이 없는 경우: 로그인 창 찾을 것
            val loginTriggerSelectors = listOf(
                "a:has-text('로그인')", "button:has-text('로그인')",
                "a:has-text('Login')", "button:has-text('Login')",
                "[role='button']:has-text('로그인')"
            )

            val loginTrigger = findVisibleElement(loginTriggerSelectors)
            if (loginTrigger == null) {
                println("로그인을 시작할 수 없습니다")
                return
            }
            loginTrigger.click()

            try {
                page.waitForLoadState()
                usernameInput = findVisibleElement(usernameSelectors)
            } catch (e: TimeoutException) {
                println("아이디 입력 필드를 찾을 수 없습니다.")
                return
            }
        }

        if (usernameInput == null) {
            println("아이디 입력 필드를 찾을 수 없습니다.")
            return
        }
        usernameInput.fill(id)
        println("아이디 입력 완료.")

        val nextButtonSelectors = listOf(
            "button:has-text('다음')",
            "button:has-text('Next')",
            "button:has-text('계속')",
            "input[type='submit']"
        )
        findVisibleElement(nextButtonSelectors)?.click()
        page.waitForLoadState()

        val passwordSelectors = listOf(
            "input[type='password']",
            "input[name='password']",
            "input[name='pass']",
            "input[name='pw']",
            "input#password",
            "input[placeholder*='비밀번호']",
            "input[placeholder*='password' i]"
        )

        val passwordInput = findVisibleElement(passwordSelectors)
        if (passwordInput == null) {
            println("비밀번호 입력 필드를 찾을 수 없습니다.")
            return
        }
        passwordInput.fill(pw)
        println("비밀번호 입력 완료.")

        val loginButtonSelectors = listOf(
            "button:has-text('로그인')",
            "button:has-text('Login')",
            "button[type='submit']",
            "input[type='submit']"
        )
        val loginButton = findVisibleElement(loginButtonSelectors)
        if (loginButton == null) {
            println("로그인 버튼을 찾을 수 없습니다.")
            return
        }

        loginButton.click()
        println("로그인 버튼 클릭 완료.")

        page.waitForLoadState()
        println("로그인 시도 완료.")
        return
    }

    /**
     * 디버깅 모드로 켠 크롬을 사용하여 접속, 타이틀 반환
     */
    fun getTitleFromExistingChrome(): String {
        var title = ""

        Playwright.create().use { playwright ->
            val browser: Browser = playwright.chromium().connectOverCDP("http://localhost:9222")
            val context = browser.contexts().firstOrNull() ?: return "연결된 브라우저에 열려 있는 탭이 없습니다"

            val page = context.pages().firstOrNull() ?: context.newPage()
            page.navigate("https://www,google.com")
            title = page.title()
        }
        return title
    }
}
