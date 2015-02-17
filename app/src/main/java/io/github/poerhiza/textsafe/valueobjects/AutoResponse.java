package io.github.poerhiza.textsafe.valueobjects;

public class AutoResponse implements java.io.Serializable {
    private static final long serialVersionUID = 6652395754986041318L;
    public static final String TAG = AutoResponse.class.getSimpleName();

    private int id;
    private int freq;
    private String title;
    private String response;

    public AutoResponse(int id, String title, String response) {
        this.id = id;
        this.title = title;
        this.response = response;
        this.freq = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", ").append("title=").append(title);
        sb.append(", ").append("response=").append(response);
        return sb.toString();
    }

    public String getSerialized() {
        return "[AutoResponse: " +
                " id=" + String.valueOf(id) +
                " title=" + title +
                " response=" + response +
                " freq=" + String.valueOf(freq) +
                "]";
    }

    public String getResponse() {
        return response;
    }

    public String getTitle() {
        return title;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }
}