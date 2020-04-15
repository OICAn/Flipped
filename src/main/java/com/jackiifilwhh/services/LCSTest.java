package com.jackiifilwhh.services;

import java.util.ArrayList;
import java.util.List;

public class LCSTest {

	public static void main(String[] args) {
		String a = "ABCABBA";
		String b = "CBABAC";
		char[] aa = a.toCharArray();
		char[] bb = b.toCharArray();
		int max = aa.length + bb.length;
		int[] v = new int[max * 2];
		List<Snake> snakes = new ArrayList<>();

		for (int d = 0; d <= aa.length + bb.length; d++) {
			System.out.println("D:" + d);
			for (int k = -d; k <= d; k += 2) {
				System.out.print("k:" + k);
				//存在的问题是可能会产生超过xy边界的值
				// down or right?
				boolean down = (k == -d || (k != d && v[k - 1 + max] < v[k + 1 + max]));
				int kPrev = down ? k + 1 : k - 1;

				// start point
				int xStart = v[kPrev + max];
				int yStart = xStart - kPrev;

				// mid point
				int xMid = down ? xStart : xStart + 1;
				int yMid = xMid - k;

				// end point
				int xEnd = xMid;
				int yEnd = yMid;

				while (xEnd < aa.length && yEnd < bb.length && aa[xEnd] == bb[yEnd]) {
					xEnd++;
					yEnd++;
				}
				// save end point
				v[k + max] = xEnd;
				// record a snake
				snakes.add(0, new Snake(xStart, yStart, xEnd, yEnd));
				System.out.print(", start:(" + xStart + "," + yStart + "), mid:(" + xMid + "," + yMid + "), end:("
						+ xEnd + "," + yEnd + ")\n");
				// check for solution
				if (xEnd >= aa.length && yEnd >= bb.length) {
					/* solution has been found */
					System.out.println("found");
					System.out.println(snakes.size());
					/* print the snakes */
					Snake current = snakes.get(0);
					System.out.println(String.format("(%2d, %2d)<-(%2d, %2d)", current.getxEnd(), current.getyEnd(),
							current.getxStart(), current.getyStart()));
					for (int i = 1; i < snakes.size(); i++) {
						Snake tmp = snakes.get(i);
						if (tmp.getxEnd() == current.getxStart() && tmp.getyEnd() == current.getyStart()) {
							current = tmp;
							System.out.println(String.format("(%2d, %2d)<-(%2d, %2d)", current.getxEnd(),
									current.getyEnd(), current.getxStart(), current.getyStart()));
							if (current.getxStart() == 0 && current.getyStart() == 0) {
								break;
							}
						}
					}
					return;
				}
			}
		}

	}

}
