package dafoo.video;

public class VideoFromId {

	private int id;
	private String title;
	private  String video_url;
	
	public VideoFromId()
	{
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getVideo_url() {
		return video_url;
	}
	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "VideoFromId {id=" + id + ", title=" + title + ", video_url=" + video_url
                + "}";
	}
	
}
