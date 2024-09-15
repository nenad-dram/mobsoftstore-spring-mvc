package com.endyary.mobsoftstore.application;

import com.endyary.mobsoftstore.config.View;
import com.endyary.mobsoftstore.rating.RatingRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Application controller class
 */
@Controller
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        setCommonModelAttributes(model);
        return View.HOME.toString();
    }

    @GetMapping("/applications/{category}")
    public String getByCategory(Model model, @PathVariable String category) {

        List<ApplicationResponse> appDtoList = new ArrayList<>();

        Category queryCategory = Category.valueOf(category.toUpperCase());

        List<Application> apps = applicationService.findByCategory(queryCategory);
        apps.forEach(app -> appDtoList.add(applicationService.getDTOFromEntity(app)));

        model.addAttribute("category", category);
        model.addAttribute("appList", appDtoList);
        setCommonModelAttributes(model);

        return View.HOME.toString();
    }

    @GetMapping("/details/{id}")
    public String getById(Model model, @PathVariable long id) {
        Application app = applicationService.findById(id);
        model.addAttribute("app", applicationService.getDTOFromEntity(app));
        model.addAttribute("rating", new RatingRequest(id, 0));
        setCommonModelAttributes(model);
        return View.APP_DETAILS.toString();
    }

    @GetMapping("/download/{id}")
    public void getArchiveById(@PathVariable long id, HttpServletResponse response, Model model) {

        Application app = applicationService.findById(id);
        byte[] appArchive = app.getArchive();

        try {
            response.setContentType("application/zip");
            response.addHeader("Content-Disposition", "attachment; filename=" + app.getArchiveName());
            response.setContentLength(appArchive.length);
            OutputStream os = response.getOutputStream();
            os.write(appArchive, 0, appArchive.length);
            response.getOutputStream().flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error while downloading the app!");
        }
        applicationService.increaseDownloadCount(app);
    }

    @GetMapping("/newapp")
    public String newApp(Model model) {
        setCommonModelAttributes(model);
        model.addAttribute("app", new ApplicationRequest());
        return View.NEW_APP.toString();
    }

    @PostMapping(value = "/addnewapp", consumes = {"multipart/form-data"})
    public String addNewApp(@ModelAttribute("app") ApplicationRequest appRequest,
                            @RequestParam("archive") MultipartFile archive,
                            Model model) {
        String message = null;
        setCommonModelAttributes(model);

        if (applicationService.existsByName(appRequest.getName())) {
            message = String.format("Application with the name %s already exist!", appRequest.getName());
        } else {
            try {
                Application application = applicationService.getEntityFromDTO(appRequest, archive);
                application = applicationService.save(application);
                if (application.getId() != null) {
                    message = "Application successfully added!";
                }
            } catch (ArchiveProcessingException | IOException exc) {
                if (exc.getClass().equals(IOException.class) || exc.getCause() != null) {
                    exc.printStackTrace();
                }
                message = exc.getMessage();
            }
        }
        model.addAttribute("message", message);
        return View.NEW_APP.toString();
    }

    @PostMapping("/apprating")
    public String appRating(@ModelAttribute("rating") RatingRequest ratingRequest, Model model) {

        applicationService.addRating(ratingRequest);
        Application app = applicationService.findById(ratingRequest.getAppId());
        model.addAttribute("app", applicationService.getDTOFromEntity(app));
        model.addAttribute("rating", new RatingRequest(ratingRequest.getAppId(), ratingRequest.getRating()));
        setCommonModelAttributes(model);

        return View.APP_DETAILS.toString();
    }

    private void setCommonModelAttributes(Model model) {
        model.addAttribute("categories", applicationService.getCategories());
        model.addAttribute("topLogos", applicationService.getImagesForTopDownloaded());
    }
}
