package de.offene_pflege.entity.info;


import com.google.common.collect.ArrayTable;
import com.google.common.collect.Lists;
import de.offene_pflege.entity.prescription.PrescriptionTools;
import de.offene_pflege.op.system.AppInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Log4j2
/**
 * still experimental
 */
public class KVExport {
    final MultiKeyMap import_map;

    final ArrayTable<Resident, String, String> export_table;

    public static KVExport create() throws IOException {
        return new KVExport();
    }

    private KVExport() throws IOException {
        import_map = new MultiKeyMap();
        ArrayList<Resident> list_residents = ResidentTools.getAllActive();
        ArrayList<String> list_fields = Lists.newArrayList("filename", "kv", "name", "geb", "kvkennung", "versno", "status",
                "betrsnr", "arztnr", "datum", "pg", "behinderung", "wirkstoffe", "verfuegung",
                "gerassess", "gadatum", "zahnu", "hm", "hm2", "impf", "grippe",
                "betr-name", "betr-str", "betr-ort", "betr-tel",
                "ang-name", "ang-str", "ang-ort", "ang-tel");

        export_table = ArrayTable.create(
                list_residents,
                list_fields
        );


        list_residents.forEach(resident ->
                ResInfoTools.getAllActive(resident).forEach(resInfo ->
                        ResInfoTools.getContent(resInfo).forEach((k, v) ->
                                import_map.put(resident, resInfo.getResInfoType().getType(), k, v)
                        )
                )
        );


        ResidentTools.getAllActive().forEach(resident -> {
            aa(resident);
            bb(resident);
            betr(resident);
            ang(resident);
        });

        FileWriter out = new FileWriter(AppInfo.getOPCache() + File.separator + "kv-nordrhein.csv");
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.Builder.create().build());
        printer.printRecord(list_fields);
        export_table.rowKeySet().forEach(resident -> {
            try {
                printer.printRecord(export_table.row(resident).values());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        printer.close();
        out.close();

//        export_table.
//                export_table.forEach((k, value) -> {
//                    MultiKey key = (MultiKey) k;
//                    export_table.get(key.getKey(0));
//                    //printer.printRecord();
//                });

        log.debug(export_table);

    }

    private void betr(Resident resident) {
        if (!import_map.containsKey(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "name") || import_map.get(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "name").toString().isEmpty()) {
            export_table.put(resident, "betr-name", "--");
        } else {
            export_table.put(resident, "betr-name", import_map.get(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "name") + ", " + import_map.get(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "firstname"));
            export_table.put(resident, "betr-str", import_map.get(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "street").toString());
            export_table.put(resident, "betr-ort", import_map.get(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "zip") + " " + import_map.get(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "city"));
            export_table.put(resident, "betr-tel", import_map.get(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "tel") + " " + import_map.get(resident, ResInfoTypeTools.TYPE_LEGALCUSTODIANS, "mobile"));
        }

    }

    private void ang(Resident resident) {
        if (import_map.containsKey(resident, ResInfoTypeTools.TYPE_SOZIALES, "name1") && !import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "name1").toString().isEmpty()) {
            export_table.put(resident, "ang-name", import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "name1").toString());
            export_table.put(resident, "ang-str", import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "ans1").toString());
            export_table.put(resident, "ang-tel", import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "tel1").toString());
            return;
        }
        if (!import_map.containsKey(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1name") || import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1name").toString().isEmpty()) {
            export_table.put(resident, "ang-name", "--");
            return;
        }
        export_table.put(resident, "ang-name", import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1name") + ", " + import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1firstname"));
        export_table.put(resident, "ang-str", import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1street").toString());
        export_table.put(resident, "ang-ort", import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1zip") + " " + import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1city"));
        export_table.put(resident, "ang-tel", import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1tel") + " " + import_map.get(resident, ResInfoTypeTools.TYPE_SOZIALES, "c1mobile"));

    }


    private void bb(Resident resident) {
        export_table.put(resident, "pg", import_map.containsKey(resident, ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade") ?
                import_map.get(resident, ResInfoTypeTools.TYPE_NURSING_INSURANCE, "grade").toString()
                : "--"
        );
        export_table.put(resident, "wirkstoffe", Integer.toString(PrescriptionTools.getPrescribedTradeforms(resident).size()));

        String hm = "";

        for (String hms : new String[]{"kruecke", "gehstock", "rollstuhl", "rollator"}) {
            if (import_map.get(resident, ResInfoTypeTools.TYPE_MOBILITY, hms).toString().equalsIgnoreCase("true")) {
                hms = StringUtils.replace(hms, "kruecke", "Unterarm-Stütze");
                hms = StringUtils.replace(hms, "gehstock", "Gehstock");
                hms = StringUtils.replace(hms, "rollstuhl", "Rollstuhl");
                hms = StringUtils.replace(hms, "rollator", "Rollator");
                hm += hms + " ";
            }
        }
        export_table.put(resident, "hm", hm.isEmpty() ? "nein" : "ja");
        export_table.put(resident, "hm2", hm);


    }

    void aa(Resident resident) {
        export_table.put(resident, "filename", ResidentTools.getTextCompact(resident));
        export_table.put(resident, "kv", import_map.get(resident, ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "hiname").toString());
        export_table.put(resident, "name", resident.getName() + ", " + resident.getFirstname());
        export_table.put(resident, "geb", SimpleDateFormat.getDateInstance(DateFormat.DEFAULT).format(resident.getDob()));
        export_table.put(resident, "kvkennung", import_map.get(resident, ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "insuranceno").toString());
        export_table.put(resident, "versno", import_map.get(resident, ResInfoTypeTools.TYPE_HEALTH_INSURANCE, "personno").toString());
        export_table.put(resident, "status", "--");
        export_table.put(resident, "betrsnr", "--");
        export_table.put(resident, "arztnr", "770222501");
        export_table.put(resident, "datum", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        export_table.put(resident, "verfuegung", import_map.containsKey(resident, ResInfoTypeTools.TYPE_LIVINGWILL, "validthru") ? "ja" : "nein");
    }


}
