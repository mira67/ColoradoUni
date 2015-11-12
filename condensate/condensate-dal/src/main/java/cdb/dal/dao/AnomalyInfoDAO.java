package cdb.dal.dao;

import cdb.dal.model.AnomalyInfoBean;
import java.sql.SQLException;
import java.util.List;

public interface AnomalyInfoDAO {

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    int deleteByPrimaryKey(Integer id) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    void insert(AnomalyInfoBean record) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    void insertSelective(AnomalyInfoBean record) throws SQLException;
    
    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    void insertSelectiveArr(List<AnomalyInfoBean> records) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    AnomalyInfoBean selectByPrimaryKey(Integer id) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    int updateByPrimaryKeySelective(AnomalyInfoBean record) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    int updateByPrimaryKeyWithBLOBs(AnomalyInfoBean record) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table anomalyinfo
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    int updateByPrimaryKeyWithoutBLOBs(AnomalyInfoBean record) throws SQLException;
}