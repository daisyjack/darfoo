package dafoo.music;

public class MusicFromCategory {

	private int id;
	private String image_url;
	private String music_url;
	private String title;
	private String authorname;
	private int update_timestamp;
	
	public MusicFromCategory()
	{
		
	}
	public int getId() {
		return id;
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
	
	public String getMusic_url() {
		return music_url;
	}
	public void setMusic_url(String music_url) {
		this.music_url = music_url;
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
	public int getUpdate_timestamp() {
		return update_timestamp;
	}
	public void setUpdate_timestamp(int update_timestamp) {
		this.update_timestamp = update_timestamp;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "MusicFromCategory [id=" + id + ", image_url=" + image_url + ", music_url="+music_url+", title=" + title+", authorname="+authorname+", update_timestamp="+update_timestamp
                + "]";
	}
}
