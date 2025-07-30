package com.module.core.models.service;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class Payment {
	
	public enum Type {
		BY_HOURS,
		FIXED_PRICE
	}
	
	private Type type;
	private BigDecimal paymentAmountMin;
	private BigDecimal paymentAmountMax;
	@ApiModelProperty(example = "EUR")
	private String currency;
	
	public Payment() {
	}

	public Payment(Type type, BigDecimal paymentAmountMin, BigDecimal paymentAmountMax, String currency) {
		this.type = type;
		this.paymentAmountMin = paymentAmountMin;
		this.paymentAmountMax = paymentAmountMax;
		this.currency = currency;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public BigDecimal getPaymentAmountMin() {
		return paymentAmountMin;
	}

	public void setPaymentAmountMin(BigDecimal paymentAmountMin) {
		this.paymentAmountMin = paymentAmountMin;
	}

	public BigDecimal getPaymentAmountMax() {
		return paymentAmountMax;
	}

	public void setPaymentAmountMax(BigDecimal paymentAmountMax) {
		this.paymentAmountMax = paymentAmountMax;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	

}
