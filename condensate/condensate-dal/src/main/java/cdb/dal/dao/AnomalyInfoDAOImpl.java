package cdb.dal.dao;

import cdb.dal.model.AnomalyInfoBean;
import com.ibatis.sqlmap.client.SqlMapClient;
import java.sql.SQLException;
import java.util.List;

public class AnomalyInfoDAOImpl implements AnomalyInfoDAO {
    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    private SqlMapClient sqlMapClient;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public AnomalyInfoDAOImpl(SqlMapClient sqlMapClient) {
        super();
        this.sqlMapClient = sqlMapClient;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public int deleteByPrimaryKey(Integer id) throws SQLException {
        AnomalyInfoBean key = new AnomalyInfoBean();
        key.setId(id);
        int rows = sqlMapClient.delete("anomalyinfo.ibatorgenerated_deleteByPrimaryKey", key);
        return rows;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public void insert(AnomalyInfoBean record) throws SQLException {
        sqlMapClient.insert("anomalyinfo.ibatorgenerated_insert", record);
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public void insertSelective(AnomalyInfoBean record) throws SQLException {
        sqlMapClient.insert("anomalyinfo.ibatorgenerated_insertSelective", record);
    }

    /** 
     * @see cdb.dal.dao.AnomalyInfoDAO#insertSelectiveArr(java.util.List)
     */
    @Override
    public void insertSelectiveArr(List<AnomalyInfoBean> records) throws SQLException {
        sqlMapClient.insert("anomalyinfo.ibatorgenerated_insertSelectiveArr", records);
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public AnomalyInfoBean selectByPrimaryKey(Integer id) throws SQLException {
        AnomalyInfoBean key = new AnomalyInfoBean();
        key.setId(id);
        AnomalyInfoBean record = (AnomalyInfoBean) sqlMapClient
            .queryForObject("anomalyinfo.ibatorgenerated_selectByPrimaryKey", key);
        return record;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public int updateByPrimaryKeySelective(AnomalyInfoBean record) throws SQLException {
        int rows = sqlMapClient.update("anomalyinfo.ibatorgenerated_updateByPrimaryKeySelective",
            record);
        return rows;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public int updateByPrimaryKeyWithBLOBs(AnomalyInfoBean record) throws SQLException {
        int rows = sqlMapClient.update("anomalyinfo.ibatorgenerated_updateByPrimaryKeyWithBLOBs",
            record);
        return rows;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    public int updateByPrimaryKeyWithoutBLOBs(AnomalyInfoBean record) throws SQLException {
        int rows = sqlMapClient.update("anomalyinfo.ibatorgenerated_updateByPrimaryKey", record);
        return rows;
    }

}