package cagtc.pay.job.utils;

import java.util.UUID;

public class UUIDUtil {

	
	/**  
     * 生成32位编码  
     * @return string  
     */    
    public static String getUUID(){    
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");    
        return uuid;    
    }    
        
    public static void main(String[] args) {
		
    	System.out.println(getUUID());
    	
	}
	
}
