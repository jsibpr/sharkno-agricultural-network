package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Entity;
import com.module.core.models.product.Product;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.Payment;

public class ProductMapper implements RowMapper<Product>{

	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs!= null) {
			Payment payment = new Payment(Payment.Type.valueOf(rs.getString("PAY.type")), rs.getBigDecimal("PAY.minAmount"), rs.getBigDecimal("PAY.maxAmount"), rs.getString("PAY.currency"));
			Entity category = new Entity(rs.getString("ENT.id"), rs.getString("ENT.name"));
			LiteProfile origin = new LiteProfile(rs.getString("PROF.id"), rs.getString("PROF.name"), null, null, null, null, null, null, rs.getInt("PROF.likeQty"));
			return new Product(rs.getString("PROD.id"), rs.getString("PROD.title"), rs.getString("PROD.description"), rs.getDate("PROD.creationDate"), rs.getDate("PROD.lastUpdate"), category,
					Product.Type.valueOf(rs.getString("PROD.type")), rs.getString("PROD.attachment"), rs.getInt("PROD.quantity"), Product.Status.valueOf(rs.getString("PROD.status")), origin,
							payment, rs.getString("PROD.url"), rs.getString("PROD.image"), null);
		} else {
			return null;
		}
	}

}
