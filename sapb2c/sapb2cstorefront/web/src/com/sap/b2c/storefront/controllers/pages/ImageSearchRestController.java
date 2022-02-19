/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.b2c.storefront.controllers.pages;

import com.sap.image.search.gcp.GCPVisionAPISearch;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@RestController
@RequestMapping("/search")
public class ImageSearchRestController extends AbstractSearchPageController
{

	private static final Logger LOG = Logger.getLogger(ImageSearchRestController.class);
	@Resource
	private GCPVisionAPISearch gcpVisionAPISearch;

	@Resource
	private ConfigurationService configurationService;

	@ResponseBody
	@RequestMapping(value = "/siteImageSearch",method = RequestMethod.POST,consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public Map <String,List<String>> siteImageSearchALL(@RequestPart final MultipartFile file)  {

		Map <String,List<String>> stringListMap = new HashMap <>();
		try{
			InputStream imageStream= file.getInputStream();
			List <String> imageLabelData = gcpVisionAPISearch.getImageLabelData(imageStream.readAllBytes());
			stringListMap.put("gcpSearchTerms",imageLabelData);
			stringListMap.put("awsSearchTerms",Collections.emptyList());
		}
		catch (Exception e)
		{
			LOG.error("Error while Fetching the Data...");
		}
		return stringListMap;
	}

}
