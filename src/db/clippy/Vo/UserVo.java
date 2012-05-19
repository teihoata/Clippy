/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.Vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Value Object
 * @author Ray
 */
public class UserVo {
    
    private String uid;
    private String uname;
    private String pwd;
    private List<PreferenceVo> preference = new ArrayList<>();

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the uname
     */
    public String getUname() {
        return uname;
    }

    /**
     * @param uname the uname to set
     */
    public void setUname(String uname) {
        this.uname = uname;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the preference
     */
    public List<PreferenceVo> getPreference() {
        return preference;
    }

    /**
     * @param preference the preference to set
     */
    public void setPreference(List<PreferenceVo> preference) {
        this.preference = preference;
    }
    
}
