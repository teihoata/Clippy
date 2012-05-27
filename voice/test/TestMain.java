/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import db.clippy.SearchEngine.GoogleNavigation;
import db.clippy.SearchEngine.GoogleSearch;
import db.clippy.Vo.CountryVo;
import db.clippy.Vo.GoogleNaviVo;
import db.clippy.Vo.PreferenceVo;
import db.clippy.Vo.UserVo;
import db.clippy.dbConnect.DBOperator;
import db.clippy.utils.CountryComparator;
import db.clippy.utils.SSHAEncrypt;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 *
 * @author Ray
 */
public class TestMain {

    public static void main(String[] args) throws UnsupportedEncodingException {

        /*
         * DBOperator dbp = new DBOperator(); UserVo uvo = new UserVo();
         * uvo.setUid(UUID.randomUUID().toString()); uvo.setUname("test21");
         * uvo.setPwd(SSHAEncrypt.getInstance().getSSHAPassword("test111"));
         * PreferenceVo pfv = new PreferenceVo(); pfv.setType("preType");
         * pfv.setValue("preferenceValue"); List<PreferenceVo> pfvl = new
         * ArrayList<>(); pfvl.add(pfv); uvo.setPreference(pfvl); try {
         * dbp.insertUser(uvo); } catch (Exception ex) {
         * System.out.println("Create user failed."); } try { UserVo uvo1 =
         * dbp.queryUserInfo("test21"); System.out.println(uvo1.getUid());
         * System.out.println(uvo1.getUname());
         * System.out.println(uvo1.getPwd()); System.out.println("Is password
         * valid: " + SSHAEncrypt.getInstance().verifySHA(uvo1.getPwd(),
         * "test111")); Iterator iter = uvo1.getPreference().iterator(); while
         * (iter.hasNext()) { PreferenceVo pv = (PreferenceVo) iter.next();
         * System.out.println(pv.getType()); System.out.println(pv.getValue());
         * } } catch (Exception ex) {
         * Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null,
         * ex); }
         */


//        try {
//            Map<String, String> map = GoogleSearch.getSearchResult("apple sd");
//            Iterator it = map.keySet().iterator();
//            while (it.hasNext()) {
//                String key = it.next().toString();
//                System.out.println(key + " <==> "
//                        + map.get(key));
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null,
//                    ex);
//        } catch (JSONException ex) {
//            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null,
//                    ex);
//        }

        List<GoogleNaviVo> list = new ArrayList<>();
        String start = "brisbane, australia";
        String end = "sydney, australia";
        System.out.println(start + " <===> " + end);
        try {
            list =
                    GoogleNavigation.GoogleNavigation(start, end);
        } catch (IOException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getHtml_instructions() + " <===> "
                    + list.get(i).getLat() + " <===> " + list.get(i).getLng() + "<===>" + list.get(i).getDistance());
        }
        
    }
}
