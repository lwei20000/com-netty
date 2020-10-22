package javabase.T01_ThreadLocal.msb;

/**
 * @Auther: weiliang
 * @Date: 2020/10/22 12:05
 * @Description: https://www.bilibili.com/video/BV1Fk4y1z7Uo?t=277&p=2 马士兵教育
 */
public class M {

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalizing...");
    }
}
