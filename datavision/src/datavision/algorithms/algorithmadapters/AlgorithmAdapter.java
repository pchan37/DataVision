package datavision.algorithms.algorithmadapters;

import datavision.algorithms.Algorithm;
import datavision.algorithms.DataSet;
import datavision.api.DataAPI;
import datavision.api.PlotAPI;
import datavision.core.AppUI;
import datavision.algorithms.algorithmconfig.AlgorithmConfigurationDialog;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import vision.core.UITemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AlgorithmAdapter {

    protected PlotAPI plotAPI;
    protected DataAPI dataAPI;
    protected LineChart<Number, Number> chart;
    protected Algorithm algorithm;

    protected final ReentrantLock lock;
    protected final Condition notFull;
    protected final Condition notEmpty;
    protected CountDownLatch latch;
    protected boolean stillRunning;

    public AlgorithmAdapter() {
        lock = null;
        notFull = null;
        notEmpty = null;
    }

    public AlgorithmAdapter(PlotAPI plotAPI, DataAPI dataAPI) {
        this.plotAPI = plotAPI;
        this.dataAPI = dataAPI;

        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public abstract void plot();

    public abstract void createAlgorithm(String algorithmType, String algorithmName, DataSet dataSet, AlgorithmConfigurationDialog algorithmConfigurationDialog);

    public void runAlgorithm() throws InterruptedException {
        Thread producer = getProducer();
        Thread consumer = getConsumer();

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }

    protected Thread getProducer() {
        return new Thread(() -> {
            lock.lock();
            stillRunning = true;
            try {
                if (stillRunning) {
                    while (dataNotTaken()) {
                        notFull.await();
                    }
                    if (!algorithm.canContinue()) {
                        stillRunning = false;
                    }
                    algorithm.runUpdateInterval();
                    setData();
                    notEmpty.signal();
                }
            } catch (InterruptedException ex) {

            } finally {
                lock.unlock();
            }
        });
    }

    protected Thread getConsumer() {
        return new Thread(() -> {
            lock.lock();
            try {
                if (stillRunning || dataNotTaken()) {
                    while (dataNotReady()) {
                        notEmpty.await();
                    }
                    latch = new CountDownLatch(1);
                    Platform.runLater(() -> {
                        if (shouldPlotTextAreaData()) {
                            plotAPI.plotTextAreaData();
                        }
                        plot();
                        latch.countDown();
                    });
                    Thread.sleep(1000);
                    latch.await();
                    notFull.signal();
                }
            } catch (InterruptedException ex) {

            } finally {
                lock.unlock();
            }
        });
    }

    protected abstract boolean dataNotTaken();

    protected abstract boolean dataNotReady();

    protected abstract void setData();

    public abstract boolean shouldPlotTextAreaData();

    public abstract boolean isValidForDataSet();

    public boolean toContinue() {
        return algorithm.canContinue();
    }

}
