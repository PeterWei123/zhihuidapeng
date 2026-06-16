package com.qkd.iotserver.pojo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

public class Record extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	public static final int OK = 1;
	public static final int FAIL = 0;
	public static final String MSG = "msg";
	public static final String CODE = "code";


	public static boolean isOK(Record record) {
		try {
			return "1".equals(record.get(CODE)) || record.get(CODE).equals(1);
		} catch (Exception e) {
			return false;
		}
	}

	public String getMsg() {
		return (String) get(MSG);
	}

	public Record setMsg(String msg) {
		put(MSG, msg);
		return this;
	}

	public Record setCode(int code) {
		put(CODE, code);
		return this;
	}

	public Record okMsg(String msg) {
		return setCode(OK).setMsg(msg);
	}

	public Record failMsg(String msg) {
		return setCode(FAIL).setMsg(msg);
	}

	public static Record ok() {
		return new Record().okMsg("操作成功");
	}

	public static Record ok(String msg) {
		return new Record().okMsg(msg);
	}

	public static Record fail() {
		return new Record().failMsg("操作失败");
	}

	public static Record fail(String msg) {
		return new Record().failMsg(msg);
	}


	@Data
	public static class Bean {
		public String label;
		public Object value;
		public Bean(String label, Object value) {
			this.label = label;
			this.value = value;
		}
		public Bean(String option) {
			this.label = option;
			this.value = option;
		}
	}

	public Record opt(String... options) {
		List<Bean> list = new ArrayList<>();
		for (String opt : options) {
			list.add(new Bean(opt));
		}
		list.remove(0);
		this.put(options[0], list);
		return this;
	}

	public Record opt(String key, List<String> options) {
		List<Bean> list = new ArrayList<>();
		for (String opt : options) {
			list.add(new Bean(opt));
		}
		this.put(key, list);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Record push(String key, String label, Object value) {
		List<Bean> list = (List<Bean>) this.get(key);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(new Bean(label, value));
		this.put(key, list);
		return this;
	}

	public Record add(String key, String label, Object... values) {
		if (values != null &&
			values.length == 1 &&
			values[0] instanceof Integer) {
			Object value = values[0];
			return this.push(key, label, value);
		} else {
			int length = values != null ? values.length + 2 : 2;
			String[] options = new String[length];
			options[0] = key;
			options[1] = label;
			if (values != null) {
				int index = 2;
				for (Object value : values) {
					String opt = value instanceof String ? (String) value : String.valueOf(value);
					options[index] = opt;
					index++;
				}
			}
			return this.opt(options);
		}
	}


}
