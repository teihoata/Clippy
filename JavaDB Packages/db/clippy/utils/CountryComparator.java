/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.utils;

import db.clippy.Vo.CountryVo;
import java.text.Collator;
import java.util.Comparator;

/**
 *
 * @author Ray
 */
public class CountryComparator implements Comparator<CountryVo> {

    private Comparator comparator;

    public CountryComparator() {
        comparator = Collator.getInstance();
    }

    /**
     * Override compare method of Comparator
     * @param o1
     * @param o2
     * @return 
     */
    @Override
    public int compare(CountryVo o1, CountryVo o2) {
        return comparator.compare(o1.name, o2.name);
    }
}
