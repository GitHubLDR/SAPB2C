/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.image.search.controller;

import static com.sap.image.search.constants.SapimagesearchConstants.PLATFORM_LOGO_CODE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sap.image.search.service.SapimagesearchService;


@Controller
public class SapimagesearchHelloController
{
	@Autowired
	private SapimagesearchService sapimagesearchService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", sapimagesearchService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}
