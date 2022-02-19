/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.b2c.core.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.sap.b2c.core.constants.Sapb2cCoreConstants;
import com.sap.b2c.core.setup.CoreSystemSetup;


/**
 * Do not use, please use {@link CoreSystemSetup} instead.
 * 
 */
public class Sapb2cCoreManager extends GeneratedSapb2cCoreManager
{
	public static final Sapb2cCoreManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Sapb2cCoreManager) em.getExtension(Sapb2cCoreConstants.EXTENSIONNAME);
	}
}
