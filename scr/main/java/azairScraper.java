import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class AzairScraper {
    public static void main(String[] args) {
        // 🔹 automatyczne pobranie właściwego chromedriver
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // uruchom bez okna
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            String url = "https://www.azair.eu/azfin.php?searchtype=flexi&tp=0&isOneway=return&srcAirport=Polska&dstAirport=Gdziekolwiek&depdate=2025-09-07&arrdate=2025-09-28&minDaysStay=1&maxDaysStay=8&currency=PLN&lang=pl";
            driver.get(url);

            // 🔹 poczekaj aż pojawią się wyniki
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.result")));

            // 🔹 pobierz wszystkie wyniki
            List<WebElement> results = driver.findElements(By.cssSelector("div.result"));

            FileWriter writer = new FileWriter("wyniki_lotow.txt", false);

            for (WebElement r : results) {
                String dep = safeFind(r, ".from");
                String arr = safeFind(r, ".to");
                String date = safeFind(r, ".date");
                String airline = safeFind(r, ".company");
                String price = safeFind(r, ".price");

                String line = dep + " | " + arr + " | " + date + " | " + airline + " | " + price;
                System.out.println(line);
                writer.write(line + "\n");
            }

            writer.close();
            System.out.println("✅ Zapisano do wyniki_lotow.txt");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    // pomocnicza metoda żeby uniknąć wyjątków
    private static String safeFind(WebElement root, String selector) {
        try {
            return root.findElement(By.cssSelector(selector)).getText();
        } catch (Exception e) {
            return "";
        }
    }
}

