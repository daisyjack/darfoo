package dafoo.video;

public class VideoFromCategory {

	private int id;
	private String title;
	private String authorname;
	private String image_url;
	private String video_url;
    private int type;
	private int update_timestamp;

	public VideoFromCategory() {

	}

	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public void setId(int id) {
		this.id = id;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUpdate_timestamp() {
		return update_timestamp;
	}

	public void setUpdate_timestamp(int update_timestamp) {
		this.update_timestamp = update_timestamp;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub

		return "VideoFromCategory [id=" + id + ", title=" + title+ ", author_name="+ authorname +", image_url=" + image_url
				+ ", video_url=" + video_url
				+ ", update_timestamp=" + update_timestamp + "]";
	}
	
}
