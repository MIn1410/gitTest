package com.hemin.mybatis.utils;
/*
每次获取SqlSession对象代码过于繁琐，封装一个工具类获取SqlSession对象
 */

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

public class SqlSessionUtil {

    //工具类的构造方法一般都是私有化的
    //工具类中所有方法都是静态的，直接采用类名即可调用，不需要new对象


    private SqlSessionUtil(){} //为了防止new对象，构造方法私有化

    private static SqlSessionFactory sqlSessionFactory;//Factory需要一直使用

    //类加载时运行
    //SqlSessionUtil工具类在进行第一次加载的时候，解析mybatis-config.xml文件。创建SqlSessionFactory对象
    static{
        try {
            //Builder对象只需要使用一次，所以将其作为局部对象
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static ThreadLocal<SqlSession> local = new ThreadLocal<>();//获取一个线程，利用其进行事务的控制

    //每调用一次openSession()可获取一个新的会话，该会话支持自动提交，故通过这个类获取的session对象不用手动commit

    public static SqlSession openSession(){
        SqlSession sqlSession = local.get();//将sqlSession与线程绑定，一个线程对应一个SqlSession
        if (sqlSession == null) {
            sqlSession = sqlSessionFactory.openSession();
            local.set(sqlSession);
        }
        return sqlSession;
    }

    /**
     * 关闭SqlSession对象
     * @param sqlSession
     */
    public static void close(SqlSession sqlSession) {
        if (sqlSession != null) {
            sqlSession.close();
        }
        local.remove();//将线程与SqlSession解绑，因为线程池里的线程会复用

    }
}