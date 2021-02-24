package sample;



import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import eu.hansolo.medusa.Clock;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.scene.control.gauge.linear.SimpleMetroArcGauge;
import jssc.*;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller extends Main implements Initializable {

    public int intIn=60;

    @FXML
    private JFXButton btnCloseSerial;

    @FXML
    private SimpleMetroArcGauge gauge;

    @FXML
    private SimpleMetroArcGauge tempGauge;

    @FXML
    private SimpleMetroArcGauge pressGauge;

    @FXML
    private SimpleMetroArcGauge altGauge;

    @FXML
    private SimpleMetroArcGauge voltGauge;





    @FXML
    private JFXTextField altitude;

    @FXML
    private JFXTextField airPressure;

    @FXML
    private JFXTextField lon;

    @FXML
    private Clock clock;

    @FXML
    private Label connectedIdentifier;

    @FXML
    private Label disConnectedIdentifier;

    @FXML
    private JFXTextField humidity;

    @FXML
    private JFXTextField temperature;

    @FXML
    private JFXTextField lat;

    @FXML
    private JFXTextField textFieldOut;

    @FXML
    private JFXButton btnOpenSerial;

    @FXML
    private Label comboBoxPorts;

    @FXML
    private JFXTextField textFieldIn;

    @FXML
    private LineChart<Integer, Integer> graph;




    @FXML
    private JFXButton btnSend;

    @FXML
    private JFXButton searchButton;

    @FXML
    private WebView webView;
    private WebEngine engine;

    private String latitude="";
    private String longitude="";

    public int tempChartCount=0;
    public int humChartCount=0;


    public int[] tempArray = new int[5000];
    public int[] humArray = new int[5000];




//    public void tempChartSeriesSetName(){
//        series1.setName("Series 1");
//    }


    @FXML
    private JFXButton showMap;

    SerialPort serialPort;
    ObservableList<String> portList;


    @FXML
    public void connect(ActionEvent event) {
        closeSerialPort();              //close serial port before open
        if(openSerialPort()){
            btnSend.setDisable(false);
            System.out.println("Port Opened");
        }else{
            btnSend.setDisable(true);
        }
        disConnectedIdentifier.setVisible(false);
        connectedIdentifier.setVisible(true);

    }
    @FXML
    public void disconnect(ActionEvent event) {
        closeSerialPort();
        btnSend.setDisable(true);
        textFieldIn.setText("");
        connectedIdentifier.setVisible(false);
        disConnectedIdentifier.setVisible(true);
    }


    @FXML
    public void search(ActionEvent event) {
        detectPort();
        comboBoxPorts.setText(portList.get(1));  //Most Important Section. Needs to be changed after the work..
//        portList.get(0);
//        XYChart.Series series1 = new XYChart.Series<>();
//        series1.setName("Series 1");
//        series1.getData().add(new XYChart.Data<>(1, 20));
//        series1.getData().add(new XYChart.Data<>(1, 100));
//        series1.getData().add(new XYChart.Data<>(1, 80));
//        series1.getData().add(new XYChart.Data<>(4, 180));
//        series1.getData().add(new XYChart.Data<>(5, 20));
//        series1.getData().add(new XYChart.Data<>(6, -10));
//        graph.getData().add(series1);
    }

    public void openMap(javafx.event.ActionEvent actionEvent) {
        webView.setVisible(true);
        latitude=lat.getText();
        longitude=lon.getText();
        System.out.println(latitude);
        engine.load("https://www.google.com/maps/@23.7805,90.4078,19z");
    }

    @FXML
    public void sendData(ActionEvent event) {
        if(serialPort != null && serialPort.isOpened()){
            try {
                String stringOut = textFieldOut.getText();
                serialPort.writeBytes(stringOut.getBytes());
            } catch (SerialPortException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("Something wrong!");
        }
        textFieldOut.setText("");

    }

    private void detectPort() {

        portList = FXCollections.observableArrayList();

        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
            portList.add(name);
        }a
    }

    public void setPower(Double hum, Double temp, Double press, Double alt, Double volt){
        try {
            gauge.setValue(hum);
            tempGauge.setValue(temp);
            pressGauge.setValue(press);
            altGauge.setValue(alt);
            voltGauge.setValue(volt);
        }
        catch(Exception e){

        }

//        System.out.println(0);

    }



    @FXML
    void showChart(ActionEvent event) {
        XYChart.Series series1 = new XYChart.Series<>();
        series1.setName("Series 1");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Series 2");



        for(int i=0; i<tempArray.length; ++i) {
            if(tempArray[i]==0){
                break;
            }
            series1.getData().add(new XYChart.Data<>(i, tempArray[i]));
            series2.getData().add(new XYChart.Data<>(i, humArray[i]));


        }
        graph.getData().addAll(series1, series2);

    }


    private boolean openSerialPort() {
        boolean success = false;

//        if (comboBoxPorts.getValue() != null
//                && !comboBoxPorts.getValue().toString().isEmpty()) {
            try {
                serialPort = new SerialPort(comboBoxPorts.getText().toString());

                serialPort.openPort();
                serialPort.setParams(
                        SerialPort.BAUDRATE_115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.addEventListener(new Controller.MySerialPortEventListener());

                success = true;
            } catch (SerialPortException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
//        }
        return success;
    }

    private void closeSerialPort() {
        if (serialPort != null && serialPort.isOpened()) {
            try {
                serialPort.closePort();
            } catch (SerialPortException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        serialPort = null;
        System.out.println("Port Closed");
    }

    class MySerialPortEventListener implements SerialPortEventListener {


        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {


            if (serialPortEvent.isRXCHAR()) {
                try {
                    int byteCount = serialPortEvent.getEventValue();
                    byte bufferIn[] = serialPort.readBytes(byteCount);

                    String stringIn = "";
                    try {
                        stringIn = new String(bufferIn, "UTF-8");
                        textFieldIn.setText(stringIn+"23.7805,90.4078");
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String[] token=stringIn.split(",", 6);
                    Double humidityD=0.0;
                    Double temperatureD=0.0;
                    Double pressD=0.0;
                    Double altD=0.0;
                    Double voltD=0.0;




                    try {
                         humidityD = Double.parseDouble(token[0]);
                         temperatureD=Double.parseDouble(token[1]);
                         pressD=Double.parseDouble(token[2]);
                         altD=Double.parseDouble(token[3]);
                         voltD=Double.parseDouble(token[4]);


                         humidity.setText(token[0]);
                         temperature.setText(token[1]);
                         airPressure.setText(token[2]);
                         altitude.setText(token[3]);
                         lat.setText("23.7807493");
                         lon.setText("90.407697");
                        setPower(humidityD, temperatureD, pressD, altD, voltD);
                        int temp= (int) Math.round(temperatureD);
                        int hum= (int) Math.round(humidityD);

                        tempArray[tempChartCount]=temp;
                        humArray[humChartCount]=hum;
                        tempChartCount++;
                        humChartCount++;
                    }
                    catch(Exception e){
                        System.out.println("DIE DIE DIE");
                    }



                } catch (SerialPortException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine=webView.getEngine();
    }
}