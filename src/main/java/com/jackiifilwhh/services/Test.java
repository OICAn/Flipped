package com.jackiifilwhh.services;

public class Test {

	public static void main(String[] args) {
		String str1 = "int a = 6;";
		String str2 = "for ( int i = 0; i < 5; i++);";
		levenshtein(str1, str2);
		System.out.println(EditDistance(str1, str2));
	}

	public static void levenshtein(String str1, String str2) {
		// ���������ַ����ĳ��ȡ�
		int len1 = str1.length();
		int len2 = str2.length();
		// ��������˵�����飬���ַ����ȴ�һ���ռ�
		int[][] dif = new int[len1 + 1][len2 + 1];
		// ����ֵ������B��
		for (int a = 0; a <= len1; a++) {
			dif[a][0] = a;
		}
		for (int a = 0; a <= len2; a++) {
			dif[0][a] = a;
		}
		// ���������ַ��Ƿ�һ�����������ϵ�ֵ
		int temp;
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					temp = 0;
				} else {
					temp = 1;
				}
				// ȡ����ֵ����С��
				dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
			}
		}
		System.out.println("�ַ���\"" + str1 + "\"��\"" + str2 + "\"�ıȽ�");
		// ȡ�������½ǵ�ֵ��ͬ����ͬλ�ô���ͬ�ַ����ıȽ�
		System.out.println("���첽�裺" + dif[len1][len2]);
		// �������ƶ�
		float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
		System.out.println("���ƶȣ�" + similarity);
	}

	// �õ���Сֵ
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
					// ����
					int insert = d[i][j - 1] + 1;
					// ɾ��
					int delete = d[i - 1][j] + 1;
					// �滻
					int replace = d[i - 1][j - 1] + 1;
					d[i][j] = Math.min(insert, delete) > Math.min(delete, replace) ? Math.min(delete, replace)
							: Math.min(insert, delete);
				}
			}
		}
		return d[sourceLen][targetLen];
	}
}