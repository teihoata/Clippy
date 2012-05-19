/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.Vo;

/**
 *
 * @author Ray
 */
public class CountryVo {

    private String iso;
    private String code;
    public String name;

    public CountryVo(String iso, String code, String name) {
        this.iso = iso;
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return iso + " - " + code + " - " + name.toUpperCase();
    }
}
