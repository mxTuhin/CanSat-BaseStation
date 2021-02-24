import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class JavaFX_jSSC extends Application {

    SerialPort serialPort;
    ObservableList<String> portList;

    ComboBox comboBoxPorts;
    TextField textFieldOut, textFieldIn;
    Button btnOpenSerial, btnCloseSerial, btnSend;

    private void detectPort() {

        portList = FXCollections.observableArrayList();

        String[] serialPortNames = SerialPortList.getPortNames();
        for (String name : serialPortNames) {
            System.out.println(name);
            portList.add(name);
        }
    }

    @Override
    public void start(Stage primaryStage) {

        detectPort();

        comboBoxPorts = new ComboBox(portList);
        textFieldOut = new TextField();
        textFieldIn = new TextField();

        btnOpenSerial = new Button("Open Serial Port");
        btnCloseSerial = new Button("Close Serial Port");
        btnSend = new Button("Send");
        btnSend.setDisable(true);   //default disable before serial port open

        btnOpenSerial.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                closeSerialPort();              //close serial port before open
                if(openSerialPort()){
                    btnSend.setDisable(false);
                }else{
                    btnSend.setDisable(true);
                }
            }
        });

        btnCloseSerial.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                closeSerialPort();
                btnSend.setDisable(true);
            }
        });

        btnSend.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {

                if(serialPort != null && serialPort.isOpened()){
                    try {
                        String stringOut = textFieldOut.getText();
                        serialPort.writeBytes(stringOut.getBytes());
                    } catch (SerialPortException ex) {
                        Logger.getLogger(JavaFX_jSSC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    System.out.println("Something wrong!");
                }
            }
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                comboBoxPorts,
                textFieldOut,
                textFieldIn,
                btnOpenSerial,
                btnCloseSerial,
                btnSend);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                closeSerialPort();
            }
        });

        primaryStage.setTitle("CanSat Base Station!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private boolean openSerialPort() {
        boolean success = false;

        if (comboBoxPorts.getValue() != null
                && !comboBoxPorts.getValue().toString().isEmpty()) {
            try {
                serialPort = new SerialPort(comboBoxPorts.getValue().toString());

                serialPort.openPort();
                serialPort.setParams(
                        SerialPort.BAUDRATE_57600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.addEventListener(new MySerialPortEventListener());

                success = true;
            } catch (SerialPortException ex) {
                Logger.getLogger(JavaFX_jSSC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return success;
    }

    private void closeSerialPort() {
        if (serialPort != null && serialPort.isOpened()) {
            try {
                serialPort.closePort();
            } catch (SerialPortException ex) {
                Logger.getLogger(JavaFX_jSSC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        serialPort = null;
    }

    class MySerialPortEventListener implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {

            if(serialPortEvent.isRXCHAR()){
                try {
                    int byteCount = serialPortEvent.getEventValue();
                    byte bufferIn[] = serialPort.readBytes(byteCount);

                    String stringIn = "";
                    try {
                        stringIn = new String(bufferIn, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(JavaFX_jSSC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    textFieldIn.setText(stringIn);

                } catch (SerialPortException ex) {
                    Logger.getLogger(JavaFX_jSSC.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }
}
