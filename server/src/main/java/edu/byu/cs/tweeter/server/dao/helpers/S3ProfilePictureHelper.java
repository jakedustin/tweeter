package edu.byu.cs.tweeter.server.dao.helpers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class S3ProfilePictureHelper {
    public final String BUCKET_NAME = "jd-cs340-profile-pictures";
    private static S3ProfilePictureHelper instance;
    private static AmazonS3 client;

    private S3ProfilePictureHelper() {
        client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .build();
    }

    public static S3ProfilePictureHelper getInstance() {
        if (instance == null) {
            instance = new S3ProfilePictureHelper();
        }

        return instance;
    }

    private AmazonS3 getClient() {
        return client;
    }

    public String handleImageString(String image, String userAlias) {
        byte[] imageBytes = Base64.getDecoder().decode(image);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/jpeg");

        PutObjectRequest imageRequest = new PutObjectRequest(BUCKET_NAME, getProfilePictureFilename(userAlias), new ByteArrayInputStream(imageBytes), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        getClient().putObject(imageRequest);
        return getPublicUrl(userAlias);
    }

    public String getPublicUrl(String userAlias) {
        return getClient().getUrl(BUCKET_NAME, getProfilePictureFilename(userAlias)).toString();
    }

    private String getProfilePictureFilename(String alias) {
        return alias + "_profile";
    }
}
