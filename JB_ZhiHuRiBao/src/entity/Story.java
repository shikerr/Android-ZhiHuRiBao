package entity;

import android.graphics.Bitmap;

public class Story extends AbstractStory {
	private String[] images;
	private Bitmap bitmap;

	public String[] getImages() {
		return images;
	}

	public void setImages(String[] images) {
		this.images = images;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

}
