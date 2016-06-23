package com.gmart.productcatalogue.resource;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.gmart.productcatalogue.dao.CatalogueDao;
import com.gmart.productcatalogue.model.Product;

@Named
@Singleton
@Path("/productdetails")
public class CatalogueResource {

	private CatalogueDao catalogueDao;

	@Inject
	public CatalogueResource(CatalogueDao catalogueDao) {
		this.catalogueDao = catalogueDao;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getProductDetail(
			@QueryParam("productType") String productType) {
		List<Product> products = catalogueDao.getProducts(productType);
		return Response.ok(products).build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	public Response addProduct(Product product) {

		catalogueDao.addProduct(product);
		//URI for created product
		UriBuilder builder = UriBuilder.fromPath("productdetails/{id}");
		builder.scheme("http").host("globomart.com");
		UriBuilder clone = builder.clone();
		URI uri = clone.build(product.getProductId());

		return Response.created(uri).build();
	}

	@DELETE
	@Path("/{productId}")
	public Response removeProduct(@PathParam("productId") String productId) {
		catalogueDao.deleteProduct(productId);

		return Response.noContent().build();
	}
}
