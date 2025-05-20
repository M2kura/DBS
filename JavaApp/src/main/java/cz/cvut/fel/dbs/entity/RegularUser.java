package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "\"Regular_User\"")
public class RegularUser extends User {

	@Column(name = "\"Subscription_Type\"", nullable = false)
	private String subscriptionType;

	@Column(name = "\"Payment_Method\"", nullable = false)
	private String paymentMethod;

	@Column(name = "\"Renewal_Date\"", nullable = false)
	private String renewalDate;

	public RegularUser() {
	}

	public RegularUser(String username, String password, String firstName, String lastName,
		String email, Date registrationDate, String subscriptionType,
		String paymentMethod, String renewalDate) {
		super(username, password, firstName, lastName, email, registrationDate);
		this.subscriptionType = subscriptionType;
		this.paymentMethod = paymentMethod;
		this.renewalDate = renewalDate;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(String renewalDate) {
		this.renewalDate = renewalDate;
	}

	@Override
	public String toString() {
		return "RegularUser{" +
		"username='" + getUsername() + '\'' +
		", subscriptionType='" + subscriptionType + '\'' +
		", paymentMethod='" + paymentMethod + '\'' +
		", renewalDate='" + renewalDate + '\'' +
		'}';
	}
}
