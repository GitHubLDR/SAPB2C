package com.sap.image.search.gcp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface GCPVisionAPISearch {


    public List <String> getImageLabelData(byte[] imageBytes) throws IOException;

    public List <String> getImageLabelDataPath(String imagePath) throws IOException;

}
