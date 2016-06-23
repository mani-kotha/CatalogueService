package com.gmart.productcatalogue.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.gmart.productcatalogue.model.Product;

@Named
@Singleton
public class CatalogueDao {
	private EmbeddedDatabase dataSource;
	private final JdbcTemplate template;

	public CatalogueDao() {
		dataSource = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript(
						"classpath:com/gmart/productcatalogue/db-scripts/schema.sql")
				.addScript(
						"classpath:com/gmart/productcatalogue/db-scripts/productcatalogue_details.sql")
				.build();
		template = new JdbcTemplate(dataSource);
	}

	public void addProduct(Product product) {
		String insertQuery = "insert into PRODUCT_DETAILS (PRODUCT_ID, PRODUCT_TYPE, PRODUCT_NAME, PRICE, CREATED_DATE_TIME, MODIFIED_DATE_TIME)"
				+ "" + " values (?,?,?,?,sysdate,sysdate)";
		template.update(insertQuery, product.getProductId(),
				product.getProductType(), product.getProductName(),
				product.getPrice());
	}

	public void deleteProduct(String productId) {
		String deleteQuery = "DELETE FROM PRODUCT_DETAILS WHERE PRODUCT_ID = ?";
		System.out.println("productId" + productId);
		template.update(deleteQuery, productId);
	}

	public List<Product> getProducts(String productType) {
		String selectQuery = "SELECT PRODUCT_ID, PRODUCT_TYPE, PRODUCT_NAME, PRICE FROM PRODUCT_DETAILS";
		if(productType!= null)
			selectQuery+=" WHERE PRODUCT_TYPE = '"+productType+"'";
		
		List<Product> products = new ArrayList<Product>();

		products = template.query(selectQuery, new ProductRowMapper());

		return products;
	}

	public static class ProductRowMapper implements RowMapper<Product> {
		public Product mapRow(ResultSet rs, int row) throws SQLException {
			Product product = new Product();
			product.setProductId(rs.getString(1));
			product.setProductType(rs.getString(2));
			product.setProductName(rs.getString(3));
			product.setPrice(rs.getInt(4));
			return product;
		}
	}
}
