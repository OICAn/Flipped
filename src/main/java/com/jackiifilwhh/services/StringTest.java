package com.jackiifilwhh.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringTest {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("E://test1.txt"));
			String tmp = null;
			List<String> str = new ArrayList<>();
			while ((tmp = br.readLine()) != null) {
				str.add(tmp);
			}
			String[] src, dst;
			src = (String[]) str.toArray(new String[str.size()]);

			tmp = null;
			str = null;
			br = null;
			br = new BufferedReader(new FileReader("E://test2.txt"));
			str = new ArrayList<>();
			while ((tmp = br.readLine()) != null) {
				str.add(tmp);
			}
			dst = (String[]) str.toArray(new String[str.size()]);

			br.close();

			Diff diff = new Diff(src, dst);
			diff.myers();
			diff.showDiff();
			System.out.println(diff.getLineJSON());

			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
