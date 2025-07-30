package com.module.core.dao.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.models.service.Payment;

@Repository
public class PaymentRepository {
	
	@Autowired
	private JdbcTemplate template;
	
	private static final String PAYMENTS_TABLE = "payments";
	private static final String PAYMENT_FIELDS = "id, type, minAmount, maxAmount, currency";
	private static final String PAYMENT_UPDATE_FIELDS = "type = ?, minAmount = ?, maxAmount = ?, currency = ?";

	public void createPayment (String id, Payment payment) {
		template.update("insert into " + PAYMENTS_TABLE + "(" + PAYMENT_FIELDS + ") values (?, ?, ?, ?, ?)", id, payment.getType().toString(), payment.getPaymentAmountMin(), payment.getPaymentAmountMax(), payment.getCurrency());		
	}
	
	public void updatePayment (String id, Payment payment) {
		template.update("update " + PAYMENTS_TABLE + " set " + PAYMENT_UPDATE_FIELDS + "where id = ?", payment.getType().toString(), payment.getPaymentAmountMin(), payment.getPaymentAmountMax(), payment.getCurrency(), id);		
	}
	
	public void deletePayment (String id) {
		template.update("delete from  " + PAYMENTS_TABLE + " where id = ? ", id);
	}
		
}
