package com.anosi.asset.test;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.anosi.asset.PdmApplication;
import com.anosi.asset.i18n.I18nComponent;
import com.anosi.asset.model.jpa.DevCategory;
import com.anosi.asset.model.jpa.DevCategoryStructures;
import com.anosi.asset.service.DevCategoryStructuresService;
import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PdmApplication.class)
@Transactional
public class TestStructures {

	@Autowired
	private DevCategoryStructuresService devCategoryStructuresService;
	@Autowired
	protected I18nComponent i18nComponent;

	@Test
	public void testCheckStructures(){
		List<String> subDevCategorys = Lists.newArrayList("abc","abc","abc","efg","efg","cde");

		DevCategoryStructures devCategoryStructures1 = new DevCategoryStructures();
		DevCategoryStructures devCategoryStructures2 = new DevCategoryStructures();
		DevCategoryStructures devCategoryStructures3 = new DevCategoryStructures();
		DevCategory devCategory1 = new DevCategory();
		DevCategory devCategory2 = new DevCategory();
		DevCategory devCategory3 = new DevCategory();

		devCategoryStructures1.setSubDevCategory(devCategory1);
		devCategoryStructures1.setAmount(2);
		devCategoryStructures2.setSubDevCategory(devCategory2);
		devCategoryStructures2.setAmount(1);
		devCategoryStructures3.setSubDevCategory(devCategory3);
		devCategoryStructures3.setAmount(1);

		List<DevCategoryStructures> devCategoryStructures=Lists.newArrayList(devCategoryStructures1,devCategoryStructures2,devCategoryStructures3);
		Map<String, Integer> result = devCategoryStructuresService.checkStructures(devCategoryStructures, subDevCategorys);
		System.out.println(result);
		System.out.println(MessageFormat.format(i18nComponent.getMessage("checkStructures.template"),
					"test", result.toString()));
	}

}
