package com.jackiifilwhh.services;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jackiifilwhh.Java.JavaAnalysis;
import com.jackiifilwhh.Java.SYM;


public class Diff {
	private String[] src;
	private String[] dst;
	private ArrayList<Snake> snakes;
	private ArrayList<Snake> result;
	private ArrayList<Line> lines;
	private ArrayList<Line> usualChanges;
	private ArrayList<Line> unusualChanges;
	private ArrayList<Line> tmpDelete;
	private ArrayList<Line> tmpInsert;
	private int srcLength;
	private int dstLength;
	private int oriX;
	private int oriY;
	private int[] v;

	//private final int EDITDISTANCE = 3;// 最大编辑距离
	//private final float SIMILARITY = 0.65f;// 最小相似度
	private String regex = " ";// 分隔符

	public Diff(String[] src, String[] dst) {
		this.src = src;
		this.dst = dst;
		srcLength = src.length;
		dstLength = dst.length;
		snakes = new ArrayList<>();
		result = new ArrayList<>();
		lines = new ArrayList<>();
		usualChanges = new ArrayList<>();
		unusualChanges = new ArrayList<>();
		tmpDelete = new ArrayList<>();
		tmpInsert = new ArrayList<>();
	}

	public void myers() {
		int max = srcLength + dstLength;
		v = new int[2 * max];
		oriX = 0;
		oriY = 0;

		for (int d = 0; d <= srcLength + dstLength; d++) {
			// System.out.println("D:" + d);
			for (int k = -d; k <= d; k += 2) {
				// System.out.print("k:" + k);
				boolean down = ((k == -d) || (k != d && v[k - 1 + max] < v[k + 1 + max]));
				int kPrev = down ? k + 1 : k - 1;

				int xStart = v[kPrev + max];
				int yStart = xStart - kPrev;

				int xMid = down ? xStart : xStart + 1;
				int yMid = xMid - k;

				int xEnd = xMid;
				int yEnd = yMid;

				while (xEnd < srcLength && yEnd < dstLength && src[xEnd].equals(dst[yEnd])) {
					xEnd++;
					yEnd++;
				}
				if (d == 0 && k == 0) {
					oriX = xEnd;
					oriY = yEnd;
				}

				v[k + max] = xEnd;

				snakes.add(0, new Snake(xStart, yStart, xEnd, yEnd));
				/*
				 * System.out.print(", start:(" + xStart + "," + yStart + "), mid:(" + xMid +
				 * "," + yMid + "), end:(" + xEnd + "," + yEnd + ")\n");
				 */

				if (xEnd >= srcLength && yEnd >= dstLength) {
					// System.out.println("found");
					Snake current = snakes.get(0);
					result.add(0, current);
					for (int i = 1; i < snakes.size(); i++) {
						Snake temp = snakes.get(i);
						if (temp.getxEnd() == current.getxStart() && temp.getyEnd() == current.getyStart()) {
							current = temp;
							result.add(0, temp);

							if (current.getxStart() == oriX && current.getyStart() == oriY) {
								if (oriX != 0)
									result.add(0, new Snake(0, 0, oriX, oriY));
								break;
							}
						}
					}
					/*
					 * for (int i = 0; i < result.size(); i++) { System.out.println(
					 * String.format("(%2d, %2d)", result.get(i).getxStart(),
					 * result.get(i).getyStart())); if (i == result.size() - 1) System.out.println(
					 * String.format("(%2d, %2d)", result.get(i).getxEnd(),
					 * result.get(i).getyEnd())); }
					 */

					return;
				}
			}
		}
	}

	public void showDiff() throws CloneNotSupportedException {
		int srcIndex = 0, dstIndex = 0;
		for (int i = 0; i < result.size(); i++) {
			Snake currPoint = result.get(i);
			int startK = currPoint.getxStart() - currPoint.getyStart();
			int endK = currPoint.getxEnd() - currPoint.getyEnd();
			int startX = currPoint.getxStart();
			if ((endK - startK) != 0) {
				if ((endK - startK) > 0) {
					for (int j = 0; j < (endK - startK); j++) {
						if (isSimpleChange(src[srcIndex]))
							usualChanges.add(new Line(src[srcIndex], 2, srcIndex + 1, null));
						else
							tmpDelete.add(new Line(src[srcIndex], 2, srcIndex + 1, null));
						// unusualChanges.add(new Line(src[srcIndex], 2, srcIndex + 1, null));
						lines.add(new Line(src[srcIndex], 2, srcIndex + 1, null));
						// System.out.println("- " + src[srcIndex++]);
						srcIndex++;
						startX++;
					}
				} else {
					for (int j = 0; j < Math.abs(endK - startK); j++) {
						if (isSimpleChange(dst[dstIndex]))
							usualChanges.add(new Line(dst[dstIndex], 1, null, dstIndex + 1));
						else
							tmpInsert.add(new Line(dst[dstIndex], 1, null, dstIndex + 1));
						// unusualChanges.add(new Line(dst[dstIndex], 1, null, dstIndex + 1));
						lines.add(new Line(dst[dstIndex], 1, null, dstIndex + 1));
						dstIndex++;
						// System.out.println("+ " + dst[dstIndex++]);
					}
				}
			}
			for (int j = startX; j < currPoint.getxEnd(); j++) {
				// System.out.println(" " + src[srcIndex]);
				lines.add(new Line(src[srcIndex], 0, srcIndex + 1, dstIndex + 1));
				srcIndex++;
				dstIndex++;
			}
		}
		isChangeValue();
	}

	public boolean isSimpleChange(String str) {
		// isBank只能放在第一个，用于筛选空串，避免后面方法出错
		return isBlanked(str) || isNotes(str);
	}

	public boolean isBlanked(String str) {
		return str.trim().length() == 0;
	}

	public boolean isNotes(String str) {
		return str.trim().substring(0, 2).equals("//");
		// 注释掉下面语句是为了能够正确处理包含在代码中的注释的语句
		// return str.contains("//");
	}

	@SuppressWarnings("unchecked")
	private void isChangeValue() throws CloneNotSupportedException {
		if (tmpDelete.size() == 0) {
			unusualChanges = (ArrayList<Line>) tmpInsert.clone();
			return;
		}
		if (tmpInsert.size() == 0) {
			unusualChanges = (ArrayList<Line>) tmpDelete.clone();
			return;
		}
		int deleteNum = tmpDelete.size(), insertNum = tmpInsert.size();
		int indexDelete = 0, indexInsert = 0;
		while (indexDelete < deleteNum) {
			String result = match(tmpDelete.get(indexDelete), tmpInsert, indexInsert);
			int index = Integer.parseInt(result.split(regex)[0]);
			//int editDistance = Integer.parseInt(result.split(regex)[1]);
			//float similarity = Float.parseFloat(result.split(regex)[2]);

			// 词法分析，必须是只有常量部分的修改才算作一般性修改
			String[] deleteStrings = tmpDelete.get(indexDelete).getText().split(" ");
			String[] insertStrings = tmpInsert.get(indexInsert).getText().split(" ");
			if (deleteStrings.length != insertStrings.length) {
				unusualChanges.add((Line) tmpDelete.get(indexDelete).clone());
			} else {
				boolean isSimple = true;
				SYM deleteSYM, insertSYM;
				JavaAnalysis javaAnalysis = new JavaAnalysis();
				for (int i = 0; i < deleteStrings.length; i++) {
					deleteSYM = javaAnalysis.getSYM(deleteStrings[i]);
					insertSYM = javaAnalysis.getSYM(insertStrings[i]);
					if (deleteSYM != insertSYM) {
						unusualChanges.add((Line) tmpDelete.get(indexDelete).clone());
						isSimple = false;
						break;
					}
				}
				if (isSimple) {
					for (int i = indexInsert; i < index; i++) {
						unusualChanges.add((Line) tmpInsert.get(i).clone());
					}
					indexInsert = index;
					usualChanges.add((Line) tmpDelete.get(indexDelete).clone());
					usualChanges.add((Line) tmpInsert.get(indexInsert).clone());
					indexInsert++;
				} else {
					unusualChanges.add((Line) tmpDelete.get(indexDelete).clone());
				}
			}
			indexDelete++;
			if (indexInsert == insertNum)
				break;
		}
		if (indexDelete < deleteNum) {
			for (; indexDelete < deleteNum; indexDelete++)
				unusualChanges.add((Line) tmpDelete.get(indexDelete).clone());
		}
		if (indexInsert < insertNum) {
			for (; indexInsert < insertNum; indexInsert++)
				unusualChanges.add((Line) tmpInsert.get(indexInsert).clone());
		}
	}

	/**
	 * 
	 * @Title: match @author: jackiifilwhh @Description: TODO(返回 strings 中与 string
	 *         最像的元素的索引、编辑距离和相似度) @param: @param string @param: @param
	 *         strings @param: @return @return: String @throws
	 */
	private String match(Line line, ArrayList<Line> lines, int start) {
		String matchResult = "";
		int index = 0;
		int minDistance = Integer.MAX_VALUE;
		float maxSimilarity = Float.MIN_VALUE;
		for (int i = start; i < lines.size(); i++) {
			String tmp = lines.get(i).getText();
			String result = levenshtein(tmp, line.getText());
			int tmpDistance = Integer.parseInt(result.split(regex)[0]);
			float tmpSimilarity = Float.parseFloat(result.split(regex)[1]);
			if (minDistance > tmpDistance && (maxSimilarity - tmpSimilarity) < 0) {
				index = i;
				minDistance = tmpDistance;
				maxSimilarity = tmpSimilarity;
			}
		}
		matchResult = index + regex + minDistance + regex + maxSimilarity;
		return matchResult;
	}

	public String levenshtein(String str1, String str2) {
		String result = "";
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
		// System.out.println("字符串\"" + str1 + "\"与\"" + str2 + "\"的比较");
		// 取数组右下角的值，同样不同位置代表不同字符串的比较
		// System.out.println("差异步骤：" + dif[len1][len2]);
		// 计算相似度
		float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
		// System.out.println("相似度：" + similarity);
		result = dif[len1][len2] + regex + similarity;
		return result;
	}

	// 得到最小值
	private int min(int... is) {
		int min = Integer.MAX_VALUE;
		for (int i : is) {
			if (min > i) {
				min = i;
			}
		}
		return min;
	}

	public String getLineJSON(ArrayList<Line> lines) {
		return JSON.toJSONString(lines, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteDateUseDateFormat);
	}

	public String[] getSrc() {
		return src;
	}

	public void setSrc(String[] src) {
		this.src = src;
		srcLength = src.length;
	}

	public String[] getDst() {
		return dst;
	}

	public void setDst(String[] dst) {
		this.dst = dst;
		dstLength = dst.length;
	}

	public ArrayList<Line> getLines() {
		return lines;
	}

	public void setLines(ArrayList<Line> lines) {
		this.lines = lines;
	}

	public ArrayList<Line> getUsualChanges() {
		return usualChanges;
	}

	public void setUsualChanges(ArrayList<Line> usualChanges) {
		this.usualChanges = usualChanges;
	}

	public ArrayList<Line> getUnusualChanges() {
		return unusualChanges;
	}

	public void setUnusualChanges(ArrayList<Line> unusualChanges) {
		this.unusualChanges = unusualChanges;
	}

	public ArrayList<Line> getTmpDelete() {
		return tmpDelete;
	}

	public void setTmpDelete(ArrayList<Line> tmpDelete) {
		this.tmpDelete = tmpDelete;
	}

	public ArrayList<Line> getTmpInsert() {
		return tmpInsert;
	}

	public void setTmpInsert(ArrayList<Line> tmpInsert) {
		this.tmpInsert = tmpInsert;
	}

}
