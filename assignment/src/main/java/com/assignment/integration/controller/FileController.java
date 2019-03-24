package com.assignment.integration.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.assignment.integration.utility.AppConstants;

/**
 * This rest-controller consists of file related API's. 
 * Currently it consists of API's to download and upload file
 * 
 * @author Pankaj
 *
 */
@RestController
public class FileController {

	/**
	 * This method allows the user to download a single file. 
	 * This method is simply a wrapper over a calling API of an external source
	 * 
	 *
	 * @param auth - Contains the value passed in Authorization header
	 * @param path - Contains the absolute path with the filename and extension. This is a required parameter.
	 * @return - Return ResponseEntity<Array of Bytes>
	 * @throws IOException
	 */
	@GetMapping("/downloadFile")
	public ResponseEntity<byte[]> downloadFile(@RequestHeader("Authorization") String auth, @RequestParam(name = "path", required = true) String path) throws IOException {
		HttpHeaders header = new HttpHeaders();
		header.set("Authorization", auth);
		String url = AppConstants.BASEURL + "/files?path=" + path;
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<>("parameters", header);
		ResponseEntity<byte[]> result = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
		return result;
	}

	/**
	 * This method allows a user to upload a file to a certain path or location.
	 * 
	 * File has to be added in the request body with "file" as the key
	 * 
	 * @param request - The request object being used to extract the file content and metadata
	 * @param auth - Contains the value passed in Authorization header
	 * @param path - Path of the folder in which the file has to be uploaded
	 * @return
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public String fileUpload(HttpServletRequest request, @RequestHeader("Authorization") String auth,
			@RequestParam(name = "path", required = true, defaultValue = "/") String path) {
		MultipartHttpServletRequest mRequest;
		HttpHeaders header = new HttpHeaders();
		header.set("Authorization", auth);
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		ResponseEntity<String> res = null;
		try {
			mRequest = (MultipartHttpServletRequest) request;
			Iterator<String> itr = mRequest.getFileNames();
			while (itr.hasNext()) {
				MultipartFile mFile = mRequest.getFile(itr.next().toString());
				File file = convert(mFile);
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add("file", new FileSystemResource(file));
				String fileName = mFile.getOriginalFilename();
				String url = AppConstants.BASEURL + "/files?overwrite=true&path=" + path + '/' + fileName;
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, header);
				RestTemplate restTemplate = new RestTemplate();
				res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			}
		} catch (Exception ex) {
			return ex.toString();
		}
		return res.toString();
	}

	/**
	 * This utility method is used to convert a multipart file to File Object
	 * 
	 * 
	 * @param file - Multipart file which needs to converted to File Object
	 * @return - Return the file Object of the given multipart file
	 * @throws IOException
	 */
	public File convert(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

}
