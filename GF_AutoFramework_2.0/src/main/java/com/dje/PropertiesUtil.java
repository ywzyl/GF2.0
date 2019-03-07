package com.dje;
import java.io.IOException;
import java.util.Properties;
/**
 * 该类专门用来处理properties文件
 * @author cuijing
 *
 */
public class PropertiesUtil {
    public static void main(String[] args) {
        System.out.println(getProperties("register"));
    }
    
    
    static Properties properties = new Properties();
    
    //只加载一次配置文件
    static {
        readURLProperties();
    }
    
    /**
     * 加载配置文件
     */
    private static void readURLProperties() {

        try {
            properties.load(PropertiesUtil.class.getResourceAsStream("/properties/config.properties"));
            
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }
    
    /**
     * 通过配置文件中的 key获取到value
     * @param propertiesKey
     * 配置文件中的key
     * @return
     */
    public static String getProperties(String propertiesKey) {
        return properties.getProperty(propertiesKey);
    }

}
