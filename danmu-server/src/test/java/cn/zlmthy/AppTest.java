package cn.zlmthy;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        String s = "s=1&s=2";
        parseRequestParams(s);
    }

    private Map<String, Object> parseRequestParams(String params){
        Map<String, Object> result = new HashMap<>();
        String[] strings = params.split("&");
        if (strings.length >0 ) {
            for (int i=0;i< strings.length;i++){
                String[] s = strings[i].split("=");
                System.out.println(s[0]);
                System.out.println(s[1]);
                result.put(s[0], s[1]);

            }
        }
        return result;
    }
}
