package com.slerpio.teachme.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Date;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import com.slerpio.teachme.entity.TmNotification;

@Entity
@Table(name = "tm_user")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@XmlAccessorType(XmlAccessType.NONE)
public class TmUser {

	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tm_user_user_id_seq")
	@SequenceGenerator(name = "tm_user_user_id_seq", sequenceName = "tm_user_user_id_seq")
	private Long userId;
	@Column(name = "phone_number")
	@Basic(optional = false)
	@Size(min = 1, max = 20)
	private String phoneNumber;
	@Column(name = "username")
	@Basic(optional = false)
	@Size(min = 1, max = 60)
	private String username;
	@Column(name = "fullname")
	@Basic(optional = false)
	private String fullname;
	@Column(name = "gender")
	@Basic(optional = false)
	@Size(min = 1, max = 1)
	private String gender;
	@Column(name = "hash_password")
	@Basic(optional = false)
	private byte[] hashPassword;
	@Column(name = "enabled")
	@Basic(optional = false)
	private Boolean enabled;
	@Column(name = "account_non_expired")
	@Basic(optional = false)
	private Boolean accountNonExpired;
	@Column(name = "account_non_locked")
	@Basic(optional = false)
	private Boolean accountNonLocked;
	@Column(name = "credentials_non_expired")
	@Basic(optional = false)
	private Boolean credentialsNonExpired;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;
	@Column(name = "address_id")
	private Long addressId;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
	private List<TmNotification> tmNotificationList;

	@JsonProperty
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@JsonProperty
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@JsonProperty
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonProperty
	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@JsonProperty
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public byte[] getHashPassword() {
		return hashPassword;
	}

	public void setHashPassword(byte[] hashPassword) {
		this.hashPassword = hashPassword;
	}

	@JsonProperty
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@JsonProperty
	public Boolean getAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	@JsonProperty
	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@JsonProperty
	public Boolean getCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	@JsonProperty
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@JsonProperty
	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	@JsonProperty
	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public List<TmNotification> getTmNotificationList() {
		return tmNotificationList;
	}

	public void setTmNotificationList(List<TmNotification> tmNotificationList) {
		this.tmNotificationList = tmNotificationList;
	}

}