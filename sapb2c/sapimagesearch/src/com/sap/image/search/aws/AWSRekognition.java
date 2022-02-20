package com.sap.image.search.aws;

import java.util.List;

public interface AWSRekognition {

    public List <String> getImageLabelData(byte[] imageBytes);

}
