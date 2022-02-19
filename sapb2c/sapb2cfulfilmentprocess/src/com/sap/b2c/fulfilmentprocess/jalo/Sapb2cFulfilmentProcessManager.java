/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.b2c.fulfilmentprocess.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.sap.b2c.fulfilmentprocess.constants.Sapb2cFulfilmentProcessConstants;

public class Sapb2cFulfilmentProcessManager extends GeneratedSapb2cFulfilmentProcessManager
{
	public static final Sapb2cFulfilmentProcessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Sapb2cFulfilmentProcessManager) em.getExtension(Sapb2cFulfilmentProcessConstants.EXTENSIONNAME);
	}
	
}
