/**
 * ObjectCompareUtils.java
 * author: yujiakui
 * 2017年12月20日
 * 下午3:33:01
 */
package com.ctfin.framework.ats;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.beust.jcommander.internal.Lists;
import org.testng.Assert;

/**
 * @author yujiakui
 *
 *         下午3:33:01
 *
 *         对象比较工具
 */
public class ObjectCompareUtils {
	public ObjectCompareUtils() {
	}

	/**
	 * 排除某系属性的对象对比
	 *
	 * @param expectedObj
	 * @param actualObj
	 * @param excludeFieldName
	 */
	public static void assertEqualsWithExcludeFields(Object expectedObj, Object actualObj,
													 String... excludeFieldName) {
		assertEqualsWithFieldsAndFlag(expectedObj, actualObj, true,
				expectedObj.getClass().getSimpleName(), excludeFieldName);
	}

	/**
	 * 仅仅对比对象的某些属性
	 *
	 * @param expectedObj
	 * @param actualObj
	 */
	public static void assertEqualsWithIncludeFields(Object expectedObj, Object actualObj,
													 String... includeFieldName) {
		assertEqualsWithFieldsAndFlag(expectedObj, actualObj, false,
				expectedObj.getClass().getSimpleName(), includeFieldName);
	}

	/**
	 * @param expectedObj
	 * @param actualObj
	 * @param excludeFlag
	 */
	private static void assertEqualsWithFieldsAndFlag(Object expectedObj, Object actualObj,
													  Boolean excludeFlag, String fieldPath, String... fieldNames) {

		if (fieldNames != null && fieldNames.length != 0) {
			List<String> fieldNameLists = Arrays.asList(fieldNames);
			Field[] fields = expectedObj.getClass().getDeclaredFields();
			Field[] var7 = fields;
			int var8 = fields.length;

			for(int var9 = 0; var9 < var8; ++var9) {
				Field field = var7[var9];
				field.setAccessible(true);
				List<String> fieldNameSuffixs = Lists.newArrayList();
				boolean fieldNameExistFlag = false;
				String fieldNamePoint = field.getName() + ".";
				Iterator var14 = fieldNameLists.iterator();

				String tmpFieldPath;
				while(var14.hasNext()) {
					String fieldName = (String)var14.next();
					if (fieldName.equals(field.getName())) {
						fieldNameExistFlag = true;
					} else if (fieldName.startsWith(fieldNamePoint)) {
						tmpFieldPath = fieldName.substring(fieldNamePoint.length());
						fieldNameSuffixs.add(tmpFieldPath);
					}
				}

				Object fieldExpectObj = getFieldObj(field, expectedObj);
				Object fieldActualObj = getFieldObj(field, actualObj);
				tmpFieldPath = fieldPath + "." + field.getName();
				if ((!excludeFlag || !fieldNameExistFlag) && (excludeFlag || fieldNameExistFlag || !CollectionUtils.isEmpty(fieldNameSuffixs))) {
					if (!excludeFlag && fieldNameExistFlag && CollectionUtils.isEmpty(fieldNameSuffixs)) {
						Assert.assertEquals(fieldActualObj, fieldExpectObj, "字段路径：" + tmpFieldPath);
					} else {
						assertEqualsWithFieldsAndFlag(fieldExpectObj, fieldActualObj, excludeFlag, tmpFieldPath, (String[])fieldNameSuffixs.toArray(new String[0]));
					}
				}
			}

		} else {
			Assert.assertEquals(actualObj, expectedObj, "字段路径:" + fieldPath);
		}
	}

	/**
	 * 字段field进行对比
	 *
	 * @param field
	 */
	private static Object getFieldObj(Field field, Object obj) {
		Object fieldObj = null;
		try {
			fieldObj = field.get(obj);

		} catch (IllegalArgumentException e) {
			System.out.println("字段name=" + field.getName() + "异常" + e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("字段name=" + field.getName() + "异常" + e.getMessage());
			e.printStackTrace();
		}

		return fieldObj;
	}
}
