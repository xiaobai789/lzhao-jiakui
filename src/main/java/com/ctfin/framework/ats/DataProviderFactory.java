/**
 * DataProviderFactory.java
 * author: yujiakui
 * 2017年9月5日
 * 上午11:52:35
 */
package com.ctfin.framework.ats;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * @author yujiakui
 *
 * 上午11:52:35
 *
 * 数据提供者工厂类
 */
public class DataProviderFactory {

    /**
     * 测试用例对应的路径
     */
    private static String USER_CASE_PATH = "src/test/resources/";

    /**
     * 组装dataProvider
     *
     * @param testClass 测试用例的相对路径
     * @param method    指定对应的测试文件，可以为空
     */
    public static Object[][] assembleDataProvider(Class<?> testClass, Method method) {
        // 获取所有的用例文件
        List<String> useCaseFileNames = extractUseCaseFileNames(testClass, method);
        if (useCaseFileNames.size() < 1) {
            throw new RuntimeException("没有一个测试用例可用");
        }
        Object[][] result = new Object[useCaseFileNames.size()][];
        Yaml yaml = new Yaml();
        int i = 0;
        for (String useCaseFileName : useCaseFileNames) {
            try {
                File file = new File(useCaseFileName);
                if (file.exists()) {
                    Iterable<Object> objIterable = yaml.loadAll(new FileInputStream(file));
                    List<Object> yamlBlocks = new ArrayList<Object>();
                    Iterator<Object> iterator = objIterable.iterator();
                    while (iterator.hasNext()) {
                        Object object = iterator.next();
                        yamlBlocks.add(object);
                    }
                    result[i] = yamlBlocks.toArray();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static XTest[] getXTestAnnotation(Class<?> testClass) {
        Annotation[] annotations = testClass.getAnnotations();
        return getXTestAnnotation(annotations);
    }

    private static XTest[] getXTestAnnotation(Annotation[] annotations) {
        if (null != annotations && annotations.length != 0) {
            List<XTest> xtests = new ArrayList();
            Annotation[] var2 = annotations;
            int var3 = annotations.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Annotation annotation = var2[var4];
                if (XTest.class.equals(annotation.annotationType())) {
                    xtests.add((XTest) annotation);
                }
            }

            return (XTest[]) xtests.toArray(new XTest[0]);
        } else {
            return null;
        }
    }

    private static XTest[] getXTestAnnotationFromMethod(Method testMethod) {
        Annotation[] annotations = testMethod.getAnnotations();
        return getXTestAnnotation(annotations);
    }

    /**
     * 根据XTest注解获取对应的测试文件的路径
     *
     * @return 返回对应的测试文件名列表
     */
    private static List<String> extractUseCaseFileNames(Class<?> testClass, Method method) {
        XTest[] xTests = getXTestAnnotation(testClass);
        XTest[] methodXTests = getXTestAnnotationFromMethod(method);
        if (null != xTests && xTests.length != 0 || null != methodXTests && methodXTests.length != 0) {
            if (null != xTests && xTests.length > 1 || null != methodXTests && methodXTests.length > 1) {
                throw new RuntimeException(MessageFormat.format("测试类className={0}或者测试方法methodName={1}没有对应的XTest注解个数不等于1", testClass.getName(), method.getName()));
            } else {
                String currentUserPath = System.getProperty("user.dir");
                String relatePath = currentUserPath + File.separator + USER_CASE_PATH;
                String testOnly = "";
                boolean isExistClassXTest = false;
                if (null != xTests && xTests.length == 1) {
                    relatePath = relatePath + xTests[0].relatePath().trim();
                    testOnly = xTests[0].testOnly().trim();
                    isExistClassXTest = true;
                }

                if (null != methodXTests && methodXTests.length == 1) {
                    if (!StringUtils.isEmpty(methodXTests[0].relatePath())) {
                        relatePath = relatePath + (isExistClassXTest ? File.separator : "");
                        relatePath = relatePath + methodXTests[0].relatePath().trim();
                    }

                    if (!StringUtils.isEmpty(methodXTests[0].testOnly())) {
                        testOnly = methodXTests[0].testOnly().trim();
                    }
                }

                if (null != testOnly && !testOnly.isEmpty() && !testOnly.equals("*")) {
                    testOnly = testOnly.replace("*", ".*");
                } else {
                    testOnly = ".*";
                }

                List<String> testOnlyFileNames = new ArrayList();
                testOnlyFileNames.add(testOnly + ".yaml");
                testOnlyFileNames.add(testOnly + ".yml");
                return TraverseFolderUtils.traverse(relatePath, testOnlyFileNames);
            }
        } else {
            throw new RuntimeException(MessageFormat.format("测试类className={0}对应的测试方法methodName={1}没有对应的XTest注解", testClass.getName(), method.getName()));
        }
    }
}
