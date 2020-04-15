package com.jackiifilwhh.services;

public class Test {

	public static void main(String[] args) {
		String str1 = "int a = 6;";
		String str2 = "for ( int i = 0; i < 5; i++);";
		levenshtein(str1, str2);
		System.out.println(EditDistance(str1, str2));
	}

	public static void levenshtein(String str1, String str2) {
		// 计算两个字符串的长度。
		int len1 = str1.length();
		int len2 = str2.length();
		// 建立上面说的数组，比字符长度大一个空间
		int[][] dif = new int[len1 + 1][len2 + 1];
		// 赋初值，步骤B。
		for (int a = 0; a <= len1; a++) {
			dif[a][0] = a;
		}
		for (int a = 0; a <= len2; a++) {
			dif[0][a] = a;
		}
		// 计算两个字符是否一样，计算左上的值
		int temp;
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 取三个值中最小的
				dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
			}
		}
		System.out.println("字符串\"" + str1 + "\"与\"" + str2 + "\"的比较");
		// 取数组右下角的值，同样不同位置代表不同字符串的比较
		System.out.println("差异步骤：" + dif[len1][len2]);
		// 计算相似度
		float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
		System.out.println("相似度：" + similarity);
	}

	// 得到最小值
	private static int min(int... is) {
		int min = Integer.MAX_VALUE;
		for (int i : is) {
			if (min > i) {
				min = i;
			}
		}
		return min;
	}

	public static int EditDistance(String source, String target) {
		char[] sources = source.toCharArray();
		char[] targets = target.toCharArray();
		int sourceLen = sources.length;
		int targetLen = targets.length;
		int[][] d = new int[sourceLen + 1][targetLen + 1];
		for (int i = 0; i <= sourceLen; i++) {
			d[i][0] = i;
		}
		for (int i = 0; i <= targetLen; i++) {
			d[0][i] = i;
		}

		for (int i = 1; i <= sourceLen; i++) {
			for (int j = 1; j <= targetLen; j++) {
				if (sources[i - 1] == targets[j - 1]) {
					d[i][j] = d[i - 1][j - 1];
				} else {
					// 插入
					int insert = d[i][j - 1] + 1;
					// 删除
					int delete = d[i - 1][j] + 1;
					// 替换
					int replace = d[i - 1][j - 1] + 1;
					d[i][j] = Math.min(insert, delete) > Math.min(delete, replace) ? Math.min(delete, replace)
							: Math.min(insert, delete);
				}
			}
		}
		return d[sourceLen][targetLen];
	}
}