package cdb.dal.dao;

import cdb.dal.model.Regiondesc;
import java.sql.SQLException;

public interface RegiondescDAO {

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    int deleteByPrimaryKey(Integer id) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    void insert(Regiondesc record) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    void insertSelective(Regiondesc record) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    Regiondesc selectByPrimaryKey(Integer id) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    int updateByPrimaryKeySelective(Regiondesc record) throws SQLException;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method corresponds to the database table regiondesc
     *
     * @ibatorgenerated Thu Nov 05 16:46:02 MST 2015
     */
    int updateByPrimaryKey(Regiondesc record) throws SQLException;
}