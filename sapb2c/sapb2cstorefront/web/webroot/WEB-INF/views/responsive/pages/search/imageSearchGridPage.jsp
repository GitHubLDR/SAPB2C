<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>


<template:page pageTitle="${pageTitle}">

	<div class="row">
		<div>
        			<cms:pageSlot position="ImageSearchResultsGridSlot" var="feature" element="div">
        				<cms:component component="${feature}" element="div"/>
        			</cms:pageSlot>
        </div>
	</div>

	<storepickup:pickupStorePopup />

</template:page>