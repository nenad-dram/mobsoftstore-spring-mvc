package com.endyary.mobsoftstore.application;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Helper class - inspects Application's archive content and gets or sets some values
 */
@Component
public class ArchiveInspector {

    public static final String PICTURE_128_NAME = "picture_128";
    public static final String PICTURE_512_NAME = "picture_512";
    public static final String PROPERTIES_FILE_EXTENSION = ".txt";
    public static final String APP_NAME_NAME = "name";
    public static final String APP_PACKAGE_NAME = "package";

    public static final String DEFAULT_PICTURE_128 = "default_128.jpg";
    public static final String DEFAULT_PICTURE_512 = "default_512.jpg";

    /**
     * Returns Archive's entry (file) map (key - entry's name, value - entry's byte content).
     *
     * @param fileContent - archive file content
     * @return Entry map (key name, value content)
     * @throws IOException if an I/O error occurs
     */
    public Map<String, byte[]> getContentMap(byte[] fileContent) throws IOException {
        Map<String, byte[]> entryMap = new HashMap<>();
        byte[] b = new byte[8192];
        int len;
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileContent))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ((len = zis.read(b)) > 0) {
                    out.write(b, 0, len);
                }
                entryMap.put(zipEntry.getName(), out.toByteArray());
                out.close();
            }
        }
        return entryMap;
    }

    /**
     * Returns names for the two logo images specified within the txt file.
     * Result is a map with property's name as a key (either "picture_128" or "picture_512")
     * and image's file name as a value.
     *
     * @param entryMap - archive entry map
     * @return logo image name map
     * @throws IOException if an I/O error occurs
     */
    public Map<String, String> getImageNameMap(Map<String, byte[]> entryMap) throws IOException {
        Map<String, String> imageNameMap = new HashMap<>();
        imageNameMap.put(PICTURE_128_NAME, null);
        imageNameMap.put(PICTURE_512_NAME, null);

        for (Map.Entry<String, byte[]> entry : entryMap.entrySet()) {

            // Find .txt file in the archive
            if (entry.getKey().endsWith(PROPERTIES_FILE_EXTENSION)) {

                // Read the content
                try (InputStream is = new ByteArrayInputStream(entry.getValue());
                     BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {

                        // Search for image properties
                        if (line.startsWith(PICTURE_128_NAME) || line.startsWith(PICTURE_512_NAME)) {

                            // Put result in the map
                            String[] imageProperty = line.split(":");
                            imageNameMap.put(imageProperty[0].trim(), imageProperty[1].trim());
                        }
                    }
                }
                break;
            }
        }
        return imageNameMap;
    }

    /**
     * Returns contents (bytes) for the two logo images within a map (key - image name, value - image content).
     * Result is based on the image name map and the archive content.
     * If the image is missing the default one will be used instead.
     *
     * @param imageNameMap images name map
     * @param entryMap     archive's content
     * @return logo images content map
     */
    public Map<String, byte[]> getImageContentMap(Map<String, String> imageNameMap, Map<String, byte[]> entryMap)
            throws IOException {
        Map<String, byte[]> imageContentMap = new HashMap<>();
        for (Map.Entry<String, String> entry : imageNameMap.entrySet()) {
            byte[] content = entryMap.get(entry.getValue());
            if (content == null) {
                content = getDefaultImageByName(entry.getKey());
            }
            imageContentMap.put(entry.getKey(), content);
        }
        return imageContentMap;
    }

    /**
     * Returns validation result for the given archive content.
     * The archive is valid if it has a {@value #PROPERTIES_FILE_EXTENSION} file
     * and values for both {@value #APP_NAME_NAME} and {@value #APP_PACKAGE_NAME}
     * are the same as the values entered on the input form
     *
     * @param entryMap       archive entry map
     * @param appName        application name from the form
     * @param appArchiveName archive name from the form
     * @return validation result
     * @throws IOException if an I/O error occurs
     */
    public boolean isArchiveValid(Map<String, byte[]> entryMap, String appName, String appArchiveName) throws IOException {
        boolean hasValidName = false;
        boolean hasValidArchiveName = false;

        // If the archive is empty
        if (entryMap.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, byte[]> entry : entryMap.entrySet()) {

            // Find properties file in the archive
            if (entry.getKey().endsWith(PROPERTIES_FILE_EXTENSION)) {

                // Read the content
                try (InputStream is = new ByteArrayInputStream(entry.getValue());
                     BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {

                        // Check application's name
                        if (line.startsWith(APP_NAME_NAME) && appName.equals(line.split(":")[1].trim())) {
                            hasValidName = true;
                        }

                        // Check application's archive name
                        if (line.startsWith(APP_PACKAGE_NAME) &&
                                appArchiveName.equals(line.split(":")[1].trim())) {
                            hasValidArchiveName = true;
                        }
                    }
                }
                break;
            }
        }
        return hasValidName && hasValidArchiveName;
    }

    /**
     * Returns default image's content for big or small image.
     * The input parameter (imageName) is name of the image parameter in the txt file -
     *
     * @param imageName {@value #PICTURE_128_NAME} or {@value #PICTURE_512_NAME}
     * @return default image's content
     * @throws IOException if an I/O error occurs
     */
    public byte[] getDefaultImageByName(String imageName) throws IOException {
        byte[] content;
        String fileName = PICTURE_128_NAME.equals(imageName) ? DEFAULT_PICTURE_128 : DEFAULT_PICTURE_512;
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            content = inputStream.readAllBytes();
        }
        return content;
    }
}
