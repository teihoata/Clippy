/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import db.clippy.Vo.PreferenceVo;
import db.clippy.Vo.UserVo;
import db.clippy.dbConnect.DBOperator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ray
 */
public class TestMain {
    public static void main(String[] args) {
        DBOperator dbp = new DBOperator();
        UserVo uvo = new UserVo();
        uvo.setUid(UUID.randomUUID().toString());
        uvo.setUname("Marcus");
        uvo.setPwd("teihoata");
        PreferenceVo pfv = new PreferenceVo();
        pfv.setType("preType");
        pfv.setValue("preferenceValue");
        List<PreferenceVo> pfvl = new ArrayList<>();
        pfvl.add(pfv);
        uvo.setPreference(pfvl);
        dbp.insertUser(uvo);
        try {
            UserVo uvo1 = dbp.queryUserInfo("test11");
            System.out.println(uvo1.getUid());
            System.out.println(uvo1.getUname());
            System.out.println(uvo1.getPwd());
            Iterator iter = uvo1.getPreference().iterator();
            while (iter.hasNext()) {
                PreferenceVo pv = (PreferenceVo) iter.next();
                System.out.println(pv.getType());
                System.out.println(pv.getValue());
            }
            uvo1 = dbp.queryUserInfo("Marcus");
            System.out.println(uvo1.getUid());
            System.out.println(uvo1.getUname());
            System.out.println(uvo1.getPwd());
            iter = uvo1.getPreference().iterator();
            while (iter.hasNext()) {
                PreferenceVo pv = (PreferenceVo) iter.next();
                System.out.println(pv.getType());
                System.out.println(pv.getValue());
            }
        } catch (SQLException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
