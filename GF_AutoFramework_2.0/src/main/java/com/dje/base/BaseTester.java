package com.dje.base;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import com.dje.PropertiesUtil;
import com.dje.pageLocator.*;
import com.dje.util.*;


public class BaseTester {
    public static WebDriver driver;
    static Map<String, Map<String, Locator>> pagesMap;
    Logger logger= Logger.getLogger(BaseTester.class);
    
    /**
     * 套件执行前，启动浏览器驱动
     */
    @BeforeSuite
    public void BeforSuit() {
        driver = SeleniumUtil.getDriver();
    }
    
    /**
     * 套件执行后，退出当前浏览器驱动，关闭所有相关窗口
     * @throws InterruptedException
     */
    @AfterSuite
    public void afterSuit() throws InterruptedException {
        Thread.sleep(5000);
        driver.quit();
    }
    

    /****************************数据提供者*****************************************/
    
    public static void main(String[] args) {
        String className = "Register_FailTester_001";
        int firstIndex = className.indexOf("_");
        int lastIndex = className.lastIndexOf("_");
        System.out.println(lastIndex);
        

        //获取到当前excel的名称（规定excel的名称与我们的类名第一个“_”前字符串相同）
        String excelName = className.substring(0, firstIndex);
        System.out.println("当前页面名称为"+excelName);
        String sheetName =  className.substring(lastIndex+1);
        System.out.println("当前sheet名称为"+sheetName);
    }
    
    
    /**
     * 数据提供者。ExcelDataProvider是通过poi从excel读取的测试数据
     * @return
     */
    @DataProvider(name="dataProvider")
    public Iterator<Object[]> dataProvider(){
        String className = this.getClass().getSimpleName();
        int firstIndex = className.indexOf("_");
        int lastIndex = className.lastIndexOf("_");
        
        //获取到当前excel的名称（规定excel的名称与我们的类名第一个“_”前字符串相同）
        String excelName = className.substring(0, firstIndex);
    //    System.out.println("当前excel名称为"+excelName);
        String sheetName =  className.substring(lastIndex+1);
    //    System.out.println("当前sheet名称为"+sheetName);
        return new ExcelDataProvider(sheetName,excelName);
        
    }
    
    
    
    /*****************************智能等待获取元素*****************************/
    
    /**
     * 获取元素
     * @param timeOut
     * 超时时间
     * @param by
     * 获取元素方法
     * @return
     * 返回获取到的元素
     */
    public WebElement getElement(int timeOut,final By by) {
        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        return wait.until(new ExpectedCondition<WebElement>() {

            @Override
            public WebElement apply(WebDriver driver) {
                
                return driver.findElement(by);
            }
        });
        
        
    }
    
    /**
     * 获取元素，指定等待时间为5S
     * @param by
     * 获取元素 方法
     * @return
     */
    public WebElement getElement(By by) {
        
        return getElement(10, by);
        
    }

    /**
     * 通过关键字定位元素
     * @param timeOut
     * 超时时间
     * @param keyword
     * 元素关键字，通过该关键字定位到元素
     * @return
     */

    public WebElement getElement(int timeOut,String keyword) {
        //通过反射，获取到运行该方法对象的类名
        //谁继承了该BaseTester类就获取到谁的类名
        String className = this.getClass().getSimpleName();
        int index = className.indexOf("_");
        String pageName = className.substring(0, index);
        //System.out.println("当前页面名称为"+pageName);
        WebElement element = getElement(pageName, keyword, timeOut);
        return element;
    }
    
    /**
     * 获取指定页面名称和关键字的元素
     * @param pageName
     * 页面名称
     * @param keyword
     * 元素关键字
     * @param timeOut
     * 超时时间
     * @return
     * WebElement,返回查找到的元素
     */
    
    public WebElement getElement(String pageName,String keyword,int timeOut) {
        //locatorUtil类读取xml，并提供获取Locator的方法，locator对象里装的是by,value,desc
        //是通过当前运行对象的类名来当做pageName的
        Locator locator = LocatorUtil.getLocator(pageName, keyword);
        
        //获取到定位方式
        final String byStr = locator.getBy();//这个对应到By类中的方法名，如id,name等（8大定位方法）
        final String value = locator.getValue();//对应定位的值，如：By.id(value)
        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        return wait.until(new ExpectedCondition<WebElement>() {

            @Override
            public WebElement apply(WebDriver driver) {
                By by = null;
                //通过反射，获取到By类的字节码文件。
                Class<By> clazz = By.class;
                
                try {
                    //通过反射，获取到指定名称的方法。
                    Method method = clazz.getDeclaredMethod(byStr, String.class);
                    //调用获取到的方法。由于By中的方法是静态的，通过类调用。这里的对象为null。invoke之后返回的是object对象，强制类型转换为By对象
                    by = (By) method.invoke(null, value);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return driver.findElement(by);
            }
        });
    
        
    }
    
    
    
    
    
    
/**
 * 通过关键字定位元素，固定等待时间为5S（获取的是当前页面的元素）
 * @param keyword
 * 元素关键字，通过该关键字定位到元素
 * @return
 */
    public WebElement getElement(String keyword) {
        
        return getElement(5, keyword);
        
    }
    
    
    
    
    
    /************************selenium简单方法的二次封装***********************************/
    
    
    /**
     * 向指定元素输入内容，对sendKeys方法的二次包装
     * @param keyWord
     * 元素关键字，通过该关键字定位到元素
     * @param context
     * 要输入的内容
     */
    public void sendkeys(String keyword,String context) {
        WebElement targetElement = null;
        try {
            targetElement = getElement(keyword);
            targetElement.sendKeys(context);
            logger.info("成功向元素"+targetElement +"输入内容"+context);
        } catch (Exception e) {
            logger.error("向元素"+targetElement +"输入内容"+context+"失败");
            e.printStackTrace();
        }
    }
    
    
    /**
     * 获取到指定元素，并点击
     * @param keyword
     * 元素关键字，通过该关键字定位到元素
     */
    public void click(String keyword) {
        WebElement targetElement=null;
        try {
            targetElement = getElement(keyword);
            targetElement.click();
            logger.info("点击元素"+targetElement);
        } catch (Exception e) {
            logger.error("点击元素"+targetElement+"失败");
            e.printStackTrace();
        }
    }    
    
    /**
     * 获取指定元素上的文本信息
     * @param keyword
     * 元素关键字，通过该关键字定位到元素
     * @return
     */
    public String getText(String keyword) {
        WebElement targetElement = null;
        String text = null;
        try {
            targetElement = getElement(keyword);
            logger.info("获取元素文本信息");
            text = targetElement.getText();
        } catch (Exception e) {
            logger.error("获取元素文本信息失败");
            e.printStackTrace();
        }
        return text;
    }
    
    public void toUrl(String propertiesKey) {
        String url = PropertiesUtil.getProperties(propertiesKey);
        driver.get(url);
        
    }
    
    
    /**********************************************检查点Assert************************************************************/
    
    /**
     * 检查元素文本与预期文本是否相同
     * @param keyword 
     * 元素关键字，通过该关键字定位到元素
     * @param expected
     * 预期文本
     */
    public void assertTextEquals(String keyword,String expected) {
        WebElement targetElement = getElement(keyword);
        String actual = targetElement.getText();
        Assert.assertEquals(actual, expected);
    }
    
    
    public void assertCanGetPointElement(String pageName,String keyword) {
        WebElement targetElement = getElement(pageName,keyword,5);
        Assert.assertFalse(targetElement==null);
    }
}
