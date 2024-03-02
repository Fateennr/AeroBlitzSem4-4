module com.example.aeroblitz {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
<<<<<<< HEAD
    requires javafx.media;
=======
>>>>>>> 1709435 (ggs)


    opens com.example.aeroblitz to javafx.fxml;
    exports com.example.aeroblitz;
}

//module com.example.aeroblitz {
//        requires javafx.controls;
//        requires javafx.fxml;
//        requires java.datatransfer;
//        requires java.desktop;
//
//        // Specify the exact module for java.awt
//        requires java.desktop;
//
//        opens com.example.aeroblitz to javafx.fxml;
//        exports com.example.aeroblitz;
//        }
