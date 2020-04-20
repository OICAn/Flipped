package com.jackiifilwhh.services;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

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

	public void showDiff() {
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
	private void isChangeValue() {
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
	}
	
	private String match() {
		String matchResult = "";
		return matchResult;
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
