package com.endyary.mobsoftstore.application;

import com.endyary.mobsoftstore.rating.Rating;
import com.endyary.mobsoftstore.rating.RatingRepository;
import com.endyary.mobsoftstore.rating.RatingRequest;
import com.endyary.mobsoftstore.user.User;
import com.endyary.mobsoftstore.user.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * Application service class
 */
@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final ArchiveInspector archiveInspector;

    private final UserService userService;

    private final RatingRepository ratingRepository;

    public ApplicationService(ApplicationRepository applicationRepository, ArchiveInspector archiveInspector,
                              UserService userService, RatingRepository ratingRepository) {
        this.applicationRepository = applicationRepository;
        this.archiveInspector = archiveInspector;
        this.userService = userService;
        this.ratingRepository = ratingRepository;
    }

    /**
     * Returns Application for the given ID.
     *
     * @param id Application's ID
     * @return Application instance
     */
    public Application findById(Long id) {
        return applicationRepository.findById(id).orElseThrow(
                () -> new RuntimeException("No Application found with id = " + id));
    }

    /**
     * Returns list of images for most downloaded applications
     *
     * @return The result list
     */
    @Cacheable("topAppLogo")
    public List<String> getImagesForTopDownloaded() {
        List<Application> appList = applicationRepository.findTop5ByOrderByDownloadCountDesc();
        List<String> byteStringContent = new ArrayList<>();
        appList.forEach(app -> byteStringContent.add(Base64.getEncoder().encodeToString(app.getPictureSmall())));
        return byteStringContent;
    }

    /**
     * Sets values for both image attributes by using the
     * {@link Application}'s archive content
     *
     * @param application {@link Application} instance
     * @throws ArchiveProcessingException if an I/O error occurs
     */
    public void setImagesContent(Application application) throws ArchiveProcessingException {
        try {
            Map<String, byte[]> entryMap = archiveInspector.getContentMap(application.getArchive());

            if (!archiveInspector.isArchiveValid(entryMap, application.getName(), application.getArchiveName())) {
                throw new ArchiveProcessingException("Archive is not valid!", null);
            }

            Map<String, String> imageNameMap = archiveInspector.getImageNameMap(entryMap);
            Map<String, byte[]> imageContentMap = archiveInspector.getImageContentMap(imageNameMap, entryMap);

            application.setPictureSmall(imageContentMap.get(ArchiveInspector.PICTURE_128_NAME));
            application.setPictureBig(imageContentMap.get(ArchiveInspector.PICTURE_512_NAME));
        } catch (IOException e) {
            throw new ArchiveProcessingException("Error while processing the archive!", e);
        }
    }

    /**
     * Returns list of available categories (from {@link Category} enum)
     *
     * @return The result list
     */
    @Cacheable("categories")
    public List<String> getCategories() {
        return Stream.of(Category.values())
                .map(Enum::name).map(String::toLowerCase).toList();
    }

    /**
     * Returns list of applications for the given category
     *
     * @param category application's category
     * @return The result list
     */
    public List<Application> findByCategory(Category category) {
        return applicationRepository.findByCategory(category);
    }

    /**
     * Saves application instance in the DB.
     * It is used for both create and update.
     *
     * @param application {@link Application}
     * @return saved {@link Application}
     */
    public Application save(Application application) {
        return applicationRepository.save(application);
    }

    /**
     * Creates {@link ApplicationResponse} for the given {@link Application}
     *
     * @param application {@link Application}
     * @return resulting {@link ApplicationResponse}
     */
    public ApplicationResponse getDTOFromEntity(Application application) {
        return new ApplicationResponse(application.getId(), application.getName(),
                application.getCategory().toString(), application.getDescription(),
                Base64.getEncoder().encodeToString(application.getPictureSmall()),
                Base64.getEncoder().encodeToString(application.getPictureBig()),
                application.getDownloadCount(), application.getAverageRating(), application.getRatings().size());
    }

    /**
     * Creates {@link Application} from the given {@link ApplicationRequest}
     * and uploaded archive file
     *
     * @param appRequest {@link ApplicationRequest}
     * @param archive    {@link MultipartFile} archive
     * @return resulting {@link Application}
     */
    public Application getEntityFromDTO(ApplicationRequest appRequest, MultipartFile archive) throws IOException {
        Application app = new Application();
        app.setName(appRequest.getName());
        app.setCategory(Category.valueOf(appRequest.getCategory().toUpperCase()));
        app.setDescription(appRequest.getDescription());
        app.setArchive(archive.getBytes());
        app.setArchiveName(archive.getOriginalFilename());

        app.setCreatedBy(userService.getCurrentUser());
        setImagesContent(app);

        return app;
    }

    /**
     * Checks whether the application exists with the given name
     *
     * @param name application's name
     * @return the result of checking
     */
    public boolean existsByName(String name) {
        return applicationRepository.existsByName(name);
    }

    /**
     * Increases the download count and triggers the cache clearing
     *
     * @param app the downloaded application
     */
    @CacheEvict(value = "topAppLogo", allEntries = true)
    public void increaseDownloadCount(Application app) {
        app.setDownloadCount(app.getDownloadCount() + 1);
        save(app);
    }

    /**
     * Adds Application rating using the given request object.
     * As one user can have only one rating per application the existing rating will be updated.
     *
     * @param ratingRequest request object
     * @return created/updated rating object
     */
    public Rating addRating(RatingRequest ratingRequest) {

        Application app = new Application();
        app.setId(ratingRequest.getAppId());
        User user = userService.getCurrentUser();

        Rating rating;
        Optional<Rating> ratingResult = ratingRepository.getByApplicationAndUser(app, user);
        if (ratingResult.isPresent()) {
            rating = ratingResult.get();
        } else {
            rating = new Rating();
            rating.setApplication(app);
            rating.setUser(user);
        }
        rating.setRating(ratingRequest.getRating());
        rating.setCreatedDate(LocalDateTime.now());

        return ratingRepository.save(rating);
    }
}
