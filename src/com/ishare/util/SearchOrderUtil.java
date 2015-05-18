package com.ishare.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ishare.bean.PoolOrderBean;
import com.ishare.bean.enums.GenderEnum;

public class SearchOrderUtil {

	public static final String GENDER_CLAUSE_ONLY_FEMALE = " and subject.gender = '"
			+ GenderEnum.FEMALE.getValue() + "' ";
	public static final String GENDER_CLAUSE_NONE_ONLY_FEMALE = " and subject.gender != '"
			+ GenderEnum.FEMALE.getValue() + "' ";

	public static final int GENDER_CARE_ONLY_FEMALE = 1;

	public static final int GENDER_CARE_NONE_ONLY_FEMALE = 2;

	public static void SortOrdersByStartTimeDesc(List<PoolOrderBean> orders) {
		Collections.sort(orders, new Comparator<PoolOrderBean>() {

			@Override
			public int compare(PoolOrderBean o1, PoolOrderBean o2) {
				if (o1.getStartTime().after(o2.getStartTime())) {
					return 1;
				}
				return 0;
			}

		});
	}
}
