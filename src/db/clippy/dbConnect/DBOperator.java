/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.dbConnect;

import db.clippy.Vo.PreferenceVo;
import db.clippy.Vo.UserVo;
import db.clippy.utils.SSHAEncrypt;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Ray
 */
public class DBOperator {
    
    private Connection getConnection() {
        Connection conn = DBConnection.getInstance().getConnect();

        return conn;
    }

    /**
     * Lookup user information by using username
     * @param userName
     * @return 
     * @throws SQLException 
     */
    public UserVo queryUserInfo(String userName) throws SQLException {
        UserVo uvo = new UserVo();
        String uuid = "";       // User's unique ID
        String pwd = "";        // user's password
        List<PreferenceVo> preference = new ArrayList<>();
        // get uuid, username, password form userdb Table
        String sql_uin = "SELECT userdb.uid, userdb.username, userdb.password FROM userdb WHERE username = ?";
        // get preference type, perference configration from userinfo Table
        String sql_pre = "SELECT userinfo.preference_type, userinfo.preference FROM userinfo WHERE userinfo.uid = ?";
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        PreparedStatement stmt1 = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql_uin);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                uuid = rs.getString("uid");
                pwd = rs.getString("password");
            }
            // query user preferences
            if (!"".equals(uuid)) {
                PreferenceVo prevo;
                stmt1 = conn.prepareStatement(sql_pre);
                stmt1.setString(1, uuid);
                rs = stmt1.executeQuery();
                String pre_ty, pre_val;
                while (rs.next()) {
                    pre_ty = rs.getString("preference_type");
                    pre_val = rs.getString("preference");
                    prevo = new PreferenceVo();
                    prevo.setType(pre_ty);
                    prevo.setValue(pre_val);
                    preference.add(prevo);
                }
            }
        } finally {
            if (null != rs) {
                rs.close();
            }
            stmt.close();
            stmt1.close();
            conn.close();
        }

        // Set value to user VO
        uvo.setUid(uuid);
        uvo.setUname(userName);
        uvo.setPwd(pwd);
        uvo.setPreference(preference);

        return uvo;
    }

    /**
     * insert a new user's information into database
     * @param uvo
     */
    public void insertUser(UserVo uvo) throws SQLException {

        String UUID = uvo.getUid();
        String uName = uvo.getUname();
        String pwd = SSHAEncrypt.getInstance().getSSHAPassword(uvo.getPwd());
        List<PreferenceVo> preference = uvo.getPreference();
        
        Connection conn = getConnection();

        PreparedStatement stmt = null;
        PreparedStatement stmt1 = null;
        try {
            // set autocommit off to manual control transaction
            conn.setAutoCommit(false);
            String sql = "INSERT INTO USERDB VALUES(?, ?, ?)";
            String sql1 = "INSERT INTO USERINFO VALUES(?, ?, ?)";
            // transaction #1
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, UUID);
            stmt.setString(2, uName);
            stmt.setString(3, pwd);
            stmt.execute();
            // transaction #2
            stmt1 = conn.prepareStatement(sql1);
            Iterator iter = preference.iterator();
            PreferenceVo preVo;
            while (iter.hasNext()) {
                preVo = (PreferenceVo) iter.next();
                stmt1.setString(1, UUID);
                stmt1.setString(2, preVo.getType());
                stmt1.setString(3, preVo.getValue());
                stmt1.execute();
            }

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            // operation failed
            conn.rollback();
        } finally {
            stmt.close();
            stmt1.close();
            conn.close();
        }

    }

    /**
     * update user's information
     * @param uvo
     */
    public void updateUser(UserVo uvo) throws SQLException {
        removeUserById(uvo);
        insertUser(uvo);
    }
    
    /**
     * Check user's login information
     * @param uvo   must contain username and password
     * @return true: login success, false: login fail 
     * @throws SQLException
     * @throws NoSuchAlgorithmException 
     */
    public boolean verifyLogin(UserVo uvo) throws SQLException, NoSuchAlgorithmException {
        boolean flag = false;
        UserVo uv = queryUserInfo(uvo.getUname());
        String storedPwd = uv.getPwd();
        String inputPwd = uvo.getPwd();
        flag = SSHAEncrypt.getInstance().verifySHA(storedPwd, inputPwd);
        return flag;
    }

    /**
     * Remove a user by uuid
     * @param uvo
     */
    public void removeUserById(UserVo uvo) throws SQLException {

        String uuid = uvo.getUid();

        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;

        String sql_usr = "DELETE FROM userdb WHERE userdb.uid = ?";
        String sql_uinfo = "DELETE FROM userInfo WHERE userInfo.uid = ?";
        try {
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql_usr);
            pstmt.setString(1, uuid);
            pstmt.executeUpdate();
            
            pstmt1 = conn.prepareStatement(sql_uinfo);
            pstmt1.setString(1, uuid);
            pstmt1.executeUpdate();
            
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            conn.rollback();
        } finally {
            pstmt.close();
            pstmt1.close();
            conn.close();
        }

    }
}
