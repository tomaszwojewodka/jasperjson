package it.streamlining.jasper.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Path("report")
public class ReportResource {
    public static final String REPORT_DIR_PATH = "C://opt/tmp/report/";
    public static final String TEMPLATE = "report.jrxml";
    public static final String DATA_JSON = "data.json";

    // works fine
    @GET
    @Path("new-parameters")
    public String generateByParameters() {
        String reportPath = String.format("%s.pdf", REPORT_DIR_PATH + getFormattedDateTime());
        try {
            JasperReport report = JasperCompileManager.compileReport(getFilePath(TEMPLATE));
            Map<String, Object> parameters = getParameters();

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters);

            JasperExportManager.exportReportToPdfFile(jasperPrint, reportPath);
        } catch (JRException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return reportPath;
    }

    // doesn't work properly
    @GET
    @Path("new-data-source")
    public String generateByDataSource() {
        String reportPath = String.format("%s.pdf", REPORT_DIR_PATH + getFormattedDateTime());
        try {
            JasperReport report = JasperCompileManager.compileReport(getFilePath(TEMPLATE));
            Map<String, Object> parameters = getParameters();

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, getDataSource());

            JasperExportManager.exportReportToPdfFile(jasperPrint, reportPath);
        } catch (JRException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return reportPath;
    }

    private JsonDataSource getDataSource() throws JRException, FileNotFoundException {
        return new JsonDataSource(new FileInputStream(getFilePath(DATA_JSON)));
    }

    private Map<String, Object> getParameters() throws FileNotFoundException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(JsonQueryExecuterFactory.JSON_INPUT_STREAM, new FileInputStream(getFilePath(DATA_JSON)));
        return parameters;
    }

    private String getFormattedDateTime() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        return date.format(formatter);
    }

    private String getFilePath(String fileName) {
        String externalFilePath = REPORT_DIR_PATH + fileName;
        return new File(externalFilePath).exists() ? externalFilePath : getFixedFilePath(fileName);
    }

    private String getFixedFilePath(String fileName) {
        return getClass().getClassLoader().getResource(fileName).getPath();
    }
}
