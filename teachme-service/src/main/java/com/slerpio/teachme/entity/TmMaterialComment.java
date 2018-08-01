package com.slerpio.teachme.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Basic;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "tm_material_comment")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@XmlAccessorType(XmlAccessType.NONE)
public class TmMaterialComment {

	@Id
	@Column(name = "material_comment_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TM_MATERIAL_COMMENT_MATERIAL_COMMENT_ID_SEQ")
	@SequenceGenerator(name = "TM_MATERIAL_COMMENT_MATERIAL_COMMENT_ID_SEQ", sequenceName = "tm_material_comment_material_comment_id_seq", initialValue = 1, allocationSize = 1)
	private Long id;
	@Column(name = "material_id")
	@Basic(optional = false)
	private Long materialId;

	@Column(name = "message")
	@Basic(optional = false)
	private String message;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;

	@Column(name = "sender_user_id", insertable = false, updatable = false)
	@Basic(optional = false)
	private Long senderUserId;

	@ManyToOne
	@JoinColumn(name = "sender_user_id", referencedColumnName = "user_id")
	private TmUser user;

	@JsonProperty
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty
	public Long getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}

	@JsonProperty
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
	public TmUser getUser() {
		return user;
	}

	public void setUser(TmUser user) {
		this.user = user;
	}


	@JsonProperty
	public Long getSenderUserId() {
		return senderUserId;
	}

	public void setSenderUserId(Long senderUserId) {
		this.senderUserId = senderUserId;
	}
}