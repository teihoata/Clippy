/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.Vo;

/**
 * Java Value Object of countries
 * @author Ray
 */
public class CountryVo {

    private String iso;
    private String code;
    public String name;

    /**
     * construction function
     * @param iso
     * @param code
     * @param name 
     */
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
