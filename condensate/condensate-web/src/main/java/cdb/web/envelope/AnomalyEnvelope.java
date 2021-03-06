package cdb.web.envelope;

import java.util.Date;

import cdb.common.lang.DateUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: AnomalyEnvelope.java, v 0.1 Nov 9, 2015 3:27:16 PM chench Exp $
 */
public class AnomalyEnvelope {
    /** the name of the date set*/
    private String dsName;
    /** the frequency of date set*/
    private String dsFreq;
    /** the date to start*/
    private Date   sDate;
    /** the date to end*/
    private Date   eDate;
    /** the month to start*/
    private int    sMonth;
    /** the month to end*/
    private int    eMonth;
    /** the year to start*/
    private int    sYear;
    /** the year to end*/
    private int    eYear;

    /**
     * Getter method for property <tt>dsName</tt>.
     * 
     * @return property value of dsName
     */
    public String getDsName() {
        return dsName;
    }

    /**
     * Setter method for property <tt>dsName</tt>.
     * 
     * @param dsName value to be assigned to property dsName
     */
    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    /**
     * Getter method for property <tt>dsFreq</tt>.
     * 
     * @return property value of dsFreq
     */
    public String getDsFreq() {
        return dsFreq;
    }

    /**
     * Setter method for property <tt>dsFreq</tt>.
     * 
     * @param dsFreq value to be assigned to property dsFreq
     */
    public void setDsFreq(String dsFreq) {
        this.dsFreq = dsFreq;
    }

    /**
     * Getter method for property <tt>sDate</tt>.
     * 
     * @return property value of sDate
     */
    public Date getsDate() {
        return sDate;
    }

    /**
     * Setter method for property <tt>sDate</tt>.
     * 
     * @param sDate value to be assigned to property sDate
     */
    public void setsDate(Date sDate) {
        this.sDate = sDate;
    }

    /**
     * Getter method for property <tt>eDate</tt>.
     * 
     * @return property value of eDate
     */
    public Date geteDate() {
        return eDate;
    }

    /**
     * Setter method for property <tt>eDate</tt>.
     * 
     * @param eDate value to be assigned to property eDate
     */
    public void seteDate(Date eDate) {
        this.eDate = eDate;
    }

    /**
     * Getter method for property <tt>sMonth</tt>.
     * 
     * @return property value of sMonth
     */
    public int getsMonth() {
        return sMonth;
    }

    /**
     * Setter method for property <tt>sMonth</tt>.
     * 
     * @param sMonth value to be assigned to property sMonth
     */
    public void setsMonth(int sMonth) {
        this.sMonth = sMonth;
    }

    /**
     * Getter method for property <tt>eMonth</tt>.
     * 
     * @return property value of eMonth
     */
    public int geteMonth() {
        return eMonth;
    }

    /**
     * Setter method for property <tt>eMonth</tt>.
     * 
     * @param eMonth value to be assigned to property eMonth
     */
    public void seteMonth(int eMonth) {
        this.eMonth = eMonth;
    }

    /**
     * Getter method for property <tt>sYear</tt>.
     * 
     * @return property value of sYear
     */
    public int getsYear() {
        return sYear;
    }

    /**
     * Setter method for property <tt>sYear</tt>.
     * 
     * @param sYear value to be assigned to property sYear
     */
    public void setsYear(int sYear) {
        this.sYear = sYear;
    }

    /**
     * Getter method for property <tt>eYear</tt>.
     * 
     * @return property value of eYear
     */
    public int geteYear() {
        return eYear;
    }

    /**
     * Setter method for property <tt>eYear</tt>.
     * 
     * @param eYear value to be assigned to property eYear
     */
    public void seteYear(int eYear) {
        this.eYear = eYear;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (sDate != null && eDate != null) {
            return "AnomalyEnvelope [dsName=" + dsName + ", dsFreq=" + dsFreq + ", sDate="
                   + DateUtil.format(sDate, DateUtil.WEB_FORMAT) + ", eDate="
                   + DateUtil.format(eDate, DateUtil.WEB_FORMAT) + "]";
        } else {
            return "AnomalyEnvelope [dsName=" + dsName + ", dsFreq=" + dsFreq + ", sMonth=" + sMonth
                   + ", eMonth=" + eMonth + ", sYear=" + sYear + ", eYear=" + eYear + "]";
        }
    }

}
