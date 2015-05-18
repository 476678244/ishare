package com.ishare.util;

import java.util.HashMap;
import java.util.Map;

import com.ishare.bean.enums.GenderEnum;
import com.ishare.bean.enums.JobEnum;

public class HeadPicUtil {

	public static Map<String, String> headPicMap = new HashMap<String, String>();

	static {
		headPicMap.put(GenderEnum.MALE.getValue() + JobEnum.STUDENT.getValue(),
				"pages/pictures/male_student.jpg");
		headPicMap.put(
				GenderEnum.MALE.getValue() + JobEnum.EMPLOYEE.getValue(),
				"pages/pictures/male_employee.jpg");
		headPicMap.put(
				GenderEnum.FEMALE.getValue() + JobEnum.STUDENT.getValue(),
				"pages/pictures/female_student.jpg");
		headPicMap.put(
				GenderEnum.FEMALE.getValue() + JobEnum.EMPLOYEE.getValue(),
				"pages/pictures/female_employee.jpg");
	}

	public static String getHeadPicUrl(String job, String gender) {
		return HeadPicUtil.headPicMap.get(gender + job);
	}
}
