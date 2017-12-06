UPDATE `sysprops` SET `V` = '6' WHERE `K` = 'dbstructure';
UPDATE `resinfotype` SET `XML` = '<tx tooltip="Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 11."/>
<label fontstyle="bold" label="Denken Sie daran, dass die Diagnose Demenz immer durch einen Arzt durchgeführt werden muss."/>
<optiongroup name="type_of_dementia" label="Form der Demenz"><option label="Nicht dement" name="none" default="true"/><option label="Primäre Demenz" name="primary" tooltip="Bei einer primären Demenz liegen neurodegenerative oder vaskuläre Veränderungen vor. Es wird unterschieden, ob die Nervenzellen des Gehirns degenerieren, also ohne äußerlich erkennbare Ursache untergehen (wie bei der Alzheimer-Krankheit), oder ob sie z.B. wegen Durchblutungsstörungen schwere Schäden erlitten haben (diese Form wird als vaskulärer Demenztyp bezeichnet)."/><option label="Sekundäre Demenz" tooltip="Hier ist der geistige Verfall die Folge einer anderen organischen Erkrankung wie einer Hirnverletzung, einer Hirngeschwulst oder einer Herz-Kreislauf-Krankheit; auch Arzneistoffe und Gifte wie Alkohol (Korsakow- Syndrom) oder andere Drogen können dazu führen. Wenn die Grunderkrankung wirksam behandelt wird, Giftstoffe das Gehirn nicht mehr belasten oder Verletzungen geheilt sind, normalisiert sich meist die geistige Leistungsfähigkeit. Oder es ist ein Stillstand des Leidens zu erreichen." name="secondary"/></optiongroup>
<label fontstyle="bold" label="Welche Tests wurden durchgeführt ?"/>
<checkbox name="cct" label="Uhrentest (Clock Completion Test)"/>
<checkbox name="demtect" label="DemTect" layout="left"/>
<checkbox name="tfdd" label="TFDD" tooltip="TFDD - Test zu Früherkennung von Demenzen mit Depressionsabgrenzung" layout="left"/>
<label fontstyle="bold" label="Bitte ausgefüllte Tests an diese Info anhängen."/>
<separator/>
<optiongroup name="demenzgrad" label="Grad der Demenz (nach Feil)">
<option label="keine klare Zuordnung möglich" name="tnz" default="true"/>
<option label="Mangelhaft unglückliche Orientierung" name="stufe1"/>
<option label="Zeitverwirrtheit" name="stufe2"/>
<option label="Sich wiederholende Bewegungen" name="stufe3" layout="br"/>
<option label="Vegetieren, vor sich hin dämmern" name="stufe4"/>
</optiongroup>
<separator/>
<label size="16" fontstyle="bold" label="Orientierungsfähigkeiten"/>
<optiongroup name="time" label="zeitlich"><option label="ja" name="yes1" default="true"/> <option label="nein" name="no1"/><option label="zeitweise" name="intermittent1"/></optiongroup>
<optiongroup layout="left" name="personal" label="persönlich"><option label="ja" name="yes2" default="true"/> <option label="nein" name="no2"/><option label="zeitweise" name="intermittent2"/></optiongroup>
<optiongroup name="location" label="örtlich"><option label="ja" name="yes3" default="true"/> <option label="nein" name="no3"/><option label="zeitweise" name="intermittent3"/></optiongroup>
<optiongroup name="situation" label="situativ"><option label="ja" name="yes4" default="true"/> <option label="nein" name="no4"/><option label="zeitweise" name="intermittent4"/></optiongroup>
<optiongroup name="runaway" label="weglauftendenz"><option label="ja" name="yes5" default="true"/> <option label="nein" name="no5"/><option label="zeitweise" name="intermittent5"/></optiongroup>
' WHERE `BWINFTYP` = 'ORIENT1';
UPDATE `resinfotype` SET `XML` = '<tx tooltip=\"[b]Seite 1, Abschnitt 4.[/b][br/]Alles was Du hier als Bemerkung einträgst, steht hinterher in der Bemerkungs-Zeile dieses Abschnitts im Überleitbogen.[br/][b]Lagerungsarten[/b] werden anhand der Pflegeplanungen bestimmt.\"/>\r\n<checkbox name=\"bedridden\" label=\"bettlägerig\"/>\r\n<!-- ==================Aufstehen================== -->\r\n<optiongroup name=\"stand\" label=\"Aufstehen\">\r\n<option label=\"trifft nicht zu\" name=\"na\" />\r\n<option label=\"selbstständig\" name=\"none\" default=\"true\"/>\r\n<option label=\"mit Anleitung\" name=\"lvl1\"/>\r\n<option label=\"teilweise Übernahme\" name=\"lvl2\"/>\r\n<option label=\"vollständige Übernahme\" name=\"lvl3\"/>\r\n</optiongroup>\r\n<textfield label=\"Hilfsmittel\" name=\"stand.aid\" innerlayout=\"left\"/>\r\n<!-- ==================Gehen================== -->\r\n<optiongroup name=\"walk\" label=\"Gehen\">\r\n<option label=\"trifft nicht zu\" name=\"na\" />\r\n<option label=\"selbstständig\" name=\"none\" default=\"true\"/>\r\n<option label=\"mit Anleitung\" name=\"lvl1\"/>\r\n<option label=\"teilweise Übernahme\" name=\"lvl2\"/>\r\n<option label=\"vollständige Übernahme\" name=\"lvl3\"/>\r\n</optiongroup>\r\n<textfield label=\"Hilfsmittel\" name=\"walk.aid\" innerlayout=\"left\"/>\r\n<!-- ==================Transfer================== -->\r\n<optiongroup name=\"transfer\" label=\"Transfer\">\r\n<option label=\"trifft nicht zu\" name=\"na\" />\r\n<option label=\"selbstständig\" name=\"none\" default=\"true\"/>\r\n<option label=\"mit Anleitung\" name=\"lvl1\"/>\r\n<option label=\"teilweise Übernahme\" name=\"lvl2\"/>\r\n<option label=\"vollständige Übernahme\" name=\"lvl3\"/>\r\n</optiongroup>\r\n<textfield label=\"Hilfsmittel\" name=\"transfer.aid\" innerlayout=\"left\"/>\r\n<!-- ==================Toilettengang================== -->\r\n<optiongroup name=\"toilet\" label=\"Toilettengang\">\r\n<option label=\"trifft nicht zu\" name=\"na\" />\r\n<option label=\"selbstständig\" name=\"none\" default=\"true\"/>\r\n<option label=\"mit Anleitung\" name=\"lvl1\"/>\r\n<option label=\"teilweise Übernahme\" name=\"lvl2\"/>\r\n<option label=\"vollständige Übernahme\" name=\"lvl3\"/>\r\n</optiongroup>\r\n<textfield label=\"Hilfsmittel\" name=\"toilet.aid\" innerlayout=\"left\"/>\r\n<!-- ==================Sitzen im Stuhl================== -->\r\n<optiongroup name=\"sitting\" label=\"Sitzen im Stuhl\">\r\n<option label=\"trifft nicht zu\" name=\"na\" />\r\n<option label=\"selbstständig\" name=\"none\" default=\"true\"/>\r\n<option label=\"mit Anleitung\" name=\"lvl1\"/>\r\n<option label=\"teilweise Übernahme\" name=\"lvl2\"/>\r\n<option label=\"vollständige Übernahme\" name=\"lvl3\"/>\r\n</optiongroup>\r\n<textfield label=\"Hilfsmittel\" name=\"sitting.aid\" innerlayout=\"left\"/>\r\n<!-- ==================Beweglichkeit im Bett================== -->\r\n<optiongroup name=\"bedmovement\" label=\"Beweglichkeit im Bett\">\r\n<option label=\"trifft nicht zu\" name=\"na\" />\r\n<option label=\"selbstständig\" name=\"none\" default=\"true\"/>\r\n<option label=\"mit Anleitung\" name=\"lvl1\"/>\r\n<option label=\"teilweise Übernahme\" name=\"lvl2\"/>\r\n<option label=\"vollständige Übernahme\" name=\"lvl3\"/>\r\n</optiongroup>\r\n<textfield label=\"Hilfsmittel\" name=\"bedmovement.aid\" innerlayout=\"left\"/>\r\n<tabgroup label=\"Hilfsmittel zur Bewegung\" name=\"movement.aid\">\r\n<checkbox name=\"wheel.aid\" label=\"Rollstuhl\"/>\r\n<checkbox name=\"crutch.aid\" label=\"Unterarmgehstütze\"/>\r\n<checkbox name=\"walker.aid\" label=\"Rollator\"/>\r\n<checkbox name=\"cane.aid\" label=\"Gehstock\"/>\r\n</tabgroup>\r\n<label label=\"Den Toilettenstuhl kannst Du unter \'Inkontinenzhilfsmittel\' eintragen.\" fontstyle=\"bold\" name=\"lab1\"/>\r\n<textfield label=\"sonstige Hilfsmittel\" name=\"other.aid\" innerlayout=\"left\"/>' WHERE `BWINFTYP` = 'MOBILITY';
INSERT INTO `resinfotype` (`BWINFTYP`, `XML`, `BWInfoKurz`, `BWInfoLang`, `BWIKID`, `type`) VALUES ('AMPUTATION', '<label size=\"16\"  fontstyle=\"bold\" label=\"Amputationsangaben (werden zur Berechnung der angepassten Köpergewichte herangezogen)\"/>\r\n<tx tooltip=\"Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 18.\"/>\r\n\r\n<optiongroup name=\"upperleft\" label=\"linkes, oberes Viertel\">\r\n<option label=\"keine Amputation\" name=\"none\" default=\"true\"/>\r\n<option label=\"linke Hand\" name=\"hand\"/>\r\n<option label=\"Unterhalb des linken Ellenbogens\" name=\"belowellbow\"/>\r\n<option label=\"Oberhalb des linken Ellenbogens\" name=\"aboveellbow\" layout=\"br tab\"/>\r\n<option label=\"Vollständige linke, obere Extremität\" name=\"complete\"/>\r\n</optiongroup>\r\n\r\n<optiongroup name=\"upperright\" label=\"rechtes, oberes Viertel\">\r\n<option label=\"keine Amputation\" name=\"none\" default=\"true\"/>\r\n<option label=\"rechte Hand\" name=\"hand\"/>\r\n<option label=\"Unterhalb des rechten Ellenbogens\" name=\"belowellbow\"/>\r\n<option label=\"Oberhalb des rechten Ellenbogens\" name=\"aboveellbow\" layout=\"br tab\"/>\r\n<option label=\"Vollständige rechte, obere Extremität\" name=\"complete\"/>\r\n</optiongroup>\r\n\r\n<optiongroup name=\"lowerleft\" label=\"linkes, unteres Viertel\">\r\n<option label=\"keine Amputation\" name=\"none\" default=\"true\"/>\r\n<option label=\"linker Fuß\" name=\"foot\"/>\r\n<option label=\"Unterhalb des linken Knies\" name=\"belowknee\"/>\r\n<option label=\"Oberhalb des linken Knies\" name=\"aboveknee\" layout=\"br tab\"/>\r\n<option label=\"Vollständige linke, untere Extremität\" name=\"complete\"/>\r\n</optiongroup>\r\n\r\n<optiongroup name=\"lowerright\" label=\"rechtes, unteres Viertel\">\r\n<option label=\"keine Amputation\" name=\"none\" default=\"true\"/>\r\n<option label=\"rechter Fuß\" name=\"foot\"/>\r\n<option label=\"Unterhalb des rechten Knies\" name=\"belowknee\"/>\r\n<option label=\"Oberhalb des rechten Knies\" name=\"aboveknee\" layout=\"br tab\"/>\r\n<option label=\"Vollständige rechte, untere Extremität\" name=\"complete\"/>\r\n</optiongroup>', 'Amputationen', '', '15', '140');
UPDATE `resinfotype` SET `XML` = '<tx tooltip=\"Diese Eintragungen werden in den Überleitbogen übernommen. Seite 2, Abschnitt 9.[br/]Der Wert für den BMI wird automatisch anhand der letzten Bewohner-Werte für Köpergröße und Gewicht berechnet.\"/>\r\n<optiongroup name=\"assistancelevelfood\" label=\"Hilfebedarf\">\r\n<option label=\"selbständig\" name=\"none\" default=\"true\"/> \r\n<option label=\"braucht Anregung\" name=\"needsmotivation\"/>\r\n<option label=\"braucht Hilfe\" name=\"needshelp\"/>\r\n<option label=\"vollständige Hilfe\" name=\"completehelp\"/>\r\n</optiongroup>\r\n<checkbox label=\"Muss zum Trinken angehalten werden\" name=\"motivationdrinking\"/>\r\n<checkbox label=\"Trinkverhalten selbstständig\" name=\"drinksalone\" layout=\"left\"/>\r\n<checkbox label=\"Nahrungskarenz\" name=\"abrosia\" layout=\"left\"/>\r\n<checkbox label=\"Diätkost\" name=\"diet\" layout=\"left\"/>\r\n<checkbox label=\"Schluckstörungen\" name=\"dysphagia\"/>\r\n<checkbox label=\"mundgerechte Zubereitung\" name=\"bitesize\" layout=\"left\"/>\r\n<separator/>\r\n<textfield name=\"likes\" label=\"Vorlieben, Essen und Trinken\" hfill=\"false\" length=\"40\"/>\r\n<textfield name=\"hates\" label=\"Abneigungen, Essen und Trinken\" hfill=\"false\" length=\"40\"/>\r\n<separator/>\r\n<textfield name=\"breadunit\" label=\"Broteinheiten (in 24h)\" tooltip=\"Hinterlegen Sie die zugehörige ärztliche Verordnung.\" hfill=\"false\"  type=\"double\"  length=\"20\"/>\r\n<textfield name=\"zieltrinkmenge\" label=\"Zieltrinkmenge (ml in 24h)\" tooltip=\"Hinterlegen Sie die zugehörige ärztliche Verordnung.\"  layout=\"br\" hfill=\"false\"  type=\"double\"  length=\"20\"/>\r\n<textfield name=\"ubw\" label=\"Übliches Körpergewicht\" tooltip=\"Was [der|die] Bewohner[in] als [ihr|sein] übliches Gewicht angibt. Einheit: kg\" type=\"double\" layout=\"br\" hfill=\"false\" length=\"20\"/>' WHERE `BWINFTYP` = 'FOOD';
UPDATE `resvaluetypes` SET `ValType` = '13' WHERE `ID` = '13';