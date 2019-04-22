/**
 * TraverseFolder.java
 * author: yujiakui
 * 2017年9月4日
 * 下午3:09:29
 */
package com.ctfin.framework.ats;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author yujiakui
 *
 *         下午3:09:29
 *
 *         遍历文件夹，取得所有的文件名称
 */
public class TraverseFolderUtils {

	/**
	 * 遍历路径获得对应的文件列表 ： 正则表达式对应的文件名称
	 *
	 * @param regexFileNames 正则表达式对应的文件名称 带扩展名的
	 */
	public static List<String> traverse(String path, List<String> regexFileNames) {

		List<String> pathFileNames = new ArrayList<String>();
		File file = new File(path);
		if (!file.exists()) {
			throw new RuntimeException(MessageFormat.format("路径path={0}对应的文件夹不存在", path));
		} else {
			LinkedList<File> list = new LinkedList();
			File[] files = file.listFiles();
			File[] var6 = files;
			int var7 = files.length;

			int var8;
			for (var8 = 0; var8 < var7; ++var8) {
				File file2 = var6[var8];
				if (file2.isDirectory()) {
					list.add(file2);
				} else {
					Iterator var10 = regexFileNames.iterator();

					while (var10.hasNext()) {
						String regexFileName = (String) var10.next();
						if (Pattern.matches(regexFileName, file2.getName())) {
							pathFileNames.add(file2.getAbsolutePath());
							break;
						}
					}
				}
			}

			while (!list.isEmpty()) {
				File temp_file = (File) list.removeFirst();
				files = temp_file.listFiles();
				File[] var14 = files;
				var8 = files.length;

				for (int var15 = 0; var15 < var8; ++var15) {
					File file2 = var14[var15];
					if (file2.isDirectory()) {
						list.add(file2);
					} else {
						Iterator var17 = regexFileNames.iterator();

						while (var17.hasNext()) {
							String regexFileName = (String) var17.next();
							if (Pattern.matches(regexFileName, file2.getName())) {
								pathFileNames.add(file2.getAbsolutePath());
								break;
							}
						}
					}
				}
			}

			return pathFileNames;
		}

	}
}
