package com.zhanxj.test;

import com.zhanxj.utils.TranslateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Hokin.jim
 * Date: 13-7-3
 * Time: 上午11:48
 * Email:zhanxuejian@gmail.com
 */
public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        String result = null;
        try {
            result = TranslateUtil.cn2en("中国");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        logger.info(result);
    }
}
