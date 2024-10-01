package com.example.tabitabi.webcrawling;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabitabi.model.Product.ProductPriceStatistics;
import com.example.tabitabi.repository.ProductPriceStatisticsRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CrawlerController {

    @Autowired
    private ProductPriceStatisticsRepository priceStatisticsRepository;

    @GetMapping("/crawling")
    public ResponseEntity<Integer> crawling(HttpServletRequest request) {
        // WebDriver 설정
        String WEB_DRIVER_ID = "webdriver.chrome.driver";
        String WEB_DRIVER_PATH = "C:\\Users\\Yeonju YOO\\.cache\\selenium\\chromedriver\\win64\\128.0.6613.119\\chromedriver.exe"; 
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);
        
        try {
            driver.get("https://www.kamis.or.kr/customer/price/retail/item.do");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 'ulitemcategorycode'에서 항목을 클릭하여 반복적으로 데이터 수집
            List<WebElement> categoryElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#ulitemcategorycode a")));
            for (int i = 0; i < categoryElements.size(); i++) {
                WebElement categoryElement = categoryElements.get(i);
                String categoryText = categoryElement.getText();
                
                // "축산물" 카테고리는 건너뛰기
                if (categoryText.equals("축산물")) {
                    continue;
                }
                
                try {
                    categoryElement.click();
                    Thread.sleep(2000); // 페이지 로딩 대기
                } catch (StaleElementReferenceException e) {
                    // 예외 발생 시 해당 요소 다시 찾기
                    categoryElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#ulitemcategorycode a")));
                    categoryElement = categoryElements.get(i);
                    categoryElement.click();
                    Thread.sleep(2000); // 페이지 로딩 대기
                }

                List<WebElement> itemElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#ulitemcode a")));
                for (int j = 0; j < itemElements.size(); j++) {
                    WebElement itemElement = itemElements.get(j);
                    String itemText = itemElement.getText();
                    
                    try {
                        itemElement.click();
                        Thread.sleep(2000); // 페이지 로딩 대기
                    } catch (StaleElementReferenceException e) {
                        // 예외 발생 시 해당 요소 다시 찾기
                        itemElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#ulitemcode a")));
                        itemElement = itemElements.get(j);  // 새롭게 찾은 요소로 대체
                        itemElement.click();
                        Thread.sleep(2000); // 페이지 로딩 대기
                    }


                    // 'ulkindcode'에서 항목을 클릭하여 반복적으로 데이터 수집
                    List<WebElement> kindcodeElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#ulkindcode a")));
                    for (int k = 0; k < kindcodeElements.size(); k++) {
                        WebElement kindcodeElement = kindcodeElements.get(k);
                        String kindcodeText = kindcodeElement.getText();
                        
                        try {
                            kindcodeElement.click();
                            Thread.sleep(2000); // 페이지 로딩 대기
                        } catch (StaleElementReferenceException e) {
                            // 예외 발생 시 해당 요소 다시 찾기
                            kindcodeElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#ulkindcode a")));
                            kindcodeElement = kindcodeElements.get(k);
                            kindcodeElement.click();
                            Thread.sleep(2000); // 페이지 로딩 대기
                        }

                        try {
                            // '조회하기' 버튼 클릭
                            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn_search")));
                            searchButton.click();
                            Thread.sleep(5000); // 데이터 로딩 대기

                            // 데이터 추출
                            WebElement dateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody/tr/th[2]")));
                            String dateText = dateElement.getText().split("\n")[1];
                            
                            WebElement averageTr = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[contains(@class, 'fwb')][1]")));
                            WebElement averageTd = averageTr.findElement(By.xpath("./td[2]"));
                            String averageText = averageTd.getText();
                            
                            WebElement maxTr = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[contains(@class, 'fwb')][2]")));
                            WebElement maxTd = maxTr.findElement(By.xpath("./td[2]"));
                            String maxText = maxTd.getText();
                            
                            WebElement minTr = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[contains(@class, 'fwb')][3]")));
                            WebElement minTd = minTr.findElement(By.xpath("./td[2]"));
                            String minText = minTd.getText();
                            
                            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3.s_tit6.color.fl")));
                            String number = element.getText();
                            
                            String[] parts = number.split(": ");
                            
                            String unit = parts[1].trim(); // "20kg" 만 추출
                            
                            // 값 변환
                            Double averageValue = parsePrice(averageText);
                            Double maxValue = parsePrice(maxText);
                            Double minValue = parsePrice(minText);
                            
                            // 중복 데이터 확인
                            boolean exists = priceStatisticsRepository.existsByCategoryAndItemAndTypeAndDateAndUnit(
                                categoryText,
                                itemText,
                                kindcodeText,
                                dateText,
                                unit
                            );
                            
                            if (!exists) {
                                // 데이터 저장
                                ProductPriceStatistics statistics = new ProductPriceStatistics();
                                statistics.setCategory(categoryText);
                                statistics.setItem(itemText);
                                statistics.setType(kindcodeText);
                                statistics.setDate(dateText);
                                statistics.setAveragePrice(averageValue);
                                statistics.setMaxPrice(maxValue);
                                statistics.setMinPrice(minValue);
                                statistics.setUnit(unit);
                                
                                priceStatisticsRepository.save(statistics);
                            } else {
                                System.out.println("중복된 데이터: " + categoryText + ", " + itemText + ", " + kindcodeText + ", " + dateText + ", " + unit);
                            }
                            
                        } catch (Exception e) {
                            // 데이터 추출에 실패한 경우 로그 출력 및 다음 항목으로 넘어감
                            System.out.println("데이터 추출 실패: " + e.getMessage());
                        }
                        // 페이지 이동을 위한 대기
                        driver.navigate().back(); // 이전 페이지로 돌아가기
                        Thread.sleep(2000); // 대기
                    }
                    
                    // 아이템 리스트로 돌아간 후 요소 재탐색
                    
                    Thread.sleep(2000); // 대기
                   
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(1);
    }

    private Double parsePrice(String priceText) {
        // 문자열에서 숫자와 소수점을 제외한 모든 문자를 제거
        String cleanedText = priceText.replaceAll("[^\\d.]", "");
        try {
            return Double.parseDouble(cleanedText);
        } catch (NumberFormatException e) {
            // 변환 실패 시 디폴트 값 반환 또는 예외 처리
            return null;
        }
    }
}
