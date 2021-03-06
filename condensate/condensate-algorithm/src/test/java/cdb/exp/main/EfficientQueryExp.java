package cdb.exp.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cdb.app.query.Position2D;
import cdb.app.query.QueryTask;
import cdb.common.lang.DistanceUtil;
import cdb.common.lang.FileUtil;
import cdb.common.lang.LoggerUtil;
import cdb.common.lang.SerializeUtil;
import cdb.common.lang.StatisticParamUtil;
import cdb.common.lang.VisualizationUtil;
import cdb.common.lang.log4j.LoggerDefineConstant;
import cdb.common.model.Cluster;
import cdb.common.model.DenseMatrix;
import cdb.common.model.Samples;
import cdb.dal.file.DatasetProc;
import cdb.dal.file.SSMIFileDtProc;
import cdb.ml.clustering.KMeansPlusPlusUtil;

/**
 * 
 * @author Chao Chen
 * @version $Id: EfficientQueryExp.java, v 0.1 Aug 17, 2015 10:42:00 AM chench Exp $
 */
public class EfficientQueryExp {

    /** the directory to store the serialized the object file*/
    public final static String  SERIALABLE_DIR = "C:/Users/chench/Desktop/SIDS/OBJ/";
    /** logger */
    private final static Logger logger         = Logger
        .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //        query();
        queryTest();
    }

    @SuppressWarnings("unchecked")
    public static void queryTest() {
        Map<String, DenseMatrix> meanInfo = (Map<String, DenseMatrix>) SerializeUtil
            .readObject(SERIALABLE_DIR + "meanInfo.obj");
        Map<String, DenseMatrix> sdInfo = (Map<String, DenseMatrix>) SerializeUtil
            .readObject(SERIALABLE_DIR + "sdInfo.obj");

        //        int p = 1;
        //        for (String key : meanInfo.) {
        //            DenseIntMatrix mean = meanInfo.get(key);
        //            MatrixFileUtil.gnuHeatmap(mean, SERIALABLE_DIR + "mean_" + p++);
        //        }
        QueryTask queryTask = new QueryTask();
        dectectPossibleQueryTask(1350, 1300, 5, meanInfo.get("tb_f17_20140804_v4_s19h.bin"),
            sdInfo.get("tb_f17_20140804_v4_s19h.bin"), queryTask);
    }

    public static void dectectPossibleQueryTask(int upperBound, int lowerBound, int tol,
                                                DenseMatrix meanInfoMatrix,
                                                DenseMatrix sdInfoMatrix, QueryTask queryTask) {
        int rowNum = meanInfoMatrix.getRowNum();
        int colNum = meanInfoMatrix.getColNum();
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                double mean = meanInfoMatrix.getVal(row, col);
                double sd = sdInfoMatrix.getVal(row, col);

                // detect possible query domain 
                if ((lowerBound <= (mean - tol * sd)) && (upperBound >= (mean + tol * sd))) {
                    queryTask.add2CompleteDomain(row, col);
                } else if ((lowerBound < (mean - tol * sd)) && (upperBound > (mean - tol * sd))) {
                    queryTask.add2QueryDomain(row, col);
                } else if ((lowerBound > (mean - tol * sd)) && (upperBound < (mean + tol * sd))) {
                    queryTask.add2QueryDomain(row, col);
                } else if ((lowerBound < (mean + tol * sd)) && (upperBound > (mean + tol * sd))) {
                    queryTask.add2QueryDomain(row, col);
                }

            }
        }

        //visualize the query domain
        DenseMatrix queryInfo = new DenseMatrix(rowNum, colNum);
        for (Position2D p : queryTask.getQueryDomain()) {
            queryInfo.setVal(p.x, p.y, 300);
        }
        for (Position2D p : queryTask.getCompleteDomain()) {
            queryInfo.setVal(p.x, p.y, 800);
        }

        LoggerUtil.info(logger,
            "Query: " + (queryTask.getQueryDomain().size() * 1.0 / (rowNum * colNum)) + "\tDone: "
                                + (queryTask.getCompleteDomain().size() * 1.0 / (rowNum * colNum)));
        VisualizationUtil.gnuHeatmap(queryInfo, SERIALABLE_DIR + "hmp");
    }

    public static void query() {
        // loading dataset
        String filePattern = "C:/Users/chench/Desktop/SIDS/2014/tb_f17_\\d{8}_v4_s19h.bin";
        List<DenseMatrix> seralData = new ArrayList<DenseMatrix>();
        List<String> fileAssigmnt = new ArrayList<String>();
        loadingDatasetStep(filePattern, seralData, fileAssigmnt);
        LoggerUtil.info(logger, "1. dataset is loaded. Count: " + seralData.size());

        // transforming dataset
        Samples dataSample = new Samples(seralData.size(), 332 + 316);
        tranformingDataStep(seralData, dataSample);
        LoggerUtil.info(logger, "2. dataset is transformed. ");

        // clustering
        int clusterNum = 12;
        int maxIter = 15;
        Cluster[] clusteringInfo = clusteringStep(dataSample, clusterNum, maxIter);
        LoggerUtil.info(logger, "3. dataset is clustered.");
        if (logger.isDebugEnabled()) {
            StringBuilder detailClusteringInfo = new StringBuilder();
            int clusterCount = 1;
            for (Cluster cluster : clusteringInfo) {
                detailClusteringInfo.append("\nCluster: ").append(clusterCount++).append('\n');

                int pCount = 1;
                for (int pIndex : cluster) {
                    detailClusteringInfo.append(pIndex).append(',');
                    if (pCount % 5 == 0) {
                        detailClusteringInfo.append('\n');
                    }
                    pCount++;
                }
            }
            LoggerUtil.debug(logger, detailClusteringInfo.toString());
        }

        // constructing query path
        Map<String, DenseMatrix> meanInfo = new HashMap<String, DenseMatrix>();
        Map<String, DenseMatrix> sdInfo = new HashMap<String, DenseMatrix>();
        constuctingQueryPathStep(seralData, clusteringInfo, fileAssigmnt, meanInfo, sdInfo);
        LoggerUtil.info(logger, "4. Query path is constructed.");

        // serializing resulting paths
        SerializeUtil.writeObject(meanInfo, SERIALABLE_DIR + "meanInfo.obj");
        SerializeUtil.writeObject(sdInfo, SERIALABLE_DIR + "sdInfo.obj");
        LoggerUtil.info(logger, "5. Query path is serialized.");
    }

    public static void loadingDatasetStep(String filePattern, List<DenseMatrix> seralData,
                                          List<String> fileAssigmnt) {
        // load data
        File[] dFiles = FileUtil.parserFilesByPattern(filePattern);
        DatasetProc dProc = new SSMIFileDtProc();
        for (File file : dFiles) {
            seralData.add(dProc.read(file.getAbsolutePath()));
            fileAssigmnt.add(file.getName());
        }
    }

    public static void tranformingDataStep(List<DenseMatrix> seralData, Samples dataSample) {
        for (int t = 0; t < seralData.size(); t++) {
            DenseMatrix datapoint = seralData.get(t);

            int p = 0;
            // compute row average
            int rowNum = datapoint.getRowNum();
            for (int r = 0; r < rowNum; r++) {
                dataSample.setValue(t, p, datapoint.rowAvg(r));
                p++;
            }

            // compute column average
            int colNum = datapoint.getColNum();
            for (int c = 0; c < colNum; c++) {
                dataSample.setValue(t, p, datapoint.colAvg(c));
                p++;
            }

        }
    }

    public static Cluster[] clusteringStep(Samples dataSample, int k, int maxIteration) {
        // clustering the data points
        return KMeansPlusPlusUtil.cluster(dataSample, k, maxIteration,
            DistanceUtil.SQUARE_EUCLIDEAN_DISTANCE);

    }

    public static void constuctingQueryPathStep(List<DenseMatrix> seralData, Cluster[] result,
                                                List<String> fileAssigmnt,
                                                Map<String, DenseMatrix> meanInfo,
                                                Map<String, DenseMatrix> sdInfo) {
        // constructing merged-layer
        for (Cluster cluster : result) {
            DenseMatrix centroid = StatisticParamUtil.meanInOneCluster(seralData, cluster);
            DenseMatrix sd = StatisticParamUtil.sdInOneCluster(seralData, cluster, centroid);
            for (int seq : cluster) {
                String fileName = fileAssigmnt.get(seq);
                meanInfo.put(fileName, centroid);
                sdInfo.put(fileName, sd);
            }
        }
    }

}
