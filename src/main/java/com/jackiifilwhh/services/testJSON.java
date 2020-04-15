package com.jackiifilwhh.services;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class testJSON {

	public static void main(String[] args) {
		List<Line> lines = new ArrayList<>();
		
		String pretty = JSON.toJSONString(lines, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, 
	            SerializerFeature.WriteDateUseDateFormat);
		System.out.println(pretty);
	}

}
