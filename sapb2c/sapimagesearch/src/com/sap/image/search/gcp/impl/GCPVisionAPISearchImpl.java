package com.sap.image.search.gcp.impl;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.sap.image.search.gcp.GCPVisionAPISearch;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GCPVisionAPISearchImpl implements GCPVisionAPISearch {

    private static final Logger LOG= Logger.getLogger(GCPVisionAPISearchImpl.class);

    private static final float SCORE_THRESOLD_90= 0.90f;
    private static final float SCORE_THRESOLD_80= 0.80f;
    private static final float SCORE_THRESOLD_70= 0.70f;
    private static final float SCORE_THRESOLD_60= 0.60f;
    private static final float SCORE_THRESOLD_50= 0.50f;

    @Override
    public List<String> getImageLabelData(byte[] imageBytes) throws IOException {


        List<String> searchTermList= new ArrayList <>();
        List <AnnotateImageRequest> requests = new ArrayList <>();

        ByteString imgBytes = ByteString.readFrom(new ByteArrayInputStream(imageBytes));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List <AnnotateImageResponse> responses = response.getResponsesList();

            CollectionUtils.emptyIfNull(responses).stream().forEach(annotateImageResponse -> {

                List <EntityAnnotation> labelAnnotationsList = annotateImageResponse.getLabelAnnotationsList();
                AtomicInteger atomicInteger= new AtomicInteger(0);

                CollectionUtils.emptyIfNull(labelAnnotationsList).stream().forEach(entityAnnotation -> {

                    if(SCORE_THRESOLD_90 < entityAnnotation.getScore()) {

                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else if(SCORE_THRESOLD_80< entityAnnotation.getScore())
                    {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else if(SCORE_THRESOLD_70< entityAnnotation.getScore())
                    {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else if(SCORE_THRESOLD_60< entityAnnotation.getScore())
                    {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else if(SCORE_THRESOLD_50< entityAnnotation.getScore())
                    {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }

                });

            });

        }
        return searchTermList;
    }


    @Override
    public List<String> getImageLabelDataPath(String imagePath) throws IOException {


        List<String> searchTermList= new ArrayList <>();
        List <AnnotateImageRequest> requests = new ArrayList <>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(imagePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List <AnnotateImageResponse> responses = response.getResponsesList();

            CollectionUtils.emptyIfNull(responses).stream().forEach(annotateImageResponse -> {

                List <EntityAnnotation> labelAnnotationsList = annotateImageResponse.getLabelAnnotationsList();
                AtomicInteger atomicInteger= new AtomicInteger(0);

                CollectionUtils.emptyIfNull(labelAnnotationsList).stream().forEach(entityAnnotation -> {

                    if(SCORE_THRESOLD_90 < entityAnnotation.getScore()) {

                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else if(SCORE_THRESOLD_80< entityAnnotation.getScore())
                    {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else if(SCORE_THRESOLD_70< entityAnnotation.getScore())
                    {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else if(SCORE_THRESOLD_60< entityAnnotation.getScore())
                    {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else if(SCORE_THRESOLD_50< entityAnnotation.getScore())
                    {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }
                    else {
                        searchTermList.add(entityAnnotation.getDescription());
                        atomicInteger.getAndIncrement();
                        LOG.info(entityAnnotation);
                    }

                });

            });

        }
        return searchTermList;
    }
}
