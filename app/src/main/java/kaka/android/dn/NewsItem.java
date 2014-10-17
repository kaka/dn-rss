package kaka.android.dn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsItem
{
    private static final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    private String title, link, description, pubDate;
    private Date date;

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getId() {
	return link;
    }

    public String getLink() {
	return link;
    }

    public void setLink(String link) {
	this.link = link;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getPubDate() {
	return pubDate;
    }

    public void setPubDate(String pubDate) {
	this.pubDate = pubDate;
	try {
	    date = format.parse(pubDate);
	} catch(Exception e) {
	    App.log.e(this, String.format("Error while parsing date: '%s'", pubDate), e);
	}
    }

    public Date getDate() {
	return date;
    }

    public String getDateWithFormat(String format) {
	return new SimpleDateFormat(format).format(date);
    }
}
