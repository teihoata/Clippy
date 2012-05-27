/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.Vo;

/**
 *
 * @author Ray
 */
public class GoogleNaviVo {
    
    private String lat;
    private String lng;
    private String html_instructions;
    private String distance;
    private int count;

    /**
     * @return the lat
     */
    public String getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     * @return the lng
     */
    public String getLng() {
        return lng;
    }

    /**
     * @param lng the lng to set
     */
    public void setLng(String lng) {
        this.lng = lng;
    }

    /**
     * @return the html_instructions
     */
    public String getHtml_instructions() {
        return html_instructions;
    }

    /**
     * @param html_instructions the html_instructions to set
     */
    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    /**
     * @return the distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }
    
}
