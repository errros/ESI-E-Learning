package com.elab.elearning.elearning.service;

import com.elab.elearning.elearning.entity.FileDB;
import com.elab.elearning.elearning.entity.Module;
import com.elab.elearning.elearning.model.DocumentType;
import com.elab.elearning.elearning.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CourseService {
    private final Path root = Paths.get("Course");

    @Autowired
    private FileRepository fileRepository;

    public void init() {
        try {
            if(Files.notExists(root)) {
                Files.createDirectory(root);

            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public void save(MultipartFile file) {
        try {
            //System.out.println(file.getOriginalFilename());
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public void delete(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                Files.delete(file);

            }
        } catch (Exception e) {
            throw new RuntimeException("Could not delete such a file. Error: " + e.getMessage());
        }


    }


    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    public List<FileDB> getAllCourses(Module module){

        return fileRepository.findAll().stream().filter(fileDB -> fileDB.getDocumentType() == DocumentType.COURSE).filter(fileDB -> fileDB.getModule()== module).collect(Collectors.toList());
    }
}

