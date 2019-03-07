package com.dje.util;

import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SeleniumUtil {
	private static Class clazz;
	private static Object obj;
	public static void main(String[] args) {
		          getDriver();
		      }
	public static WebDriver getDriver() {
		          Document document = null;
		          Element driverNameElement= null;
		          String driverName =null;
		          
		          SAXReader reader = new SAXReader();
		          try {
		              document = reader.read(SeleniumUtil.class.getResourceAsStream("/driverProperties.xml"));
		          } catch (DocumentException e) {		  
		              e.printStackTrace();
		          }
		  
		          /**
		           * 下面是通过解析XML，获取到驱动的类全名
		           */
		          Element rootElement = document.getRootElement(); //获取到根节点
		          int index = Integer.parseInt(rootElement.attributeValue("driverIndex"));//获取到根节点上的driverIndex并转成int类型		          		          
		          //获取到所有"name"子节点，遍历，找出与根节点中的driverIndex相同的，将其value属性值获取出来，作为类全名用于反射
		          List<Element> driverNameElements = rootElement.elements("name");
		          for (Element driverNameElement1 : driverNameElements) {
		              int i = Integer.parseInt(driverNameElement1.attributeValue("index"));
		              if (i == index) {
		                  driverName = driverNameElement1.attributeValue("value");//获取到name子节点的“value”属性值
		                  driverNameElement = driverNameElement1;//将该节点赋值给driverElement，后续根据它来获得子节点
		              }
		              
		          }
		          		  
		          /**
		           * 通过类全名，反射出驱动类来
		           */
		          try {
		              clazz = Class.forName(driverName);
		          } catch (ClassNotFoundException e) {
		  
		              e.printStackTrace();
		          }
		          
		          /**
		           * 下面是解析XML中的系统参数以及能力参数
		           */
		          
		          Element propertiesElement = driverNameElement.element("properties");
		          List<Element> propertyElements = propertiesElement.elements("property");
		  
		          //设置系统参数
		          for (Element property : propertyElements) {		              
		              System.setProperty(property.attributeValue("name"), property.attributeValue("value"));
		              
		          }
		         
		          //设置能力（ie的话，需要设置忽略域设置等级 以及忽略页面百分比的能力）
		          Element capabilitiesElement = driverNameElement.element("capabilities");
		          if (capabilitiesElement != null) {
		              //创建能力对象
		              DesiredCapabilities realCapabilities = new DesiredCapabilities();
		              //获得能力列表
		              List<Element> capabilitiesElements = capabilitiesElement.elements("capability");
		              for (Element capability : capabilitiesElements) {
		                  //遍历能力列表，并给能力赋值
		                  realCapabilities.setCapability(capability.attributeValue("name"), true);
		              }
		          }
		          
		          
		          /*
		           * 通过反射，创建驱动对象
		           */
		          
		         try {
		              obj = clazz.newInstance();
		          } catch (InstantiationException e) {
		             e.printStackTrace();
		         } catch (IllegalAccessException e) {
		             e.printStackTrace();
		        }
		         
		         WebDriver driver = (WebDriver) obj;
		         return driver;
		     }
}
