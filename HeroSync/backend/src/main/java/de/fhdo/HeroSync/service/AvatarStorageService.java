package de.fhdo.HeroSync.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private static final String UPLOAD_DIR = "uploads/avatars";

    public AvatarStorageService() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    /**
     * Saves a 3D model (Base64) to a file and returns the relative URL.
     * If the input is not a 3D model or large data URL, it returns it as-is (e.g. for simple seeds).
     */
    public String processAvatar(String avatarData) {
        if (avatarData == null || avatarData.isEmpty()) {
            return avatarData;
        }

        // Only save to file if it's a large data URL (3D model)
        if (avatarData.startsWith("data:") && avatarData.length() > 5000) {
            return saveToFile(avatarData);
        }

        return avatarData;
    }

    private String saveToFile(String dataUrl) {
        try {
            String[] parts = dataUrl.split(",");
            if (parts.length < 2) return dataUrl;

            String header = parts[0];
            String base64Data = parts[1];
            
            String extension = ".glb"; // Default for models
            if (header.contains("image/png")) extension = ".png";
            else if (header.contains("image/jpeg")) extension = ".jpg";
            else if (header.contains("image/svg+xml")) extension = ".svg";

            String filename = UUID.randomUUID().toString() + extension;
            Path path = Paths.get(UPLOAD_DIR, filename);

            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            Files.write(path, decodedBytes);

            // Return the URL that the frontend will use to fetch the file
            return "/api/uploads/avatars/" + filename;
        } catch (Exception e) {
            System.err.println("Failed to save avatar to file: " + e.getMessage());
            return dataUrl; // Fallback to raw data if saving fails
        }
    }
}
