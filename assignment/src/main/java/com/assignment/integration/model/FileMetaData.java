package com.assignment.integration.model;

import java.util.HashMap;

/**
 * This is the class for the file metadata which is returned from third Party's API.
 * 
 * @author Pankaj
 *
 */
public class FileMetaData {

	private String path;

	private String createdDate;

	private int size;

	private String parentFolderId;

	private String name;

	private String modifiedDate;

	private String id;

	private Boolean directory;

	private HashMap<String, String> properties;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getDirectory() {
		return directory;
	}

	public void setDirectory(Boolean directory) {
		this.directory = directory;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

}
