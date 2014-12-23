/*
 Copyright 2014 Peter-Josef Meisch (pj.meisch@sothawo.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.sothawo.mapjfx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Showcase application.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class Showcase extends Application {
// ------------------------------ FIELDS ------------------------------

    private static final Logger logger = Logger.getLogger(Showcase.class.getCanonicalName());

    /** some coordinates from around town */
    private static final Coordinate coordKarlsruheCastle = new Coordinate(49.013517, 8.404435);
    private static final Coordinate coordKarlsruheHarbour = new Coordinate(49.015511, 8.323497);
    private static final Coordinate coordKarlsruheStation = new Coordinate(48.993284, 8.402186);

    /** the MapView */
    private MapView mapView;

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("starting showcase...");
        BorderPane borderPane = new BorderPane();

        // MapView in the center with an initial coordinate (optional)
        // the MapView is created first as the other elements reference it
        mapView = new MapView(coordKarlsruheHarbour);
        // animate pan and zoom with 500ms
        mapView.setAnimationDuration(500);
        borderPane.setCenter(mapView);

        // at the top some buttons with coordinates
        Pane topPane = createTopPane();
        borderPane.setTop(topPane);

        // at the bottom some infos
        borderPane.setBottom(createBottomPane());

        // add listener for mapView initialization state
        mapView.initializedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    topPane.setDisable(false);
                }
            }
        });

        // now initialize the mapView
        mapView.initialize();

        // show the whole thing
        Scene scene = new Scene(borderPane, 800, 600);

        primaryStage.setTitle("sothawo mapjfx showcase");
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.finer(() -> "application started.");
    }

    /**
     * creates the top pane with the different location buttons.
     *
     * @return Pane
     */
    private Pane createTopPane() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setSpacing(5);

        Button btn = new Button();
        btn.setText("Karlsruhe castle");
        btn.setOnAction(event -> mapView.setCenter(coordKarlsruheCastle));
        hbox.getChildren().add(btn);

        btn = new Button();
        btn.setText("Karlsruhe harbour");
        btn.setOnAction(event -> mapView.setCenter(coordKarlsruheHarbour));
        hbox.getChildren().add(btn);

        btn = new Button();
        btn.setText("Karlsruhe station");
        btn.setOnAction(event -> mapView.setCenter(coordKarlsruheStation));
        hbox.getChildren().add(btn);

        btn = new Button();
        btn.setText("zoom 16");
        btn.setOnAction(event -> mapView.setZoom(16));
        hbox.getChildren().add(btn);

        Slider slider = new Slider(MapView.MIN_ZOOM, MapView.MAX_ZOOM, MapView.INITIAL_ZOOM);
        slider.setBlockIncrement(1);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.setMajorTickUnit(MapView.MAX_ZOOM / 4);
        slider.setMinorTickCount((MapView.MAX_ZOOM / 4) - 1);
        slider.valueProperty().bindBidirectional(mapView.zoomProperty());
        slider.setSnapToTicks(true);
        HBox.setHgrow(slider, Priority.ALWAYS);
        hbox.getChildren().add(slider);

        hbox.setDisable(true);
        return hbox;
    }

    /**
     * creates the bottom pane with status labels.
     *
     * @return Pane
     */
    private Pane createBottomPane() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setSpacing(10);

        // label for showing the map's center
        Label labelCenter = new Label();
        hbox.getChildren().add(labelCenter);
        // add an observer for the map's center property to adjust the corresponding label
        mapView.centerProperty().addListener(new ChangeListener<Coordinate>() {
            @Override
            public void changed(ObservableValue<? extends Coordinate> observable, Coordinate oldValue,
                                Coordinate newValue) {
                labelCenter.setText(newValue == null ? "" : ("center: " + newValue.toString()));
            }
        });

        // label for showing the map's zoom
        Label labelZoom = new Label();
        hbox.getChildren().add(labelZoom);
        // add an observer to adjust the label
        mapView.zoomProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                labelZoom.setText(null == newValue ? "" : ("zoom: " + newValue.toString()));
            }
        });
        return hbox;
    }
// --------------------------- main() method ---------------------------

    public static void main(String[] args) {
        // init the logging from the classpath logging.properties
        InputStream inputStream = Showcase.class.getResourceAsStream("/logging.properties");
        if (null != inputStream) {
            try {
                LogManager.getLogManager().readConfiguration(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        launch(args);
    }
}
