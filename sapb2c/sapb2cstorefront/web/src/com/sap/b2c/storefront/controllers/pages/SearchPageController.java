/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.b2c.storefront.controllers.pages;

import com.sap.image.search.gcp.GCPVisionAPISearch;
import com.sap.b2c.facades.product.data.ImageSearchData;
import de.hybris.platform.acceleratorcms.model.components.SearchBoxComponentModel;
import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteResultData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetRefinement;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/search")
public class SearchPageController extends AbstractSearchPageController
{
	private static final String SEARCH_META_DESCRIPTION_ON = "search.meta.description.on";
	private static final String SEARCH_META_DESCRIPTION_RESULTS = "search.meta.description.results";

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SearchPageController.class);

	private static final String COMPONENT_UID_PATH_VARIABLE_PATTERN = "{componentUid:.*}";
	private static final String FACET_SEPARATOR = ":";

	private static final String SEARCH_CMS_PAGE_ID = "search";
	private static final String NO_RESULTS_CMS_PAGE_ID = "searchEmpty";

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "searchBreadcrumbBuilder")
	private SearchBreadcrumbBuilder searchBreadcrumbBuilder;

	@Resource(name = "customerLocationService")
	private CustomerLocationService customerLocationService;

	@Resource(name = "cmsComponentService")
	private CMSComponentService cmsComponentService;



	@Resource
	private GCPVisionAPISearch gcpVisionAPISearch;

	@Resource
	private ConfigurationService configurationService;

	@RequestMapping(method = RequestMethod.GET, params = "!q")
	public String textSearch(@RequestParam(value = "text", defaultValue = "") final String searchText,
			final HttpServletRequest request, final Model model) throws CMSItemNotFoundException, IOException {
		final ContentPageModel noResultPage = getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID);
		if (StringUtils.isNotBlank(searchText))
		{
			List <String> imageLabelData = gcpVisionAPISearch.getImageLabelDataPath(configurationService.getConfiguration().getString("sap.image.search.path","/home/ccv2/CAT/1.jpeg"));

			final PageableData pageableData = createPageableData(0, getSearchPageSize(), null, ShowMode.Page);

			final SearchStateData searchState = new SearchStateData();
			final SearchQueryData searchQueryData = new SearchQueryData();

			searchQueryData.setValue(imageLabelData.get(0));
			searchState.setQuery(searchQueryData);
			searchQueryData.setSearchQueryContext(SearchQueryContext.IMAGE_SEARCH);


			ProductSearchPageData<SearchStateData, ProductData> searchPageData = null;

			try
			{
				searchPageData = encodeSearchPageData(productSearchFacade.textSearch(searchState, pageableData));
			}
			catch (final ConversionException e) // NOSONAR
			{
				// nothing to do - the exception is logged in SearchSolrQueryPopulator
			}

			if (searchPageData == null)
			{
				storeCmsPageInModel(model, noResultPage);
			}
			else if (searchPageData.getKeywordRedirectUrl() != null)
			{
				// if the search engine returns a redirect, just
				return "redirect:" + searchPageData.getKeywordRedirectUrl();
			}
			else if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
			{
				model.addAttribute("searchPageData", searchPageData);
				storeCmsPageInModel(model, noResultPage);
				updatePageTitle(searchText, model);
			}
			else
			{
				storeContinueUrl(request);
				populateModel(model, searchPageData, ShowMode.Page);
				storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
				updatePageTitle(searchText, model);
			}
			model.addAttribute("userLocation", customerLocationService.getUserLocation());
			getRequestContextData(request).setSearch(searchPageData);
			if (searchPageData != null)
			{
				model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null, searchText,
						CollectionUtils.isEmpty(searchPageData.getBreadcrumbs())));
			}
		}
		else
		{
			storeCmsPageInModel(model, noResultPage);
		}
		model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_FOLLOW);

		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(getMessageSource().getMessage(SEARCH_META_DESCRIPTION_RESULTS, null,
						SEARCH_META_DESCRIPTION_RESULTS, getI18nService().getCurrentLocale()) + " " + searchText + " "
						+ getMessageSource().getMessage(SEARCH_META_DESCRIPTION_ON, null, SEARCH_META_DESCRIPTION_ON,
								getI18nService().getCurrentLocale())
						+ " " + getSiteName());
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);

		return getViewForPage(model);
	}

	@RequestMapping(method = RequestMethod.GET, params = "q")
	public String refineSearch(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@RequestParam(value = "text", required = false) final String searchText, final HttpServletRequest request,
			final Model model) throws CMSItemNotFoundException
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode,
				sortCode, getSearchPageSize());

		populateModel(model, searchPageData, showMode);
		model.addAttribute("userLocation", customerLocationService.getUserLocation());

		if (searchPageData.getPagination().getTotalNumberOfResults() == 0)
		{
			updatePageTitle(searchPageData.getFreeTextSearch(), model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID));
		}
		else
		{
			storeContinueUrl(request);
			updatePageTitle(searchPageData.getFreeTextSearch(), model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
		}
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null, searchPageData));
		model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());

		final String metaDescription = MetaSanitizerUtil
				.sanitizeDescription(getMessageSource().getMessage(SEARCH_META_DESCRIPTION_RESULTS, null,
						SEARCH_META_DESCRIPTION_RESULTS, getI18nService().getCurrentLocale()) + " " + searchText + " "
						+ getMessageSource().getMessage(SEARCH_META_DESCRIPTION_ON, null, SEARCH_META_DESCRIPTION_ON,
								getI18nService().getCurrentLocale())
						+ " " + getSiteName());

		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText);
		setUpMetaData(model, metaKeywords, metaDescription);

		return getViewForPage(model);
	}

	protected ProductSearchPageData<SearchStateData, ProductData> performSearch(final String searchQuery, final int page,
			final ShowMode showMode, final String sortCode, final int pageSize)
	{
		final PageableData pageableData = createPageableData(page, pageSize, sortCode, showMode);

		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		return encodeSearchPageData(productSearchFacade.textSearch(searchState, pageableData));
	}

	@ResponseBody
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public SearchResultsData<ProductData> jsonSearchResults(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = performSearch(searchQuery, page, showMode,
				sortCode, getSearchPageSize());
		final SearchResultsData<ProductData> searchResultsData = new SearchResultsData<>();
		searchResultsData.setResults(searchPageData.getResults());
		searchResultsData.setPagination(searchPageData.getPagination());
		return searchResultsData;
	}

	@ResponseBody
	@RequestMapping(value = "/facets", method = RequestMethod.GET)
	public FacetRefinement<SearchStateData> getFacets(@RequestParam("q") final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode) throws CMSItemNotFoundException
	{
		final SearchStateData searchState = new SearchStateData();
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);
		searchState.setQuery(searchQueryData);

		final ProductSearchPageData<SearchStateData, ProductData> searchPageData = productSearchFacade.textSearch(searchState,
				createPageableData(page, getSearchPageSize(), sortCode, showMode));
		final List<FacetData<SearchStateData>> facets = refineFacets(searchPageData.getFacets(),
				convertBreadcrumbsToFacets(searchPageData.getBreadcrumbs()));
		final FacetRefinement<SearchStateData> refinement = new FacetRefinement<>();
		refinement.setFacets(facets);
		refinement.setCount(searchPageData.getPagination().getTotalNumberOfResults());
		refinement.setBreadcrumbs(searchPageData.getBreadcrumbs());
		return refinement;
	}

	@ResponseBody
	@RequestMapping(value = "/autocomplete/" + COMPONENT_UID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public AutocompleteResultData getAutocompleteSuggestions(@PathVariable final String componentUid,
			@RequestParam("term") final String term) throws CMSItemNotFoundException
	{
		final AutocompleteResultData resultData = new AutocompleteResultData();

		final SearchBoxComponentModel component = (SearchBoxComponentModel) cmsComponentService.getSimpleCMSComponent(componentUid);

		if (component.isDisplaySuggestions())
		{
			resultData.setSuggestions(subList(productSearchFacade.getAutocompleteSuggestions(term), component.getMaxSuggestions()));
		}

		if (component.isDisplayProducts())
		{
			resultData.setProducts(subList(productSearchFacade.textSearch(term, SearchQueryContext.SUGGESTIONS).getResults(),
					component.getMaxProducts()));
		}

		return resultData;
	}

	protected <E> List<E> subList(final List<E> list, final int maxElements)
	{
		if (CollectionUtils.isEmpty(list))
		{
			return Collections.emptyList();
		}

		if (list.size() > maxElements)
		{
			return list.subList(0, maxElements);
		}

		return list;
	}

	protected void updatePageTitle(final String searchText, final Model model)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(
				getMessageSource().getMessage("search.meta.title", null, "search.meta.title", getI18nService().getCurrentLocale())
						+ " " + searchText));
	}

	@RequestMapping(value = "/siteImageSearchGCP",method = RequestMethod.POST)
	public String siteImageSearch(@RequestBody final ImageSearchData imageSearchData, HttpServletRequest request,final Model model) throws CMSItemNotFoundException, IOException {


		InputStream imageStream= imageSearchData.getFile().getInputStream();

		final ContentPageModel noResultPage = getContentPageForLabelOrId(NO_RESULTS_CMS_PAGE_ID);


		List <String> imageLabelData = gcpVisionAPISearch.getImageLabelData(imageStream.readAllBytes());
		model.addAttribute("searchTerms",imageLabelData);

		AtomicReference <String> searchText= new AtomicReference <>("");
		AtomicReference <ProductSearchPageData <SearchStateData, ProductData>> searchPageData = new AtomicReference <>();
		AtomicInteger atomicInteger= new AtomicInteger(1);

		CollectionUtils.emptyIfNull(imageLabelData).stream().forEach(text -> {

			final PageableData pageableData = createPageableData(0,getSearchPageSize(),null,ShowMode.Page);
			final SearchStateData searchState = new SearchStateData();
			final SearchQueryData searchQueryData = new SearchQueryData();
			searchQueryData.setValue(text);
			searchState.setQuery(searchQueryData);
			searchQueryData.setSearchQueryContext(SearchQueryContext.IMAGE_SEARCH);

			if (1 == atomicInteger.get()) {
				searchText.set(text);
				searchPageData.set(encodeSearchPageData(productSearchFacade.textSearch(searchState,pageableData)));
			}


			if (1 < atomicInteger.get() && null != searchPageData.get() && searchPageData.get().getPagination().getTotalNumberOfResults() == 0)
			{
				searchText.set(text);
				searchPageData.set(encodeSearchPageData(productSearchFacade.textSearch(searchState,pageableData)));
			}
			atomicInteger.getAndIncrement();

		});
			try {

				if (searchPageData.get() == null)
				{
					storeCmsPageInModel(model, noResultPage);
				}
				else if (searchPageData.get().getKeywordRedirectUrl() != null)
				{
					// if the search engine returns a redirect, just
					return "redirect:" + searchPageData.get().getKeywordRedirectUrl();
				}
				else if (searchPageData.get().getPagination().getTotalNumberOfResults() == 0)
				{
					model.addAttribute("searchPageData", searchPageData);
					storeCmsPageInModel(model, noResultPage);
					updatePageTitle(searchText.get(), model);
				}
				else
				{
					storeContinueUrl(request);
					populateModel(model,searchPageData.get(), ShowMode.Page);
					storeCmsPageInModel(model, getContentPageForLabelOrId(SEARCH_CMS_PAGE_ID));
					updatePageTitle(searchText.get(), model);
				}
				model.addAttribute("userLocation", customerLocationService.getUserLocation());
				getRequestContextData(request).setSearch(searchPageData.get());
				if (searchPageData.get() != null)
				{
					model.addAttribute(WebConstants.BREADCRUMBS_KEY, searchBreadcrumbBuilder.getBreadcrumbs(null,searchText.get(),
							CollectionUtils.isEmpty(searchPageData.get().getBreadcrumbs())));
				}

		else
			{
				storeCmsPageInModel(model, noResultPage);
			}
			model.addAttribute("pageType", PageType.PRODUCTSEARCH.name());
			model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_FOLLOW);

			final String metaDescription = MetaSanitizerUtil
					.sanitizeDescription(getMessageSource().getMessage(SEARCH_META_DESCRIPTION_RESULTS, null,
							SEARCH_META_DESCRIPTION_RESULTS, getI18nService().getCurrentLocale()) + " " + searchText + " "
							+ getMessageSource().getMessage(SEARCH_META_DESCRIPTION_ON, null, SEARCH_META_DESCRIPTION_ON,
							getI18nService().getCurrentLocale())
							+ " " + getSiteName());
			final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(searchText.get());
			setUpMetaData(model, metaKeywords, metaDescription);




			}catch (Exception e)
			{
				LOG.error("Error while searching with image.."+e);

			}

		return getViewForPage(model);
	}

	@ResponseBody
	@RequestMapping(value ="/textSearch", method = RequestMethod.GET)
	public ResponseEntity textSearchByTerm(@RequestParam String searchText)
	{

		try {
			final PageableData pageableData = createPageableData(0,getSearchPageSize(),null,ShowMode.Page);

			final SearchStateData searchState = new SearchStateData();
			final SearchQueryData searchQueryData = new SearchQueryData();

			searchQueryData.setValue(searchText);
			searchState.setQuery(searchQueryData);
			searchQueryData.setSearchQueryContext(SearchQueryContext.IMAGE_SEARCH);

			ProductSearchPageData <SearchStateData, ProductData> searchStateDataProductDataProductSearchPageData = encodeSearchPageData(productSearchFacade.textSearch(searchState,pageableData));

			return new ResponseEntity(searchStateDataProductDataProductSearchPageData,HttpStatus.OK);
		}catch(Exception e)
		{
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@ResponseBody
	@RequestMapping(value = "/siteImageSearch",method = RequestMethod.POST)
	public String siteImageSearchALL(@RequestBody final MultipartFile file, HttpServletRequest request,final Model model) throws CMSItemNotFoundException, IOException {

		InputStream imageStream= file.getInputStream();
		List <String> imageLabelData = gcpVisionAPISearch.getImageLabelData(imageStream.readAllBytes());
		model.addAttribute("gcpSearchTerms",imageLabelData);
		model.addAttribute("azureSearchTerms",Collections.emptyList());
		model.addAttribute("awsSearchTerms",Collections.emptyList());

		final ContentPageModel cmPage = getContentPageForLabelOrId("ImageSearch");
		storeCmsPageInModel(model, cmPage);
		setUpMetaDataForContentPage(model, cmPage);
		final List<Breadcrumb> breadcrumbs = new ArrayList <>();
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	}
