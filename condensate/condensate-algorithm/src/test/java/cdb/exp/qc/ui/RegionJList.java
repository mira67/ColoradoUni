package cdb.exp.qc.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.StringUtil;
import cdb.common.model.Point;
import cdb.common.model.RegionAnomalyInfoVO;
import cdb.common.model.RegionInfoVO;
import cdb.ml.qc.QualityControllHelper;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionJList.java, v 0.1 Nov 3, 2015 10:58:59 AM chench Exp $
 */
public class RegionJList extends JList<String> {
    /**  default values*/
    private static final long         serialVersionUID = 1L;
    /** the list of anomalies objects*/
    private List<RegionAnomalyInfoVO> regnAnmlInfoArrs;

    public RegionJList(Vector<String> labelArr, String regnInfoRootDir, int fContriNum,
                       String[] labels) {
        super(labelArr);
        ((DefaultListCellRenderer) getCellRenderer()).setHorizontalAlignment(JLabel.CENTER);

        this.addMouseListener(new RegionMouseAdapter(regnInfoRootDir, fContriNum, labels));
    }

    /**
     * Setter method for property <tt>regnAnmlInfoArrs</tt>.
     * 
     * @param regnAnmlInfoArrs value to be assigned to property regnAnmlInfoArrs
     */
    public void setRegnAnmlInfoArrs(List<RegionAnomalyInfoVO> regnAnmlInfoArrs) {
        this.regnAnmlInfoArrs = regnAnmlInfoArrs;
    }

    protected class RegionMouseAdapter extends MouseAdapter {
        /**  the directory of region information files*/
        private String   regnInfoRootDir;
        /** the labels of each field*/
        private String[] labels;
        /** the number of field making the top contributions*/
        private int      fContriNum;

        /**
         * @param regnInfoRootDir
         */
        public RegionMouseAdapter(String regnInfoRootDir, int fContriNum, String[] labels) {
            super();
            this.regnInfoRootDir = regnInfoRootDir;
            this.fContriNum = fContriNum;
            this.labels = labels;
        }

        /** 
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }

            try {
                int selectedIndx = ((RegionJList) e.getSource()).getSelectedIndex();
                RegionAnomalyInfoVO one = regnAnmlInfoArrs.get(selectedIndx);
                int rRIndx = one.getX() / one.getWidth();
                int cRIndx = one.getY() / one.getHeight();

                Date anmlDate = DateUtil.parse(one.getDateStr(), DateUtil.SHORT_FORMAT);
                Calendar cal = Calendar.getInstance();
                cal.setTime(anmlDate);
                int sYear = cal.get(Calendar.YEAR);
                int sDayInYear = cal.get(Calendar.DAY_OF_YEAR);

                // find the top three maximum field
                int[] fIndices = StatisticParamUtil.findTopAbsMaxNum(one.getdPoint(), fContriNum,
                    labels.length);

                collectXYSeries(rRIndx, cRIndx, sYear, sDayInYear, fIndices);
            } catch (ParseException e1) {
                ExceptionUtil.caught(e1, "Date format error.");
            }
        }

        private void collectXYSeries(int rRIndx, int cRIndx, int sYear, int sDayInYear,
                                     int[] fIndices) throws ParseException {
            String fileName = "" + rRIndx + '_' + cRIndx;
            String[] lines = FileUtil.readLines(regnInfoRootDir + fileName);

            // group by year
            Map<Integer, XYSeries> fRep1 = new HashMap<Integer, XYSeries>();
            Map<Integer, XYSeries> fRep2 = new HashMap<Integer, XYSeries>();
            Map<Integer, XYSeries> fRep3 = new HashMap<Integer, XYSeries>();
            Calendar cal = Calendar.getInstance();
            for (String line : lines) {
                Point point = null;
                Date date = null;
                if (StringUtil.isEmpty(line)) {
                    // ignore
                    RegionInfoVO regnVO = RegionInfoVO.parseOf(line);
                    point = QualityControllHelper.make12Features(regnVO);
                    date = DateUtil.parse(regnVO.getDateStr(), DateUtil.SHORT_FORMAT);
                } else {
                    point = Point.parseOf(line);
                    date = DateUtil.parse(String.valueOf((int) point.getValue(12)),
                        DateUtil.SHORT_FORMAT);
                }

                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int daysInYear = cal.get(Calendar.DAY_OF_YEAR);

                fillRepInDaysFasion(fIndices[0], daysInYear, year, point, fRep1);
                fillRepInDaysFasion(fIndices[1], daysInYear, year, point, fRep2);
                fillRepInDaysFasion(fIndices[2], daysInYear, year, point, fRep3);
            }

            // transform to matrix
            XYSeriesCollection fData1 = fillDataset(fRep1, true, sYear);
            XYSeriesCollection fData2 = fillDataset(fRep2, true, sYear);
            XYSeriesCollection fData3 = fillDataset(fRep3, true, sYear);
            XYSeriesCollection[] fDatas = { fData1, fData2, fData3 };
            String[] titles = { labels[fIndices[0]], labels[fIndices[1]], labels[fIndices[2]] };

            RegionChartFrame regnChartFrame = new RegionChartFrame("Analysis Result", fDatas,
                titles, sDayInYear);
            RefineryUtilities.centerFrameOnScreen(regnChartFrame);
            regnChartFrame.setSize(1000, 600);
            regnChartFrame.setVisible(true);
        }

        private void fillRepInDaysFasion(int fieldIndx, int daysInYear, int year, Point point,
                                         Map<Integer, XYSeries> fRep) {
            XYSeries fXYSer = fRep.get(year);
            if (fXYSer == null) {
                fXYSer = new XYSeries(String.valueOf(year));
                fRep.put(year, fXYSer);
            }

            fXYSer.add(daysInYear, point.getValue(fieldIndx));
        }

        private XYSeriesCollection fillDataset(Map<Integer, XYSeries> fRep, boolean isSpecificYear,
                                               int sYear) {
            XYSeriesCollection fData = new XYSeriesCollection();
            if (isSpecificYear) {
                fData.addSeries(fRep.get(sYear));
            } else {
                for (XYSeries xySer : fRep.values()) {
                    fData.addSeries(xySer);
                }
            }
            return fData;
        }
    }
}
