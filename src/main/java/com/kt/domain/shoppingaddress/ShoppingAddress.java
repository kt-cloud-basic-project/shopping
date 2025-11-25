package com.kt.domain.shoppingaddress;

import com.kt.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ShoppingAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30)
	private String name;

	@Column(nullable = false, length = 256)
	private String address;

	@Column(nullable = false, length = 13)
	private String mobile;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 100)
	private ShoppingAddressType infoType;

	@Column(length = 256)
	private String infoDesc;

	private boolean isDefault;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public ShoppingAddress(
		User user, String name, String address, String mobile,
		ShoppingAddressType infoType, String infoDesc, boolean isDefault) {
		this.name = name;
		this.user = user;
		this.mobile = mobile;
		this.infoType = infoType;
		this.infoDesc = infoDesc;
		this.address = address;
		this.isDefault = isDefault;
	}

	public void update(User user, String name, String address, String mobile,
		ShoppingAddressType infoType, String infoDesc, boolean isDefault) {
		this.name = name;
		this.user = user;
		this.mobile = mobile;
		this.infoType = infoType;
		this.infoDesc = infoDesc;
		this.address = address;
		this.isDefault = isDefault;
	}

	public void setDefault() {
		this.isDefault = true;
	}

	public void unsetDefault() {
		this.isDefault = false;
	}
}
