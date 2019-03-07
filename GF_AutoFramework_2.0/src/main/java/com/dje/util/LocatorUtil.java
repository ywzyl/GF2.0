package com.dje.util;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dje.pageLocator.Locator;
/**
 * 1.解析页面定位信息的Url,将解析内容装到map中
 * <代码是通过下面的pageName来定位去读取哪个页面的定位信息
 * pageName是取测试case的类名前半部分如Register_SuccessTester_002，取的是 Register
 * 所以页面定位信息的xml文件 取名必须注意！！！
 * 
 * 2.将项目中用到的测试页面都写到一个xml中的，所以固定读取的是 /locatorxml/locater.xml
 * @author zhangj
 *
 */

public class LocatorUtil {
	public static void main(String[] args) {
        Locator locator = getLocator("RegisterTester", "手机号输入框");
        System.out.println(locator.getBy());
    }
    
    //将页面元素的定位信息存储在map中
    //通过页面名称，获取到指定页面的所有定位信息
    //通过定位元素的描述，获取到locator对象
    private static Map<String,Map<String, Locator>> pageMap = new HashMap<>();
    
    //该类第一次被使用，就会去读取指定xml文件（读取页面的所有元素定位信息）
        static {
            readXml();
        }
           
    /**
     * 
     * @param pageName
     * 页面名称
     * @param desc
     * 元素的关键字描述
     * @return
     */
    public static Locator getLocator(String pageName,String desc) {
        //获取到指定页面的所有定位信息        
        Map<String, Locator> map = pageMap.get(pageName);
        //System.out.println("通过页面名称获取到页面所有定位信息"+map);
        //返回指定描述locator对象
        Locator locator = map.get(desc);
        //System.out.println("通过关键字"+desc +"获取到locator对象"+locator);
        return locator; 
        }
    

    /**
     * 读取xml文件
     */
    public static void readXml() {
        SAXReader reader = new SAXReader();
        Document doucment = null;
        try {
            doucment = reader.read(LocatorUtil.class.getResourceAsStream("/locatorxml/locater.xml"));
        } catch (DocumentException e) {
            System.out.println("读取xml失败");
            e.printStackTrace();
        }
        //获取到根节点pages
        Element rootEleent = doucment.getRootElement();
        //System.out.println("获取到根节点"+rootEleent.getName());
        //获取到子节点page
        List<Element> elements = rootEleent.elements();
        //System.out.println("获取到"+elements.size()+"个页面");
        
        
        //遍历所有的page
        for (Element element : elements) {
            String pageName = element.attribute("pageName").getStringValue();
            //System.out.println("pageName为" +pageName);
            
            
            
            
            //获取到page的子节点 locators
            Element locatorsElement = element.element("locaters");
            //获取到所有的locator
            List<Element> locatorElements = locatorsElement.elements();
            
            //创建map
            Map<String, Locator> LocatorMap = new HashMap<>();
            for (Element locatorElement : locatorElements) {                
                Locator locatorObj = new Locator(locatorElement.attributeValue("by"),locatorElement.attributeValue("value"),locatorElement.attributeValue("des"));
                //System.out.println(locatorObj);
                
                
                //通过描述信息，获取到locator对象
                LocatorMap.put(locatorElement.attributeValue("des"), locatorObj);
            }            
            pageMap.put(pageName, LocatorMap);                        
            System.out.println(pageMap);
            
        }                            
    }
    
}
