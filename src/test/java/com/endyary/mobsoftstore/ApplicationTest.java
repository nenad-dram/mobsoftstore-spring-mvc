package com.endyary.mobsoftstore;

import com.endyary.mobsoftstore.application.Application;
import com.endyary.mobsoftstore.application.ApplicationService;
import com.endyary.mobsoftstore.application.ArchiveInspector;
import com.endyary.mobsoftstore.application.Category;
import com.endyary.mobsoftstore.config.MvcConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MvcConfig.class})
@WebAppConfiguration
class ApplicationTest {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    ArchiveInspector archiveInspector;

    @Test
    void findById_validId_appFound() {
        Application dbApp = applicationService.findById(1L);
        Assertions.assertNotNull(dbApp.getId());
    }

    @Test
    void findById_validId_blobsSaved() {
        Application dbApp = applicationService.findById(1L);
        Assertions.assertNotNull(dbApp.getArchive());
        Assertions.assertNotNull(dbApp.getPictureSmall());
    }

    @Test
    void findById_validId_createdByValid() {
        Application dbApp = applicationService.findById(1L);
        Assertions.assertEquals(1, dbApp.getCreatedBy().getId());
    }

    @Test
    void mostDownloadedTest() {
        List<String> appList = applicationService.getImagesForTopDownloaded();
        Assertions.assertEquals(5, appList.size());
    }

    @Test
    void findByCategoryTest() {
        String category = "tools".toUpperCase();
        List<Application> appList = applicationService.findByCategory(Category.valueOf(category));
        Assertions.assertFalse(appList.isEmpty());
    }

    @Test
    void validateArchiveTest() {
        String fileSource = "src/test/resources/empty/empty.zip";
        String appName = "Empty sample";
        String appArchiveName = "empty.zip";

        try (FileInputStream fis = new FileInputStream(fileSource)) {
            byte[] fileContent = fis.readAllBytes();
            Map<String, byte[]> entryMap = archiveInspector.getContentMap(fileContent);
            Assertions.assertTrue(archiveInspector.isArchiveValid(entryMap, appName, appArchiveName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void defaultPictureTest() {
        try {
            byte[] smallImage = archiveInspector.getDefaultImageByName(ArchiveInspector.PICTURE_128_NAME);
            byte[] bigImage = archiveInspector.getDefaultImageByName(ArchiveInspector.PICTURE_512_NAME);
            Assertions.assertTrue(smallImage.length < bigImage.length);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findById_validId_ratingValid() {
        Application dbApp = applicationService.findById(1L);
        Assertions.assertNotNull(dbApp.getAverageRating());
    }
}
