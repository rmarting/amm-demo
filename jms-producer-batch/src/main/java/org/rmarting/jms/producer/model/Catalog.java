package org.rmarting.jms.producer.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Catalog implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private int version;

	private String artist;

	private String title;

	private String description;

	private Float price;

	private Date publicationDate;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (artist != null && !artist.trim().isEmpty())
			result += "artist: " + artist;
		if (title != null && !title.trim().isEmpty())
			result += ", title: " + title;
		if (description != null && !description.trim().isEmpty())
			result += ", description: " + description;
		if (price != null)
			result += ", price: " + price;
		return result;
	}
}