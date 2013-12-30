package com.gwssi.test;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class TestTech {
	public static void main(String[] args) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < 10; i++) {
			map.put(i + "", "第" + i + "条");
			list.add("第" + i + "条");
		}
//		System.out.println(list.indexOf(map.get("3")));
//		list.remove(map.get("3"));
		int size = list.size();
		for (int i = 0; i < size-(size-4+1); i++) {
			System.out.println(list.get(i));
		}
		for (int i = list.size(); i == 4; --i) {
			System.out.println(list.get(i));
		}
//		System.out.println(list);
//		System.out.println(map);
	}
}
