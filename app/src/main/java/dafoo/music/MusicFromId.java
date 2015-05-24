package dafoo.music;

public class MusicFromId {

	private int id;
	private  String music_url;
	private String title;
	
	public MusicFromId()
	{
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "MusicFromId {id=" + id + ", url=" + music_url + ", title=" + title
                + "}";
	}
}
