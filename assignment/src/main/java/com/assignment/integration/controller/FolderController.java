package com.assignment.integration.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.assignment.integration.model.FileMetaData;
import com.assignment.integration.utility.AppConstants;

/**
 * @author Pankaj
 *
 */
@RestController
public class FolderController {

	@Value("${folder.location}")
	String location;

	/**
	 * This method will download all files and folders of a given folder. 
	 * All nested folder and files will be downloaded asynchronously.
	 * The base location is available in application.properties
	 * 
	 * @param auth
	 * @param path
	 * @throws IOException
	 */
	@RequestMapping(name = "/downloadAll", method = RequestMethod.GET)
	public void downloadBulk(@RequestHeader("Authorization") String auth,
			@RequestParam(name = "path", required = true) String path) throws IOException {

		String url = AppConstants.BASEURL + "/folders/contents?path=" + path;
		HttpHeaders header = new HttpHeaders();
		header.set("Authorization", auth);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<>("parameters", header);
		ResponseEntity<FileMetaData[]> result = restTemplate.exchange(url, HttpMethod.GET, entity,
				FileMetaData[].class);
		FileMetaData[] fileMetaData = result.getBody();
		String newLocation = location + path;
		for (int i = 0; i < fileMetaData.length; i++) {
			baseMethod(fileMetaData, i, entity, newLocation, auth);

		}

	}

	/**
	 * This is a asynchrous method which will being called from bulkDownload method
	 * 
	 * @param fileMetaData
	 * @param i
	 * @param entity
	 * @param newLocation
	 * @param auth
	 * @throws IOException
	 */
	@Async
	public void baseMethod(FileMetaData[] fileMetaData, int i, HttpEntity<String> entity, String newLocation,
			String auth) throws IOException {
		new Thread() {
			public void run() {
				if (fileMetaData[i].getDirectory() == false) {
					String fileurl = AppConstants.BASEURL + "/files?path=" + fileMetaData[i].getPath();
					RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<byte[]> resultByte = restTemplate.exchange(fileurl, HttpMethod.GET, entity,
							byte[].class);

					if (!new File(newLocation).exists()) {
						new File(newLocation).mkdirs();
					}
					Path pathh = Paths.get(newLocation + "/" + fileMetaData[i].getName());
					try {
						Files.write(pathh, resultByte.getBody());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						downloadBulk(auth, fileMetaData[i].getPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}.start();
	}
}