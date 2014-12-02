package entity;

import android.graphics.Bitmap;

public class TopStory extends AbstractStory {
	private String image;
	private Bitmap bitmap;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
