package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Gallery {

    ObservableList<Photo> photos = FXCollections.observableArrayList();

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public ObservableList<Photo> getPhotos() {
        return photos;
    }

    public void clear() {
        photos.clear();
    }
}
