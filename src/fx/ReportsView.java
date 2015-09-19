package fx;

import entity.info.Resident;
import entity.reports.NReportTools;
import gui.FXTools;
import gui.interfaces.Cleanable;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import op.tools.SYSCalendar;
import org.joda.time.LocalDate;
import org.joda.time.MutableInterval;
import org.joda.time.YearMonth;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by tloehr on 17.09.15.
 */
public class ReportsView implements Cleanable {

    private final Resident resident;
    private Map<LocalDate, String> holidays;
    private MutableInterval minmax = null;
    private LocalDate min = null;
    private LocalDate max = null;
    private HashSet<LocalDate> yearDividers, monthDividers; // just to remind, if a new divider is necessary
    private VBox root;
    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");

    public ReportsView(Resident resident) {
        this.resident = resident;
        root = new VBox();
        yearDividers = new HashSet<>();
        monthDividers = new HashSet<>();
        minmax = NReportTools.getMinMax(resident);
        if (minmax != null) {
            // further restrictions
            min = SYSCalendar.bom(minmax.getStart().toLocalDate());
            max = resident.isActive() ? new LocalDate() : SYSCalendar.bom(minmax.getEnd().toLocalDate());
        }
        holidays = SYSCalendar.getHolidays(minmax.getStart().getYear(), minmax.getEnd().getYear());

    }

    public Scene createMainScene() {


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox scrollRoot = new VBox();
        scrollRoot.getChildren().add(scrollPane);
        scrollPane.setContent(root);

        Scene scene = new Scene(scrollRoot, Color.ALICEBLUE);


        if (minmax != null) {

//            int maxYears = Years.yearsBetween(start.toDateTimeAtStartOfDay(), end.toDateTimeAtStartOfDay()).getYears();


            for (int year = max.getYear(); year >= min.getYear(); year--) {
                root.getChildren().add(createTitledPane(year));
            }
        }


        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(new MenuItem("menu1"));
        contextMenu.getItems().add(new MenuItem("menu2"));
        contextMenu.getItems().add(new MenuItem("menu3"));
        contextMenu.getItems().add(new MenuItem("menu4"));

        Button menuButton = new Button("i have a menu");


        menuButton.setOnAction(event -> {
            contextMenu.show(menuButton, Side.LEFT, 0d, 0d);
        });


        return scene;
    }


    private TitledPane createTitledPane(final int year) {
        final VBox content = new VBox();

        final LocalDate start = new LocalDate(year, 1, 1).isBefore(min.dayOfMonth().withMinimumValue()) ? min.dayOfMonth().withMinimumValue() : new LocalDate(year, 1, 1);
        final LocalDate end = new LocalDate(year, 12, 31).isAfter(max.dayOfMonth().withMaximumValue()) ? max.dayOfMonth().withMaximumValue() : new LocalDate(year, 12, 31);
        TitledPane titledPane = FXTools.createTitledPane(root, Integer.toString(year), null, content, new Button("pushMe"));

        for (LocalDate month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
            content.getChildren().add(createTitledPane(new YearMonth(month)));
        }

        return titledPane;
    }

    private TitledPane createTitledPane(final YearMonth yearMonth) {
        final VBox content = new VBox();


        final LocalDate start = SYSCalendar.max(yearMonth.toLocalDate(1), min.dayOfMonth().withMinimumValue());
        final LocalDate end = SYSCalendar.max(SYSCalendar.eom(yearMonth.toLocalDate(1)), min.dayOfMonth().withMaximumValue());

        TitledPane titledPane = FXTools.createTitledPane(root, yearMonth.toString("MMMM yyyy"), null, content, new Button("pushMe"));

        for (LocalDate day = end; day.compareTo(start) >= 0; day = day.minusDays(1)) {
            content.getChildren().add(createTitledPane(day));
        }

        return titledPane;
    }

    private TitledPane createTitledPane(final LocalDate day) {
           final VBox content = new VBox();
           content.getChildren().addAll(new Label("Content"), new Label("Goes"), new Label("Here"));


           TitledPane titledPane = FXTools.createTitledPane(root, day.toString("EEEE, dd.MM.yyyy"), null, content, new Button("pushMe"));

//           for (LocalDate month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
//               content.getChildren().add(titledPane);
//           }

           return titledPane;
    }

    @Override
    public void cleanup() {
        yearDividers.clear();
        monthDividers.clear();
    }
}
