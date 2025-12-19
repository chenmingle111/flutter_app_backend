package com.example.springboot_01.service;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class SmileService {

    private CascadeClassifier faceCascade;
    private CascadeClassifier smileCascade;

    @PostConstruct
    public void init() throws IOException {
        OpenCV.loadLocally();

        faceCascade = loadClassifier("haarcascades/haarcascade_frontalface_default.xml");
        smileCascade = loadClassifier("haarcascades/haarcascade_smile.xml");
    }

    private CascadeClassifier loadClassifier(String resourcePath) throws IOException {
        // OpenCV needs a real file path, so we copy the resource to a temp file
        ClassPathResource resource = new ClassPathResource(resourcePath);
        File tempFile = File.createTempFile("opencv", ".xml");
        try (InputStream in = resource.getInputStream()) {
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        tempFile.deleteOnExit();

        CascadeClassifier classifier = new CascadeClassifier(tempFile.getAbsolutePath());
        if (classifier.empty()) {
            throw new IOException("Failed to load classifier: " + resourcePath);
        }
        return classifier;
    }

    public double analyzeSmile(MultipartFile imageFile) throws IOException {
        // Convert MultipartFile to OpenCV Mat
        // Since OpenCV Java doesn't easily read from InputStream, we write to temp file
        File tempImage = File.createTempFile("upload", ".jpg");
        imageFile.transferTo(tempImage);

        Mat src = Imgcodecs.imread(tempImage.getAbsolutePath());
        if (src.empty()) {
            tempImage.delete();
            throw new IOException("Failed to load image");
        }

        Mat gray = new Mat();
        org.opencv.imgproc.Imgproc.cvtColor(src, gray, org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY);
        org.opencv.imgproc.Imgproc.equalizeHist(gray, gray);

        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(gray, faces);

        Rect[] facesArray = faces.toArray();
        double maxSmileScore = 0.0;

        for (Rect face : facesArray) {
            Mat faceROI = gray.submat(face);
            MatOfRect smiles = new MatOfRect();

            // Adjust parameters for smile detection: scaleFactor, minNeighbors, flags,
            // minSize, maxSize
            // these parameters might need tuning
            smileCascade.detectMultiScale(faceROI, smiles, 1.8, 20, 0, new Size(25, 25), new Size());

            Rect[] smilesArray = smiles.toArray();
            if (smilesArray.length > 0) {
                // Simple scoring logic: If a smile is detected, give a high score.
                // We can refine this by checking the size of the smile rect relative to the
                // face,
                // or the number of detected smile rectangles (neighbors).
                // For now, let's map existence to a score range.

                // If multiple smiles detected (false positives or very strong smile), higher
                // score.
                double currentScore = 60 + (smilesArray.length * 10);
                if (currentScore > 100)
                    currentScore = 100;
                if (currentScore > maxSmileScore) {
                    maxSmileScore = currentScore;
                }
            } else {
                // No smile detected
                if (maxSmileScore < 50)
                    maxSmileScore = 50; // Base score for having a face
            }
        }

        // Cleanup
        tempImage.delete();

        // If no face detected, return 0
        if (facesArray.length == 0) {
            return 0.0;
        }

        return maxSmileScore;
    }
}
