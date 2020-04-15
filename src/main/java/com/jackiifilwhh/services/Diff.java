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
	}

	protected void myers() {
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

	protected void showDiff() {
		int srcIndex = 0, dstIndex = 0;
		for (int i = 0; i < result.size(); i++) {
			Snake currPoint = result.get(i);
			int startK = currPoint.getxStart() - currPoint.getyStart();
			int endK = currPoint.getxEnd() - currPoint.getyEnd();
			int startX = currPoint.getxStart();
			if ((endK - startK) != 0) {
				if ((endK - startK) > 0) {
					for (int j = 0; j < (endK - startK); j++) {
						lines.add(new Line(src[srcIndex], 2, srcIndex + 1, null));
						// System.out.println("- " + src[srcIndex++]);
						srcIndex++;
						startX++;
					}
				} else {
					for (int j = 0; j < Math.abs(endK - startK); j++) {
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
	}

	protected String getLineJSON() {
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

}
