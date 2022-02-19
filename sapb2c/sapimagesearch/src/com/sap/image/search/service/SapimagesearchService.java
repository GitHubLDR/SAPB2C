/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.image.search.service;

public interface SapimagesearchService
{
	String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);
}
