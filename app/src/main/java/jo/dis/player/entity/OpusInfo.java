package jo.dis.player.entity;

import java.io.Serializable;

/**
 *
 */

public class OpusInfo implements Serializable {

    public String id;  //视频id
    public String title;  //标题
    public int topic_id;  //话题id,0为未关联话题
    public String topic_title;  //话题标题
    public String topic_mark;  //话题描述
    public String song;  //歌曲名
    public int likes;  //赞的次数
    public boolean isLike;  //是否赞过
    public String img;  //主播头像
    public int user_id;  //主播用户id
    public String nick_name;  //主播昵称
    public int fans;  //主播的粉丝数,
    public int live_status;  //1表示直播中
    public int live_type;  //0为普通直播，1为移动直播
    public int room_id;  //主播房间id
    public String link;  //视频云存储下载链接
    public String gif;  //视频封面地址
    public String song_cover;  //歌曲封面图片，不一定有值

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(int topic_id) {
        this.topic_id = topic_id;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public void setTopic_title(String topic_title) {
        this.topic_title = topic_title;
    }

    public String getTopic_mark() {
        return topic_mark;
    }

    public void setTopic_mark(String topic_mark) {
        this.topic_mark = topic_mark;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getLive_status() {
        return live_status;
    }

    public void setLive_status(int live_status) {
        this.live_status = live_status;
    }

    public int getLive_type() {
        return live_type;
    }

    public void setLive_type(int live_type) {
        this.live_type = live_type;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public String getSong_cover() {
        return song_cover;
    }

    public void setSong_cover(String song_cover) {
        this.song_cover = song_cover;
    }
}
