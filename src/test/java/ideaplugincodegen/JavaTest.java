package ideaplugincodegen;

/**
 * @author zhangyinghui
 * @date 2023/8/7
 */
public class JavaTest {
    enum T{
        T1("t1"),
        T2("t2");
        private final String data;
        private T(String s){
            data = s;
        }
    }
    public static void main(String[] args){
//        String s = "{CLS_PREFIX}xxxx";
//        String r = s.replaceAll("\\{\\s*"+"CLS_PREFIX"+"\\s*\\}", "ABC");
//        System.out.println("result:"+r);
        System.out.println(""+T.T1);
        System.out.println(""+T.T1.ordinal());
        System.out.println(""+T.T2.ordinal());
    }
}
