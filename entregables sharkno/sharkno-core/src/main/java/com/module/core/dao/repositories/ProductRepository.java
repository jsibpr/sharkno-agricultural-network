package com.module.core.dao.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.ProductMapper;
import com.module.core.dao.mappers.StringMapper;
import com.module.core.models.product.Product;

import io.micrometer.core.instrument.util.StringUtils;

@Repository
public class ProductRepository {
	
	@Autowired
	private JdbcTemplate template;
	
	private static final String PRODUCTS_TABLE = "products PROD";
	private static final String SERVICES_PRODUCTS_TABLE = "services_products";
	private static final String PRODUCTS_FIELDS_INSERTS = "id, title, description, creationDate, lastUpdate, category, type, attachment, quantity, status, origin, url, image";
	private static final String PRODUCTS_FIELDS_UPDATE = "title=?, description=?, lastUpdate=?, category=?, type=?, attachment=?, quantity=?, status=?, url=?, image=?";
	private static final String PROFILES_TABLE = "profiles PROF";
	private static final String PAYMENTS_TABLE = "payments PAY";
	private static final String ENTITIES_TABLE = "entities ENT";
	private static final String PRODUCTS_GET_FIELDS =	"PAY.type, PAY.minAmount, PAY.maxAmount, PAY.currency, ENT.id, ENT.name, PROF.id, PROF.name, PROF.likeQty";
	private static final String UPDATE = "update ";
	private static final String LEFT_JOIN = " LEFT JOIN ";
	private static final String WHERE_ID = "where id = ?";

	public void insertProduct (Product product) {
		template.update("INSERT INTO products (" + PRODUCTS_FIELDS_INSERTS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", product.getId(), product.getTitle(), product.getDescription(),
				product.getCreationDate(), product.getLastUpdate(), product.getCategory().getId(), product.getType().toString(), product.getAttachment(), product.getQuantity(),
				product.getStatus().toString(), product.getOrigin().getId(), product.getUrl(), product.getImage());
	}
	
	public void deleteProduct (String id) {
		template.update("DELETE FROM " + PRODUCTS_TABLE + " WHERE id = ?", id);
	}
	
	public Product getProduct (String id) {
		try {
			return template.queryForObject("SELECT PROD.*, " + PRODUCTS_GET_FIELDS  + " FROM " + PRODUCTS_TABLE 
				+ LEFT_JOIN + PAYMENTS_TABLE + " ON PROD.id = PAY.id "
				+ LEFT_JOIN + PROFILES_TABLE + " ON PROD.origin = PROF.id "
				+ LEFT_JOIN + ENTITIES_TABLE + " ON PROD.category = ENT.id"
				+ " WHERE PROD.id = ?", new Object[] {id}, new ProductMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public void updateProduct (Product product) {
		template.update("UPDATE products SET " + PRODUCTS_FIELDS_UPDATE + " WHERE id = ?", product.getTitle(), product.getDescription(),
				product.getLastUpdate(), product.getCategory().getId(), product.getType().toString(), product.getAttachment(), product.getQuantity(),
				product.getStatus().toString(), product.getUrl(), product.getImage(), product.getId());
	}
	
	public void updateProductStatus (String productId, Product.Status status) {
		template.update("UPDATE " + PRODUCTS_TABLE + " SET status = ? WHERE id = ?", status.toString(), productId);
	}
	
	public List<Product> searchProducts(String originId, String titleFragment) {
		String likeParameter = "%" + titleFragment + "%";
		String statement = "SELECT * FROM " + PRODUCTS_TABLE + LEFT_JOIN + PAYMENTS_TABLE + " ON PAY.id = PROD.id "
				+ LEFT_JOIN + PROFILES_TABLE + " ON PROD.origin = PROF.id "
				+ LEFT_JOIN + ENTITIES_TABLE + " ON PROD.category = ENT.id"
				.concat(StringUtils.isNotBlank(originId) ? " WHERE origin = ? AND title LIKE ?" : " WHERE title LIKE ?");
		
		List<Object> parameterList = new ArrayList<>();
		if (StringUtils.isNotBlank(originId)) {
			parameterList.add(originId);
		}
		parameterList.add(likeParameter);
		return template.query(statement, parameterList.toArray(), new ProductMapper());
	}

	public List<String> getProductsfromService(String serviceId){
		return template.query( "SELECT productId AS content FROM " + SERVICES_PRODUCTS_TABLE +
		 " WHERE serviceId = ?", new Object[] { serviceId }, new StringMapper());
	}
	
	public void updateProductImage (String imageUrl, String productId) {
		template.update(UPDATE + "products SET image = ? " + WHERE_ID, imageUrl, productId);
	}
}
