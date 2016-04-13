package cdb.exp.main.ssmi;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StopWatch;

import cdb.common.lang.DateUtil;
import cdb.common.lang.ExceptionUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.ImageWUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.dal.file.DatasetProc;
import cdb.dal.file.SSMIFileDtProc;
import cdb.common.model.DenseMatrix;
import cdb.common.model.Location;
import cdb.common.model.Point;
import cdb.common.model.SparseMatrix;
import cdb.exp.main.ssmi.D1ParameterCmp.CmpThread;
import cdb.ml.anomaly.AnomalyDetection;
import cdb.ml.anomaly.SimpleAnomalyDetecion;

/**
 * 
 * @author Chao Chen
 * @version $Id: D3MultiThreadDetection.java, v 0.1 Oct 6, 2015 2:40:30 PM chench Exp $
 */
public class D3MultiThreadDetection extends AbstractArcticAnalysis {

    /** frequency identity*/
    protected final static String FREQNCY_ID = "n19v";

    /**
     * 
     * @param args
     */

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        case1();
        //        case2();
        stopWatch.stop();
        LoggerUtil.info(logger, "OVERALL TIME SPENDED: " + stopWatch.getTotalTimeMillis() / 1000.0);
    }

    @SuppressWarnings("unchecked")
    public static void case1() {
        // fetch related mean and sd files
        LoggerUtil.info(logger, "1. compute statistical parameter.");
        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };

        HashMap<String, DenseMatrix> meanRep = (HashMap<String, DenseMatrix>) SerializeUtil
            .readObject(ROOT_DIR + "Condensate/mean_" + FREQNCY_ID + ".OBJ");
        HashMap<String, DenseMatrix> sdRep = (HashMap<String, DenseMatrix>) SerializeUtil
            .readObject(ROOT_DIR + "Condensate/sd_" + FREQNCY_ID + ".OBJ");

        // task id lists
        Queue<String> taskIds = null;
        try {
            LoggerUtil.info(logger, "2. detect anomalies.");
            Date sDate = DateUtil.parse("19980101", DateUtil.SHORT_FORMAT);
            Date eDate = DateUtil.parse("20150101", DateUtil.SHORT_FORMAT);
            taskIds = testsetGroupByMonth(seasons, sDate, eDate);
        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Check date format.");
        }
        AnomalyThread.task = taskIds;

        //test locations
        DatasetProc dProc = new SSMIFileDtProc();
        int[] dimsns = dProc.dimensions(FREQNCY_ID);
        List<Location> locals = new ArrayList<Location>();
        for (int i = 0; i < dimsns[0]; i++) {
            for (int j = 0; j < dimsns[1]; j++) {
                locals.add(new Location(i, j));
            }
        }
        Location[] locs = locals.toArray(new Location[locals.size()]);

        //detecting
        try {
            CmpThread.task = taskIds;
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new AnomalyThread(meanRep, sdRep, locs, dProc));
            exec.execute(new AnomalyThread(meanRep, sdRep, locs, dProc));
            exec.execute(new AnomalyThread(meanRep, sdRep, locs, dProc));
            exec.execute(new AnomalyThread(meanRep, sdRep, locs, dProc));
            exec.shutdown();
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "ExecutorService await crush! ");
        } finally {
        }
    }

    public static void case2() {
        // fetch related mean and sd files
        LoggerUtil.info(logger, "1. compute statistical parameter.");
        String[] seasons = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                             "12" };
        Map<String, DenseMatrix> meanRep = new HashMap<String, DenseMatrix>();
        Map<String, DenseMatrix> sdRep = new HashMap<String, DenseMatrix>();
        cmpParams(seasons, meanRep, sdRep);
    }

    public static void cmpParams(String[] seasons, Map<String, DenseMatrix> meanRep,
                                 Map<String, DenseMatrix> sdRep) {
        // make regular files
        int seasonNum = seasons.length;
        String[] meanFiles = new String[seasonNum];
        String[] sdFiles = new String[seasonNum];
        for (int seasonIndx = 0; seasonIndx < seasonNum; seasonIndx++) {
            meanFiles[seasonIndx] = ROOT_DIR + "Condensate/mean_\\d{4}" + seasons[seasonIndx] + "_"
                                    + FREQNCY_ID + ".OBJ";
            sdFiles[seasonIndx] = ROOT_DIR + "Condensate/sd_\\d{4}" + seasons[seasonIndx] + "_"
                                  + FREQNCY_ID + ".OBJ";
        }

        // mean parameters
        for (int seasonIndx = 0; seasonIndx < seasonNum; seasonIndx++) {
            // read partial mean
            List<DenseMatrix> meanPartial = new ArrayList<DenseMatrix>();
            File[] files = FileUtil.parserFilesByPattern(meanFiles[seasonIndx]);
            for (File file : files) {
                meanPartial.add((DenseMatrix) SerializeUtil.readObject(file.getAbsolutePath()));
            }

            // compute mean
            meanRep.put(seasons[seasonIndx], StatisticParamUtil.mean(meanPartial));
        }
        SerializeUtil.writeObject(meanRep, ROOT_DIR + "Condensate/mean_" + FREQNCY_ID + ".OBJ");

        // standard deviation parameters
        for (int seasonIndx = 0; seasonIndx < seasonNum; seasonIndx++) {
            // read partial mean
            List<DenseMatrix> sdPartial = new ArrayList<DenseMatrix>();
            File[] files = FileUtil.parserFilesByPattern(sdFiles[seasonIndx]);
            for (File file : files) {
                sdPartial.add((DenseMatrix) SerializeUtil.readObject(file.getAbsolutePath()));
            }

            // compute mean
            sdRep.put(seasons[seasonIndx], StatisticParamUtil.mean(sdPartial));
        }
        SerializeUtil.writeObject(sdRep, ROOT_DIR + "Condensate/sd_" + FREQNCY_ID + ".OBJ");
    }

    public static class AnomalyThread extends Thread {

        private Map<String, DenseMatrix> meanRep;
        private Map<String, DenseMatrix> sdRep;
        private Location[]               locs;
        private DatasetProc              dProc = new SSMIFileDtProc();
        public static Queue<String>      task;

        public static synchronized String task() {
            return task.poll();
        }

        /**
         * @param meanRep
         * @param sdRep
         * @param locs
         * @param dProc
         */
        public AnomalyThread(Map<String, DenseMatrix> meanRep, Map<String, DenseMatrix> sdRep,
                             Location[] locs, DatasetProc dProc) {
            super();
            this.meanRep = meanRep;
            this.sdRep = sdRep;
            this.locs = locs;
            this.dProc = dProc;
        }

        /** 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            String taskId = null;
            while ((taskId = task()) != null) {
                LoggerUtil.info(logger, "Processing " + taskId);
                detectAnomalies(meanRep, sdRep, taskId, locs, dProc);
                //                SerializeUtil.writeObject(anomalies,
                //                    ROOT_DIR + "Anomaly/" + taskId + "_" + FREQNCY_ID + ".OBJ");
                //                ImageWUtil.plotGrayImage(anomalies,
                //                    ROOT_DIR + "Anomaly/" + taskId + "_" + FREQNCY_ID + ".jpg",
                //                    ImageWUtil.JPG_FORMMAT);

            }
        }

        protected SparseMatrix detectAnomalies(Map<String, DenseMatrix> meanRep,
                                               Map<String, DenseMatrix> sdRep, String taskId,
                                               Location[] locs, DatasetProc dProc) {
            String fileName = binFileConvntn(taskId, FREQNCY_ID);
            if (!FileUtil.exists(fileName)) {
                return null;
            }

            DenseMatrix tMatrix = dProc.read(fileName);
            SparseMatrix sMatrix = new SparseMatrix(tMatrix.getRowNum(), tMatrix.getColNum());

            int locSize = locs.length;
            Point[][] domains = new Point[locSize][1];
            for (int locIndx = 0; locIndx < locSize; locIndx++) {

                if (tMatrix == null) {
                    domains[locIndx][0] = null;
                    continue;
                }

                Location loc = locs[locIndx];
                domains[locIndx][0] = new Point(tMatrix.getVal(loc.x(), loc.y()));
            }

            // anomaly detection
            String season = taskId.substring(4, 6);
            for (int locIndx = 0; locIndx < locSize; locIndx++) {
                Location loc = locs[locIndx];

                AnomalyDetection detector = new SimpleAnomalyDetecion(
                    meanRep.get(season).getVal(loc.x(), loc.y()),
                    sdRep.get(season).getVal(loc.x(), loc.y()), 2.0);
                int[] anmlyIndx = detector.detect(domains[locIndx], 0, 0);
                if (anmlyIndx == null) {
                    continue;
                } else {
                    sMatrix.setValue(loc.x(), loc.y(), domains[locIndx][0].getValue(0));
                }
            }

            ImageWUtil.plotRGBImageWithMask(tMatrix,
                ROOT_DIR + "Anomaly/1990to1995/" + taskId + "_" + FREQNCY_ID + ".bmp", sMatrix,
                ImageWUtil.BMP_FORMAT);
            //            SerializeUtil.writeObject(sMatrix,
            //                ROOT_DIR + "StatisticAnomaly/" + taskId + "_" + FREQNCY_ID + ".OBJ");
            return sMatrix;
        }

    }

    public static Queue<String> testsetGroupByMonth(String[] seasons, Date sDate, Date eDate) {
        Queue<String> testSet = new LinkedList<String>();

        Date curDate = sDate;
        while (curDate.before(eDate)) {
            testSet.add(DateUtil.format(curDate, DateUtil.SHORT_FORMAT));
            //move to next day
            curDate.setTime(curDate.getTime() + 24 * 60 * 60 * 1000);
        }
        return testSet;
    }

}