package ideaplugincodegen;

/**
 * @author zhangyinghui
 * @date 2023/8/7
 */
public class JavaTest {
    public static void main(String[] args){
        String s = "{CLS_PREFIX}xxxx";
        String r = s.replaceAll("\\{\\s*"+"CLS_PREFIX"+"\\s*\\}", "ABC");
        System.out.println("result:"+r);
    }
}
