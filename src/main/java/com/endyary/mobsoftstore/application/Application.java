package com.endyary.mobsoftstore.application;

import com.endyary.mobsoftstore.rating.Rating;
import com.endyary.mobsoftstore.user.User;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Application entity class
 */
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id", referencedColumnName = "id")
    private User createdBy;

    @Lob
    @Column(name = "archive", columnDefinition = "BLOB", nullable = false)
    private byte[] archive;

    @Column(nullable = false)
    private String archiveName;

    @Lob
    @Column(name = "pictureSmall", columnDefinition = "BLOB")
    private byte[] pictureSmall;

    @Lob
    @Column(name = "pictureBig", columnDefinition = "BLOB")
    private byte[] pictureBig;
    @Column(name = "downloadCount", nullable = false)
    private int downloadCount;

    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER)
    private List<Rating> ratings;

    private LocalDateTime createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public byte[] getArchive() {
        return archive;
    }

    public void setArchive(byte[] archive) {
        this.archive = archive;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public byte[] getPictureSmall() {
        return pictureSmall;
    }

    public void setPictureSmall(byte[] pictureSmall) {
        this.pictureSmall = pictureSmall;
    }

    public byte[] getPictureBig() {
        return pictureBig;
    }

    public void setPictureBig(byte[] pictureBig) {
        this.pictureBig = pictureBig;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Returns average rating value rounded to one decimal place
     *
     * @return calculated average rating
     */
    public String getAverageRating() {

        if (this.getRatings().isEmpty()) {
            return "0";
        }
        float sum = this.getRatings().stream().mapToInt(Rating::getRating).sum();
        return new DecimalFormat("0.0").format(sum / this.getRatings().size());
    }
}
