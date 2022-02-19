/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.image.search.setup;

import static com.sap.image.search.constants.SapimagesearchConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.sap.image.search.constants.SapimagesearchConstants;
import com.sap.image.search.service.SapimagesearchService;


@SystemSetup(extension = SapimagesearchConstants.EXTENSIONNAME)
public class SapimagesearchSystemSetup
{
	private final SapimagesearchService sapimagesearchService;

	public SapimagesearchSystemSetup(final SapimagesearchService sapimagesearchService)
	{
		this.sapimagesearchService = sapimagesearchService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapimagesearchService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapimagesearchSystemSetup.class.getResourceAsStream("/sapimagesearch/sap-hybris-platform.png");
	}
}
