package com.slerpio.teachme.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Basic;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "tm_notification")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@XmlAccessorType(XmlAccessType.NONE)
public class TmNotification {

	@Id
	@Column(name = "notification_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TM_NOTIFICATION_NOTIFICATION_ID_SEQ")
	@SequenceGenerator(name = "TM_NOTIFICATION_NOTIFICATION_ID_SEQ", sequenceName = "tm_notification_notification_id_seq", allocationSize = 1)
	private Long id;
	@Column(name = "message")
	@Basic(optional = false)
	@NotNull(message = "com.slerpio.teachme.entity.TmNotification.message")
	@Size(min = 1, max = 60)
	private String message;
	@Column(name = "title")
	@Basic(optional = false)
	@NotNull(message = "com.slerpio.teachme.entity.TmNotification.title")
	@Size(min = 1, max = 60)
	private String title;
	@Column(name = "text")
	@Basic(optional = false)
	@NotNull(message = "com.slerpio.teachme.entity.TmNotification.text")
	private String text;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "sent")
	@Basic(optional = false)
	@NotNull(message = "com.slerpio.teachme.entity.TmNotification.sent")
	private Boolean sent;
	@Column(name = "update_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateAt;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private TmUser userId;

	@JsonProperty
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonProperty
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@JsonProperty
	public Boolean getSent() {
		return sent;
	}

	public void setSent(Boolean sent) {
		this.sent = sent;
	}

	@JsonProperty
	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	@JsonProperty
	public TmUser getUserId() {
		return userId;
	}

	public void setUserId(TmUser userId) {
		this.userId = userId;
	}
}