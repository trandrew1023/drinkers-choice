package com.example.demo.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.user.Users;

@RestController
public class ImageController {

	@Autowired
	ImageRepository imageRepository;

	/**
	 * 
	 * @return all images
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/images")
	public List<Images> getAllImages() {
		List<Images> results = imageRepository.findAll();
		return results;
	}

	/**
	 * 
	 * @param imageID id of requested image
	 * @return image
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/request/image/{imageID}")
	public Optional<Images> getImageByID(@PathVariable("imageID") int imageID) {
		Optional<Images> image = imageRepository.findById(imageID);
		return image;
	}

	/**
	 * 
	 * @param image image to be saved
	 * @return saved image
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/image")
	public Images saveImage(@RequestBody Images image) {
		imageRepository.save(image);
		return image;
	}
	
	@RequestMapping(method = RequestMethod.PUT, path = "/post/update/image/{imageID}")
	public Images updateImage(@PathVariable("imageID") int imageID, @RequestBody Images image) {
		Optional<Images> imageOptional = imageRepository.findById(imageID);
		image.setImageID(imageID);
		imageRepository.save(image);
		return image;
	}
	
	/**
	 * 
	 * @param image
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/post/new/string/image")
	public String saveImageString(@RequestBody Images image) {
		imageRepository.save(image);
		return "success";
	}

	private static String UPLOADED_FOLDER;

	/**
	 * a working way to post a file (such as an image) on the server
	 * @param uploadfile
	 * file to be uploaded
	 * @param request
	 * stuff
	 * @return
	 * a response with the path
	 */
	@PostMapping("/post/upload/image")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadfile, HttpServletRequest request) {
		String directoryPath = request.getSession().getServletContext().getRealPath("/");
		UPLOADED_FOLDER = directoryPath;
		if (uploadfile.isEmpty()) {
			return new ResponseEntity("No file", HttpStatus.OK);
		}
		try {
			saveUploadedFiles(Arrays.asList(uploadfile));
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Images image = new Images();
		image.setPath(UPLOADED_FOLDER + "/" + uploadfile.getOriginalFilename());
		imageRepository.save(image);
		return new ResponseEntity("Successfully uploaded - " + uploadfile.getOriginalFilename() + UPLOADED_FOLDER, new HttpHeaders(),	HttpStatus.OK);
	}

	/**
	 * 
	 * @param files
	 * @throws IOException
	 */
	private void saveUploadedFiles(List<MultipartFile> files) throws IOException {
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				continue;
			}
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);
		}

	}
}
